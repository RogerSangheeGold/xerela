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
import org.xerela.nio.nioagent.datagram.tftp.AckResponder;
import org.xerela.nio.nioagent.datagram.tftp.PacketConstants;

import junit.framework.Assert;


public class MockAckResponder implements AckResponder, PacketConstants
{

    public void respondToAck(InetSocketAddress local, InetSocketAddress remote, int ackBlockNum, Int dataBlockNum, byte[] data, Int dataLen, Bool terminate,
                             Bool ignore)
    {
        Assert.assertEquals(871, ackBlockNum);
        dataBlockNum.value = ackBlockNum + 1;
        dataLen.value = 512;
        for (int i = DATA_OFFSET; i < DATA_OFFSET + dataLen.value; i++)
        {
            data[i] = 0x22;
        }
        terminate.value = false;
        ignore.value = false;
    }

    public void produce(byte[] data, Int dataLen)
    {
        // do nothing
    }
}
