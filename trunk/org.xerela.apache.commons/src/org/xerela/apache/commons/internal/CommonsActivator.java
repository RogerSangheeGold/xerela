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

package org.xerela.apache.commons.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * CommonsActivator
 */
public class CommonsActivator implements BundleActivator
{
    private static final String COMMONS_LOGGING_PROPERTIES = "commons-logging/commons-logging.properties"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        // Load commons logging configuration from {osgi.configuration.area}/commons/logging/commons-logging.properties
        String commonsLogging = context.getProperty("osgi.configuration.area").replace(" ", "%20"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (commonsLogging == null)
        {
            throw new RuntimeException("Unable to activate: osgi.configuration.area property is not defined."); //$NON-NLS-1$
        }

        File file = new File(URI.create(commonsLogging + COMMONS_LOGGING_PROPERTIES));
        if (file.exists())
        {
            InputStream is = new FileInputStream(file);
            System.getProperties().load(is);
            is.close();
        }
        else
        {
            System.getProperties().load(context.getBundle().getResource(COMMONS_LOGGING_PROPERTIES).openStream());
        }

        // Force commons logging initialization
        Log log = LogFactory.getLog(CommonsActivator.class);
        log.debug("Apache Commons Logging initialized using logger: " + log.getClass().toString()); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
    }
}
