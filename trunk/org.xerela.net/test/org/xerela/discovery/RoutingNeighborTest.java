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
 * All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2006
 *
 *   $Author: rkruse $
 *     $Date: 2008/08/04 15:36:00 $
 * $Revision: 1.2 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/test/org/xerela/discovery/RoutingNeighborTest.java,v $e
 */

package org.xerela.discovery;

import junit.framework.TestCase;

import org.xerela.addressing.IPAddress;
import org.xerela.discovery.RoutingNeighbor.RoutingProtocol;

/**
 * @author rkruse
 */
public class RoutingNeighborTest extends TestCase
{
    public void testConstructor()
    {
        RoutingNeighbor rn = new RoutingNeighbor(new IPAddress("10.100.4.8"), RoutingProtocol.EIGRP);
        assertEquals("EIGRP", rn.getRoutingProtocol().name());
        assertEquals("10.100.4.8", rn.getIpAddress().getIPAddress());
    }
    
    public void testIfName()
    {
        RoutingNeighbor rn = new RoutingNeighbor(new IPAddress("10.100.4.8"));
        assertEquals("", rn.getIfName());
        rn.setIfName("GigabitEthernet0/1");
        assertEquals("GigabitEthernet0/1", rn.getIfName());
    }
    
    public void testToStringAndHashcode()
    {
        RoutingNeighbor rn1 = new RoutingNeighbor(new IPAddress("10.100.4.8"));
        RoutingNeighbor rn2 = new RoutingNeighbor(new IPAddress("10.100.4.8"));
        RoutingNeighbor rn3 = new RoutingNeighbor(new IPAddress("99.99.99.99"));
        
        assertEquals(rn1, rn2);
        assertFalse(rn1.equals(rn3));
        
        rn2.setIfName("something");
        assertFalse(rn1.equals(rn2));
        assertFalse(rn1.hashCode() == rn2.hashCode());
        rn1.setIfName("something");
        assertEquals(rn1, rn2);
        assertEquals(rn1.hashCode(), rn2.hashCode());
        
        rn1.setRoutingProtocol(RoutingProtocol.ISIS);
        assertFalse(rn1.equals(rn2));
        assertFalse(rn1.hashCode() == rn2.hashCode());
    }
    
    /**
     * make sure there are no exception
     */
    public void testToString()
    {
        RoutingNeighbor rn1 = new RoutingNeighbor(new IPAddress("10.100.4.8"));
        rn1.setIfName("Serial0");
        System.out.println(rn1.toString());
    }
}
