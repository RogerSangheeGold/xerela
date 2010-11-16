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

package org.xerela.provider.launchers;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class LaunchersActivator implements BundleActivator
{
    private static final Logger LOGGER = Logger.getLogger(LaunchersActivator.class);
    
    private static ServiceTracker sessionTracker;
    private static LaunchersProvider launchersProvider;
    
    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception
    {
        LOGGER.info("Launchers provider starting...");
        
        sessionTracker = new ServiceTracker(context, SessionFactory.class.getName(), null);
        sessionTracker.open();
        
        launchersProvider = new LaunchersProvider();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception
    {
        // Close the Session service tracker and tear it down
        sessionTracker.close();
        sessionTracker = null;
        launchersProvider = null;
    }
    
    /**
     * Lookup the session factory service.
     * 
     * @return The session factory instance.
     */
    public static SessionFactory getSessionFactory()
    {
        return (SessionFactory) sessionTracker.getService();
    }
    
    /**
     * @return the telemetryProvider
     */
    public static LaunchersProvider getLaunchersProvider()
    {
        return launchersProvider;
    }
    
}
