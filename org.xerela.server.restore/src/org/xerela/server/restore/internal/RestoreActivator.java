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

package org.xerela.server.restore.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xerela.provider.configstore.IConfigStore;
import org.xerela.provider.scheduler.IScheduler;
import org.xerela.server.dispatcher.OperationManager;

/**
 * The {@link RestoreActivator} class provides the mechanism for starting and stopping the functionality of the
 * org.xerela.server.restore bundle.
 * 
 * @author Dylan White (dylamite@ziptie.org)
 */
public class RestoreActivator implements BundleActivator
{
    private static ServiceTracker configStoreTracker;
    private static ServiceTracker operationManagerTracker;
    private static ServiceTracker schedulerTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception
    {
        operationManagerTracker = new ServiceTracker(context, OperationManager.class.getName(), null);
        operationManagerTracker.open();

        configStoreTracker = new ServiceTracker(context, IConfigStore.class.getName(), null);
        configStoreTracker.open();

        schedulerTracker = new ServiceTracker(context, IScheduler.class.getName(), null);
        schedulerTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception
    {
        schedulerTracker.close();
        schedulerTracker = null;

        configStoreTracker.close();
        configStoreTracker = null;

        operationManagerTracker.close();
        operationManagerTracker = null;
    }

    /**
     * Get the Operation Manager for device operations.
     *
     * @return the Operation Manager
     */
    public static OperationManager getOperationManager()
    {
        return (OperationManager) operationManagerTracker.getService();
    }

    /**
     * Look up the config store service.
     * 
     * @return The config store service.
     */
    public static IConfigStore getConfigStoreService()
    {
        return (IConfigStore) configStoreTracker.getService();
    }

    /**
     * Look up the scheduler service.
     * 
     * @return The scheduler service.
     */
    public static IScheduler getSchedulerService()
    {
        return (IScheduler) schedulerTracker.getService();
    }
}
