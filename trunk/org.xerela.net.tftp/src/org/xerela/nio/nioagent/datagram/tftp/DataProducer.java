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

import org.xerela.nio.common.ILogger;
import org.xerela.nio.common.Int;

/**
 * An interface for data producers.  The typical operation of a TFTP client or
 * server is to produce data from files on the filesystem, but this interface
 * allows the TFTP implementation to be extended to produce data from any number
 * of sources. 
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public interface DataProducer
{
    /**
     * If nothing left to produce, must set dataLen.value to 0.
     */
    public void produce(int dataOff, byte[] data, Int dataLen);

    public static interface Factory
    {
        public DataProducer createProducer(final String filename, final int blockSize, final ILogger logger);
    }

}
