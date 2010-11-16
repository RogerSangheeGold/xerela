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

package org.xerela.server.security;

import java.util.List;

import org.xerela.zap.security.ISecurityService;

/**
 * ISecurityServiceEx
 */
public interface ISecurityServiceEx extends ISecurityService
{
    /**
     * Creates a one time use authentication token that can be used in lieu of a username and password.
     * The token will become invalid after a reasonably short period. (like 30 minutes)
     * @param user The user to create the authentication token for or <code>null</code> to use the current user.
     *             This must be <code>null</code> if there is a current user session on the thread as a user can
     *             not get a token for another user.  Only the system may get arbitrary users' tokens.
     * @return The token in the form of "&lt;user&gt;@&lt;temp-password&gt;"
     */
    String createAuthenticationToken(String user);

    /**
     * Validates whether the specified one time use authentication token is still valid.
     *
     * @param token the one time use token to validate
     * @return true if valid, false otherwise
     */
    boolean validateAuthenticationToken(String token);

    /**
     * Get/create the ZPrincipal object for the supplied username.  Null is
     * returned if the user does not exist.
     *
     * @param username the name of the user
     * @return the ZPrincipal object for the user
     */
    ZPrincipal getZPrincipal(String username);

    /**
     * Get all permissions defined for the system.
     *
     * @return a list of permissions defined for the system
     */
    List<String> getAvailablePermissions();
}
