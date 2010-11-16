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

import org.xerela.nio.common.ByteArrayUtils;
import org.xerela.nio.common.Int;
import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.datagram.tftp.PacketConstants;
import org.xerela.nio.nioagent.datagram.tftp.RequestCodecUtils;
import org.xerela.nio.nioagent.datagram.tftp.TftpProperties;

import junit.framework.TestCase;


public class RequestUtilsTest extends TestCase implements PacketConstants, SystemLogger.Injector
{

    // -- static fields
    private static final String testFilename = "abcdef";
    private static final String testMode = "ghijk";

    // -- member fields
    private byte[] in;
    private Int inLen;
    private StringBuffer filename;
    private StringBuffer mode;
    private Int blksize;
    private Int timeout;

    // -- constructors
    public RequestUtilsTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods

    public void testDecodeBlkSizeNone()
    {
        RequestCodecUtils.decodeRequest(in, inLen.value, filename, mode, blksize, timeout, logger);
        makeAssertions(filename.toString(), mode.toString(), DEFAULT_BLOCK_SIZE, blksize.value, TftpProperties.getInstance(logger).getDefaultTimeoutInterval(),
                       timeout.value);
    }

    public void testDecodeBlkSize1024()
    {
        int expectedSize = 1024;
        setBlksize(expectedSize);
        RequestCodecUtils.decodeRequest(in, inLen.value, filename, mode, blksize, timeout, logger);
        makeAssertions(filename.toString(), mode.toString(), expectedSize, blksize.value, TftpProperties.getInstance(logger).getDefaultTimeoutInterval(),
                       timeout.value);
    }

    public void testDecodeBlkSizeFoo()
    {
        setBlksize("Foo");
        RequestCodecUtils.decodeRequest(in, inLen.value, filename, mode, blksize, timeout, logger);
        makeAssertions(filename.toString(), mode.toString(), DEFAULT_BLOCK_SIZE, blksize.value, TftpProperties.getInstance(logger).getDefaultTimeoutInterval(),
                       timeout.value);
    }

    public void testDecodeBlkSizeSecond()
    {
        int expectedTimeout = 200;
        setTimeout(expectedTimeout);
        int expectedSize = 1400;
        setBlksize(expectedSize);
        RequestCodecUtils.decodeRequest(in, inLen.value, filename, mode, blksize, timeout, logger);
        makeAssertions(filename.toString(), mode.toString(), expectedSize, blksize.value, expectedTimeout, timeout.value);
    }

    // -- protected methods
    @Override
    protected void setUp() throws Exception
    {
        in = new byte[2048];
        inLen = new Int(0);
        ByteArrayUtils.append(new byte[] { 0, 1 }, in, inLen);
        nullTerminateAndAppendToIn(testFilename);
        nullTerminateAndAppendToIn(testMode);
        filename = new StringBuffer();
        mode = new StringBuffer();
        blksize = new Int(0);
        timeout = new Int(0);
    }

    // -- private methods
    private void nullTerminateAndAppendToIn(String string)
    {
        ByteArrayUtils.ntStringAndAppend(string, in, inLen);
    }

    private void makeAssertions(String filename, String mode, int expectedBlksize, int blksize, int expectedTimeout, int timeout)
    {
        assertEquals(testFilename, filename);
        assertEquals(testMode, mode);
        assertEquals(expectedBlksize, blksize);
        assertEquals(expectedTimeout, timeout);
    }

    private void setOption(String option, String value)
    {
        nullTerminateAndAppendToIn(option);
        nullTerminateAndAppendToIn(value);
    }

    private void setBlksize(String value)
    {
        setOption("blksize", value);
    }

    private void setBlksize(int value)
    {
        setBlksize(Integer.toString(value));
    }

    private void setTimeout(String value)
    {
        setOption("timeout", value);
    }

    private void setTimeout(int value)
    {
        setTimeout(Integer.toString(value));
    }

}
