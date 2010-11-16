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

package org.xerela.server.job.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.xerela.net.adapters.AdapterMetadata;
import org.xerela.provider.devices.ServerDeviceElf;
import org.xerela.provider.devices.ZDeviceCore;
import org.xerela.provider.devices.ZDeviceStatus;
import org.xerela.server.job.internal.CoreJobsActivator;

/**
 * DeviceStatusBackupPersister
 */
public class DeviceStatusBackupPersister implements IBackupPersister
{
    private static List<String> pathsOfInterest;
    private static Map<String, State> localName2StateMap;

    private AdapterMetadata adapterMetadata;
    private ZDeviceStatus deviceStatus;

    private State state;
    private StringBuilder charData;

    static
    {
        pathsOfInterest = new ArrayList<String>();
        pathsOfInterest.add("/XerelaElementDocument/biosVersion"); //$NON-NLS-1$
        pathsOfInterest.add("/XerelaElementDocument/systemName"); //$NON-NLS-1$
        pathsOfInterest.add("/XerelaElementDocument/deviceType"); //$NON-NLS-1$
        pathsOfInterest.add("/XerelaElementDocument/osInfo/version"); //$NON-NLS-1$
        pathsOfInterest.add("/XerelaElementDocument/chassis/asset/factoryinfo/make"); //$NON-NLS-1$
        pathsOfInterest.add("/XerelaElementDocument/chassis/asset/factoryinfo/modelNumber"); //$NON-NLS-1$
        pathsOfInterest.add("/XerelaElementDocument/chassis/asset/factoryinfo/serialNumber"); //$NON-NLS-1$

        localName2StateMap = new HashMap<String, State>();
        localName2StateMap.put("biosVersion", State.HW_VERSION); //$NON-NLS-1$
        localName2StateMap.put("systemName", State.SYSTEM_NAME); //$NON-NLS-1$
        localName2StateMap.put("deviceType", State.DEVICE_TYPE); //$NON-NLS-1$
        localName2StateMap.put("version", State.SW_VERSION); //$NON-NLS-1$
        localName2StateMap.put("make", State.HW_VENDOR); //$NON-NLS-1$
        localName2StateMap.put("modelNumber", State.MODEL); //$NON-NLS-1$
        localName2StateMap.put("serialNumber", State.ASSET_IDENTITY); //$NON-NLS-1$
    }

    /**
     * Default constructor.
     */
    public DeviceStatusBackupPersister()
    {
        state = State.NONE;
        charData = new StringBuilder();
        deviceStatus = new ZDeviceStatus();
    }

    /** {@inheritDoc} */
    public void setModelFile(File file)
    {
        // nothing to do.
    }

    /** {@inheritDoc} */
    public void setDevice(ZDeviceCore device)
    {
        // We're ALWAYS called within the context of a transaction.
        SessionFactory factory = CoreJobsActivator.getSessionFactory();
        Session session = factory.getCurrentSession();

        deviceStatus = (ZDeviceStatus) session.get(ZDeviceStatus.class, device.getDeviceId());
        if (deviceStatus == null)
        {
            deviceStatus = new ZDeviceStatus();
            deviceStatus.setDeviceId(device.getDeviceId());
        }

        adapterMetadata = CoreJobsActivator.getAdapterService().getAdapterMetadata(device.getAdapterId());
    }

    /** {@inheritDoc} */
    public void startDocument(XMLEvent xmlEvent)
    {
    }

    /** {@inheritDoc} */
    public void startElement(XMLEvent xmlEvent)
    {
        StartElement element = xmlEvent.asStartElement();
        String localPart = element.getName().getLocalPart();
        state = localName2StateMap.get(localPart);
        if (state == null)
        {
            state = State.NONE;
        }
    }

    /** {@inheritDoc} */
    public void characterData(XMLEvent xmlEvent)
    {
        Characters characters = xmlEvent.asCharacters();
        if (characters.isIgnorableWhiteSpace())
        {
            return;
        }

        charData.append(characters.getData());
    }

    /** {@inheritDoc} */
    public void endElement(XMLEvent xmlEvent)
    {
        switch (state)
        {
        case SW_VERSION:
            deviceStatus.setOsVersion(charData.toString());
            if (adapterMetadata != null)
            {
                deviceStatus.setCanonicalOsVersion(ServerDeviceElf.computeCononicalVersion(charData.toString(), adapterMetadata.getSoftwareVersionRegEx()));
            }
            break;
        case HW_VERSION:
            deviceStatus.setHwVersion(charData.toString());
            if (adapterMetadata != null)
            {
                deviceStatus.setCanonicalHwVersion(ServerDeviceElf.computeCononicalVersion(charData.toString(), adapterMetadata.getBiosVersionRegEx()));
            }
            break;
        case SYSTEM_NAME:
            deviceStatus.setHostname(charData.toString());
            break;
        case MODEL:
            deviceStatus.setModel(charData.toString());
            break;
        case HW_VENDOR:
            deviceStatus.setHardwareVendor(charData.toString());
            break;
        case SW_VENDOR:
            deviceStatus.setSoftwareVendor(charData.toString());
            break;
        case ASSET_IDENTITY:
            deviceStatus.setAssetIdentity(charData.toString());
            break;
        case DEVICE_TYPE:
            deviceStatus.setDeviceType(charData.toString());
            break;
        default:
            break;
        }

        charData.setLength(0);
        state = State.NONE;
    }

    /** {@inheritDoc} */
    public void endDocument(XMLEvent xmlEvent)
    {
        if (deviceStatus.getSoftwareVendor() == null)
        {
            deviceStatus.setSoftwareVendor(deviceStatus.getHardwareVendor());
        }

        SessionFactory factory = CoreJobsActivator.getSessionFactory();
        Session session = factory.getCurrentSession();
        session.saveOrUpdate(deviceStatus);
    }

    /** {@inheritDoc} */
    public void cleanup()
    {
    }

    /** {@inheritDoc} */
    public List<String> getPathsOfInterest()
    {
        return pathsOfInterest;
    }

    // ----------------------------------------------------------------------
    //                            Private Enums
    // ----------------------------------------------------------------------

    /**
     * State
     */
    private enum State
    {
        NONE,
        HW_VENDOR,
        HW_VERSION,
        SW_VENDOR,
        SW_VERSION,
        MODEL,
        ASSET_IDENTITY,
        SYSTEM_NAME,
        DEVICE_TYPE,
    }
}
