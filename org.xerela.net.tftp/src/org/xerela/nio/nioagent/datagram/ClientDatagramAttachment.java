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

package org.xerela.nio.nioagent.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.ChannelWriter;
import org.xerela.nio.nioagent.SharedBuffer;
import org.xerela.nio.nioagent.WrapperException;
import org.xerela.nio.nioagent.Interfaces.KeyAttachment;

/**
 * An implementation of the KeyAttachment interface intended for the client
 * portion of datagram protocols that use random high ports for their
 * connections after the initial request is sent to the server on a well-known
 * port.  Instances of this class contain a DatagramAttachment which is used 
 * after the initial request is sent to the server.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class ClientDatagramAttachment implements KeyAttachment
{

    // -- static fields
    private final ILogger logger;
    private final SharedBuffer out;
    private final SharedBuffer in;
    private final byte[] outArr;
    private final int outLen;
    private final byte[] inArr;
    private final SocketAddress serverAddr;
    private final SharedBuffer.User sendUser;
    private final SharedBuffer.User receiveUser;
    private final ChannelWriter writer;
    private final DatagramAttachment att;
    private boolean isInitialConnection;
    private SelectionKey key;

    // -- constructors
    public ClientDatagramAttachment(final byte[] initOutArr, final int initOutLen, final SocketAddress serverAddress, final ChannelWriter writer,
            final ILogger logger, final Integer bufferSize)
    {
        this.logger = logger;
        this.out = SharedBuffer.getOutboundBuffer(logger, bufferSize);
        this.in = SharedBuffer.getInboundBuffer(logger, bufferSize);
        this.outArr = initOutArr;
        this.outLen = initOutLen;
        this.inArr = SharedBuffer.getInboundBuffer(logger, bufferSize).createByteArray();
        this.serverAddr = serverAddress;
        this.sendUser = new SendUser();
        this.receiveUser = new ReceiveUser();
        this.writer = writer;
        this.att = new DatagramAttachment(writer, logger, bufferSize);
        this.isInitialConnection = true;
        this.key = null;
    }

    // -- public methods
    public void control(SelectionKey sKey)
    {
        key = sKey;

        if (isInitialConnection)
        {

            if (key.isValid() && key.isWritable())
            {
                out.use(sendUser);
            }

            if (key.isValid() && key.isReadable())
            {
                in.use(receiveUser);
                isInitialConnection = false;
            }

        }
        else
        {
            att.control(key);
        }

    }

    // -- Inner classes
    private class SendUser implements SharedBuffer.User
    {
        public void use(ByteBuffer buf)
        {
            buf.put(outArr, 0, outLen);
            buf.flip();
            sendChannel((DatagramChannel) key.channel(), buf);
            if (buf.hasRemaining())
            {
                String msg = "Partial channel send.";
                logger.error(msg);
                key.cancel();
                throw new RuntimeException(msg);
            }
            else
            {
                key.interestOps(SelectionKey.OP_READ);
            }
        }

        private void sendChannel(DatagramChannel chan, ByteBuffer buf)
        {
            try
            {
                chan.send(buf, serverAddr);
            }
            catch (IOException e)
            {
                logger.error("Failed to send to datagram channel.");
                key.cancel();
                throw new WrapperException(e);
            }
        }
    }

    private class ReceiveUser implements SharedBuffer.User
    {
        public void use(ByteBuffer buf)
        {
            DatagramChannel chan = ((DatagramChannel) key.channel());
            InetSocketAddress serverAddr = (InetSocketAddress) receive(chan, buf);
            if (0 == buf.position() || null == serverAddr)
            {
                throw new RuntimeException("Failed to receive initial response packet from server.");
            }
            logger.debug("Client received datagram from server at " + serverAddr + ".");
            connect(chan, serverAddr);
            buf.flip();
            buf.get(inArr, 0, buf.limit());
            writer.direct(key, local(chan), serverAddr, inArr, buf.limit());
        }

        private SocketAddress receive(DatagramChannel chan, ByteBuffer buf)
        {
            try
            {
                return chan.receive(buf);
            }
            catch (IOException e)
            {
                logger.error("Failed to receive from datagram channel.");
                key.cancel();
                throw new WrapperException(e);
            }
        }

        private void connect(DatagramChannel chan, SocketAddress serverDataAddr)
        {
            try
            {
                chan.connect(serverDataAddr);
            }
            catch (IOException e)
            {
                logger.error("Failed to connect datagram channel to server data address " + serverDataAddr);
                key.cancel();
                throw new WrapperException(e);
            }
        }

        private InetSocketAddress local(DatagramChannel chan)
        {
            return (InetSocketAddress) chan.socket().getLocalSocketAddress();
        }

    }

}
