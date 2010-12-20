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
 * Portions created by AlterPoint are Copyright (C) 2006, 2007,
 * AlterPoint, Inc. All Rights Reserved.
 * 
 * Contributor(s): Dylan White (dylamite@ziptie.org)
 */

package org.xerela.server.job.backup;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.xerela.net.client.Properties;
import org.xerela.net.client.Property;
import org.xerela.net.client.Protocol;
import org.xerela.protocols.ProtocolProperty;
import org.xerela.protocols.ProtocolSet;

/**
 * The <code>ProtocolElf</code> class provides a number of helper functions for converting internal Xerela <code>Protocol</code>
 * objects into SOAP-compatible <code>Protocol</code> objects.
 * 
 * @author Dylan White (dylamite@ziptie.org)
 */
public final class ProtocolElf
{
    /**
     * Private default constructor for the <code>ProtocolElf</code> class in order to hide it from being used.
     */
    private ProtocolElf()
    {
        // Does nothing
    }

    /**
     * Converts an internal Xerela <code>Protocol</code> to a SOAP-compatible <code>Protocol</code> object.
     * 
     * @param xerelaProtocol An internal Xerela <code>Protocol</code> object to be converted.
     * @return A SOAP-compatible <code>Protocol</code> object.
     */
    public static Protocol convertProtocolToSoapProtocol(org.xerela.protocols.Protocol xerelaProtocol)
    {
        Protocol soapProtocol = null;

        // Only convert the internal Xerela protocol object if it is valid
        if (xerelaProtocol != null)
        {
            // Create a new SOAP-compatible protocol object
            soapProtocol = new Protocol();

            // Set the name and port value on the SOAP-compatible protocol object
            soapProtocol.setName(xerelaProtocol.getName());
            soapProtocol.setPort(xerelaProtocol.getPort());

            // Convert the Xerela protocol properties into SOAP-compatible protocol properties
            Property[] soapProtocolProperties = convertProtocolPropertiesToSoapProtocolProperties(xerelaProtocol.getProperties());

            // Set the properties
            Properties props = new Properties();
            Collections.addAll(props.getProperty(), soapProtocolProperties);

            soapProtocol.setProperties(props);
        }

        // Return the newly created SOAP-compatible protocol
        return soapProtocol;
    }

    /**
     * Converts an array of internal Xerela <code>Protocol</code> objects to an array of SOAP-compatible <code>Protocol</code>
     * objects.
     *
     * @param xerelaProtocolSet An array of internal Xerela <code>Protocol</code> objects.
     * @return An array of SOAP-compatiable <code>Protocol</code> objects.
     */
    public static Protocol[] convertProtocolsToSoapProtocols(ProtocolSet xerelaProtocolSet)
    {
        Protocol[] soapProtocols = null;

        // Only convert the internal Xerela protocol set if it is valid
        if (xerelaProtocolSet != null)
        {
            // Grab the set of Xerela protocol objects
            Set<org.xerela.protocols.Protocol> xerelaProtocols = xerelaProtocolSet.getProtocols();

            // Create an array of SOAP-compatible protocols equal to the number of Xerela protocols
            soapProtocols = new Protocol[xerelaProtocols.size()];

            // Create a counter to keep track of where we are in the array of SOAP-compatible protocols
            int i = 0;

            // For each Xerela protocol, convert it to a SOAP-compatible protocol
            for (org.xerela.protocols.Protocol xerelaProtocol : xerelaProtocols)
            {
                soapProtocols[i++] = convertProtocolToSoapProtocol(xerelaProtocol);
            }
        }

        // Return the newly created array of SOAP-compatible protocol objects
        return soapProtocols;
    }

    /**
     * Converts an internal Xerela protocol property to a SOAP-compatible protocol <code>Property</code> object.
     * 
     * @param xerelaProtocolProperty A <code>ProtocolProperty</code> object representing a property on an internal Xerela
     * <code>Protocol</code> object.
     * @return A SOAP-compatible protocol <code>Property</code> object.
     */
    public static Property convertProtocolPropertyToSoapProtocolProperty(ProtocolProperty xerelaProtocolProperty)
    {
        Property soapProtocolProperty = null;

        // Only convert the internal Xerela protocol property if it is valid
        if (xerelaProtocolProperty != null)
        {
            // Create a SOAP-compatible protocol property
            soapProtocolProperty = new Property();

            // Set the name and the value of the SOAP-compatible protocol property
            soapProtocolProperty.setName(xerelaProtocolProperty.getKey());
            soapProtocolProperty.setValue(xerelaProtocolProperty.getValue());
        }

        // Return the newly created SOAP-compatible protocol property
        return soapProtocolProperty;
    }

    /**
     * Converts an array of internal Xerela protocol properties to an array of SOAP-compatible protocol <code>Property</code>
     * objects.
     *
     * @param propertiesList A <code>List</code> of <code>ProtocolProperty</code> object representing all of the properties for a
     * certain protocol.
     * @return An array of SOAP-compatiable protocol <code>Property</code> objects.
     */
    public static Property[] convertProtocolPropertiesToSoapProtocolProperties(List<ProtocolProperty> propertiesList)
    {
        Property[] soapProtocolProperties = null;

        // Only convert the array of internal Xerela protocol properties if the map representing it is valid
        if (propertiesList != null)
        {
            // Create an array of SOAP-compatible protocol property objects equal to the number of protocol properties that
            // were specified on the Xerela protocol object
            soapProtocolProperties = new Property[propertiesList.size()];

            // Create a counter to keep track of where we are in the array of SOAP-compatible protocol properties
            int i = 0;

            // For each Xerela protocol property, convert it to a SOAP-compatible protocol property object
            for (ProtocolProperty protocolProperty : propertiesList)
            {
                soapProtocolProperties[i++] = convertProtocolPropertyToSoapProtocolProperty(protocolProperty);
            }
        }

        // Return the newly created array of SOAP-compatible protocol property objects
        return soapProtocolProperties;
    }
}
