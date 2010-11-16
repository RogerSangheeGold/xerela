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

package org.xerela.adaptertool.art;

import java.util.LinkedList;
import java.util.List;

/**
 * Central access to a pool of loopback addresses.
 */
public final class LoopbackAddresses
{
    private static final List<String> ADDRESSES;
    private static int octet;

    static
    {
        ADDRESSES = new LinkedList<String>();
    }

    private LoopbackAddresses()
    {
    }

    /**
     * Acquire an unused loopback address.
     * @return The address.
     */
    public static synchronized String acquire()
    {
        if (ADDRESSES.isEmpty())
        {
            octet++;
            return "127.0.0." + octet; //$NON-NLS-1$
        }
        return ADDRESSES.remove(0);
    }

    /**
     * Releases the loopback address so that it can be acquired by others.
     * @param address The address to release.
     */
    public static synchronized void release(String address)
    {
        ADDRESSES.add(address);
    }
}
