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

import org.xerela.nio.common.Bool;
import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;
import org.xerela.nio.nioagent.datagram.tftp.EventListener.RequestType;
import org.xerela.nio.nioagent.datagram.tftp.EventListener.TftpMode;


/**
 * Responds to TFTP write requests (WRQ).  This class contains no state, so it
 * can be shared across all clients.  The main purpose of this class is to
 * create a DataConsumer for each client that will do something useful with the
 * bytes of data sent from the client to the server.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 */
public class WrqResponder implements PacketConstants
{

    // -- member fields
    private final DataConsumer.Factory factory;
    private final SecurityManager manager;
    private final EventListener listener;
    private final ILogger logger;

    // -- constructors
    public WrqResponder(final DataConsumer.Factory factory, final SecurityManager manager, final EventListener listener, final ILogger logger)
    {
        this.factory = factory;
        this.manager = manager;
        this.listener = listener;
        this.logger = logger;
    }

    // -- public methods
    public BinaryCodec respondToWrq(InetSocketAddress local, InetSocketAddress remote, String filename, String mode, Bool terminate)
    {
        terminate.value = manager.denyWrite(remote, filename, mode);
        final BinaryCodec dataCodec;
        if (!terminate.value)
        {
            dataCodec = DataCodecImpl.create(responder(filename), logger);
            listener.transferStarted(local, remote, RequestType.write, filename, TftpMode.valueOf(mode.toLowerCase()));
        }
        else
        {
            dataCodec = null;
        }
        return dataCodec;
    }

    // -- private methods
    private DataResponder responder(String filename)
    {
        return DataResponderImpl.create(factory.createConsumer(filename, logger), listener);
    }

}
