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
import org.xerela.nio.common.Int;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;
import org.xerela.nio.nioagent.datagram.tftp.PacketConstants;
import org.xerela.nio.nioagent.datagram.tftp.RrqResponder;

import junit.framework.Assert;


public class MockRrqResponder implements RrqResponder, PacketConstants
{

    public BinaryCodec respondToRrq(InetSocketAddress local, InetSocketAddress remote, String filename, String mode, int blksize, int timeout, byte[] data,
                                    Int dataLen, Bool terminate)
    {
        Assert.assertEquals("foo", filename);
        Assert.assertEquals("bar", mode);
        dataLen.value = 512;

        for (int i = DATA_OFFSET; i < DATA_OFFSET + dataLen.value; i++)
        {
            data[i] = 0x22;
        }
        return null;
    }

}
