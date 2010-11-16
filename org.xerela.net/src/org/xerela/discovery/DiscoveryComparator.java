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
 *     $Date: 2007/04/02 16:32:34 $
 * $Revision: 1.2 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/discovery/DiscoveryComparator.java,v $
 */

package org.xerela.discovery;

/**
 * Anybody placing Runnables on the <code>DiscoveryEngine</code> thread pool needs to implement
 * this interface so the different types of Runnables can have their priority determined.
 * 
 * @author rkruse
 */
interface DiscoveryComparator
{
    /**
     * Deliver a numeric value of this objects priority. This will be used to determine how it
     * compares with another {@link DiscoveryComparator} for the priority based thread pool.
     * 
     * @return
     */
    Integer getPriority();

    /**
     * If the values are equal, the user can optionally try to use this tiebreaker long to see who
     * is first. This will allow making the <code>PriorityBlockingQueue</code> a true FIFO queue
     * when all values are equal.
     * 
     * @return
     */
    Long getTieBreaker();
}
