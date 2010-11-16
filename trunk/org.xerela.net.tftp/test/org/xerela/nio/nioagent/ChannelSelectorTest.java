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

/**
 * 
 */
package org.xerela.nio.nioagent;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;

import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.ChannelSelectorImpl;
import org.xerela.nio.nioagent.ManagedThreadImpl;
import org.xerela.nio.nioagent.Interfaces.KeyAttachment;

import junit.framework.TestCase;


/**
 * @author bedwards
 * 
 */
public class ChannelSelectorTest extends TestCase implements SystemLogger.Injector
{

    // -- member fields
    private ChannelSelectorImpl channelSelectorImpl;

    // -- constructors
    public ChannelSelectorTest(String arg0)
    {
        super(arg0);
    }

    // -- protected methods
    protected void setUp() throws Exception
    {
        channelSelectorImpl = (ChannelSelectorImpl) ChannelSelectorImpl.getInstance(logger);
    }

    public final void testChannelSelector() throws InterruptedException, IOException, IllegalAccessException, NoSuchFieldException
    {
        assertNotNull(channelSelectorImpl);
        channelSelectorImpl.stop();
        channelSelectorImpl.start();
        assertRunning(channelSelectorImpl);

        assertNoneRegistered(channelSelectorImpl);
        channelSelectorImpl.register(Pipe.open().sink().configureBlocking(false), SelectionKey.OP_WRITE, new MockAttachment());
        Thread.sleep(500);
        assertSomeRegistered(channelSelectorImpl);

        assertRunning(channelSelectorImpl);
        channelSelectorImpl.stop();
        assertStopped(channelSelectorImpl);
    }

    // -- private methods
    private Thread getThread(ChannelSelectorImpl selector) throws IllegalAccessException, NoSuchFieldException
    {
        return ((ManagedThreadImpl) channelSelectorImpl.managedThread).thread;
    }

    private void assertRunning(ChannelSelectorImpl selector) throws IllegalAccessException, NoSuchFieldException
    {
        assertNotNull(getThread(selector));
    }

    private void assertStopped(ChannelSelectorImpl selector) throws IllegalAccessException, NoSuchFieldException
    {
        assertNull(getThread(selector));
    }

    private void assertEmptyKeySetIs(boolean bool, ChannelSelectorImpl cSelector) throws IllegalAccessException, NoSuchFieldException
    {
        assertEquals(bool, channelSelectorImpl.selector.keys().isEmpty());
    }

    private void assertNoneRegistered(ChannelSelectorImpl cSelector) throws IllegalAccessException, NoSuchFieldException
    {
        assertEmptyKeySetIs(true, cSelector);
    }

    private void assertSomeRegistered(ChannelSelectorImpl cSelector) throws IllegalAccessException, NoSuchFieldException
    {
        assertEmptyKeySetIs(false, cSelector);
    }

    // -- inner classes
    private static class MockAttachment implements KeyAttachment
    {

        public void control(SelectionKey key)
        {
            key.interestOps(0);
        }

    }

}
