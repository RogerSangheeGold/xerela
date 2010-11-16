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

package org.xerela.log4j.internal;

import java.io.File;
import java.net.URI;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Log4jActivator
 */
public class Log4jActivator implements BundleActivator
{
    private static final String LOG4J_PROPERTIES = "log4j/log4j.properties"; //$NON-NLS-1$
    private static final String LOG4J_XML = "log4j/log4j.xml";
    private static final int THIRTY_SECONDS = 30000;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        String configArea = context.getProperty("osgi.configuration.area").replace(" ", "%20"); //$NON-NLS-1$
        if (configArea == null)
        {
            throw new RuntimeException("Unable to activate: osgi.configuration.area property is not defined.");
        }

        File file = new File(URI.create(configArea + LOG4J_PROPERTIES));
        if (file.exists())
        {
            String log4j = file.getAbsolutePath();
            PropertyConfigurator.configureAndWatch(log4j, THIRTY_SECONDS);
        }
        else
        {
            file = new File(URI.create(configArea + LOG4J_XML));
            DOMConfigurator.configureAndWatch(file.getAbsolutePath(), THIRTY_SECONDS);
        }
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
    }
}
