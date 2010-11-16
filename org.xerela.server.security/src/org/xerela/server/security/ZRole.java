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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

/**
 * ZRole
 */
@Entity(name = "ZRole")
@Table(name = "roles")
public class ZRole implements Serializable
{
    private static final long serialVersionUID = -1344123399052677071L;

    @Id
    @Column(name = "role")
    private String name;

    @Column(name = "permissions")
    private String permString;

    @Transient
    private Set<String> permissions;

    /**
     * Default constructor.
     */
    public ZRole()
    {
        // default constructor
    }

    /**
     * Constructor.
     *
     * @param name the role name
     */
    public ZRole(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the role.
     *
     * @return the role name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the role name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return a the set of permissions for this role
     */
    @XmlTransient
    public Set<String> getPermissionSet()
    {
        Set<String> perms = new HashSet<String>();
        if (permissions == null)
        {
            for (String perm : permString.split(",")) //$NON-NLS-1$
            {
                perms.add(perm);
            }

            permissions = perms;
        }
        return permissions;
    }

    /**
     * Set the permissions for this role.
     *
     * @param permList a list of permission strings
     */
    public void setPermissionSet(Set<String> permList)
    {
        StringBuilder sb = new StringBuilder();
        for (String perm : permList)
        {
            sb.append(perm).append(',');
        }
        sb.setLength(sb.length() - 1); // trim the last ^

        permString = sb.toString();
        this.permissions = permList;
    }

    /**
     * Get the string of permissions.
     *
     * @return the aggregate permissions string
     */
    public List<String> getPermissions()
    {
        List<String> perms = new ArrayList<String>();
        perms.addAll(getPermissionSet());

        return perms;
    }

    /**
     * Set the aggregate permissions.  This method is a no-op and should not
     * be called.
     *
     * @param permissions the list of permissions.
     */
    public void setPermissions(List<String> permissions)
    {
        // no-op.  permissions cannot be set by this method.
    }

    /**
     * Check whether this role contains the specified permission.
     *
     * @param permission the permission to add
     * @return true if the role contains the permission, false otherwise
     */
    public boolean hasPermission(String permission)
    {
        return getPermissionSet().contains(permission);
    }
}
