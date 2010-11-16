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

import java.util.List;

import javax.jws.WebService;

import org.xerela.addressing.NetworkAddressElf;
import org.xerela.provider.devices.internal.DeviceProviderActivator;
import org.xerela.server.security.SecurityHandler;

/**
 * DeviceProviderDelegate
 */
@WebService(endpointInterface = "org.xerela.provider.devices.IDeviceProvider", //$NON-NLS-1$
serviceName = "DevicesService", portName = "DevicesPort")
public class DeviceProviderDelegate implements IDeviceProvider
{
    /**
     * Default constructor.
     */
    public DeviceProviderDelegate()
    {
        // nothing
    }

    /** {@inheritDoc} */
    public void createDevice(String ipAddress, String managedNetwork, String adapterId)
    {
        if (NetworkAddressElf.isValidAddress(ipAddress))
        {
            getProvider().createDevice(ipAddress, managedNetwork, adapterId);
        }
    }

    /** {@inheritDoc} */
    public List<ZDeviceCore> createDeviceBatched(List<ZDeviceCore> devices)
    {
        return getProvider().createDeviceBatched(devices);
    }

    /** {@inheritDoc} */
    public void deleteDevice(String ipAddress, String managedNetwork)
    {
        if (NetworkAddressElf.isValidAddress(ipAddress))
        {
            getProvider().deleteDevice(ipAddress, managedNetwork);
        }
    }

    /** {@inheritDoc} */
    public List<String> getAllHardwareVendors()
    {
        return getProvider().getAllHardwareVendors();
    }

    /** {@inheritDoc} */
    public ZDeviceCore getDevice(String ipAddress, String managedNetwork)
    {
        if (NetworkAddressElf.isValidAddress(ipAddress))
        {
            return getProvider().getDevice(ipAddress, managedNetwork);
        }
        return null;
    }

    /** {@inheritDoc} */
    public ZDeviceCore getDeviceByInterfaceIp(String ipAddress, String managedNetwork)
    {
        if (NetworkAddressElf.isValidAddress(ipAddress))
        {
            return getProvider().getDeviceByInterfaceIp(ipAddress, managedNetwork);
        }
        return null;
    }

    /** {@inheritDoc} */
    public ZDeviceStatus getDeviceStatus(String ipAddress, String managedNetwork)
    {
        if (NetworkAddressElf.isValidAddress(ipAddress))
        {
            return getProvider().getDeviceStatus(ipAddress, managedNetwork);
        }

        return null;
    }

    /** {@inheritDoc} */
    public List<ZDeviceLite> getDeviceLites(String[] devices)
    {
        return getProvider().getDeviceLites(devices);
    }

    /** {@inheritDoc} */
    public void updateDevice(String ipAddress, String managedNetwork, ZDeviceCore device)
    {
        if (NetworkAddressElf.isValidAddress(ipAddress))
        {
            getProvider().updateDevice(ipAddress, managedNetwork, device);
        }
    }

    /**
     * This is an accessor to get the 'true' scheduler as a service.  If the bundle
     * has been restarted, this may return a different Scheduler than previous
     * invocations.  But they should be backed by the same job store, so it would
     * be transparent to the client.
     * 
     * @return the Scheduler to which to delegate
     */
    private IDeviceProvider getProvider()
    {
        IDeviceProvider provider = DeviceProviderActivator.getDeviceProvider();
        if (provider == null)
        {
            throw new RuntimeException(Messages.DeviceProviderDelegate_deviceProviderUnavailable);
        }

        return (IDeviceProvider) SecurityHandler.newProxy(provider);
    }
}
