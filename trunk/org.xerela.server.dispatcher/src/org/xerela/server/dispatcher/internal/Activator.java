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

package org.xerela.server.dispatcher.internal;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.xerela.server.dispatcher.OperationManager;

/**
 * Activator
 */
public class Activator implements BundleActivator
{
    private static final Logger DEV_LOG = Logger.getLogger(Activator.class);

    private static BundleContext context;
    private OperationManager operationManager;
    private ServiceRegistration serviceRegistration;

    /** {@inheritDoc} */
    public void start(BundleContext ctx) throws Exception
    {
        context = ctx;

        operationManager = new OperationManager();
        serviceRegistration = context.registerService(OperationManager.class.getName(), operationManager, null);

        Properties props = new Properties();
        String configArea = context.getProperty("osgi.configuration.area").replace(" ", "%20"); //$NON-NLS-1$
        configArea += (configArea != null ? "/dispatcher/dispatcher.properties" : "osgi-config/dispatcher/dispatcher.properties"); //$NON-NLS-1$
        File file = new File(URI.create(configArea));
        if (file.exists())
        {
            FileInputStream fis = new FileInputStream(file);
            props.load(fis);
        }

        if (props.getProperty("maxThreadCount") != null)
        {
            operationManager.setMaxThreadCount(Integer.valueOf(props.getProperty("maxThreadCount")));
        }

        DEV_LOG.info("Operation Dispatcher started.");
    }

    /** {@inheritDoc} */
    public void stop(BundleContext ctx) throws Exception
    {
        if (operationManager != null)
        {
            serviceRegistration.unregister();
            operationManager.shutdown();
        }
        DEV_LOG.info("Operation Dispatcher shutdown.");
    }
}
