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

import org.xerela.nio.common.Bool;
import org.xerela.nio.nioagent.datagram.tftp.BlockNumberImpl;
import org.xerela.nio.nioagent.datagram.tftp.DataResponder;
import org.xerela.nio.nioagent.datagram.tftp.DataResponderImpl;

import junit.framework.TestCase;


public class DataResponderTest extends TestCase
{

    // -- members
    private DataResponder dataResponder;
    private byte[] data;
    private Bool ignore;

    // -- constructors    
    public DataResponderTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods
    public void testRespondToDataNormal() throws Exception
    {
        // data for next block number in middle of transfer
        // respond with ack
        int currentBlockNumber = 101;
        setCurrentBlockNumber(currentBlockNumber);
        dataResponder.respondToData(null, null, currentBlockNumber, data, 512, ignore);
        assertFalse(ignore.value);
    }

    public void testRespondToDataForPreviousBlockNumber() throws Exception
    {
        // data for current (most recently acked) block number
        // silently ignore and continue
        int currentBlockNumber = 101;
        setCurrentBlockNumber(currentBlockNumber);
        dataResponder.respondToData(null, null, currentBlockNumber - 1, data, 512, ignore);
        assertTrue(ignore.value);
    }

    // -- protected methods
    protected void setUp() throws Exception
    {
        dataResponder = DataResponderImpl.create(new MockDataConsumer(), new MockEventListener());
        data = new byte[2048];
        ignore = new Bool(false);
    }

    // -- private methods
    private void setCurrentBlockNumber(int currentBlockNumber)
    {
        ((BlockNumberImpl) ((DataResponderImpl) dataResponder).blockNumber).value = currentBlockNumber;
    }
}
