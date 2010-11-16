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

package org.xerela.provider.devices;

/**
 * IDeviceStoreObserver
 */
public interface IDeviceStoreObserver
{
    /**
     * Called by the Device Provider when a device is created.
     *
     * @param device the created device
     */
    void deviceCreated(ZDeviceCore device);

    /**
     * Called by the Device Provider when a device is deleted.
     *
     * @param device the deleted device
     */
    void deviceDeleted(ZDeviceCore device);

    /**
     * Called by the Device Provider when a device type (adapter) is changed.
     * 
     * @param device the changed device
     */
    void deviceTypeChanged(ZDeviceCore device);
}
