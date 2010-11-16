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

import org.xerela.nio.common.FileOut;
import org.xerela.nio.common.ILogger;

/**
 * Implementation of the DataConsumer interface that writes bytes of data
 * received from the remote host to a file on the local filesystem.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class FileDataConsumer implements DataConsumer
{

    // -- member fields
    private final FileOut fileOut;

    // -- constructors
    public FileDataConsumer(final String dir, final String filename, final ILogger logger)
    {
        this.fileOut = new FileOut(dir, filename, logger);
    }

    // -- public methods
    public void consume(byte[] data, int dataOff, int dataLen, boolean isFinal)
    {
        fileOut.write(data, dataOff, dataLen);
        if (isFinal)
        {
            fileOut.close();
        }
    }

    // -- private methods

    // -- inner classes
    public static class Factory implements DataConsumer.Factory
    {

        private final String dir;

        public Factory(String dir)
        {
            this.dir = dir;
        }

        public DataConsumer createConsumer(final String filename, final ILogger logger)
        {
            return new FileDataConsumer(dir, filename, logger);
        }

    }

}
