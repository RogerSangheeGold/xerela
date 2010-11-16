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

package org.xerela.provider.events.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.xerela.provider.events.EventProvider;
import org.xerela.provider.events.IEventProvider;
import org.xerela.server.security.ISecurityServiceEx;
import org.xerela.zap.security.ISecurityService;

/**
 * EventsActivator
 */
public class EventsActivator implements BundleActivator
{
    private ServiceRegistration epRegistration;
    private static ServiceTracker eventTracker;
    private static ServiceTracker securityTracker;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        securityTracker = new ServiceTracker(context, ISecurityService.class.getName(), null);
        securityTracker.open();

        EventProvider ep = new EventProvider();

        epRegistration = context.registerService(IEventProvider.class.getName(), ep, null);
        
        eventTracker = new ServiceTracker(context, IEventProvider.class.getName(), null);
        eventTracker.open();
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
        epRegistration.unregister();
        eventTracker.close();
        securityTracker.close();
    }

    /**
     * Get the IEventProvider.
     *
     * @return the IEventProvider
     */
    public static IEventProvider getEventProvider()
    {
        return (IEventProvider) eventTracker.getService();
    }

    public static ISecurityServiceEx getSecurityService()
    {
        return (ISecurityServiceEx) securityTracker.getService();
    }
}
