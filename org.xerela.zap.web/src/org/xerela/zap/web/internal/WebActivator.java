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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.xerela.zap.security.ISecurityService;
import org.xerela.zap.web.IWebService;
import org.xerela.zap.web.WebService;

/**
 * WebActivator
 */
@SuppressWarnings("nls")
public class WebActivator implements BundleActivator
{
    private static final Logger LOGGER = Logger.getLogger(WebActivator.class);

    private static BundleContext context;

    private IExtensionRegistry extensionRegistry;

    private WebService webService;

    private ServiceRegistration registeredService;

    /** {@inheritDoc} */
    public void start(BundleContext ctx) throws Exception
    {
        LOGGER.info("Starting web container...");

        WebActivator.context = ctx;

        webService = new WebService(context);

        extensionRegistry = Platform.getExtensionRegistry();
        extensionRegistry.addRegistryChangeListener(webService, WebService.EXTENSION_NAMESPACE);

        String extension = WebService.EXTENSION_NAMESPACE + "." + WebService.EXTENSION_POINT_ID;
        IConfigurationElement[] configElements = extensionRegistry.getConfigurationElementsFor(extension);
        for (IConfigurationElement element : configElements)
        {
            webService.register(element);
        }

        registeredService = context.registerService(IWebService.class.getName(), webService, null);

        webService.start();

        LOGGER.info("Web container started.");
    }

    /** {@inheritDoc} */
    public void stop(BundleContext ctx) throws Exception
    {
        webService.stop();
        registeredService.unregister();
        LOGGER.info("Web container shutdown.");
    }

    /**
     * Get the registered ISecurityService if it exists.
     *
     * @return the registered ISecurityService if it exists, or null
     */
    public static ISecurityService getSecurityService()
    {
        ServiceReference serviceReference = context.getServiceReference(ISecurityService.class.getName());
        if (serviceReference != null)
        {
            return (ISecurityService) context.getService(serviceReference);
        }

        return null;
    }
}
