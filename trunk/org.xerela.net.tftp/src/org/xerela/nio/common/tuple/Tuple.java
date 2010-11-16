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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xerela.nio.common.AkinFields;
import org.xerela.nio.common.StringFactory;


public abstract class Tuple
{

    // -- fields
    final List<Object> fields = new LinkedList<Object>();

    // -- constructors
    Tuple()
    {
        // do nothing
    }

    // -- public methods
    @Override
    public String toString()
    {
        // {username: testlab, password: hobbit, authMethod: password, cipher: null, clientVersion: default}
        String string = "(";
        string += listBasedPart(fields, new ToStringFactory(), ")");
        return string;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (Object field : fields)
        {
            hashCode = hashCode * 31 + (field == null ? 0 : field.hashCode());
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object them)
    {
        final boolean areEqual;
        if (them == null)
        {
            areEqual = false;
        }
        else if (this == them)
        {
            areEqual = true;
        }
        else
        {
            areEqual = castAndEqualFields(them);
        }
        return areEqual;
    }

    // -- private methods
    private static String listBasedPart(List list, StringFactory factory, String terminator)
    {
        String string = "";
        final String separator = ", ";
        for (Object elem : list)
        {
            string += factory.create(elem) + separator;
        }
        string = string.substring(0, string.length() - separator.length());
        string += terminator;
        return string;
    }

    private static List<AkinFields> zipFields(Iterator myFields, Iterator theirFields)
    {
        List<AkinFields> list = new LinkedList<AkinFields>();
        while (myFields.hasNext() && theirFields.hasNext())
        {
            list.add(new AkinFields(myFields.next(), theirFields.next()));
        }
        return list;
    }

    private boolean equalFields(List theirFields)
    {
        boolean areEqual = true;
        for (AkinFields akinFields : zipFields(fields.iterator(), theirFields.iterator()))
        {
            if (!(null == akinFields.mine() ? null == akinFields.theirs() : akinFields.mine().equals(akinFields.theirs())))
            {
                areEqual = false;
                break;
            }
        }
        return areEqual;
    }

    private boolean castAndEqualFields(Object them)
    {
        try
        {
            return equalFields(((Tuple) them).fields);
        }
        catch (ClassCastException cce)
        {
            return false;
        }
    }

    // -- inner classes
    private static class ToStringFactory implements StringFactory
    {
        public String create(Object obj)
        {
            return null == obj ? "null" : obj.toString();
        }
    }

}
