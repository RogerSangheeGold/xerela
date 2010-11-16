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

package org.xerela.zap.web;

import javax.servlet.http.HttpSessionListener;

/**
 * IWebService
 */
public interface IWebService
{
    String PRIMARY_CONNECTOR = "Primary"; //$NON-NLS-1$

    /**
     * Get the access scheme of the named connector.
     *
     * @param connectorName the named connector
     * @return the access scheme (HTTP/HTTPS)
     */
    String getScheme(String connectorName);

    /**
     * Get the bound host name of the named connector.
     *
     * @param connectorName the named connector
     * @return the host name
     */
    String getHost(String connectorName);

    /**
     * Get the listen port of the named connector.
     *
     * @param connectorName the named connector
     * @return the listen port
     */
    int getPort(String connectorName);

    /**
     * Register an HttpSessionListener with the web container.
     *
     * @param listener the listener
     */
    void registerSessionListener(HttpSessionListener listener);

    /**
     * Unregister an HttpSessionListener from the web container.
     *
     * @param listener the listener
     */
    void unregisterSessionListener(HttpSessionListener listener);
}
