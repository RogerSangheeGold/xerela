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
 * A server security manager interface.  An implementation of this interface can
 * deny requests on the basis of remote socket address, filename and mode.  This
 * only applies to TFTP servers because clients do not receive request packets. 
 *  
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public interface SecurityManager
{
    public boolean denyRead(SocketAddress remote, String filename, String mode);

    public boolean denyWrite(SocketAddress remote, String filename, String mode);
}
