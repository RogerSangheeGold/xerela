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

package org.xerela.server.security.internal;

import org.hibernate.SessionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.xerela.server.security.ISecurityServiceEx;
import org.xerela.server.security.SecurityService;
import org.xerela.zap.security.ISecurityService;
import org.xerela.zap.web.IWebService;

/**
 * The activator class controls the plug-in life cycle
 */
public class SecurityActivator implements BundleActivator
{
    private static SecurityActivator theOneTrueActivator;
    private static BundleContext context;
    private String bootstrapVersion;
    private SecurityService service;
    private ServiceRegistration registration;

    /** {@inheritDoc} */
    public void start(BundleContext ctx) throws Exception
    {
        theOneTrueActivator = this;

        context = ctx;
        service = new SecurityService();
        registration = context.registerService(ISecurityService.class.getName(), service, null);
    }

    /** {@inheritDoc} */
    public void stop(BundleContext ctx) throws Exception
    {
        theOneTrueActivator = null;

        service = null;
        registration.unregister();
    }

    /**
     * Get the version of the bootstrap bundle.
     *
     * @return the bootstrap version
     */
    public static synchronized String getBootstrapVersion()
    {
        if (theOneTrueActivator.bootstrapVersion == null)
        {
            Bundle[] bundles = context.getBundles();
            for (Bundle bundle : bundles)
            {
                if (bundle.getSymbolicName().contains("bootstrap")) //$NON-NLS-1$
                {
                    theOneTrueActivator.bootstrapVersion = (String) bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
                    break;
                }
            }
        }

        return theOneTrueActivator.bootstrapVersion;
    }

    /**
     * Get the ISecurityService impl. reference.
     *
     * @return the ISecurityService
     */
    public static ISecurityServiceEx getSecurityService()
    {
        return theOneTrueActivator.service;
    }

    /**
     * Get the IWebService implementation.
     *
     * @return the IWebService implementation
     */
    public static IWebService getWebService()
    {
        ServiceReference serviceReference = context.getServiceReference(IWebService.class.getName());
        return (IWebService) context.getService(serviceReference);
    }

    /**
     * Get the Hibernate session factory.
     *
     * @return the SessionFactory
     */
    public static SessionFactory getSessionFactory()
    {
        ServiceReference serviceReference = context.getServiceReference(SessionFactory.class.getName());
        return (SessionFactory) context.getService(serviceReference);
    }
}
