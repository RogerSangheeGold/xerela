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
 *     $Date: 2008/07/09 19:27:39 $
 * $Revision: 1.2 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/test/org/xerela/discovery/DiscoveryHostTest.java,v $e
 */

package org.xerela.discovery;

import junit.framework.TestCase;

import org.xerela.addressing.IPAddress;
import org.xerela.exception.ValueFormatFault;

public class DiscoveryHostTest extends TestCase
{
    public DiscoveryHost host;

    protected void setUp()
    {
        host = new DiscoveryHost(new IPAddress("10.100.4.8"));
    }

    public void testSetupByIP()
    {
        assertNotNull(host);
    }

    public void testIPAddress() throws ValueFormatFault
    {
        host.setIpAddress(new IPAddress("10.100.4.8"));
        assertEquals("10.100.4.8", host.getIpAddress().getIPAddress());
    }

    /**
     * There is an algorithm to determine the best administrative IP for a
     * device. Normally this will be used unless the boolean for
     * 'calculateAdminIp' is set to false.
     * 
     */
    public void testCalculateAdminIp()
    {
        assertTrue(host.isCalculateAdminIp());
        host.setCalculateAdminIp(false);
        assertFalse(host.isCalculateAdminIp());
    }

    /**
     * Test out a default value
     * 
     */
    public void testDefaults()
    {
        assertFalse(host.isBypassCache());
        host.setBypassCache(true);
        assertTrue(host.isBypassCache());
    }

}