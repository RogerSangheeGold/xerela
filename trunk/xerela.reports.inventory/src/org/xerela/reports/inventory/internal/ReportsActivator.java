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

package org.xerela.reports.inventory.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xerela.provider.configstore.IConfigStore;

/**
 * ReportsActivator
 */
public class ReportsActivator implements BundleActivator
{
    private static ServiceTracker configStoreTracker;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        configStoreTracker = new ServiceTracker(context, IConfigStore.class.getName(), null);
        configStoreTracker.open();
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
    }

    /**
     * Get a reference to the configuration store.
     *
     * @return a reference to an IConfigStore
     */
    public static IConfigStore getConfigStore()
    {
        return (IConfigStore) configStoreTracker.getService();
    }
}
