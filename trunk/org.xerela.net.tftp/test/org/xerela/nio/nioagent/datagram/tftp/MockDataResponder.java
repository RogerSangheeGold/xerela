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
import org.xerela.nio.nioagent.datagram.tftp.DataResponder;
import org.xerela.nio.nioagent.datagram.tftp.PacketConstants;

import junit.framework.Assert;


public class MockDataResponder implements DataResponder, PacketConstants
{

    public void respondToData(InetSocketAddress local, InetSocketAddress remote, int dataBlockNum, byte[] data, int dataLen, Bool ignore)
    {
        Assert.assertEquals(1025, dataBlockNum);

        for (int i = DATA_OFFSET; i < DATA_OFFSET + dataLen; i++)
        {
            Assert.assertEquals(0x22, data[i]);
        }
    }
}
