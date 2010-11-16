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

import java.nio.ByteBuffer;

import org.xerela.nio.common.ILogger;

public class SharedBuffer
{

    // -- fields
    private static SharedBuffer in = null;
    private static SharedBuffer out = null;
    ByteBuffer buf;

    // -- Constructors
    private SharedBuffer()
    {
        // do nothing
    }

    // -- Public methods
    public synchronized static SharedBuffer getInboundBuffer(final ILogger logger, final Integer bufferSize)
    {
        if (null == in)
        {
            in = create(logger, bufferSize);
        }
        return in;
    }

    public synchronized static SharedBuffer getOutboundBuffer(final ILogger logger, final Integer bufferSize)
    {
        if (null == out)
        {
            out = create(logger, bufferSize);
        }
        return out;
    }

    public void use(User user)
    {
        buf.clear();
        user.use(buf);
    }

    public byte[] createByteArray()
    {
        return new byte[buf.capacity()];
    }

    // -- private methods
    private static SharedBuffer create(final ILogger logger, final int bufferSize)
    {
        SharedBuffer impl = new SharedBuffer();
        impl.buf = ByteBuffer.allocateDirect(bufferSize);
        return impl;
    }

    // -- inner classes
    public static interface User
    {

        /**
         * Callers MUST clear buffer prior to calling this method.
         * 
         * Implementors MUST not reference the buffer after this method returns
         * (i.e. must not assign the buffer to a member field).
         */
        public void use(ByteBuffer buf);

    }

}
