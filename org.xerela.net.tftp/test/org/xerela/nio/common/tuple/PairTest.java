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

package org.xerela.nio.common.tuple;

import org.xerela.nio.common.tuple.Pair;

import junit.framework.TestCase;

public class PairTest extends TestCase
{
    // -- fields
    private static final String name = "Rover";

    // -- public methods
    public void testToString()
    {
        assertEquals("(" + name + ", 23)", createPair().toString());
    }

    public void testHashCode()
    {
        assertEquals(-1841249166, createPair().hashCode());
        assertEquals(createPair().hashCode(), createPair().hashCode());
    }

    public void testEquals()
    {
        assertEquals(createPair(), createPair());
        ExamplePair pair = createPair();
        assertEquals(pair, pair);
        assertNotSame(pair, new ExamplePair(name, 47));
    }

    // -- private classes
    private static ExamplePair createPair()
    {
        return new ExamplePair(name, 23);
    }

    // -- inner classes
    static class ExamplePair extends Pair<String, Integer>
    {

        protected ExamplePair(final String name, final Integer count)
        {
            super(name, count);
        }

        String name()
        {
            return a;
        }

        int count()
        {
            return b;
        }

    }

}
