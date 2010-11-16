/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is Ziptie Client Framework.
 * 
 * The Initial Developer of the Original Code is AlterPoint.
 * Portions created by AlterPoint are Copyright (C) 2006,
 * AlterPoint, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 */

package org.xerela.provider.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.xerela.provider.devices.DeviceResolutionElf;
import org.xerela.provider.devices.ZDeviceCore;
import org.xerela.provider.devices.ZDeviceLite;
import org.xerela.provider.scheduler.ExecutionData;
import org.xerela.provider.scheduler.ISecureJob;
import org.xerela.provider.scheduler.JobData;
import org.xerela.provider.tools.internal.PluginsActivator;
import org.xerela.server.dispatcher.ITask;
import org.xerela.server.dispatcher.ITaskListener;
import org.xerela.server.dispatcher.OperationManager;
import org.xerela.server.dispatcher.Outcome;
import org.xerela.server.dispatcher.TaskCompleteEvent;
import org.xerela.server.dispatcher.TaskEvent;
import org.xerela.server.job.backup.BackupResultListener;
import org.xerela.server.security.ZPrincipal;
import org.xerela.zap.jms.EventElf;
import org.xerela.zap.jta.TransactionElf;
import org.xerela.zap.security.IUserSession;

/**
 * ScriptToolJob
 */
public class ScriptToolJob implements InterruptableJob, ISecureJob, ITaskListener
{
    private static final Logger LOGGER = Logger.getLogger(ScriptToolJob.class);

    private static final String TOOL_NAME = "tool"; //$NON-NLS-1$

    private static final String PLUGIN_QUEUE = "plugins"; //$NON-NLS-1$
    private static final String EVENT_SCRIPT_START = "started"; //$NON-NLS-1$
    private static final String EVENT_SCRIPT_DETAIL = "script"; //$NON-NLS-1$
    private static final String UTF_8_ENCODING = "UTF-8"; //$NON-NLS-1$
    private static final String UNABLE_TO_SEND_JMS_EVENT = "Unable to send JMS event"; //$NON-NLS-1$

    private OperationManager operationManager;
    private Integer batchID;
    private Semaphore semaphore;
    private JobDetail jobDetail;
    private ExecutionData execution;
    private String toolName;

    private ZToolProperties toolProperties;

    /**
     * Default constructor.
     */
    public ScriptToolJob()
    {
        operationManager = PluginsActivator.getOperationManager();
    }

    /** {@inheritDoc} */
    public void interrupt() throws UnableToInterruptJobException
    {
        if (operationManager == null || batchID == null)
        {
            return;
        }

        operationManager.cancelJobs(batchID);
        semaphore.release(Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public void execute(JobExecutionContext executionContext) throws JobExecutionException
    {
        jobDetail = executionContext.getJobDetail();
        execution = (ExecutionData) executionContext.get(ExecutionData.class);

        LOGGER.info(Messages.bind(Messages.ScriptToolJob_startingTool, jobDetail.getGroup(), jobDetail.getName()));

        JobDataMap mergedJobDataMap = executionContext.getMergedJobDataMap();

        // We OWN this transaction.
        TransactionElf.beginOrJoinTransaction();

        try
        {
            toolName = mergedJobDataMap.getString(TOOL_NAME);
            String ipData = mergedJobDataMap.getString("ipResolutionData"); //$NON-NLS-1$
            String ipScheme = mergedJobDataMap.getString("ipResolutionScheme"); //$NON-NLS-1$
            String username = mergedJobDataMap.getString("username"); //$NON-NLS-1$

            ScriptPluginManager scp = (ScriptPluginManager) PluginsActivator.getPluginManager(ScriptPluginManager.class.getName());
            toolProperties = scp.getPluginProperties(toolName);
            String toolScript = scp.getToolScript(toolName);

            List<ZDeviceLite> devices = DeviceResolutionElf.resolveDevices(ipScheme, ipData);

            Properties inputProperties = new Properties();
            String[] keys = mergedJobDataMap.getKeys();
            for (String key : keys)
            {
                if (key.startsWith("input.")) //$NON-NLS-1$
                {
                    inputProperties.put(key, mergedJobDataMap.get(key));
                }
            }

            createExecRecord();

            List<ITask> tasks = new LinkedList<ITask>();
            if (toolProperties.getMode() == ZToolProperties.ToolMode.COMBINED)
            {
                ITask task = new ScriptToolTask(devices, toolName, toolScript, toolProperties, inputProperties, username);
                tasks.add(task);
            }
            else
            {
                // The reasons for shuffling are complex, but basically because access to devices
                // are locked (only one operation per-device at a time), if multiple jobs are running
                // against the same set of devices then if they are in the same order one job's
                // operations will fall into lock-step with the other's, even though one could
                // possibly execute quickly (like a Ping tool) and the other slowly (a backup).
                // By ensuring the devices are not processed in the same order, we avoid any
                // such "lock-stepping" between multiple jobs against the same device sets.
                Collections.shuffle(devices);
                for (ZDeviceLite device : devices)
                {
                    ITask task = new ScriptToolTask(device, toolName, toolScript, toolProperties, inputProperties, username);
                    tasks.add(task);
                }
            }

            sendStartEvent(devices.size());

            try
            {
                semaphore = new Semaphore(tasks.size());
                semaphore.acquire(tasks.size());
                batchID = operationManager.submitJobs(tasks, false, this);
                semaphore.acquire(tasks.size());
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        finally
        {
            TransactionElf.commit();
        }
    }

    /**
     * Create a record of the execution.
     *
     * @param toolProperties 
     */
    private void createExecRecord()
    {
        PluginExecRecord execRecord = new PluginExecRecord();
        execRecord.setPluginName(toolName);
        execRecord.setOutputFormat(toolProperties.getOutputFormat());
        execRecord.setExecutionData(execution);

        save(execRecord);
    }

    /**
     * Process events from the dispatcher.
     *
     * @param taskEvent a dispatcher task event
     */
    public void eventOccurred(TaskEvent taskEvent)
    {
        if (taskEvent instanceof TaskCompleteEvent)
        {
            TaskCompleteEvent completeEvent = (TaskCompleteEvent) taskEvent;
            ScriptToolTask task = (ScriptToolTask) taskEvent.getTask();

            ZDeviceLite device = task.getDevice();
            if (device == null)
            {
                device = task.getDevices().get(0); // use the first device as the identifier on combined mode scripts
            }

            ToolRunDetails detail = createDetail(task);
            Outcome outcome = completeEvent.getOutcome();
            try
            {
                if (device != null)
                {

                    Object[] args = { device.getIpAddress(), device.getManagedNetwork(), jobDetail.getFullName() };
                    if (outcome == Outcome.SUCCESS)
                    {
                        LOGGER.info(Messages.bind(Messages.ScriptToolJob_success, args));
                    }
                    else if (outcome == Outcome.EXCEPTION)
                    {
                        LOGGER.warn(Messages.bind(Messages.ScriptToolJob_exception, args));

                        StringWriter writer = new StringWriter();
                        completeEvent.getThrowable().printStackTrace(new PrintWriter(writer));

                        detail.setDetails(null);
                        detail.setGridData(null);
                        detail.setError(writer.toString());
                    }
                    else if (outcome == Outcome.CANCELLED)
                    {
                        LOGGER.info(Messages.bind(Messages.ScriptToolJob_canceled, args));
                    }
                }
            }
            finally
            {
                semaphore.release();

                if (outcome != Outcome.CANCELLED)
                {
                    save(detail);
                }

                sendEvent(detail, device);
            }
        }
    }

    private ToolRunDetails createDetail(ScriptToolTask task)
    {
        ZDeviceLite device = task.getDevice();
        if (device == null)
        {
            device = task.getDevices().get(0); // use the first device as the identifier on combined mode scripts
        }

        ToolRunDetails detail = new ToolRunDetails();
        if (device != null)
        {
            ZDeviceCore core = new ZDeviceCore();
            core.setDeviceId(device.getDeviceId());
            detail.setDevice(core);
        }
        detail.setExecutionId(execution.getId());
        detail.setStartTime(task.getStartTime());
        detail.setEndTime(task.getEndTime());

        if (toolProperties.getOutputFormat().startsWith("grid")) //$NON-NLS-1$
        {
            splitOutput(task.getResultString(), detail);
        }
        else
        {
            detail.setDetails(task.getResultString());
        }

        return detail;
    }

    private void splitOutput(String output, ToolRunDetails detail)
    {
        BufferedReader reader = new BufferedReader(new StringReader(output));

        boolean processGrid = true;
        StringBuilder sb = new StringBuilder();
        try
        {
            while (true)
            {
                String readLine = reader.readLine();
                if (readLine == null)
                {
                    detail.setDetails(sb.toString());
                    break;
                }
                else if (processGrid && readLine.length() == 0)
                {
                    detail.setGridData(sb.toString());
                    sb.setLength(0);
                    processGrid = false;
                }
                else
                {
                    sb.append(readLine).append('\n');
                }
            }
        }
        catch (IOException io)
        {
            LOGGER.error("Error processing tool result", io); //$NON-NLS-1$
        }
    }

    @SuppressWarnings("nls")
    private void sendStartEvent(int totalDevices)
    {
        try
        {
            Properties properties = new Properties();
            properties.setProperty("ExecutionId", String.valueOf(execution.getId()));
            properties.setProperty("TotalDevices", String.valueOf(totalDevices));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            properties.storeToXML(baos, "", UTF_8_ENCODING); //$NON-NLS-1$

            // Tell the producer to send the message
            TextMessage message = EventElf.createTextMessage(PLUGIN_QUEUE, baos.toString(UTF_8_ENCODING));
            message.setJMSType(EVENT_SCRIPT_START);
            EventElf.sendMessage(PLUGIN_QUEUE, message);
        }
        catch (Exception e)
        {
            Logger.getLogger(BackupResultListener.class).error(UNABLE_TO_SEND_JMS_EVENT, e);
        }
    }

    @SuppressWarnings("nls")
    private void sendEvent(ToolRunDetails details, ZDeviceLite device)
    {
        try
        {
            Properties properties = new Properties();
            properties.setProperty("ExecutionId", String.valueOf(details.getExecutionId()));
            properties.setProperty("RecordId", String.valueOf(details.getId()));

            if (device != null)
            {
                properties.setProperty("IpAddress", device.getIpAddress());
                properties.setProperty("ManagedNetwork", device.getManagedNetwork());
            }

            if (details.getGridData() != null)
            {
                properties.setProperty("GridData", details.getGridData());
            }
            else if (details.getError() != null)
            {
                properties.setProperty("Error", details.getError());
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            properties.storeToXML(baos, "", UTF_8_ENCODING); //$NON-NLS-1$

            // Tell the producer to send the message
            TextMessage message = EventElf.createTextMessage(PLUGIN_QUEUE, baos.toString(UTF_8_ENCODING));
            message.setJMSType(EVENT_SCRIPT_DETAIL);
            EventElf.sendMessage(PLUGIN_QUEUE, message);
        }
        catch (Exception e)
        {
            Logger.getLogger(BackupResultListener.class).error(UNABLE_TO_SEND_JMS_EVENT, e);
        }
    }

    private void save(Object object)
    {
        boolean success = false;
        boolean ownTransaction = TransactionElf.beginOrJoinTransaction();
        try
        {
            Session session = PluginsActivator.getSessionFactory().getCurrentSession();

            session.save(object);

            success = true;
        }
        finally
        {
            if (success)
            {
                if (ownTransaction)
                {
                    TransactionElf.commit();
                }
            }
            else
            {
                TransactionElf.rollback();
            }
        }
    }

    /** {@inheritDoc} */
    public boolean validateCudOperation(JobData jobData)
    {
        String tool = jobData.getJobParameter(TOOL_NAME);
        ScriptPluginManager scp = (ScriptPluginManager) PluginsActivator.getPluginManager(ScriptPluginManager.class.getName());
        ZToolProperties properties = scp.getPluginProperties(tool);

        return validate(properties);
    }

    /** {@inheritDoc} */
    public boolean validateRunOperation(JobDetail detail)
    {
        String tool = detail.getJobDataMap().getString(TOOL_NAME);

        ScriptPluginManager scp = (ScriptPluginManager) PluginsActivator.getPluginManager(ScriptPluginManager.class.getName());
        ZToolProperties properties = scp.getPluginProperties(tool);

        return validate(properties);
    }

    private boolean validate(ZToolProperties properties)
    {
        IUserSession session = PluginsActivator.getSecurityService().getUserSession();
        ZPrincipal principal = (ZPrincipal) session.getPrincipal();
        String runPermission = properties.getRunPermission();
        if (runPermission != null && !principal.getRole().hasPermission(runPermission))
        {
            return false;
        }

        return true;
    }
}
