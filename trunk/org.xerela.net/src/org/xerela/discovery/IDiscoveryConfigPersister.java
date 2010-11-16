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
 * $Revision: 1.3 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/discovery/IDiscoveryConfigPersister.java,v $e
 */

package org.xerela.discovery;

import org.xerela.exception.PersistenceException;

/**
 * This can be provided to the {@link DiscoveryEngine} startup if you want to persist the {@link DiscoveryConfig}.<br>
 * <br>
 * The <code>DiscoveryEngine</code> will cache the <code>DiscoveryConfig</code> and only call load and save when
 * necessary. So there is no need to make an implementation of this Interface high performing.
 * 
 * @author rkruse
 */
public interface IDiscoveryConfigPersister
{
    /**
     * Load up the {@link DiscoveryConfig} from the data store.
     * 
     * @return a discovery configuration
     */
    DiscoveryConfig loadDiscoveryConfig();

    /**
     * Save the details of the {@link DiscoveryConfig}
     * @param discoveryConfig the discovery configuration to save
     * 
     * @throws PersistenceException then if there is an error persisting the configuration
     */
    void saveDiscoveryConfig(DiscoveryConfig discoveryConfig) throws PersistenceException;
}
