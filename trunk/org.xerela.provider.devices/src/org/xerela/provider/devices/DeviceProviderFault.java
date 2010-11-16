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

import javax.xml.bind.annotation.XmlType;

/**
 * DeviceProviderFault
 */
@XmlType(name = "DeviceProviderFault")
public class DeviceProviderFault extends RuntimeException
{
    private static final long serialVersionUID = -3487073746568837520L;

    /**
     * Default constructor.
     */
    public DeviceProviderFault()
    {
        super();
    }

    /**
     * Constructor with a throwable.
     *
     * @param t a throwable
     */
    public DeviceProviderFault(Throwable t)
    {
        super(t);
    }

    /**
     * Constructor with a message.
     *
     * @param msg a message
     */
    public DeviceProviderFault(String msg)
    {
        super(msg);
    }

    /**
     * Constructor with a message and a throwable.
     *
     * @param msg a message
     * @param t a throwable
     */
    public DeviceProviderFault(String msg, Throwable t)
    {
        super(msg, t);
    }
}
