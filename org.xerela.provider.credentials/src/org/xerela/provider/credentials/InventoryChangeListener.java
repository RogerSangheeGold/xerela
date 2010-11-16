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

package org.xerela.provider.credentials;

import org.apache.log4j.Logger;
import org.xerela.exception.PersistenceException;
import org.xerela.provider.devices.IDeviceStoreObserver;
import org.xerela.provider.devices.ZDeviceCore;

/**
 * Make changes to the stored credentials and protocols based on 
 * changes to the inventory.
 */
public class InventoryChangeListener implements IDeviceStoreObserver
{
    private static Logger LOGGER = Logger.getLogger(InventoryChangeListener.class);

    /**
     * {@inheritDoc}
     */
    public void deviceCreated(ZDeviceCore device)
    {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void deviceDeleted(ZDeviceCore device)
    {
        try
        {
            String deviceId = Integer.toString(device.getDeviceId());
            XerelaCredentialsManager.getInstance().clearDeviceToCredentialSetMapping(deviceId);
            XerelaProtocolManager.getInstance().clearDeviceToProtocolMapping(deviceId);
        }
        catch (PersistenceException e)
        {
            LOGGER.error(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deviceTypeChanged(ZDeviceCore device)
    {
        try
        {
            String deviceId = Integer.toString(device.getDeviceId());
            XerelaProtocolManager.getInstance().clearDeviceToProtocolMapping(deviceId);
        }
        catch (PersistenceException e)
        {
            LOGGER.error(e);
        }
    }
}
