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

/**
 * Static utility methods for codecs.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class CodecUtils
{
    /**
     * Converts two bytes to an integer
     * @param b0 the high order byte
     * @param b1 the low order byte 
     * @return an int representing the unsigned short
     */
    public static final int unsignedShortToInt(byte b0, byte b1)
    {
        int i = 0;
        i |= b0 & 0xFF;
        i <<= 8;
        i |= b1 & 0xFF;
        return i;
    }

}
