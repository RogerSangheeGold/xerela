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

/* Alterpoint, Inc.
 *
 * The contents of this source code are proprietary and confidential
 * All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2008
 */

package org.xerela.zap.security;

import java.util.List;

import org.xerela.zap.security.internal.SecurityActivator;

/**
 * Track the SecurityPermission extension point for all currently registered permissions.
 */
public final class PermissionTracker
{
    private PermissionTracker()
    {
    }

    /**
     * @return the list of permissions as registered through the SecurityPermission extension point
     */
    public static List<String> getAvailablePermissions()
    {
        return SecurityActivator.getGlobalPermissions();
    }
}

