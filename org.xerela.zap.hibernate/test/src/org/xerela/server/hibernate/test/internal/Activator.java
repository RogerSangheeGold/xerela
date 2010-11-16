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

package org.xerela.server.hibernate.test.internal;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xerela.server.hibernate.test.TestAll;
import junit.framework.TestSuite;
import junit.framework.TestResult;

/**
 * Activator.
 */
public class Activator implements BundleActivator
{
    private static Logger USER_LOG = Logger.getLogger(Activator.class);

    /**
     * Default constructor.
     */
    public Activator()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bundleContext) throws Exception
    {
        USER_LOG.info("HibernateBundle test starting...");
        try
        {
            TestAll.setBundleContext(bundleContext);
            TestSuite suite = TestAll.suite();
            TestResult result = junit.textui.TestRunner.run(suite);

            if (result.wasSuccessful())
            {
                USER_LOG.info("HibernateBundle test finished.");
            }
            else
            {
                USER_LOG.error("HibernateBundle test failed.");
            }
        }
        catch (Exception e)
        {
            USER_LOG.fatal("HibernateBundle test failed to start.", e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext bundleContext) throws Exception
    {
    }
}
