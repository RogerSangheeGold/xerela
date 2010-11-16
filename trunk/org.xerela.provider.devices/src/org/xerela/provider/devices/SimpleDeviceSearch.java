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

import org.apache.log4j.Logger;

/**
 * SimpleDeviceSearch
 */
public class SimpleDeviceSearch implements ISimpleDeviceSearch
{
    private static final Logger LOGGER = Logger.getLogger(SimpleDeviceSearch.class);

    /** {@inheritDoc} */
    public PageData search(String scheme, String query, PageData pageData, String sortColumn, boolean descending)
    {
        IDeviceResolutionScheme resolver = DeviceResolutionElf.getResolutionScheme(scheme);

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(String.format("Device search with scheme '%s' and query:\n%s", scheme, query)); //$NON-NLS-1$
        }

        return resolver.resolve(scheme, query, pageData, sortColumn, descending);
    }
}
