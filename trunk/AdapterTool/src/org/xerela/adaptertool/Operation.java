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

package org.xerela.adaptertool;

/**
 * Supported adapter operations
 * Operations
 */
public enum Operation
{
    /**
     * backup is the primary and default adapter operation
     */
    backup,

    /**
     * Restore is the adapter's way of moving files from the server to the device
     */
    restore,

    /**
     * Commands is an abstract way of executing any series of commands on a device
     */
    commands,

    /**
     * Pull an operating system image or images from the network device
     */
    ospull,

    /**
     * Create a DiscoveryEvent via an adapter operation
     */
    telemetry,
}
