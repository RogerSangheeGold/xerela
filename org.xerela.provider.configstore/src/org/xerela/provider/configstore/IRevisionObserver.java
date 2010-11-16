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

package org.xerela.provider.configstore;

import java.util.List;

import org.xerela.provider.devices.ZDeviceCore;

/**
 * IRevisionObserver
 */
public interface IRevisionObserver
{
    /**
     * Called on the implementer of this interface to inform them of
     * a revision change.
     *
     * @param device the device whose revision(s) changed
     * @param configs a list of ConfigHolder objects reflecting changes
     */
    void revisionChange(ZDeviceCore device, List<ConfigHolder> configs);
}
