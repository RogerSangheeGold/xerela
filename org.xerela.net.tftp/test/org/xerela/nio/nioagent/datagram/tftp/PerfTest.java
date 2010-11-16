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

import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.ChannelSelectorImpl;
import org.xerela.nio.nioagent.Interfaces.ChannelSelector;
import org.xerela.nio.nioagent.datagram.tftp.TftpServer;
import org.xerela.nio.nioagent.datagram.tftp.server.BasicTftpServer;

import junit.framework.TestCase;


public class PerfTest extends TestCase implements SystemLogger.Injector
{
    // -- static fields
    private static final byte[] testPattern = new byte[] { 'g', 'o', '_', 'g', 'a', 't', 'o', 'r', 's', '!', '_' };
    private static final int numPatternRepetitions = 1000;

    // -- public methods
    public void testPerf() throws Exception
    {

        ChannelSelector channelSelector = ChannelSelectorImpl.getInstance(logger);
        channelSelector.stop();
        channelSelector.start();
        int numClients = 200;
        logger.debug("Running perf test with " + numClients + " transfers.");
        FileGenerator gen = new FileGenerator("var/tftp", "perftest.txt", testPattern, numPatternRepetitions);
        TftpServer server = BasicTftpServer.getInstance(logger);
        server.start();
        ClientRunner clientRunner = new ClientRunner(numClients, "perftest.txt", gen);
        clientRunner.run();
        server.stop();
        channelSelector.stop();
    }

}
