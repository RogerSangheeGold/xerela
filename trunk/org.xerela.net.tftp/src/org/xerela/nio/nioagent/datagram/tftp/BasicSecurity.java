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

package org.xerela.nio.nioagent.datagram.tftp;

import java.net.SocketAddress;

/**
 * Implementation of security manager that denies packets if the mode field does
 * not equal octet.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class BasicSecurity implements SecurityManager
{

    // -- public methods
    public boolean denyRead(SocketAddress remote, String filename, String mode)
    {
        return denyMode(mode);
    }

    public boolean denyWrite(SocketAddress remote, String filename, String mode)
    {
        return denyMode(mode);
    }

    // -- private methods
    private boolean denyMode(String mode)
    {
        return !("octet".equalsIgnoreCase(mode) || "netascii".equalsIgnoreCase(mode));
    }
}
