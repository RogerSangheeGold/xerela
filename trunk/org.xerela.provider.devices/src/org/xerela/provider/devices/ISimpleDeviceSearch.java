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

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * ISimpleDeviceSearch
 */
@WebService(name = "DeviceSearch", targetNamespace = "http://www.xerela.org/server/devicesearch")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ISimpleDeviceSearch
{
    /**
     * @param scheme The device resolution scheme.
     * @param query The scheme specific query.
     * @param pageData The page to retrieve.
     * @param sortColumn The column to sort by or <code>null</code>.
     * @param descending <code>true</code> for descending sort or <code>false</code> for ascending.
     * @return The requested page.
     */
    PageData search(@WebParam(name = "scheme") String scheme,
                    @WebParam(name = "query") String query,
                    @WebParam(name = "pageData") PageData pageData,
                    @WebParam(name = "sortColumn") String sortColumn,
                    @WebParam(name = "descending") boolean descending);
}
