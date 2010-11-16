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

package org.xerela.zap.security;

import java.util.List;

import javax.servlet.http.HttpSession;


/**
 * ISecurityService
 */
public interface ISecurityService
{
    /**
     * Associate the user and HttpSession with the current thread.
     *
     * @param session the principal's HttpSession
     * @return the user session
     */
    IUserSession associateSession(HttpSession session);

    /**
     * Disassociate the user from the current thread.
     */
    void disassociateSession();

    /**
     * Get the thread-local session for the user executing on the current
     * thread.
     *
     * @return the IUserSession object
     */
    IUserSession getUserSession();

    /**
     * Register a session listener.
     *
     * @param listener the session listener
     */
    void registerUserSessionListener(IUserSessionListener listener);

    /**
     * Unregister a session listener.
     *
     * @param listener the session listener
     */
    void unregisterUserSessionListener(IUserSessionListener listener);

    /**
     * @return the list of global permission names
     */
    List<String> getAvailablePermissions();
}
