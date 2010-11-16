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

import java.util.List;

import org.apache.log4j.Logger;
import org.xerela.net.snmp.TrapSender;
import org.xerela.provider.configstore.internal.ConfigStoreActivator;
import org.xerela.provider.devices.ZDeviceCore;

/**
 * SnmpTrapRevisionObserver
 */
public class SnmpTrapRevisionObserver implements IRevisionObserver
{
    private static final Logger LOGGER = Logger.getLogger(SnmpTrapRevisionObserver.class);

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public void revisionChange(ZDeviceCore device, List<ConfigHolder> configs)
    {
        TrapSender trapSender = ConfigStoreActivator.getTrapSender();
        for (ConfigHolder holder : configs)
        {
            // Don't raise an event for the ZED ... this is an internal artifact of configuration
            // change and is typically not meaningful to externally integrated systems.
            if (holder.getFullName().contains(ConfigBackupPersister.XERELA_ELEMENT_DOCUMENT))
            {
                continue;
            }

            if (holder.getType().equals("A") || holder.getType().equals("M"))
            {
                try
                {
                    String hostname = (device.getHostname() == null ? "" : device.getHostname());
                    trapSender.sendConfigChangeTrap(hostname, device.getIpAddress(), device.getManagedNetwork(), holder.getFullName());
                }
                catch (Throwable t)
                {
                    LOGGER.warn("Unable to raise change trap.", t); //$NON-NLS-1$
                }
            }
        }
    }
}
