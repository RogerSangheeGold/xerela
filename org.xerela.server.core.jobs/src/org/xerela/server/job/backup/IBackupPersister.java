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

import java.util.List;

import javax.xml.stream.events.XMLEvent;

import org.xerela.provider.devices.ZDeviceCore;


/**
 * IBackupPersister
 */
public interface IBackupPersister
{
    /**
     * @param device the device.
     */
    void setDevice(ZDeviceCore device);

    /**
     * @return The paths of the nodes that this persister wants to be called for.
     */
    List<String> getPathsOfInterest();

    /**
     * @param xmlEvent The StAX event.
     */
    void startDocument(XMLEvent xmlEvent);

    /**
     * @param xmlEvent The StAX event.
     */
    void endDocument(XMLEvent xmlEvent);

    /**
     * @param xmlEvent The StAX event.
     */
    void startElement(XMLEvent xmlEvent);

    /**
     * @param xmlEvent The StAX event.
     */
    void endElement(XMLEvent xmlEvent);

    /**
     * @param xmlEvent The StAX event.
     */
    void characterData(XMLEvent xmlEvent);

    /**
     * We guarantee that cleanup will always be called after endDocument() or
     * in the event of an exception in processing.
     */
    void cleanup();
}
