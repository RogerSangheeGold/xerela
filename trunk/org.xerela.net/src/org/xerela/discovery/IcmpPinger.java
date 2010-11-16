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
 *   $Author: brettw $
 *     $Date: 2007/07/21 20:38:56 $
 * $Revision: 1.4 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/discovery/IcmpPinger.java,v $e
 */

package org.xerela.discovery;

import java.util.concurrent.atomic.AtomicLong;

import org.xerela.addressing.IPAddress;
import org.xerela.net.ping.icmp.Pinger;

/**
 * On Windows systems we shell out to the "ping.exe" program for determining device reachability.
 * 
 * @author rkruse
 */
class IcmpPinger extends AbstractPinger
{
    private AtomicLong tieBreaker;
    /**
     * @param engine
     */
    public IcmpPinger(DiscoveryEngine engine)
    {
        super(engine);
        tieBreaker = new AtomicLong();
    }

    /**
     * @see org.xerela.discovery.AbstractPinger#ping(org.xerela.addressing.NetworkAddress, boolean,
     *      boolean)
     */
    @Override
    void ping(IPAddress ipAddress, boolean fromInventorySource, boolean extendUsingNeighbors)
    {
        if (isRunning())
        {
            super.ping(ipAddress, fromInventorySource, extendUsingNeighbors);
            Runnable pingRunner = new PingRunnable(ipAddress, fromInventorySource, extendUsingNeighbors);
            getEngine().executeRunnable(pingRunner);
        }
    }

    /**
     * All pings are send to a thread pool outside of this class, so this pinger is never busy.
     * 
     * @see org.xerela.discovery.AbstractPinger#getActivePings()
     */
    @Override
    int getActivePings()
    {
        return 0;
    }

    /**
     * Since each ping is synchronous we let the master thread pool in the {@link DiscoveryEngine}
     * do the work.
     * 
     * @author rkruse
     */
    private class PingRunnable implements Runnable, Comparable<DiscoveryComparator>, DiscoveryComparator
    {
        private IPAddress ipAddress;
        private boolean fromInventorySource;
        private boolean extendUsingNeighbors;
        private Long localTieBreaker;

        /**
         * 
         * @param ipAddress
         * @param fromInventorySource
         * @param extendUsingNeighbors
         */
        public PingRunnable(IPAddress ipAddress, boolean fromInventorySource, boolean extendUsingNeighbors)
        {
            this.ipAddress = ipAddress;
            this.fromInventorySource = fromInventorySource;
            this.extendUsingNeighbors = extendUsingNeighbors;
            this.localTieBreaker = tieBreaker.getAndIncrement();
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @SuppressWarnings("nls")
        public void run()
        {
            Thread runner = Thread.currentThread();
            runner.setName(ipAddress.getIPAddress() + "-" + THREAD_NAME);
            DiscoveryConfig dc = getEngine().getDiscoveryConfig();
            boolean isAlive = Pinger.getInstance().ping(ipAddress, dc.getPingTimeout(), dc.getPingSize(), dc.getPingCount());

            if (isAlive)
            {
                onSuccess(ipAddress, fromInventorySource, extendUsingNeighbors);
            }
            else
            {
                onFailure(ipAddress);
            }
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(DiscoveryComparator other)
        {
            return DiscoveryElf.compare(this, other);
        }

        /**
         * @see org.xerela.discovery.DiscoveryComparator#getPriority()
         */
        public Integer getPriority()
        {
            return DiscoveryEngine.PING_PRIORITY;
        }

        /**
         * @see org.xerela.discovery.DiscoveryComparator#getTieBreaker()
         */
        public Long getTieBreaker()
        {
            return localTieBreaker;
        }
    }

}

