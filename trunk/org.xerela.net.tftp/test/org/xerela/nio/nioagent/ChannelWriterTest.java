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
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.Arrays;

import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.ChannelWriter;
import org.xerela.nio.nioagent.RetransmitExtension;
import org.xerela.nio.nioagent.SharedBuffer;
import org.xerela.nio.nioagent.WrapperException;

import junit.framework.TestCase;


public class ChannelWriterTest extends TestCase implements SystemLogger.Injector
{

    // -- Members
    private final byte byteValue = 0x22;
    private ChannelWriter writer;
    private SinkChannel sink;
    private SourceChannel source;
    private byte[] input;
    private byte[] output;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ChannelWriterTest.class);
    }

    // -- Constructors
    public ChannelWriterTest(String arg0)
    {
        super(arg0);
    }

    protected void setUp() throws Exception
    {
        Pipe pipe = Pipe.open();
        sink = pipe.sink();
        sink.configureBlocking(false);
        source = pipe.source();
        source.configureBlocking(false);
        output = SharedBuffer.getOutboundBuffer(logger).createByteArray();
        input = SharedBuffer.getInboundBuffer(logger).createByteArray();
    }

    protected void tearDown() throws Exception
    {
        sink.close();
        source.close();
    }

    public final void testChannelWriter() throws IOException
    {
        writer = new ChannelWriter(new FillerCodec(byteValue), RetransmitExtension.create(500, 0, logger), logger);
        SelectionKey key = new MockKey(sink);
        Arrays.fill(input, byteValue);
        writer.direct(key, null, null, input, 0);
        while (key.isWritable())
        {
            writer.write(key);
        }
        SharedBuffer.getOutboundBuffer(logger).use(new MockUser());
        for (int i = 0; i < output.length; i++)
        {
            assertTrue(byteValue == output[i]);
        }
    }

    public void testChannelWriterTwice() throws Exception
    {
        testChannelWriter();

        ChannelWriterTest test2 = new ChannelWriterTest("test2");
        test2.setUp();
        test2.testChannelWriter();
        test2.tearDown();
    }

    private class MockUser implements SharedBuffer.User
    {
        public void use(ByteBuffer buf)
        {
            try
            {
                source.read(buf);
            }
            catch (IOException e)
            {
                logger.error("Error reading source.", e);
                throw new WrapperException(e);
            }
            buf.flip();
            buf.get(output, 0, buf.capacity());
        }
    }

}
