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

import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.CodecResult;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;
import org.xerela.nio.nioagent.datagram.tftp.AckCodecImpl;

import junit.framework.TestCase;


public class AckCodecTest extends TestCase implements SystemLogger.Injector
{

    // -- members
    private BinaryCodec codec;

    // -- constructors    
    public AckCodecTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods
    public void testDecodeEncode()
    {
        byte[] ack = new byte[] { 0x00, 0x04, 0x03, 0x67 };
        byte[] out = new byte[2048];
        CodecResult tuple = codec.decodeEncode(null, null, ack, ack.length, out);
        assertEquals(0x00, out[0]);
        assertEquals(0x03, out[1]);
        assertEquals(0x03, out[2]);
        assertEquals(0x68, out[3]);
        assertEquals(516, tuple.outLen());
        for (int i = 4; i < tuple.outLen(); i++)
        {
            assertEquals(0x22, out[i]);
        }
    }

    // -- protected methods
    @Override
    protected void setUp() throws Exception
    {
        codec = AckCodecImpl.create(new MockAckResponder(), logger);
    }

}
