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

package org.xerela.server.update.devices.internal;


import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.hibernate.Session;
import org.xerela.provider.update.ISummaryBuilder;

/**
 * Provides a summary of the inventory for the ZipForge.
 */
public class InventorySummaryBuilder implements ISummaryBuilder
{
    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public void buildSummary(XMLStreamWriter writer) throws XMLStreamException
    {
        Session session = UpdateDevicesActivator.getSessionFactory().getCurrentSession();

        // Device Count...
        writer.writeStartElement("deviceCount");
        writer.writeCharacters(session.createQuery("SELECT count(d.deviceId) FROM ZDeviceLite d").uniqueResult().toString());
        writer.writeEndElement();

        // Hardware Vendor Counts...
        writer.writeStartElement("vendors");
        List<?> list = session.createQuery("SELECT d.hardwareVendor, count(d.hardwareVendor) FROM ZDeviceLite d GROUP BY d.hardwareVendor").list();
        for (Object object : list)
        {
            Object[] result = (Object[]) object;
            if (result[1].equals(0L))
            {
                continue;
            }

            writer.writeStartElement("vendor");
            writer.writeAttribute("name", result[0] == null ? "" : result[0].toString());
            writer.writeAttribute("count", result[1].toString());
            writer.writeEndElement();
        }
        writer.writeEndElement();

        // Adapter Counts...
        writer.writeStartElement("adapters");
        list = session.createQuery("SELECT d.adapterId, count(d.adapterId) FROM ZDeviceLite d GROUP BY d.adapterId").list();
        for (Object object : list)
        {
            Object[] result = (Object[]) object;

            writer.writeStartElement("adapter");
            writer.writeAttribute("id", result[0].toString());
            writer.writeAttribute("count", result[1].toString());
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }
}
