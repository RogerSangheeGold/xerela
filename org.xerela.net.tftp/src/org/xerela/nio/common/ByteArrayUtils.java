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

package org.xerela.nio.common;

public class ByteArrayUtils
{

    // -- public methods
    public static void append(byte[] src, byte[] dst, Int dstLen)
    {
        System.arraycopy(src, 0, dst, dstLen.value, src.length);
        dstLen.value = src.length + dstLen.value.intValue();
    }

    public static void ntStringAndAppend(String string, byte[] dst, Int dstLen)
    {
        append((string + '\00').getBytes(), dst, dstLen);
    }

    public static void ntNumberAndAppend(int number, byte[] dst, Int dstLen)
    {
        ntStringAndAppend(new Integer(number).toString(), dst, dstLen);
    }

    public static void nextNtString(byte[] buf, int len, Int pos, StringBuffer string)
    {
        for (int i = pos.value; i < len; i++)
        {
            if (0x00 == buf[i])
            {
                pos.value = i + 1;
                break;
            }
            string.append((char) buf[i]);
        }
    }

    public static void nextNtNumber(byte[] buf, int len, Int pos, Int number)
    {
        StringBuffer string = new StringBuffer();
        nextNtString(buf, len, pos, string);
        number.value = Integer.parseInt(string.toString());
    }

}
