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

/**
 * IUserSessionListener
 */
public interface IUserSessionListener
{
    /**
     * Called when a user's session is created.
     *
     * @param session the IUserSession
     */
    void sessionCreated(IUserSession session);

    /**
     * Called when a user's session is destroyed through some action such
     * as logout or session expiration.
     *
     * @param session the IUserSession that is being destroyed
     */
    void sessionDestroyed(IUserSession session);
}
