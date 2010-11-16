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

package org.xerela.provider.configstore;

import org.xerela.provider.configstore.internal.ConfigStoreActivator;
import org.xerela.provider.devices.IDeviceStoreObserver;
import org.xerela.provider.devices.ZDeviceCore;

/**
 * DeviceDeletionObserver
 */
public class DeviceStoreObserver implements IDeviceStoreObserver
{
    /** {@inheritDoc} */
    public void deviceTypeChanged(ZDeviceCore device)
    {
        // no-op
    }

    /** {@inheritDoc} */
    public void deviceCreated(ZDeviceCore device)
    {
        // no-op
    }

    /** {@inheritDoc} */
    public void deviceDeleted(ZDeviceCore device)
    {
        RuntimeException re = null;
        try
        {
            ConfigStore configStore = (ConfigStore) ConfigStoreActivator.getConfigStore();
            configStore.deleteRevisionHistory(device);
        }
        catch (RuntimeException e)
        {
            // Store the exception, but continue on with deleting the device from the full
            // text search index.
            re = e;
        }

        ConfigSearch configSearch = (ConfigSearch) ConfigStoreActivator.getConfigSearch();
        configSearch.deleteFromIndex(device);

        // If the first phase had an exception, re-throw it
        if (re != null)
        {
            throw re;
        }
    }
}
