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

package org.xerela.zap.web.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.PathMap;
import org.mortbay.jetty.servlet.PathMap.Entry;
import org.mortbay.resource.Resource;
import org.osgi.framework.Bundle;

/**
 * ZResourceHandler
 */
public class ZResourceHandler extends ResourceHandler
{
    private PathMap pathMap;
    /**
     * Constructor
     */
    public ZResourceHandler()
    {
        pathMap = new PathMap(true);
    }

    /**
     * Add a PathSpec/Bundle mapping.
     *
     * @param pathSpec the pathspec
     * @param bundle the bundle
     */
    @SuppressWarnings("unchecked")
    public void addBundle(String pathSpec, Bundle bundle)
    {
        Entry match = pathMap.getMatch(pathSpec);
        if (match != null)
        {
            List<Bundle> bundles = (List<Bundle>) match.getValue();
            bundles.add(bundle);
            return;
        }

        LinkedList<Bundle> bundles = new LinkedList<Bundle>();
        bundles.add(bundle);
        pathMap.put(pathSpec, bundles);
    }

    /**
     * Remove a PathSpec/Bundle mapping.
     *
     * @param pathSpec the pathspec
     * @param bundle the bundle
     */
    @SuppressWarnings("unchecked")
    public void removeBundle(String pathSpec, Bundle bundle)
    {
        Entry match = pathMap.getMatch(pathSpec);
        if (match != null)
        {
            List<Bundle> candidates = (List<Bundle>) match.getValue();
            candidates.remove(bundle);
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Resource getResource(String resource) throws MalformedURLException
    {
        List<Bundle> candidates = (List<Bundle>) pathMap.match(resource);
        if (candidates != null)
        {
            for (Bundle bundle : candidates)
            {
                URL entry = bundle.getEntry(resource);
                if (entry != null)
                {
                    try
                    {
                        return Resource.newResource(entry);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return null;
    }
}
