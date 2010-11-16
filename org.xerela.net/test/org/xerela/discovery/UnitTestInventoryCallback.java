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
 *   $Source: /usr/local/cvsroot/org.xerela.net/test/org/xerela/discovery/UnitTestInventoryCallback.java,v $e
 */

package org.xerela.discovery;

import java.net.UnknownHostException;

import org.xerela.addressing.IPAddress;

/**
 * returns a simple <code>CredentialSet</code> for every
 * <code>IPAddress</code>.
 * 
 * @author rkruse
 */
public class UnitTestInventoryCallback implements IInventoryCallbacks
{

    /**
     * 
     *
     */
    public UnitTestInventoryCallback()
    {
    }

    /**
     * {@inheritDoc}
     */
    public IPAddress getPreferredIpAddress(IPAddress ipAddress) throws UnknownHostException
    {
        throw new UnknownHostException("UNIT TEST CALLBACK....NO INVENTORY SOURCE");
    }

    /**
     * {@inheritDoc}
     */
    public DiscoveryEvent discoveryMethod(DiscoveryHost discoveryHost, boolean runTelemetry)
    {
        return new DiscoveryEvent(discoveryHost.getIpAddress());
    }

}
