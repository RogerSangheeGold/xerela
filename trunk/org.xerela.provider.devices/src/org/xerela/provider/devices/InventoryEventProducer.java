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

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.xerela.zap.jms.EventElf;

/**
 * Produces events for device changes.
 */
@SuppressWarnings("nls")
public class InventoryEventProducer implements IDeviceStoreObserver
{
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String QUEUE = "devices";

    /** {@inheritDoc} */
    public void deviceCreated(ZDeviceCore device)
    {
        Properties props = new Properties();
        props.setProperty("IpAddress", device.getIpAddress());
        props.setProperty("ManagedNetwork", device.getManagedNetwork());

        sendEvent("created", props);
    }

    /** {@inheritDoc} */
    public void deviceDeleted(ZDeviceCore device)
    {
        Properties props = new Properties();
        props.setProperty("IpAddress", device.getIpAddress());
        props.setProperty("ManagedNetwork", device.getManagedNetwork());

        sendEvent("deleted", props);
    }


    /** {@inheritDoc} */
    public void deviceTypeChanged(ZDeviceCore device)
    {
    }

    private void sendEvent(String type, Properties properties)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            properties.storeToXML(baos, "", UTF_8_ENCODING); //$NON-NLS-1$

            // Tell the producer to send the message
            TextMessage message = EventElf.createTextMessage(QUEUE, baos.toString(UTF_8_ENCODING));
            message.setJMSType(type);
            EventElf.sendMessage(QUEUE, message);
        }
        catch (Exception e)
        {
            Logger.getLogger(getClass()).error("Unable to send JMS event", e);
        }
    }
}
