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


public class DataResponderImpl implements DataResponder, PacketConstants
{
    // -- member fields
    public DataConsumer dataConsumer;
    public EventListener eventListener;
    public BlockNumber blockNumber;
    public int filesize;

    // -- constructors
    private DataResponderImpl()
    {
        // do nothing
    }

    // -- public methods
    public static DataResponder create(DataConsumer consumer, EventListener listener)
    {
        DataResponderImpl dataResponderImpl = new DataResponderImpl();
        dataResponderImpl.init(consumer, listener);
        return dataResponderImpl;
    }

    public void respondToData(InetSocketAddress local, InetSocketAddress remote, int dataBlockNum, byte[] data, int dataLen, Bool ignore)
    {
        if (blockNumber.isCurrent(dataBlockNum))
        {
            boolean isLastData = DEFAULT_BLOCK_SIZE > dataLen;
            dataConsumer.consume(data, DATA_OFFSET, dataLen, isLastData);
            filesize += dataLen;
            ignore.value = false;
            blockNumber.next();
            if (isLastData)
            {
                blockNumber.invalidate();
                eventListener.transferComplete(local, remote, filesize);
            }
        }
        else
        {
            ignore.value = true;
        }
    }

    // -- private methods
    private void init(DataConsumer consumer, EventListener listener)
    {
        dataConsumer = consumer;
        eventListener = listener;
        filesize = 0;
        blockNumber = BlockNumberImpl.create(1);
    }

}
