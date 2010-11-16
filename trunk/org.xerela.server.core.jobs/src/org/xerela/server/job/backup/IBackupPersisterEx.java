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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * IBackupPersisterEx
 */
public interface IBackupPersisterEx extends IBackupPersister
{
    /**
     * If a class implementing IBackupPersister also implements this interface,
     * then this method will be invoked for every event encountered by the STaX
     * processor.
     *
     * @param xmlEvent a STaX XMLEvent
     * @throws XMLStreamException thrown if there is a processing error
     */
    void handleEvent(XMLEvent xmlEvent)  throws XMLStreamException;
}
