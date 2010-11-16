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

/* Alterpoint, Inc.
 *
 * The contents of this source code are proprietary and confidential
 * All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2006
 *
 *   $Author: rkruse $
 *     $Date: 2007/02/06 18:53:11 $
 * $Revision: 1.4 $
 *   $Source: /usr/local/cvsroot/org.xerela.net.util/test/org/xerela/common/RegExUtilitiesTest.java,v $e
 */

package org.xerela.common;

import junit.framework.TestCase;

/**
 * @author rkruse
 */
public class RegExUtilitiesTest extends TestCase
{

    public void testGoodMatch()
    {
        String targetText = "Hello There";
        String regex = "l+o\\s+";
        assertTrue(RegExUtilities.regexMatch(targetText, regex));
    }

    public void testNoMatch()
    {
        String targetText = "Hello There";
        String regex = "\\d+";
        assertFalse(RegExUtilities.regexMatch(targetText, regex));
    }

    public void testGoodLastIndex()
    {
        String targetText = "/usr: directory\n3dns1:/#";
        String regex = "3dns1\\:/#";
        assertEquals(16, RegExUtilities.regexLastIndexOf(targetText, regex));
    }

    public void testGoodLastIndex2()
    {
        String targetText = "aaa";
        String regex = "aa";
        assertEquals(1, RegExUtilities.regexLastIndexOf(targetText, regex));
    }

    public void testNoLastIndex()
    {
        String targetText = "aaa";
        String regex = "b";
        assertEquals(-1, RegExUtilities.regexLastIndexOf(targetText, regex));
    }

}

// -------------------------------------------------
// $Log: RegExUtilitiesTest.java,v $
// Revision 1.4  2007/02/06 18:53:11  rkruse
// merging from another branch
//
// Revision 1.2  2007/02/06 18:15:39  BEdwards
// add a last index of method
//
// Revision 1.1  2006/10/16 21:33:30  Rkruse
// Xerela utilities project
//
// Revision 1.0 Oct 16, 2006 rkruse
// Initial revision
// --------------------------------------------------
