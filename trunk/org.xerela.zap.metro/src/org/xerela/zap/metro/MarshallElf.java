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

package org.xerela.zap.metro;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 * MarshallElf.  Helps to serialize objects using JAXB.
 */
public final class MarshallElf
{
    /**
     * Private constructor
     */
    private MarshallElf()
    {
        // private constructor
    }

    /**
     * Create a JAXB string from an object.
     *
     * @param object the object to marshal
     * @return the marshalled object string
     */
    public static String createJaxbObjectString(Object object)
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);

            return writer.toString();
        }
        catch (PropertyException e)
        {
            throw new RuntimeException(e);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }
}
