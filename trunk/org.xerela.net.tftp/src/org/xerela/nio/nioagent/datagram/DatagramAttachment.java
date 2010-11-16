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
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.ChannelWriter;
import org.xerela.nio.nioagent.SharedBuffer;
import org.xerela.nio.nioagent.WrapperException;
import org.xerela.nio.nioagent.Interfaces.KeyAttachment;

/**
 * An implementation of KeyAttachment for use with datagram protocols.  This is
 * the base implementation handling the simple act of packet exchange between
 * two socket addresses.  It does not handle the switchover from the well-known
 * server port to the random high port.  ServerDatagramAttachment and
 * ClientDatagramAttachment use this class to perform the high port connection.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class DatagramAttachment implements KeyAttachment
{

    // -- fields
    private final ILogger logger;
    private final SharedBuffer in;
    private final byte[] inArr;
    private final ChannelWriter writer;
    private final SharedBuffer.User bufferUser;
    private SelectionKey key;

    // -- Constructors
    public DatagramAttachment(ChannelWriter writer, ILogger logger, final Integer bufferSize)
    {
        this.logger = logger;
        this.in = SharedBuffer.getInboundBuffer(logger, bufferSize);
        this.inArr = in.createByteArray();
        this.writer = writer;
        this.bufferUser = new DatagramUser();
    }

    // -- Public methods
    public void control(final SelectionKey key)
    {
        this.key = key;
        if (key.isValid() && key.isReadable())
        {
            in.use(bufferUser);
        }
        if (key.isValid() && key.isWritable())
        {
            writer.write(key);
        }
    }

    // -- Inner classes
    private class DatagramUser implements SharedBuffer.User
    {

        // -- public methods
        public void use(ByteBuffer buf)
        {
            DatagramChannel chan = (DatagramChannel) key.channel();
            readChannel((DatagramChannel) chan, buf);
            checkForErrors(buf.position());
            InetSocketAddress remote = (InetSocketAddress) chan.socket().getRemoteSocketAddress();
            logger.debug("Read datagram from " + remote + ".");
            buf.flip();
            buf.get(inArr, 0, buf.limit());
            writer.direct(key, local(chan), remote, inArr, buf.limit());
        }

        // -- private methods
        private void readChannel(DatagramChannel chan, ByteBuffer buf)
        {

            try
            {
                chan.read(buf);
            }
            catch (IOException e)
            {
                logger.debug("Failed to receive from channel.");
                key.cancel();
                throw new WrapperException(e);
            }

        }

        private void checkForErrors(int bufferPosition)
        {
            if (0 == bufferPosition)
            {
                String errorMsg = "Buffer is empty after reading from datagram channel.";
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }

        private InetSocketAddress local(DatagramChannel chan)
        {
            return (InetSocketAddress) chan.socket().getLocalSocketAddress();
        }

    }

}
