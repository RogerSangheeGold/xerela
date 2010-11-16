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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectionKey;
import java.util.Arrays;

import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.ChannelWriter;
import org.xerela.nio.nioagent.CodecResult;
import org.xerela.nio.nioagent.RetransmitExtension;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;
import org.xerela.nio.nioagent.datagram.DatagramAttachment;

import junit.framework.TestCase;


public class DatagramAttachmentTest extends TestCase implements SystemLogger.Injector
{

    // -- statics
    private static final int retransmitDelay = 500;
    private static final int maxRetransmits = 0;

    // -- members
    private InetSocketAddress addr;
    private DatagramChannel chan;
    private ByteBuffer src;
    private int sz;
    private byte data;
    private boolean ranCodec;
    private SelectionKey key;
    private Selector selector;

    // -- constructors
    public DatagramAttachmentTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods
    public void testReadableKey() throws Exception
    {
        key = createKey(SelectionKey.OP_READ);
        DatagramChannel clientChan = openAndInitClientChannel();
        clientChan.write(src);
        clientChan.close();

        assertFalse(ranCodec);

        new DatagramAttachment(new ChannelWriter(new BinaryCodec()
        {
            public CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out)
            {
                assertEquals(sz, inLen);
                for (int i = 0; i < inLen; i++)
                {
                    assertEquals(data, in[0]);
                }
                ranCodec = true;
                return new CodecResult(0, false, false, 0);
            }
        }, RetransmitExtension.create(retransmitDelay, maxRetransmits, logger), logger), logger).control(key);

        assertTrue(ranCodec);
    }

    public void testReadbleNoData() throws Exception
    {
        key = createKey(SelectionKey.OP_READ);
        DatagramChannel clientChan = openAndInitClientChannel();
        clientChan.close();
        DatagramAttachment readNoDataAtt = new DatagramAttachment(new ChannelWriter(new BinaryCodec()
        {
            public CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out)
            {
                fail("No data, should not call decodeEncode.");
                return null;
            }
        }, RetransmitExtension.create(retransmitDelay, maxRetransmits, logger), logger), logger);
        try
        {
            readNoDataAtt.control(key);
        }
        catch (RuntimeException e)
        {
            assertEquals("Buffer is empty after reading from datagram channel.", e.getMessage());
        }

    }

    public void testWritableKey() throws Exception
    {
        key = createKey(SelectionKey.OP_WRITE);
        DatagramChannel clientChan = openAndInitClientChannel();
        clientChan.close();

        new DatagramAttachment(new ChannelWriter(new BinaryCodec()
        {
            public CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out)
            {
                fail("Not readable, should not call decodeEncode.");
                return null;
            }
        }, RetransmitExtension.create(retransmitDelay, maxRetransmits, logger), logger), logger).control(key);

    }

    // -- protected methods
    protected void setUp() throws Exception
    {
        addr = new InetSocketAddress("localhost", 10987);
        chan = DatagramChannel.open();
        chan.configureBlocking(false);
        chan.socket().bind(addr);
        sz = 512;
        byte[] srcArr = new byte[sz];
        data = (byte) 0x22;
        Arrays.fill(srcArr, data);
        src = ByteBuffer.wrap(srcArr);
        ranCodec = false;
        selector = Selector.open();
    }

    protected void tearDown() throws Exception
    {
        chan.close();
    }

    // -- private methods

    private DatagramChannel openAndInitClientChannel() throws Exception
    {
        DatagramChannel clientChan = DatagramChannel.open();
        clientChan.connect(addr);
        clientChan.write(ByteBuffer.wrap(new byte[] { (byte) 0x65 }));
        SocketAddress clientAddr = chan.receive(ByteBuffer.wrap(new byte[1]));
        chan.connect(clientAddr);
        return clientChan;
    }

    private SelectionKey createKey(final int ops)
    {
        return new AbstractSelectionKey()
        {

            @Override
            public SelectableChannel channel()
            {
                return chan;
            }

            @Override
            public int interestOps()
            {
                return ops;
            }

            @Override
            public SelectionKey interestOps(int ops)
            {
                return this;
            }

            @Override
            public int readyOps()
            {
                return ops;
            }

            @Override
            public Selector selector()
            {
                return selector;
            }
        };
    }

}
