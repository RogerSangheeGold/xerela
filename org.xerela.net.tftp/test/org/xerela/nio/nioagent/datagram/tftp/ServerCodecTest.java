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

import org.xerela.nio.common.ByteArrayUtils;
import org.xerela.nio.common.Int;
import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.CodecResult;
import org.xerela.nio.nioagent.datagram.tftp.PacketConstants;
import org.xerela.nio.nioagent.datagram.tftp.ServerCodec;

import junit.framework.TestCase;


public class ServerCodecTest extends TestCase implements PacketConstants, SystemLogger.Injector
{

    // -- members 
    private ServerCodec codec;
    private String filename;
    private String mode;
    private byte[] in;
    private Int inLen;

    // -- constructors
    public ServerCodecTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods
    public void testDecodeEncodeRrq()
    {
        createRequest((byte) 0x01);
        byte[] out = new byte[2048];
        CodecResult tuple = codec.decodeEncode(null, null, in, inLen.value, out);
        assertEquals(0x00, out[0]);
        assertEquals(0x03, out[1]);
        assertEquals(0x00, out[2]);
        assertEquals(0x01, out[3]);
        assertEquals(516, tuple.outLen());
        for (int i = 4; i < tuple.outLen(); i++)
        {
            assertEquals(0x22, out[i]);
        }
    }

    public void testRrqWithOptions()
    {
        doTestRequestWithOptions(OPCODE_RRQ);
    }

    public void testDecodeEncodeWrq()
    {
        createRequest((byte) 0x02);
        byte[] out = new byte[2048];
        CodecResult tuple = codec.decodeEncode(null, null, in, inLen.value, out);
        assertEquals(0x00, out[0]);
        assertEquals(0x04, out[1]);
        assertEquals(0x00, out[2]);
        assertEquals(0x00, out[3]);
        assertEquals(4, tuple.outLen());
    }

    public void testWrqWithOptions()
    {
        doTestRequestWithOptions(OPCODE_WRQ);
    }

    public void testDecodeEncodeError()
    {
        ByteArrayUtils.append(new byte[] { 0x00, 0x05, 0x00, 0x01 }, in, inLen);
        ByteArrayUtils.ntStringAndAppend("File not found.", in, inLen);
        byte[] out = new byte[2048];
        codec.decodeEncode(null, null, in, inLen.value, out);
    }

    // -- protected methods

    @Override
    protected void setUp() throws Exception
    {
        codec = new ServerCodec(new MockWrqResponder(), new MockRrqResponder(), logger);
        filename = "foo";
        mode = "bar";
        in = new byte[2048];
        inLen = new Int(0);
    }

    // -- private methods
    private void createRequest(byte op)
    {
        ByteArrayUtils.append(new byte[] { 0x00, op }, in, inLen);
        ByteArrayUtils.ntStringAndAppend(filename, in, inLen);
        ByteArrayUtils.ntStringAndAppend(mode, in, inLen);
    }

    private void assertOption(String expectedOption, int expectedValue, byte[] out, int outLen, Int outPos)
    {
        StringBuffer option = new StringBuffer();
        ByteArrayUtils.nextNtString(out, outLen, outPos, option);
        assertEquals(expectedOption, option.toString());
        Int number = new Int(0);
        ByteArrayUtils.nextNtNumber(out, outLen, outPos, number);
        assertEquals(expectedValue, number.value.intValue());
    }

    private void doTestRequestWithOptions(byte opcode)
    {
        createRequest(opcode);
        ByteArrayUtils.ntStringAndAppend(OPTION_BLKSIZE, in, inLen);
        int blksizeValue = 1432;
        ByteArrayUtils.ntNumberAndAppend(blksizeValue, in, inLen);
        ByteArrayUtils.ntStringAndAppend(OPTION_TIMEOUT, in, inLen);
        int timeoutValue = 30;
        ByteArrayUtils.ntNumberAndAppend(timeoutValue, in, inLen);
        byte[] out = new byte[2048];
        CodecResult tuple = codec.decodeEncode(null, null, in, inLen.value, out);
        assertEquals(0x00, out[0]);
        assertEquals(0x06, out[1]);
        Int outPos = new Int(2);
        assertOption(OPTION_BLKSIZE, blksizeValue, out, tuple.outLen(), outPos);
        assertOption(OPTION_TIMEOUT, timeoutValue, out, tuple.outLen(), outPos);
    }

}
