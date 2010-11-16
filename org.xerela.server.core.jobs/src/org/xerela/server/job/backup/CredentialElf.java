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
 * Contributor(s): Dylan White (dylamite@xerela.org)
 */

package org.xerela.server.job.backup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.xerela.credentials.CredentialSet;
import org.xerela.exception.PersistenceException;
import org.xerela.net.adapters.AdapterMetadata;
import org.xerela.net.adapters.AdapterServiceException;
import org.xerela.net.adapters.Operation;
import org.xerela.net.client.Credential;
import org.xerela.protocols.NoEnabledProtocolsException;
import org.xerela.protocols.ProtocolSet;
import org.xerela.provider.credentials.CredentialsProvider;
import org.xerela.provider.devices.ServerDeviceElf;
import org.xerela.provider.devices.ZDeviceCore;
import org.xerela.provider.devices.ZDeviceLite;
import org.xerela.security.PermissionDeniedException;
import org.xerela.server.job.internal.CoreJobsActivator;

/**
 * The <code>CredentialElf</code> class provides a number of helper functions for converting internal Xerela <code>Credential</code>
 * objects into SOAP-compatible <code>Credential</code> objects.
 * 
 * @author Dylan White (dylamite@xerela.org)
 */
public final class CredentialElf
{
    /**
     * Private default constructor for the <code>CredentialElf</code> class in order to hide it from being used.
     */
    private CredentialElf()
    {
        // Does nothing
    }

    /**
     * Converts an internal Xerela <code>Credential</code> to a SOAP-compatible <code>Credential</code> object.
     * 
     * @param xerelaCredential An internal Xerela <code>Credential</code> object to be converted.
     * @return A SOAP-compatible <code>Credential</code> object.
     */
    public static Credential convertCredentialToSoapCredential(org.xerela.credentials.Credential xerelaCredential)
    {
        Credential soapCredential = null;

        // Only convert the internal Xerela credential object if it is valid
        if (xerelaCredential != null)
        {
            // Create a new SOAP-compatible credential object
            soapCredential = new Credential();

            // Set the name and the value on the SOAP-compatible credential object
            soapCredential.setName(xerelaCredential.getName());
            soapCredential.setValue(xerelaCredential.getValue());
        }

        // Return the newly created SOAP-compatible credential object
        return soapCredential;
    }

    /**
     * Converts an array of internal Xerela <code>Credential</code> objects to an array of SOAP-compatible <code>Credential</code>
     * objects.
     *
     * @param xerelaCredentialSet An array of internal Xerela <code>Credential</code> objects.
     * @return An array of SOAP-compatiable <code>Credential</code> objects.
     */
    public static Credential[] convertCredentialsToSoapCredentials(CredentialSet xerelaCredentialSet)
    {
        Credential[] soapCredentials = null;

        // Only convert the internal Xerela credential set if it is valid
        if (xerelaCredentialSet != null)
        {
            // Grab the set of Xerela credential objects
            Set<org.xerela.credentials.Credential> xerelaCredentials = xerelaCredentialSet.getCredentials();

            // Create an array of SOAP-compatible credentials equal to the number of Xerela credentials
            soapCredentials = new Credential[xerelaCredentials.size()];

            // Create a counter to keep track of where we are in the array of SOAP-compatible credentials
            int i = 0;

            // For each Xerela credential, convert it to a SOAP-compatible credential
            for (org.xerela.credentials.Credential xerelaCredential : xerelaCredentials)
            {
                soapCredentials[i++] = convertCredentialToSoapCredential(xerelaCredential);
            }
        }

        // Return the newly created array of SOAP-compatible credential objects
        return soapCredentials;
    }

    /**
     * Get the protocol sets that are supported by both the device and the operation.
     * @param device The device.
     * @param operationName The name of the operation (ie: backup)
     * @return The {@link ProtocolSet}s that are supported.
     * @throws PersistenceException If there is an issue loading the protocols 
     * @throws NoEnabledProtocolsException If there are no protocols that are supported.
     */
    @SuppressWarnings("nls")
    public static List<ProtocolSet> calculateProtocolSets(ZDeviceCore device, String operationName) throws PersistenceException, NoEnabledProtocolsException
    {
        String deviceType = device.getAdapterId();

        // Get the protocols to use for this device
        AdapterMetadata adapterMetadata = CoreJobsActivator.getAdapterService().getAdapterMetadata(deviceType);
        if (adapterMetadata == null)
        {
            String errorName = null == deviceType ? "null" : deviceType; //$NON-NLS-1$

            String message = "UNSUPPORTED_ADAPTER: '" + errorName + "' is an unsupported or unknown adapter.";
            throw new IllegalArgumentException(message);
        }

        Operation operation = adapterMetadata.getOperation(operationName);
        if (operation == null)
        {
            return new ArrayList<ProtocolSet>();
        }

        CredentialsProvider credProvider = CoreJobsActivator.getCredentialsProvider();

        List<ProtocolSet> protocolSetsFromAdapter = operation.getProtocolSets();

        String deviceId = Integer.toString(device.getDeviceId());

        // When calculating the protocol sets to use, do not include stale protocols.  If a protocol has been marked stale,
        // we want to make sure it is either not used or is re-examined so that it could be used again.
        return credProvider.calculateSupportedProtocolSets(protocolSetsFromAdapter, deviceId, false);
    }

    /**
     * Calculates the list of {@link CredentialSet} objects that can be used with the specified device.
     * This is done by checking to see if there is a current credential set mapped to the device and that the
     * credential set is not stale.  If this is the case, the mapped credential set will be returned; otherwise, all of the
     * credential sets associated with the IP of the device will be retrieved and can be used.
     * 
     * Get the credential sets that are configured for the device.
     * @param device The device
     * @return The list of {@link CredentialSet} objects.
     * @throws PersistenceException if there is an error retrieving a previous device to credential set mapping from the data store
     * @throws PermissionDeniedException if there is an error retrieving the information.
     */
    public static List<CredentialSet> calculateCredentialSets(ZDeviceCore device) throws PersistenceException, PermissionDeniedException
    {
        CredentialsProvider credProvider = CoreJobsActivator.getCredentialsProvider();

        String deviceId = Integer.toString(device.getDeviceId());

        return credProvider.calculateCredentialSets(deviceId, false);
    }

    /**
     * @param device a ZDeviceLite object
     * @param operationName The name of the operation (ie: backup)
     * @return The {@link ProtocolSet}s that are supported.
     * @throws AdapterServiceException If there is a problem getting the adapter service.
     * @throws PersistenceException If there is an issue loading the protocols 
     * @throws NoEnabledProtocolsException If there are no protocols that are supported.
     */
    public static List<ProtocolSet> calculateProtocolSets(ZDeviceLite device, String operationName) throws AdapterServiceException, PersistenceException,
            NoEnabledProtocolsException
    {
        ZDeviceCore core = ServerDeviceElf.convertLiteToCore(device);
        return calculateProtocolSets(core, operationName);
    }

    /**
     * Calculates the list of {@link CredentialSet} objects that can be used with the specified device.
     * This is done by checking to see if there is a current credential set mapped to the device and that the
     * credential set is not stale.  If this is the case, the mapped credential set will be returned; otherwise, all of the
     * credential sets associated with the IP of the device will be retrieved and can be used.
     * 
     * Get the credential sets that are configured for the device.
     * @param device The device
     * @return The list of {@link CredentialSet} objects.
     * @throws PersistenceException if there is an error retrieving a previous device to credential set mapping from the data store
     * @throws PermissionDeniedException if there is an error retrieving the information.
     */
    public static List<CredentialSet> calculateCredentialSets(ZDeviceLite device) throws PersistenceException, PermissionDeniedException
    {
        ZDeviceCore core = ServerDeviceElf.convertLiteToCore(device);
        return calculateCredentialSets(core);
    }

    /**
     * Returns the enabled protocols on the device.
     * @param device the device to look at
     * @return the protocols as a ProtocolSet
     * @throws PersistenceException when things go bad
     */
    public static ProtocolSet getEnabledProtocols(ZDeviceLite device) throws PersistenceException
    {
        CredentialsProvider credProvider = CoreJobsActivator.getCredentialsProvider();
        return credProvider.getAllEnabledProtocols(device.getIpAddress(), Integer.toString(device.getDeviceId()));
    }
}
