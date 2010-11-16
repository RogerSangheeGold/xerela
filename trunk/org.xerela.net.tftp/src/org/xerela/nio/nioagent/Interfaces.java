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

import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface Interfaces
{
    /**
     * A binary codec that takes in a byte buffer, decodes it based of the packet
     * formats defined by the protocol, formulates a response based on the contents
     * of the packet, and encodes that response into an outbound byte buffer.
     */
    public interface BinaryCodec
    {
        public CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out);

        public interface Factory<T extends BinaryCodec>
        {
            T createBinaryCodec();
        }
    }

    public interface ChannelSelector
    {
        void start();

        void register(SelectableChannel chan, int op_write, KeyAttachment att);

        void stop();
    }

    public interface KeyAttachment
    {
        void control(SelectionKey key);
    }

    public interface ManagedThread
    {
        void start();

        void stop();
    }

}
