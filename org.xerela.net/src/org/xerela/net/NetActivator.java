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

package org.xerela.net;

import java.io.File;
import java.net.URI;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.xerela.net.common.NILProperties;
import org.xerela.net.snmp.TrapSender;

/**
 * Activator for the org.xerela.net bundle.
 */
public class NetActivator implements BundleActivator
{
    private TrapSender trapSender;
    private ServiceRegistration trapSenderRegistration;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        setupNilProperties(context);
        trapSender = TrapSender.getInstance();
        trapSenderRegistration = context.registerService(TrapSender.class.getName(), trapSender, null);
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
        NILProperties.reset();
        trapSender.shutdown();
        trapSender = null;
        trapSenderRegistration.unregister();
    }

    /**
     * Setup the NILProperties prop handler
     *  
     * @param context
     */
    private void setupNilProperties(BundleContext context)
    {
        String configArea = context.getProperty("osgi.configuration.area").replace(" ", "%20"); //$NON-NLS-1$
        configArea += (configArea != null ? "/network/" + NILProperties.NIL_PROPERTIES : "osgi-config/network/" + NILProperties.NIL_PROPERTIES); //$NON-NLS-1$
        File file = new File(URI.create(configArea));
        NILProperties.setup(file);
    }
}
