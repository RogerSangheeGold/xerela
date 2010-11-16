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

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;

/**
 * IUserSession
 */
public interface IUserSession extends Map<String, Serializable>
{
    /**
     * Get the Principal associated with this session.
     *
     * @return the Principal associated with this session
     */
    Principal getPrincipal();

    /**
     * Set the Principal for this session.
     *
     * @param principal the principal
     */
    void setPrincipal(Principal principal);

    /**
     * Get the locale that the user is running in (as of the last
     * server request).
     *
     * @return the Locale for the user who owns this session
     */
    Locale getLocale();

    /**
     * @param permissionName the name of a system permission, i.e., one returned by
     * PermissionTracker.getAvailablePermissions()
     * 
     * @return true if the user involved with this session has the indicated permission
     */
    boolean checkHasPermission(String permissionName);

    /**
     * Invalidate this user's session.
     */
    void invalidate();
}
