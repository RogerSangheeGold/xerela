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

import java.io.File;

import org.xerela.nio.common.Int;
import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.datagram.tftp.DataProducer;
import org.xerela.nio.nioagent.datagram.tftp.FileDataProducer;
import org.xerela.nio.nioagent.datagram.tftp.PacketConstants;

import junit.framework.TestCase;


public class FileDataProducerTest extends TestCase implements PacketConstants, SystemLogger.Injector
{

    private File file;

    // -- constructors
    public FileDataProducerTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods
    public final void testNothingLeftToProduce() throws Exception
    {
        file = new File("var/testNothingLeftToProduce");
        file.delete();
        file.createNewFile();
        DataProducer prod = FileDataProducer.create("var", file.getName(), DEFAULT_BLOCK_SIZE, logger);
        Int dataLen = new Int(0);
        prod.produce(0, new byte[0], dataLen);
        assertEquals(0, dataLen.value.intValue());
        prod.produce(0, new byte[0], dataLen);
        assertEquals(0, dataLen.value.intValue());
        file.delete();
    }

    // -- protected methods
    protected void setUp() throws Exception
    {

    }

    protected void tearDown() throws Exception
    {
        file.delete();
    }

}
