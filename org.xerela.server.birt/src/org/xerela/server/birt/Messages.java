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

package org.xerela.server.birt;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.xerela.server.birt.messages"; //$NON-NLS-1$

    public static String ReportJob_badAddresses;

    public static String ReportJob_emailSubject;

    public static String ReportJob_emptyAddresses;

    public static String ReportJob_errorSending;

    public static String ReportJob_noFormat;

    public static String ReportJob_reportDefinitionNotFound;

    public static String ReportJob_reportJobFinished;

    public static String ReportJob_reportPersistFailure;

    public static String ReportJob_startingReportJob;

    public static String ReportPluginManager_errorReadingReport;
    public static String ReportPluginManager_definitionNotFound;

    public static String ReportPluginManager_discoveredReport;

    public static String ReportPluginManager_pluginTypeDisplayName;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
