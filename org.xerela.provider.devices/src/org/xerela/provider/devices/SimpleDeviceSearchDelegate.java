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

import javax.jws.WebService;

import org.xerela.provider.devices.internal.DeviceProviderActivator;

/**
 * SimpleDeviceSearchDelegate
 */
@WebService(endpointInterface = "org.xerela.provider.devices.ISimpleDeviceSearch", //$NON-NLS-1$
serviceName = "DeviceSearchService", portName = "DeviceSearchPort")
public class SimpleDeviceSearchDelegate implements ISimpleDeviceSearch
{
    private static final PageData EMPTY_PAGEDATA;

    static
    {
        EMPTY_PAGEDATA = new PageData();
    }

    /** {@inheritDoc} */
    public PageData search(String scheme, String query, PageData pageData, String sortColumn, boolean descending)
    {
        if (scheme == null || query == null || pageData == null)
        {
            return EMPTY_PAGEDATA;
        }
        return getProvider().search(scheme, query, pageData, sortColumn, descending);
    }

    /**
     * This is an accessor to get the 'true' scheduler as a service.  If the bundle
     * has been restarted, this may return a different Scheduler than previous
     * invocations.  But they should be backed by the same job store, so it would
     * be transparent to the client.
     * 
     * @return the Scheduler to which to delegate
     */
    private ISimpleDeviceSearch getProvider()
    {
        ISimpleDeviceSearch provider = DeviceProviderActivator.getSearch();
        if (provider == null)
        {
            throw new RuntimeException(Messages.SimpleDeviceSearchDelegate_searchProviderUnavailable);
        }

        return provider;
    }
}
