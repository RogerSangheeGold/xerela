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
import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.CodecResult;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;


/**
 * Decodes a inbound byte buffer which is known to be a TFTP data packet into an
 * easy-to-use representation of that packet.  Formulates an appropriate ACK
 * packet for the repsonse.  Encodes the ACK packet into an outbound byte
 * buffer.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class DataCodecImpl implements BinaryCodec, PacketConstants
{

    // -- members
    DataResponder dataResponder;
    ILogger logger;

    // -- constructors
    private DataCodecImpl()
    {
        // do nothing
    }

    // -- public methods
    public static BinaryCodec create(final DataResponder responder, final ILogger logger)
    {
        DataCodecImpl impl = new DataCodecImpl();
        impl.dataResponder = responder;
        impl.logger = logger;
        return impl;
    }

    public CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out)
    {
        final CodecResult tuple;
        if (OPCODE_DATA == in[1])
        {
            tuple = decodeEncodeData(local, remote, in, inLen, out);
        }
        else
        {
            tuple = new CodecResult(0, false, true, 0);
        }
        return tuple;
    }

    // -- package-private methods
    CodecResult decodeEncodeData(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out)
    {
        // decode data
        int blockNumber = CodecUtils.unsignedShortToInt(in[2], in[3]);
        logger.debug("Received data with block number " + blockNumber + ".");
        Bool ignore = new Bool(false);

        // respond to data
        dataResponder.respondToData(local, remote, blockNumber, in, inLen - DATA_OFFSET, ignore);

        final int outLen;
        if (!ignore.value)
        {
            // encode ack
            out[0] = 0x00;
            out[1] = OPCODE_ACK;
            out[2] = in[2];
            out[3] = in[3];
            outLen = 4;
            logger.debug("Sending ack with block number " + blockNumber + ".");
        }
        else
        {
            outLen = 0;
        }
        return new CodecResult(outLen, false, ignore.value, 0);
    }

}
