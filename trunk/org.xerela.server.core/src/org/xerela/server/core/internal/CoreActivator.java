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

package org.xerela.server.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class CoreActivator implements BundleActivator
{
    private static ServiceTracker bundleTracker;

    public void start(BundleContext context) throws Exception
    {
        bundleTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        bundleTracker.open();
    }

    public void stop(BundleContext context) throws Exception
    {
        bundleTracker.close();
    }

    public static PackageAdmin getPackageAdmin()
    {
        return (PackageAdmin) bundleTracker.getService();
    }
}
