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

package org.xerela.provider.devices.internal;

import java.math.BigInteger;

import org.hibernate.Query;

/**
 * BaseResolutionScheme
 */
public abstract class BaseResolutionScheme
{
    /**
     * Get the count from the supplied count query.
     *
     * @param query the count query
     * @return the count
     */
    protected int getCount(Query query)
    {
        Object uniqueResult = query.uniqueResult();
        if (uniqueResult instanceof Integer)
        {
            return (Integer) uniqueResult;
        }
        else if (uniqueResult instanceof Long)
        {
            return ((Long) uniqueResult).intValue();
        }
        else
        {
            return ((BigInteger) uniqueResult).intValue();
        }
    }
}
