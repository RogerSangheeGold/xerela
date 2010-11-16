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

package org.xerela.nio.nioagent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;

import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;

public class ChannelWriter
{
    // -- fields
    private final ILogger logger;
    private final SharedBuffer out;
    private final BinaryCodec codec;
    private final WriterExtension extension;
    private final byte[] outArr;
    private final SharedBuffer.User bufferUser;
    private int outLen;
    private SelectionKey key;

    // -- constructors
    public ChannelWriter(final BinaryCodec codec, final ILogger logger, final Integer bufferSize)
    {
        this(codec, new NoopExtension(), logger, bufferSize);
    }

    public ChannelWriter(final BinaryCodec codec, final WriterExtension extension, final ILogger logger, final Integer bufferSize)
    {
        this.logger = logger;
        this.out = SharedBuffer.getOutboundBuffer(logger, bufferSize);
        this.codec = codec;
        this.extension = extension;
        this.outArr = SharedBuffer.getOutboundBuffer(logger, bufferSize).createByteArray();
        this.bufferUser = new WriterUser();
        this.key = null;
    }

    // -- public methods
    public void direct(final SelectionKey key, final InetSocketAddress local, final InetSocketAddress remote, final byte[] in, final int inLen)
    {
        this.key = key;
        CodecResult tuple = decodeEncode(local, remote, in, inLen);
        if (!tuple.ignore())
        {
            extension.notIgnored();
            if (tuple.terminate())
            {
                key.cancel();
            }
            else if (0 < tuple.outLen())
            {
                outLen = tuple.outLen();
                extension.readyToWrite(tuple.delay());
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    public void write(final SelectionKey key)
    {
        this.key = key;
        out.use(bufferUser);
    }

    // -- private methods
    private CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen)
    {
        try
        {
            return codec.decodeEncode(local, remote, in, inLen, outArr);
        }
        catch (RuntimeException e)
        {
            logger.error("Failure while decoding/encoding. Cancelling retransmit task and key", e);
            cancel();
            throw e;
        }
    }

    private void cancel()
    {
        extension.cancel();
        key.cancel();
    }

    // -- inner classes
    private class WriterUser implements SharedBuffer.User
    {
        public void use(ByteBuffer buf)
        {
            buf.put(outArr, 0, outLen);
            buf.flip();
            writeChannel((WritableByteChannel) key.channel(), buf);
            if (buf.hasRemaining())
            {
                cancel();
                throw new RuntimeException("Partial channel write.");
            }
            else
            {
                extension.successfulWrite(key);
                key.interestOps(SelectionKey.OP_READ);
            }
        }

        private void writeChannel(WritableByteChannel chan, ByteBuffer buf)
        {
            try
            {
                chan.write(buf);
            }
            catch (IOException e)
            {
                logger.error("Failed to write to channel.", e);
                cancel();
                throw new WrapperException(e);
            }
        }
    }

    private static class NoopExtension implements WriterExtension
    {
        // -- public methods
        public void cancel()
        {
            // do nothing
        }

        public void notIgnored()
        {
            // do nothing
        }

        public void readyToWrite(long delay)
        {
            // do nothing
        }

        public void successfulWrite(SelectionKey key)
        {
            // do nothing
        }
    }

}
