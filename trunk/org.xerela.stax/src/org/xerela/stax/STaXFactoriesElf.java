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
 * All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2008
 */

package org.xerela.stax;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public final class STaXFactoriesElf
{
    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outputFactory;
    private static XMLEventFactory eventFactory;

    static
    {
        inputFactory = XMLInputFactory.newInstance();
        if (!inputFactory.isPropertySupported(XMLInputFactory.IS_NAMESPACE_AWARE))
        {
            throw new RuntimeException("A namespace-aware STaX parser is not available."); //$NON-NLS-1$
        }

        outputFactory = XMLOutputFactory.newInstance();

        eventFactory = XMLEventFactory.newInstance();
    }

    private STaXFactoriesElf()
    {
        // this is an Elf
    }

    /**
     * @return the one true event factory
     */
    public static XMLEventFactory getEventFactory()
    {
        return eventFactory;
    }

    /**
     * @return the one true input factory
     */
    public static XMLInputFactory getInputFactory()
    {
        return inputFactory;
    }

    /**
     * @return the one true output factory
     */
    public static XMLOutputFactory getOutputFactory()
    {
        return outputFactory;
    }

}
