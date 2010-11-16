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
 * Abstracts the block number state machine which is used by both DATA and ACK
 * codecs.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class BlockNumberImpl implements BlockNumber
{

    // -- members
    public int value;
    public boolean isValid;

    // -- constructors
    private BlockNumberImpl()
    {
        // do nothing
    }

    // -- public methods
    public static BlockNumber create(int initialValue)
    {
        BlockNumberImpl blockNumberImpl = new BlockNumberImpl();
        blockNumberImpl.init(initialValue);
        return blockNumberImpl;
    }

    public boolean isCurrent(int other)
    {
        if (isValid)
        {
            if (other == value)
            {
                return true;
            }
            else if (other == 0 && value == 1)
            {
                /*
                 * TFTP clients can ack with blknum=0.  This
                 * is unusual, so we handle it as a case here and 
                 * not the default, which is blknum=1.
                 */
                value = 0;
                return true;
            }
        }
        return false;
    }

    public void next()
    {
        if (isValid)
        {
            value = 65535 == value ? 0 : value + 1;
        }
    }

    public int getValue()
    {
        if (isValid)
        {
            return value;
        }
        throw new RuntimeException("Cannot get value of invalid block number.");
    }

    public void invalidate()
    {
        isValid = false;
    }

    // -- private methods
    private void init(int initialValue)
    {
        value = initialValue;
        isValid = true;
    }

}
