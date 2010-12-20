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
 * Contributor(s): rkruse, Dylan White (dylamite@ziptie.org)
 */

package org.xerela.provider.credentials;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.xerela.addressing.AddressSet;
import org.xerela.addressing.IPAddress;
import org.xerela.credentials.AbstractCredentialsManager;
import org.xerela.credentials.Credential;
import org.xerela.credentials.CredentialConfig;
import org.xerela.credentials.CredentialNotSetException;
import org.xerela.credentials.CredentialSet;
import org.xerela.credentials.ICredentialsPersister;
import org.xerela.exception.PersistenceException;
import org.xerela.provider.credentials.internal.CredentialsProviderActivator;
import org.xerela.provider.devices.ZDeviceCore;
import org.xerela.security.ISecurityCheck;
import org.xerela.security.NoSecurityCheck;
import org.xerela.security.PermissionDeniedException;

/**
 * The {@link XerelaCredentialsManager} class extends the functionality laid down by the {@link AbstractCredentialsManager} class and provides
 * it as a singleton instance.  It also leverages the Device provider to provide device ID to device resolution so that an IP address and
 * managed network associated with the device can be retrieved and used to determine which credentials sets should be used.
 * 
 * @author Dylan White (dylamite@ziptie.org)
 * @author rkruse
 */
public class XerelaCredentialsManager extends AbstractCredentialsManager
{
    private static final String ERROR_NOT_INITIALIZED = "The XerelaCredentialsManager has not yet been initialized!"; //$NON-NLS-1$
    private static final String ERROR_SETTING_DEFAULT_CREDENTIALS = "Error setting the XerelaCredentialManager's default credentials during startup."; //$NON-NLS-1$
    private static final Logger LOGGER = Logger.getLogger(XerelaCredentialsManager.class);

    private static XerelaCredentialsManager instance;
    private static Object staticMutex = new Object();
    private static Object saveMutex = new Object();

    /**
     * Private default constructor for the {@link XerelaCredentialsManager} class to ensure that there is no way to create an instance externally.
     */
    private XerelaCredentialsManager()
    {
        // Do nothing.
    }

    /**
     * Retrieves a singleton instance of the {@link XerelaCredentialsManager} class.  The {@link XerelaCredentialsManager} must be initialized first
     * by using one of the {@link startup} methods.  A {@link RuntimeException} is thrown if the XerelaCredentialsManager singleton hasn't been initialized
     * yet.
     *
     * @return The singleton instance of the {@link XerelaCredentialsManager} class.
     */
    public static XerelaCredentialsManager getInstance()
    {
        synchronized (staticMutex)
        {
            if (instance == null)
            {
                throw new RuntimeException(ERROR_NOT_INITIALIZED);
            }
            return instance;
        }
    }

    /**
     * Creates an instance of the {@link XerelaCredentialsManager} class but allows you to set some
     * of the other services that require persistence. This allows for easy stub work while testing.
     * 
     * @param persister An implementation of the {@link ICredentialsPersister} interface.
     * @return The singleton instance of the {@link XerelaCredentialsManager} class.
     */
    public static XerelaCredentialsManager startup(ICredentialsPersister persister)
    {
        return startup(persister, new NoSecurityCheck());
    }

    /**
     * Creates an instance of the {@link XerelaCredentialsManager} class but allows you to set some
     * of the other services that require persistence. This allows for easy stub work while testing.
     * 
     * @param persister An implementation of the {@link ICredentialsPersister} interface.
     * @param securityCheck An implementation of the {@link ISecurityCheck} interface.
     * @return The singleton instance of the {@link XerelaCredentialsManager} class.
     */
    public static XerelaCredentialsManager startup(ICredentialsPersister persister, ISecurityCheck securityCheck)
    {
        instance = new XerelaCredentialsManager();
        instance.init(persister, securityCheck);
        return instance;
    }

    /**
     * Initializes the {@link XerelaCredentialsManager} class by ensuring that a default {@link CredentialConfig} object is setup and
     * persisted.
     * 
     * @param persister The implementation of the {@link ICredentialsPersister} interface to use for persistence.
     * @param securityCheck The implementation of the {@link ISecurityCheck} interface to use for security checks.
     */
    private void init(ICredentialsPersister persister, ISecurityCheck securityCheck)
    {
        synchronized (staticMutex)
        {
            // Mark the instance of the XerelaCredentialsManager class as the "this" instance
            instance = this;

            // Set the persister and security check implementations to use
            setPersister(persister);
            setSecurityCheck(securityCheck);

            // Load up the default credentials if there isn't one set
            try
            {
                CredentialConfig cc = getDefaultCredentialConfig();
                if (cc == null)
                {
                    CredentialSet defaultCS = new CredentialSet("default set");

                    // Set lab credentials to ease development
                    if (Boolean.getBoolean("dev.default.credentials")) //$NON-NLS-1$
                    {
                        defaultCS.addCredential(new Credential("username", "testlab")); //$NON-NLS-1$ //$NON-NLS-2$
                        defaultCS.addCredential(new Credential("password", "hobbit")); //$NON-NLS-1$//$NON-NLS-2$
                        defaultCS.addCredential(new Credential("enablePassword", "bigtex")); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    defaultCS.addCredential(new Credential("roCommunityString", "public"));
                    defaultCS.addCredential(new Credential("rwCommunityString", "private"));
                    defaultCS.setPriority(1);

                    CredentialConfig newDefaultCC = new CredentialConfig("Default");
                    newDefaultCC.addCredentialSet(defaultCS);
                    persister.saveDefaultCredentialConfig(newDefaultCC);
                }
            }
            catch (PersistenceException e)
            {
                throw new RuntimeException(ERROR_SETTING_DEFAULT_CREDENTIALS, e);
            }
            catch (PermissionDeniedException e)
            {
                throw new RuntimeException(ERROR_SETTING_DEFAULT_CREDENTIALS, e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mapDeviceToCredentialSet(String deviceID, CredentialSet credentialSet) throws PermissionDeniedException, PersistenceException
    {
        if (credentialSet.getId() > -1)
        {
            try
            {
                getPersister().mapDeviceToCredentialSetMapping(deviceID, credentialSet);
            }
            catch (PersistenceException e)
            {
                /*
                 * This will be a normal occurrence. If a device operation
                 * starts with a credentialSet, and while it is running the
                 * credentialSet is deleted, there should be a Persistence
                 * exception.
                 */
                LOGGER.debug("The credentialSet " + credentialSet.getName() + " has been removed from the datastore.  Unabled to map to device ID: " + deviceID
                        + ".");
            }
        }

        /*
         * Save a new credentialSet
         */
        else
        {
            getSecurityCheck().checkWritePrivileges();
            synchronized (saveMutex)
            {
                // Use the device provider to retrieve the device and to retrieve the IP address of the device.
                ZDeviceCore device = CredentialsProviderActivator.getDeviceProvider().getDevice(Integer.parseInt(deviceID));
                IPAddress address = device != null ? new IPAddress(device.getIpAddress()) : new IPAddress();

                CredentialConfig credentialConfig = getFirstMatchingCredentialConfig(address);
                boolean isDefault = false;
                if (credentialConfig == null)
                {
                    isDefault = true;
                    credentialConfig = getDefaultCredentialConfig();
                }

                boolean foundExistingMatch = false;
                int largestPriority = 0;
                for (CredentialSet credSet : credentialConfig.getCredentialSets())
                {
                    if (credSet.credentialsEqual(credentialSet))
                    {
                        getPersister().mapDeviceToCredentialSetMapping(deviceID, credSet);
                        foundExistingMatch = true;
                        break;
                    }
                    else if (credSet.getPriority() > largestPriority)
                    {
                        largestPriority = credSet.getPriority();
                    }
                }

                if (!foundExistingMatch)
                {
                    credentialSet.setPriority(largestPriority + 1);
                    credentialConfig.addCredentialSet(credentialSet);

                    CredentialConfig postSave = null;
                    if (isDefault)
                    {
                        postSave = getPersister().saveDefaultCredentialConfig(credentialConfig);
                    }
                    else
                    {
                        postSave = getPersister().saveCredentialConfig(credentialConfig);
                    }

                    for (CredentialSet credSet : postSave.getCredentialSets())
                    {
                        if (credSet.credentialsEqual(credentialSet))
                        {
                            getPersister().mapDeviceToCredentialSetMapping(deviceID, credSet);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the first matching {@link CredentialConfig} for the device associated with the specified IP address.
     * 
     * @return null if there isn't a matching config, which indicates you should
     *         use the default.
     * @throws PersistenceException
     * @throws PermissionDeniedException
     */
    private CredentialConfig getFirstMatchingCredentialConfig(IPAddress address) throws PermissionDeniedException, PersistenceException
    {
        List<CredentialConfig> all = getAllCredentialConfigs();
        for (CredentialConfig currConfig : all)
        {
            AddressSet addressSet = currConfig.getAddressSet();

            if (addressSet.contains(address))
            {
                return currConfig;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<CredentialSet> calculateCredentialSets(String deviceID, boolean returnStaleCredentials) throws PersistenceException, PermissionDeniedException
    {
        CredentialSet workingCredentialSet = getPersister().getCredentialSetByDeviceID(deviceID, returnStaleCredentials);
        if (workingCredentialSet != null)
        {
            List<CredentialSet> toReturn = new ArrayList<CredentialSet>(1);
            toReturn.add(0, workingCredentialSet);
            return toReturn;
        }
        else
        {
            // Use the device provider to retrieve the device and to retrieve the IP address of the device.
            ZDeviceCore device = CredentialsProviderActivator.getDeviceProvider().getDevice(Integer.parseInt(deviceID));
            IPAddress ipAddress = device != null ? new IPAddress(device.getIpAddress()) : new IPAddress();

            // Return all of the credential sets associated with the specified IP address
            return getCredentialSetsByIpAddress(ipAddress);
        }
    }

    /** {@inheritDoc} 
     * @throws PermissionDeniedException 
     * @throws PersistenceException */
    @Override
    public void updateSingleCredential(String ipAddress, String managedNetwork, String credentialKey, String credentialValue) throws PersistenceException,
            PermissionDeniedException
    {
        updateDependentCredential(ipAddress, managedNetwork, credentialKey, credentialValue, null, null);
    }

    /** {@inheritDoc} */
    @Override
    public void updateDependentCredential(String ipAddress, String managedNetwork, String credentialKey, String credentialValue, String dependentCredentialKey,
                                          String dependentCredentialValue) throws PersistenceException, PermissionDeniedException
    {
        ZDeviceCore device = CredentialsProviderActivator.getDeviceProvider().getDevice(ipAddress, managedNetwork);
        if (device == null)
        {
            LOGGER.error("Unable to find the device " + ipAddress + "@" + managedNetwork);
        }
        else
        {
            String deviceId = Integer.toString(device.getDeviceId());
            List<CredentialSet> calculateCredentialSets = calculateCredentialSets(deviceId, true);
            if (calculateCredentialSets.size() > 0)
            {
                CredentialSet currentSet = calculateCredentialSets.get(0);
                try
                {
                    if (dependentCredentialKey == null || currentSet.getCredentialValue(dependentCredentialKey).equals(dependentCredentialValue))
                    {
                        CredentialSet newCredSet = currentSet.clone();
                        newCredSet.resetIds();
                        newCredSet.addOrUpdate(credentialKey, credentialValue);
                        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
                        Date now = new Date();
                        String[] name = newCredSet.getName().split("\\s+\\(");
                        newCredSet.setName(name[0] + " (Updated " + df.format(now) + ")");
                        mapDeviceToCredentialSet(deviceId, newCredSet);
                    }

                }
                catch (CloneNotSupportedException e)
                {
                    LOGGER.error("Can't clone the credentials for " + ipAddress, e);
                }
                catch (CredentialNotSetException e)
                {
                    LOGGER.debug("Will not update credentials for " + ipAddress + " as the current credentials don't contain the matching '"
                            + dependentCredentialKey + "'.0");
                }
            }
            else
            {
                LOGGER.debug("No matching credentials to update for " + ipAddress + "@" + managedNetwork);
            }
        }
    }
}
