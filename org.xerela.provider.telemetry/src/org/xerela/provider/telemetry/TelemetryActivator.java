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

package org.xerela.provider.telemetry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.xerela.discovery.DiscoveryEngine;
import org.xerela.provider.devices.DeviceProvider;
import org.xerela.provider.devices.IDeviceProvider;

public class TelemetryActivator implements BundleActivator
{
    private static final String DISCOVERY_HANDLER_ID = "TELEMETRY_HANDLER";
    private static final Logger LOGGER = Logger.getLogger(TelemetryActivator.class);
    
    private DiscoveryEngine discoveryEngine;
    private static ServiceTracker dsTracker;
    private static ServiceTracker sessionTracker;
    private static ServiceTracker deviceTracker;
    private static TelemetryProvider telemetryProvider;
    
    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception
    {
        LOGGER.info(Messages.providerStarting);
        ServiceReference serviceReference = context.getServiceReference(DiscoveryEngine.class.getName());
        discoveryEngine = (DiscoveryEngine) context.getService(serviceReference);
        discoveryEngine.registerEventHandler(DISCOVERY_HANDLER_ID, new DiscoveryEventHandler());
        
        sessionTracker = new ServiceTracker(context, SessionFactory.class.getName(), null);
        sessionTracker.open();
        
        deviceTracker = new ServiceTracker(context, IDeviceProvider.class.getName(), null);
        deviceTracker.open();
        
        try
        {
            dsTracker = new ServiceTracker(context, DataSource.class.getName(), null);
            dsTracker.open();
            
            telemetryProvider = new TelemetryProvider();
        }
        catch (Exception ex)
        {
            LOGGER.error(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception
    {
        discoveryEngine.unregisterEventHandler(DISCOVERY_HANDLER_ID);
        deviceTracker.close();
        deviceTracker = null;
        dsTracker.close();
        // Close the Session service tracker and tear it down
        sessionTracker.close();
        sessionTracker = null;
        telemetryProvider = null;
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
    
    public static synchronized DataSource getDataSource()
    {
        if (dsTracker != null)
        {
            return (DataSource) dsTracker.getService();
        }
        else
        {
            throw new RuntimeException("DataSource Service Tracker has not be initialized."); //$NON-NLS-1$
        }
    }

    /**
     * @return the telemetryProvider
     */
    public static TelemetryProvider getTelemetryProvider()
    {
        return telemetryProvider;
    }
    
    /**
     * Lookup the device provider service.
     * @return The device provider instance.
     */
    public static DeviceProvider getDeviceProvider()
    {
        return (DeviceProvider) deviceTracker.getService();
    }

}
