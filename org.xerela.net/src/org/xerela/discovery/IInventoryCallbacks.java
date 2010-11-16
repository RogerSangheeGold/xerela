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
 *     $Date: 2008/08/27 14:26:29 $
 * $Revision: 1.5 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/discovery/IInventoryCallbacks.java,v $e
 */

package org.xerela.discovery;

import java.net.UnknownHostException;

import org.xerela.addressing.IPAddress;

/**
 * Allows for the customization of the credentials used by the
 * {@link DiscoveryEngine}. <br>
 * <br>
 * The credential names used are:<br>
 * <li>username - the username to login to a device (ssh or telnet)
 * <li>password - the password to login to a device (ssh or telnet)
 * <li>getCommunity - the SNMP community string used for SNMP gets or walks
 * 
 * @author rkruse
 */
public interface IInventoryCallbacks
{
    /**
     * The implementor of this method should return the preferred
     * {@link IPAddress} of the given IP. This will cause the
     * <code>DiscoverDevice</code> process to not use its own algorithm to
     * determine the administrative IP of the device just learned about. <br>
     * <br>
     * 
     * @param ipAddress an IP address
     * @return never should return null. Throw the exception if it isn't in the
     *         inventory
     * @throws UnknownHostException - if the host isn't known to the inventory
     *             source
     */
    IPAddress getPreferredIpAddress(IPAddress ipAddress) throws UnknownHostException;

    /**
     * Tells the DiscoveryEngine how it should get a {@link DiscoveryEvent} from a host.
     * An implementation of this method should communicate with the device via any desired means
     * to create the necessary {@link DiscoveryEvent}.
     * 
     * @param discoveryHost the host to target
     * @param runTelemetry if set to true, the implementation should run the extended telemetry operation.  If false,
     *  the implementation is only responsible for retrieving top level system information.
     * @return the filled out DiscoveryEvent
     */
    public DiscoveryEvent discoveryMethod(DiscoveryHost discoveryHost, boolean runTelemetry);
}
