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

package org.xerela.provider.security.internal;

import org.hibernate.SessionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.xerela.provider.security.ISecurityProvider;
import org.xerela.provider.security.SecurityProvider;
import org.xerela.server.security.ISecurityServiceEx;
import org.xerela.zap.security.ISecurityService;

public class SecurityProviderActivator implements BundleActivator
{

    private static SecurityProviderActivator theOneTrueActivator;
    private static BundleContext context;
    private ISecurityProvider securityProvider;
    private ServiceRegistration providerRegistration;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        SecurityProviderActivator.context = context;

        theOneTrueActivator = this;

        securityProvider = new SecurityProvider();
        providerRegistration = context.registerService(ISecurityProvider.class.getName(), securityProvider, null);
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
        providerRegistration.unregister();
    }

    /**
     * Get the ISecurityProvider implementation.
     * 
     * @return the ISecurityProvider implementation
     */
    public static ISecurityProvider getSecurityProvider()
    {
        return theOneTrueActivator.securityProvider;
    }

    /**
     * Get the SessionFactory.
     *
     * @return the SessionFactory
     */
    public static SessionFactory getSessionFactory()
    {
        ServiceReference serviceReference = context.getServiceReference(SessionFactory.class.getName());
        return (SessionFactory) context.getService(serviceReference);
    }

    /**
     * Get the security service.
     *
     * @return the ISecurityServiceEx implementation
     */
    public static ISecurityServiceEx getSecurityService()
    {
        ServiceReference serviceReference = context.getServiceReference(ISecurityService.class.getName());
        return (ISecurityServiceEx) context.getService(serviceReference);        
    }
}
