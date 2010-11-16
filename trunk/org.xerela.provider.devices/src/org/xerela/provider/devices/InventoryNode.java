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

/**
 * An InventoryNode represents the shallow information for a device or folder in the inventory
 * tree.
 * 
 * Example values of the fields
 *   parent: devices/cisco
 *   label: 10.100.4.8 - cisco2610-LAB.eclyptic.com
 *   isFolder: false
 * 
 */
public class InventoryNode
{
    private final String parent;
    private final String label;
    private final boolean isFolder;

    /**
     * @param parent the parent
     * @param label the label
     * @param isFolder true if this is a folder
     */
    public InventoryNode(final String parent, final String label, final boolean isFolder)
    {
        this.parent = parent;
        this.label = label;
        this.isFolder = isFolder;
    }

    /**
     * @return the isFolder
     */
    public boolean isFolder()
    {
        return isFolder;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @return the parent
     */
    public String getParent()
    {
        return parent;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return String.format("{parent:%s,label:%s,isFolder:%s}", parent, label, isFolder); //$NON-NLS-1$
    }

}
