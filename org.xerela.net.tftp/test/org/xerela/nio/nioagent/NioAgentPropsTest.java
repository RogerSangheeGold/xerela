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

import org.xerela.nio.common.SystemLogger;
import org.xerela.nio.nioagent.NioAgentPropsImpl;

import junit.framework.TestCase;


public class NioAgentPropsTest extends TestCase implements SystemLogger.Injector
{

    // -- member fields
    private NioAgentPropsImpl nioAgentPropsImpl;

    // -- constructors
    public NioAgentPropsTest(String arg0)
    {
        super(arg0);
    }

    // -- public methods
    public void testPropertiesFile() throws Exception
    {
        String testValue = (String) nioAgentPropsImpl.props.get("props.test");
        assertEquals("foo", testValue);
    }

    // -- protected methods
    protected void setUp() throws Exception
    {
        nioAgentPropsImpl = (NioAgentPropsImpl) NioAgentPropsImpl.getInstance(logger);
    }

}
