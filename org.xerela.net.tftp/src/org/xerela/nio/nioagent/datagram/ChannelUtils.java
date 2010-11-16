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

package org.xerela.nio.nioagent.datagram;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

import org.xerela.nio.common.ILogger;
import org.xerela.nio.nioagent.WrapperException;


/**
 * Static utility methods for DatagramChannels.
 * 
 * @author Brian Edwards (bedwards@alterpoint.com)
 *
 */
public class ChannelUtils
{
    // -- constructors
    private ChannelUtils()
    {
        // do nothing
    }

    // -- public methods
    public static DatagramChannel openInit(ILogger logger)
    {
        DatagramChannel chan = open(logger);
        init(chan, logger);
        return chan;
    }

    public static void close(DatagramChannel chan, ILogger logger)
    {
        try
        {
            chan.close();
        }
        catch (IOException e)
        {
            logger.error("Failed to close datagram channel.");
            throw new WrapperException(e);
        }
    }

    // -- private methods
    private static DatagramChannel open(ILogger logger)
    {
        try
        {
            return DatagramChannel.open();
        }
        catch (IOException e)
        {
            logger.error("Failed to open datagram channel.");
            throw new WrapperException(e);
        }
    }

    private static void init(DatagramChannel chan, ILogger logger)
    {
        try
        {
            chan.configureBlocking(false);
            chan.socket().setReuseAddress(true);
        }
        catch (IOException e)
        {
            logger.error("Failed to initialize data channel.");
            close(chan, logger);
            throw new WrapperException(e);
        }
    }
}
