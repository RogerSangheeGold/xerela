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

/**
 * A interface for the data consuming side of the TFTP connection.  Typically
 * TFTP implmentations will write received data to a file on the local
 * filesystem.  By using this interface this TFTP implementation can be extended
 * to write the data to any place imaginable.  
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 * @see DataProducer, FileDataConsumer
 */
public interface DataConsumer
{
    public void consume(byte[] data, int dataOff, int dataLen, boolean isFinal);

    public static interface Factory
    {
        public DataConsumer createConsumer(final String filename, final ILogger logger);
    }
}
