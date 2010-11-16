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

package org.xerela.nio.common;

public class MutableAdapter<T>
{
    public T value;

    // -- constructors
    public MutableAdapter(T value)
    {
        this.value = value;
    }

    // -- public methods
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (this == obj)
        {
            return true;
        }
        try
        {
            MutableAdapter other = (MutableAdapter) obj;
            return null == value ? null == other.value : value.equals(other.value);
        }
        catch (ClassCastException cce)
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public String toString()
    {
        return value.toString();
    }

}
