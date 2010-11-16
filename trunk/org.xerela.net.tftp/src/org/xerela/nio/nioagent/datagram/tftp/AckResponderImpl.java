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


public class AckResponderImpl implements AckResponder, PacketConstants
{

    // -- member fields
    public DataProducer dataProducer;
    public EventListener eventListener;
    public BlockNumber blockNumber;
    public int filesize;
    public int lastDataLen;

    // -- constructors
    private AckResponderImpl()
    {
        // do nothing
    }

    // -- public methods
    public static AckResponder create(DataProducer producer, EventListener listener, int firstAckBlocknum)
    {
        AckResponderImpl ackResponderImpl = new AckResponderImpl();
        ackResponderImpl.init(producer, listener, firstAckBlocknum);
        return ackResponderImpl;
    }

    public void produce(byte[] data, Int dataLen)
    {
        dataProducer.produce(DATA_OFFSET, data, dataLen);
        lastDataLen = dataLen.value;
        filesize += dataLen.value;
    }

    public void respondToAck(InetSocketAddress local, InetSocketAddress remote, int ackBlockNum, Int dataBlockNum, byte[] data, Int dataLen, Bool terminate,
                             Bool ignore)
    {
        if (blockNumber.isCurrent(ackBlockNum))
        {
            if (DEFAULT_BLOCK_SIZE > lastDataLen)
            {
                // terminate
                dataLen.value = 0;
                terminate.value = true;
                ignore.value = false;
                lastDataLen = 0;
                blockNumber.invalidate();
                eventListener.transferComplete(local, remote, filesize);
            }
            else
            {
                // send next data
                produce(data, dataLen);
                blockNumber.next();
                dataBlockNum.value = blockNumber.getValue();
                terminate.value = false;
                ignore.value = false;
            }
        }
        else
        {
            // wrong block number -- ignore and continue
            dataLen.value = 0;
            terminate.value = false;
            ignore.value = true;
        }
    }

    // -- private methods
    private void init(DataProducer producer, EventListener listener, int firstAckBlocknum)
    {
        dataProducer = producer;
        eventListener = listener;
        blockNumber = BlockNumberImpl.create(firstAckBlocknum);
        lastDataLen = 512;
        filesize = 0;
    }
}
