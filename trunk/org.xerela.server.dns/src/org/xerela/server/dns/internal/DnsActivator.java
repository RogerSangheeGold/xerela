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

package org.xerela.server.dns.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.xerela.server.dns.DnsService;
import org.xerela.server.dns.IDnsService;

/**
 * DnsActivator
 */
public class DnsActivator implements BundleActivator
{
    private ServiceRegistration serviceRegistration;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        DnsService dnsService = new DnsService();

        serviceRegistration = context.registerService(IDnsService.class.getName(), dnsService, null);
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception
    {
        serviceRegistration.unregister();
    }
}
