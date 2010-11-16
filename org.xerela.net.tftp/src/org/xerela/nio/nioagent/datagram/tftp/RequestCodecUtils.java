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
import org.xerela.nio.common.ILogger;
import org.xerela.nio.common.Int;

/**
 * Static utility methods for decoding TFTP request packets (both RRQ and WRQ).
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class RequestCodecUtils implements PacketConstants
{
    // -- constructors
    private RequestCodecUtils()
    {
        // do nothing
    }

    // -- public methods    
    public static void decodeRequest(byte[] in, int inLen, StringBuffer filename, StringBuffer mode, int defaultTimeoutInterval, Int blksize, Int timeout,
            ILogger logger)
    {
        Int inPos = new Int(2);
        ByteArrayUtils.nextNtString(in, inLen, inPos, filename);
        ByteArrayUtils.nextNtString(in, inLen, inPos, mode);
        blksize.value = DEFAULT_BLOCK_SIZE;
        timeout.value = defaultTimeoutInterval;
        decodeOptions(in, inLen, inPos, blksize, timeout);
    }

    // -- private methods
    private static int parseInt(String string)
    {
        try
        {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e)
        {
            return DEFAULT_BLOCK_SIZE;
        }
    }

    private static void decodeValue(byte[] buf, int len, Int pos, Int value)
    {
        StringBuffer valueField = new StringBuffer();
        ByteArrayUtils.nextNtString(buf, len, pos, valueField);
        value.value = parseInt(valueField.toString());
    }

    private static void decodeOption(String option, byte[] buf, int len, Int pos, Int blksize, Int timeout)
    {
        if ("blksize".equalsIgnoreCase(option))
        {
            decodeValue(buf, len, pos, blksize);
        }
        else if ("timeout".equalsIgnoreCase(option))
        {
            decodeValue(buf, len, pos, timeout);
        }
        else
        {
            decodeValue(buf, len, pos, new Int(0));
        }
    }

    private static void decodeOptions(byte[] buf, int len, Int pos, Int blksize, Int timeout)
    {
        while (len > pos.value)
        {
            StringBuffer option = new StringBuffer();
            ByteArrayUtils.nextNtString(buf, len, pos, option);
            decodeOption(option.toString(), buf, len, pos, blksize, timeout);
        }
    }

}
