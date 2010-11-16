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

import java.net.InetSocketAddress;

/**
 * A TFTP file transfer even listener.  Classes that implement this interface
 * allow a user class to receive callbacks when predefined events occur.  The
 * user thread can correlate multiple events by matching up the local and remote
 * socket addresses.  The event listener can be used to calculate statistics of 
 * a transfer (e.g. throughput) or all transfers (concurrent sessions and
 * aggregate throughput).  It is important to note that these callbacks are
 * processed on the NIO selector thread, so implementations of this interface
 * must either return quickly from each method call, or place the necessary
 * information on a concurrent queue for long-running processing by another
 * thread.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public interface EventListener
{

    public void transferStarted(InetSocketAddress local, InetSocketAddress remote, RequestType requestType, String filename, TftpMode mode);

    public void transferComplete(InetSocketAddress local, InetSocketAddress remote, int filesize);

    public void transferFailed(InetSocketAddress local, InetSocketAddress remote, String message);

    public static enum RequestType
    {
        read,
        write,
    }

    public static enum TftpMode
    {
        octet,
        netascii,
    }

}
