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

package org.xerela.zap.hibernate.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * An overridable resource representing a single 'resource' from the
 * PersistenceUnit extension point.
 */
class OverridableResource
{
    private URL resource;
    private Map<String, URL> dialectToResourceMap;

    OverridableResource(URL resource)
    {
        this.resource = resource;
    }

    URL getResource(String dialect)
    {
        URL rval = null;
        if (dialectToResourceMap != null)
        {
            rval = dialectToResourceMap.get(dialect);
        }

        if (rval == null)
        {
            rval = resource;
        }

        return rval;
    }

    void addOverride(String dialect, URL rsrc)
    {
        if (dialectToResourceMap == null)
        {
            dialectToResourceMap = new HashMap<String, URL>();
        }

        dialectToResourceMap.put(dialect, rsrc);
    }

    /** {@inheritDoc} for debugging */
    public String toString()
    {
        return "OverridableResource URL=" + resource + " dialectToResourceMap=" + dialectToResourceMap;
    }
}
