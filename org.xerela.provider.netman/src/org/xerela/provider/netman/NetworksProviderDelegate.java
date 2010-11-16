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

package org.xerela.provider.netman;

import java.util.List;

import javax.jws.WebService;

import org.xerela.provider.netman.internal.NetworksActivator;
import org.xerela.server.security.SecurityHandler;

/**
 * NetworksProviderDelegate
 */
@WebService(endpointInterface = "org.xerela.provider.netman.INetworksProvider",
            serviceName = "NetworksService", portName = "NetworksPort")
public class NetworksProviderDelegate implements INetworksProvider
{
    /** {@inheritDoc} */
    public void defineManagedNetwork(String name)
    {
        getProvider().defineManagedNetwork(name);
    }

    /** {@inheritDoc} */
    public void deleteManagedNetwork(String name)
    {
        getProvider().deleteManagedNetwork(name);
    }

    /** {@inheritDoc} */
    public ManagedNetwork getDefaultManagedNetwork()
    {
        return getProvider().getDefaultManagedNetwork();
    }

    /** {@inheritDoc} */
    public ManagedNetwork getManagedNetwork(String name)
    {
        return getProvider().getManagedNetwork(name);
    }

    /** {@inheritDoc} */
    public List<String> getManagedNetworkNames()
    {
        return getProvider().getManagedNetworkNames();
    }

    /** {@inheritDoc} */
    public void setDefaultManagedNetwork(String name)
    {
        getProvider().setDefaultManagedNetwork(name);
    }

    /** {@inheritDoc} */
    public void updateManagedNetwork(ManagedNetwork managedNetwork)
    {
        getProvider().updateManagedNetwork(managedNetwork);
    }

    private INetworksProvider getProvider()
    {
        INetworksProvider provider = NetworksActivator.getNetworksProvider();
        if (provider == null)
        {
            throw new RuntimeException(Messages.serviceUnavailable);
        }

        return (INetworksProvider) SecurityHandler.newProxy(provider);
    }
}
