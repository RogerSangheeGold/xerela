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

package org.xerela.provider.devices;

import org.xerela.net.adapters.AdapterMetadata;
import org.xerela.net.adapters.IAdapterService;
import org.xerela.net.snmp.TrapSender;
import org.xerela.provider.devices.internal.DeviceProviderActivator;

/**
 * SnmpTrapDeviceObserver
 */
public class SnmpTrapDeviceObserver implements IDeviceStoreObserver
{
    /** {@inheritDoc} */
    public void deviceTypeChanged(ZDeviceCore device)
    {
        // no-op
    }

    /** {@inheritDoc} */
    public void deviceCreated(ZDeviceCore device)
    {
        IAdapterService adapterService = DeviceProviderActivator.getAdapterService();
        AdapterMetadata adapterMetadata = adapterService.getAdapterMetadata(device.getAdapterId());

        TrapSender trapSender = DeviceProviderActivator.getTrapSender();
        String hostname = device.getHostname() == null ? "" : device.getHostname(); //$NON-NLS-1$
        trapSender.sendAddDeviceTrap(hostname, device.getIpAddress(), device.getManagedNetwork(), device.getAdapterId(), adapterMetadata.getShortName());
    }

    /** {@inheritDoc} */
    public void deviceDeleted(ZDeviceCore device)
    {
        IAdapterService adapterService = DeviceProviderActivator.getAdapterService();
        AdapterMetadata adapterMetadata = adapterService.getAdapterMetadata(device.getAdapterId());

        TrapSender trapSender = DeviceProviderActivator.getTrapSender();
        String hostname = device.getHostname() == null ? "" : device.getHostname(); //$NON-NLS-1$
        trapSender.sendDeleteDeviceTrap(hostname, device.getIpAddress(), device.getManagedNetwork(), device.getAdapterId(), adapterMetadata.getShortName());
    }
}
