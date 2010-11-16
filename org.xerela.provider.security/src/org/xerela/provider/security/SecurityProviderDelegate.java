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

package org.xerela.provider.security;

import java.util.List;

import javax.jws.WebService;

import org.xerela.provider.security.internal.SecurityProviderActivator;
import org.xerela.server.security.SecurityHandler;
import org.xerela.server.security.ZPrincipal;
import org.xerela.server.security.ZRole;

/**
 * SecurityProviderDelegate
 */
@WebService(endpointInterface = "org.xerela.provider.security.ISecurityProvider",
            serviceName = "SecurityService", portName = "SecurityPort")
public class SecurityProviderDelegate implements ISecurityProvider
{
    public void logoutCurrentUser()
    {
        getProvider().logoutCurrentUser();
    }

    /** {@inheritDoc} */
    public void changePassword(String username, String password)
    {
        getProvider().changePassword(username, password);
    }

    /** {@inheritDoc} */
    public void changeMyPassword(String password)
    {
        getProvider().changeMyPassword(password);
    }

    /** {@inheritDoc} */
    public void createRole(String role, List<String> permissions)
    {
        getProvider().createRole(role, permissions);
    }

    /** {@inheritDoc} */
    public void createUser(String username, String fullName, String email, String password, String role)
    {
        getProvider().createUser(username, fullName, email, password, role);
    }

    /** {@inheritDoc} */
    public void deleteRole(String role)
    {
        getProvider().deleteRole(role);
    }

    /** {@inheritDoc} */
    public void deleteUser(String username)
    {
        getProvider().deleteUser(username);
    }

    /** {@inheritDoc} */
    public List<String> getAvailablePermissions()
    {
        return getProvider().getAvailablePermissions();
    }

    /** {@inheritDoc} */
    public List<ZRole> getAvailableRoles()
    {
        return getProvider().getAvailableRoles();
    }

    /** {@inheritDoc} */
    public ZPrincipal getCurrentUser()
    {
        return getProvider().getCurrentUser();
    }

    /** {@inheritDoc} */
    public ZRole getRole(String role)
    {
        return getProvider().getRole(role);
    }

    /** {@inheritDoc} */
    public void updateRole(ZRole zrole)
    {
        getProvider().updateRole(zrole);
    }

    /** {@inheritDoc} */
    public void updateUser(String username, String fullName, String email, String role)
    {
        getProvider().updateUser(username, fullName, email, role);
    }

    /** {@inheritDoc} */
    public List<ZPrincipal> listUsers()
    {
        return getProvider().listUsers();
    }
    
    /** {@inheritDoc} */
    public ZPrincipal getUser(String username)
    {
        return getProvider().getUser(username);
    }

    /** {@inheritDoc} */
    public License getLicense()
    {
        return getProvider().getLicense();
    }

    /**
     * Get the underlying ISecurityProvider implementation.
     *
     * @return the ISecurityProvider implementation
     */
    private ISecurityProvider getProvider()
    {
        ISecurityProvider provider = SecurityProviderActivator.getSecurityProvider();
        if (provider == null)
        {
            throw new RuntimeException("SecurityProvider is unavailable."); //$NON-NLS-1$
        }

        return (ISecurityProvider) SecurityHandler.newProxy(provider);
    }
}
