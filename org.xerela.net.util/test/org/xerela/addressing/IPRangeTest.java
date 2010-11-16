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

package org.xerela.addressing;

import junit.framework.TestCase;

public class IPRangeTest extends TestCase
{

    public void testIPv4Range()
    {
        IPRange range = new IPRange("1.2.3.4", "1.2.3.10");
        assertTrue(range.contains(new IPAddress("1.2.3.6")));
    }

    public void testIPv6Range()
    {
        IPRange range = new IPRange("aaa::1", "aaa::5");
        assertTrue(range.contains(new IPAddress("aaa::4")));
    }

    public void testUnorderedIpV6Range()
    {
        try
        {
            new IPRange("99ff:d::", "99ff:c::");
            fail("This should have thrown an exception");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Noticed the bad range...yay.");
        }
    }

    /**
     * Give one IPv4 and one IPv6 address.   Make sure it blows up.
     */
    public void testIpV4andV6Mix()
    {
        try
        {
            new IPRange("10.100.20.22", "aaa::5");
            fail("This should have thrown an exception");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Noticed the bad range...yay.");
        }
    }

    public void testWalkIPv6Range()
    {
        IPRange range = new IPRange("::1", "::5");
        int counter = 0;
        for (IPAddress ipAddress : range)
        {
            System.out.println(ipAddress);
            counter++;
        }
        assertEquals(counter, 5);
    }
    
    public void testContains()
    {
        IPRange range = new IPRange("9FFE:FFFF:0:C002::", "9FFE:FFFF:0:C002::3");
        assertTrue(range.contains(new IPAddress("9FFE:FFFF:0:C002::"))); 
    }

}
