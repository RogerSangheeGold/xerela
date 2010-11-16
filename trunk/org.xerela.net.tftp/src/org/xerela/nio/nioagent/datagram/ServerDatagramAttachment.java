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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.ChannelWriter;
import org.xerela.nio.nioagent.RetransmitExtension;
import org.xerela.nio.nioagent.SharedBuffer;
import org.xerela.nio.nioagent.WrapperException;
import org.xerela.nio.nioagent.WriterExtension;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;
import org.xerela.nio.nioagent.Interfaces.KeyAttachment;

/**
 * Implementation of KeyAttachment for the server side of datagram protocols.  A
 * ServerDatagramAttachement binds to the well-known port for the protocol and 
 * listens for requests from various clients.  Each time it receives a request
 * it creates a new DatagramAttachment to handle the transfer of packets to and
 * from a random high port. 
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class ServerDatagramAttachment implements KeyAttachment
{

    // -- fields
    ILogger logger;
    BinaryCodec.Factory codecFactory;
    long retransmitTimeout;
    int maxRetransmits;
    SharedBuffer.User bufferUser;
    SharedBuffer in;
    byte[] inArr;
    SelectionKey serverKey;
    Integer bufferSize;

    // -- constructors
    private ServerDatagramAttachment()
    {
        // do nothing
    }

    // -- public methods
    public static KeyAttachment create(final BinaryCodec.Factory factory, final long timeout, final int retries, final ILogger logger, final Integer bufferSize)
    {
        ServerDatagramAttachment impl = new ServerDatagramAttachment();
        impl.logger = logger;
        impl.codecFactory = factory;
        impl.retransmitTimeout = timeout;
        impl.maxRetransmits = retries;
        impl.bufferUser = impl.new ServerDatagramUser();
        impl.in = SharedBuffer.getInboundBuffer(logger, bufferSize);
        impl.inArr = impl.in.createByteArray();
        impl.bufferSize = bufferSize;
        return impl;
    }

    public void control(SelectionKey sKey)
    {
        serverKey = sKey;

        if (serverKey.isValid() && serverKey.isReadable())
        {
            in.use(bufferUser);
        }

    }

    // -- innner classes
    private class ServerDatagramUser implements SharedBuffer.User
    {

        public void use(ByteBuffer buf)
        {
            InetSocketAddress clientAddr = receiveChannel((DatagramChannel) serverKey.channel(), buf);
            checkForErrors(clientAddr, buf.position());
            logger.debug("Received datagram from " + clientAddr + ".");
            DatagramChannel dataChan = ChannelUtils.openInit(logger);
            connectChannel(dataChan, clientAddr);
            ChannelWriter writer = new ChannelWriter(codecFactory.createBinaryCodec(), retransmit(), logger, bufferSize);
            SelectionKey dataKey = registerWithSelector(dataChan, writer);
            buf.flip();
            buf.get(inArr, 0, buf.limit());
            writer.direct(dataKey, local(dataChan), clientAddr, inArr, buf.limit());
        }

        WriterExtension retransmit()
        {
            return RetransmitExtension.create(retransmitTimeout, maxRetransmits, logger);
        }

        InetSocketAddress receiveChannel(DatagramChannel chan, ByteBuffer buf)
        {
            try
            {
                return (InetSocketAddress) chan.receive(buf);
            }
            catch (IOException e)
            {
                logger.error("Failed to receive from server channel.");
                throw new WrapperException(e);
            }
        }

        void checkForErrors(SocketAddress clientAddr, int bufferPosition)
        {
            if (null == clientAddr || 0 == bufferPosition)
            {
                String errorMsg = errorMsg(clientAddr, bufferPosition);
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }

        String errorMsg(SocketAddress clientAddr, int bufferPosition)
        {
            final String errorMsg;
            if (null == clientAddr && 0 == bufferPosition)
            {
                errorMsg = "Client socket address is null and buffer is empty after receiving from server channel.";
            }
            else if (null == clientAddr)
            {
                errorMsg = "Client socket address is null after receiving from server channel.";
            }
            else
            {
                errorMsg = "Buffer is empty after receiving from server channel.";
            }
            return errorMsg;
        }

        void connectChannel(DatagramChannel dataChan, SocketAddress clientAddr)
        {
            try
            {
                dataChan.connect(clientAddr);
            }
            catch (IOException e)
            {
                logger.error("Failed to connect datagram channel to " + clientAddr + ".");
                ChannelUtils.close(dataChan, logger);
                throw new WrapperException(e);
            }
        }

        SelectionKey registerWithSelector(DatagramChannel chan, ChannelWriter writer)
        {
            try
            {
                return chan.register(serverKey.selector(), 0, new DatagramAttachment(writer, logger, bufferSize));
            }
            catch (ClosedChannelException e)
            {
                logger.error("Failed to register data channel with the selector.");
                ChannelUtils.close(chan, logger);
                throw new WrapperException(e);
            }
        }

        InetSocketAddress local(DatagramChannel chan)
        {
            return (InetSocketAddress) chan.socket().getLocalSocketAddress();
        }

    }

}
