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

package org.xerela.provider.scheduler;

import java.util.Date;

import org.hibernate.Session;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.xerela.provider.scheduler.internal.SchedulerActivator;
import org.xerela.zap.jta.TransactionElf;

/**
 * Listens to the scheduler.
 */
public class ExecutionTriggerListener extends TriggerListenerSupport
{
    /**
     * Constructor.
     */
    public ExecutionTriggerListener()
    {
        getLog().info(this.getClass().getSimpleName() + " registered with scheduler.");
    }

    /** {@inheritDoc} */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode)
    {
        ExecutionData execution = (ExecutionData) context.get(ExecutionData.class);

        execution.setEndTime(new Date(context.getFireTime().getTime() + context.getJobRunTime()));

        Scheduler scheduler = SchedulerActivator.getScheduler();
        if (scheduler != null)
        {
            scheduler.save(execution);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context)
    {
        TransactionElf.beginOrJoinTransaction();

        boolean success = false;
        try
        {
            Scheduler scheduler = SchedulerActivator.getScheduler();

            ExecutionData execution = (ExecutionData) context.get(ExecutionData.class);
            if (execution == null)
            {
                Integer id = (Integer) context.getMergedJobDataMap().get("executionId");
                if (id == null || id == -1)
                {
                    execution = scheduler.createExecution(trigger, context.getJobDetail().getJobClass());
                    execution.setStartTime(context.getFireTime());
                    scheduler.save(execution);
                }
                else
                {
                    if (!SchedulerActivator.isRAMStore())
                    {
                        Session session = SchedulerActivator.getSessionFactory().getCurrentSession();

                        execution = (ExecutionData) session.get(ExecutionData.class, id);
                        execution.setStartTime(context.getFireTime());
                        session.update(execution);
                    }
                }

                context.put(ExecutionData.class, execution);
            }
            else
            {
                execution.setStartTime(context.getFireTime());
                scheduler.save(execution);
            }

            success = true;
        }
        finally
        {
            if (success)
            {
                TransactionElf.commit();
            }
            else
            {
                TransactionElf.rollback();
            }
        }
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return this.getClass().getSimpleName();
    }
}
