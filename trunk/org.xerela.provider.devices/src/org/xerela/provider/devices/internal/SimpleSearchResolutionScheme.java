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

package org.xerela.provider.devices.internal;

import org.xerela.provider.devices.DeviceResolutionElf;
import org.xerela.provider.devices.IDeviceResolutionScheme;
import org.xerela.provider.devices.PageData;

/**
 * SimpleSearchResolutionScheme
 *
 * This resolution scheme utilizes the SimpleSearch service to resolve
 * devices.  The input data is a comma separated list consisting of
 * the following format:
 * 
 *    method,arg1,arg2,arg3
 * 
 * Where 'method' is the literal name of a method from the ISimpleDeviceSearch
 * interface, and the arguments are the first N parameters of that method up to
 * but not including the PageData parameter.  For example:
 * 
 *   searchByMakeModel,Cisco,262*
 *
 * (note that wildcards and any standard search parameters are supported).
 */
public class SimpleSearchResolutionScheme implements IDeviceResolutionScheme
{
    /** {@inheritDoc} */
    public PageData resolve(String scheme, String data, PageData page, String sortColumn, boolean descending)
    {
        String[] split = data.split(",", 2); //$NON-NLS-1$

        String newData = split.length > 1 ? split[1] : ""; //$NON-NLS-1$
        String newScheme;
        String method = split[0];
        if ("searchByAddress".equals(method)) //$NON-NLS-1$
        {
            newScheme = "ipAddress"; //$NON-NLS-1$
        }
        else if ("searchByHostname".equals(method)) //$NON-NLS-1$
        {
            newScheme = "hostname"; //$NON-NLS-1$
        }
        else if ("searchByMakeModel".equals(method)) //$NON-NLS-1$
        {
            newScheme = "makeModel"; //$NON-NLS-1$
        }
        else if ("searchByOsVersion".equals(method)) //$NON-NLS-1$
        {
            newScheme = "osVersion"; //$NON-NLS-1$
        }
        else if ("searchByTag".equals(method)) //$NON-NLS-1$
        {
            newScheme = "tag"; //$NON-NLS-1$
        }
        else
        {
            throw new IllegalArgumentException("Unknown resolution type: " + method); //$NON-NLS-1$
        }

        return DeviceResolutionElf.getResolutionScheme(newScheme).resolve(newScheme, newData, page, sortColumn, descending);
    }
}
