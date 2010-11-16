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
 *     $Date: 2008/08/04 15:36:00 $
 * $Revision: 1.6 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/discovery/DiscoveryElf.java,v $
 */

package org.xerela.discovery;

/**
 * Easy Little Functions for discovery
 * 
 * @author rkruse
 */
public final class DiscoveryElf
{
    /**
     * Private constructor for the <code>DiscoveryElf</code> class to disable support of a public default constructor.
     *
     */
    private DiscoveryElf()
    {
        // Does nothing.
    }

    /**
     * This is a special comparator for a <code>PritorityQueue</code> used within discovery. We
     * want to make the queue act as a pure FIFO queue when tasks have an equal priority. The
     * problem is that the <code>PriorityQueue</code> arbitrarily chooses a head of the queue when
     * the comparators are equals. In reality if all tasks are equal, the PritorityQueue effectively
     * becomes a LIFO queue, instead of the desired FIFO queue.
     * 
     * This method will do a standard compare unless they are equal, then it will do a special compare.
     * 
     * @param one the first Comparator
     * @param two the second Comparator
     * @return -1 if one is less than two, 0 if they are equal, 1 if one is greater than 2
     */
    public static int compare(DiscoveryComparator one, DiscoveryComparator two)
    {
        if (one.getPriority().equals(two.getPriority()))
        {
            return one.getTieBreaker().compareTo(two.getTieBreaker());
        }
        else
        {
            return one.getPriority().compareTo(two.getPriority());
        }
    }

    /**
     * Reconcile any missing fields from the <code>DiscoveryEvent</code> that can be populated with data from the <code>DiscoveryHost</code>
     * @param host the incoming host
     * @param event the event coming out
     */
    public static void cleanUpEvent(DiscoveryHost host, DiscoveryEvent event)
    {
        event.setExtendUsingNeighbors(host.isExtendUsingNeighbors());
        if (event.getAddress() == null)
        {
            event.setAddress(host.getIpAddress());
        }
        /*
         * Check to see if there is system data on the host, probably from a
         * LLDP or CDP neighbor
         */
        if (!event.isGoodEvent() && (host.isFromXdp()))
        {
            event.setGoodEvent(true);
            XdpEntry xdp = host.getXdpEntry();
            event.setSysOID(xdp.getSysOid());
            event.setSysName(xdp.getSysName());
            event.setSysDescr(xdp.getSysDescr());
        }
    }
}
