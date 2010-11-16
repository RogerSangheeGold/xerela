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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.osgi.framework.Bundle;
import org.xerela.provider.scheduler.ExecutionData;
import org.xerela.provider.tools.internal.PluginsActivator;
import org.xerela.zap.jta.TransactionElf;

/**
 * PluginDetailServlet
 */
public class PluginDetailServlet extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(PluginDetailServlet.class);
    private static final long serialVersionUID = 4064267748277646916L;

    private static final String EXTENSION_POINT_ID = "org.xerela.provider.plugins.servletDetail"; //$NON-NLS-1$
    private static final String HTTP_EXECUTION_ID = "executionId"; //$NON-NLS-1$

    private Map<String, IPluginDetailStreamer> streamerMap;

    /** {@inheritDoc} */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        initialize();

        String executionId = req.getParameter(HTTP_EXECUTION_ID);
        if (executionId == null)
        {
            resp.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "No executionId query parameter specified."); //$NON-NLS-1$
            return;
        }

        ExecutionData executionData = getExecutionData(executionId);
        if (executionData == null)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No execution record with specified ID was found."); //$NON-NLS-1$
            return;
        }

        IPluginDetailStreamer streamer = streamerMap.get(executionData.getJobClass());
        if (streamer == null)
        {
            resp.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "No handler found to supply detail for the associated job type."); //$NON-NLS-1$
            return;
        }

        streamer.doGet(req, resp);
    }

    /**
     * Initialize the map of extensions that provide detail output for specific
     * job types.
     */
    private synchronized void initialize()
    {
        if (streamerMap != null)
        {
            return;
        }

        streamerMap = new HashMap<String, IPluginDetailStreamer>();

        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] configElements = extensionRegistry.getConfigurationElementsFor(EXTENSION_POINT_ID);

        if (configElements.length == 0)
        {
            LOGGER.warn(String.format("No %s extensions discovered.", EXTENSION_POINT_ID)); //$NON-NLS-1$
        }
        else
        {
            for (IConfigurationElement element : configElements)
            {
                String className = element.getAttribute("class"); //$NON-NLS-1$
                String jobClass = element.getAttribute("jobClass"); //$NON-NLS-1$
                try
                {
                    String targetBundle = element.getContributor().getName();
                    Bundle bundle = PluginsActivator.getBundle(targetBundle);
                    Class<?> clazz = bundle.loadClass(className);

                    IPluginDetailStreamer newInstance = (IPluginDetailStreamer) clazz.newInstance();
                    streamerMap.put(jobClass, newInstance);
                }
                catch (Exception cnfe)
                {
                    LOGGER.error(String.format("ConfigStore bundle unable to load extension class '%s'", className), cnfe); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Get the ExecutionData record by the requested executionId.
     *
     * @param executionId the execution ID
     * @return an ExecutionData object or <code>null</code>
     */
    private ExecutionData getExecutionData(String executionId)
    {
        boolean ownTransaction = TransactionElf.beginOrJoinTransaction();

        try
        {
            Session session = PluginsActivator.getSessionFactory().getCurrentSession();
            Criteria criteria = session.createCriteria(ExecutionData.class);
            criteria.add(Restrictions.idEq(Integer.valueOf(executionId)));
            ExecutionData uniqueResult = (ExecutionData) criteria.uniqueResult();

            return uniqueResult;
        }
        finally
        {
            if (ownTransaction)
            {
                TransactionElf.commit();
            }
        }
    }
}
