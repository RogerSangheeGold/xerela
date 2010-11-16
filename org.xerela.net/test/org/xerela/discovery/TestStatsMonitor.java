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

/* Alterpoint, Inc.
 *
 * The contents of this source code are proprietary and confidential
 * All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2006
 *
 *   $Author: rkruse $
 *     $Date: 2008/09/02 16:43:01 $
 * $Revision: 1.4 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/test/org/xerela/discovery/TestStatsMonitor.java,v $e
 */

package org.xerela.discovery;

import java.util.Date;

/**
 * Implementation of the {@link AbstractBaseStatsMonitor} for testing.
 * 
 * @author rkruse
 */
public final class TestStatsMonitor extends AbstractBaseStatsMonitor
{
    private static TestStatsMonitor instance;

    /**
     * Private constructor
     */
    private TestStatsMonitor()
    {
        super();
    }

    /**
     * Retrieve <i>THE</i> instance of the <code>TestStatsMonitor</code>.
     * Starts a new monitor if it hasn't already been started.
     * 
     * @return the monitor
     */
    public static synchronized TestStatsMonitor getInstance()
    {
        if (instance == null)
        {
            instance = new TestStatsMonitor();
        }
        return instance;
    }

    /**
     * @see com.alterpoint.discovery.AbstractBaseStatsMonitor#logIfIdle(org.xerela.discovery.DiscoveryStatus)
     */
    protected void logIfIdle(DiscoveryStatus newStats)
    {
        if (!newStats.isActive() && getLatestDiscoveryStatus().isActive())
        {
            String timeDiff = TimeElf.getDuration(newStats.getStartedRunning(), new Date());
            String logMessage = "The Discovery Engine went idle after " + timeDiff + ". "
                    + newStats.getAddressesAnalyzed() + " total addresses analyzed, " + newStats.getRespondedToSnmp()
                    + " were SNMP enabled.";
            System.out.println(logMessage);
        }
    }

    /**
     * Create an event saying that there is a new {@link DiscoveryStatus} object
     * to get off the bean.
     * 
     * @param newStats
     */
    protected void processJmsAlerts(DiscoveryStatus newStats)
    {
        System.out.println("****STAT UPDATE****\n" + newStats.toString());
    }
}
