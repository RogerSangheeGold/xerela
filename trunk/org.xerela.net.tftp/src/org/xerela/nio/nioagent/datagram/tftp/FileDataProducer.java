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

import org.xerela.nio.common.FileIn;
import org.xerela.nio.common.ILogger;
import org.xerela.nio.common.Int;

/**
 * Implmentation of the DataProducer interface that reads bytes of data from a
 * file on the local filesystem.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class FileDataProducer implements DataProducer, PacketConstants
{

    // -- member fields
    FileIn fileIn;
    int blockSize;

    // -- constructors
    private FileDataProducer()
    {
        // do nothing
    }

    // -- public mehtods
    public static DataProducer create(final String dir, final String filename, final int blockSize, final ILogger logger)
    {
        FileDataProducer impl = new FileDataProducer();
        impl.fileIn = new FileIn(dir, filename, logger);
        impl.blockSize = blockSize;
        return impl;
    }

    public void produce(int dataOff, byte[] data, Int dataLen)
    {
        dataLen.value = 0;
        int numBytes = 0;
        do
        {
            numBytes = fileIn.read(data, dataOff + dataLen.value, blockSize - dataLen.value);
            dataLen.value = numBytes + dataLen.value.intValue();
        }
        while (0 < numBytes && blockSize > dataLen.value);
    }

    // -- inner classes
    public static class Factory implements DataProducer.Factory
    {
        private final String dir;

        public Factory(String dir)
        {
            this.dir = dir;
        }

        public DataProducer createProducer(final String filename, final int blockSize, final ILogger logger)
        {
            return create(dir, filename, blockSize, logger);
        }
    }
}
