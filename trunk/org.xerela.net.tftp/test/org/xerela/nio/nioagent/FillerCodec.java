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

package org.xerela.nio.nioagent;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.xerela.nio.nioagent.CodecResult;
import org.xerela.nio.nioagent.Interfaces.BinaryCodec;


public class FillerCodec implements BinaryCodec
{

    // -- members
    private byte byteValue;

    // -- constructors
    public FillerCodec(byte byteValue)
    {
        this.byteValue = byteValue;
    }

    // -- public methods
    public CodecResult decodeEncode(InetSocketAddress local, InetSocketAddress remote, byte[] in, int inLen, byte[] out)
    {
        Arrays.fill(out, byteValue);
        return new CodecResult(out.length, false, false, 0);
    }

}
