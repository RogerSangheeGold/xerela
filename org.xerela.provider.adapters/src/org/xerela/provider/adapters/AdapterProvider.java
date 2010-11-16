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

package org.xerela.provider.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xerela.credentials.CredentialKey;
import org.xerela.credentials.utils.CredentialKeyElf;
import org.xerela.net.adapters.AdapterMetadata;
import org.xerela.net.adapters.IAdapterService;
import org.xerela.net.adapters.Operation;
import org.xerela.provider.adapters.internal.AdapterProviderActivator;

/**
 * AdapterProvider
 */
public class AdapterProvider implements IAdapterProvider
{
    private static final String CREDENTIAL_KEY_FILE = "credentialKeys.xml"; //$NON-NLS-1$
    private List<CredentialKey> credentialKeys;

    /** {@inheritDoc} */
    public List<AdapterLite> getAvailableAdapters()
    {
        Collection<AdapterMetadata> metadata = AdapterProviderActivator.getAdapterService().getAllAdapterMetadata();
        ArrayList<AdapterLite> result = new ArrayList<AdapterLite>(metadata.size());

        for (AdapterMetadata adapter : metadata)
        {
            // Create a new AdapterLite object to travel over the wire
            AdapterLite adapterLite = new AdapterLite(adapter.getAdapterId(), adapter.getShortName(), adapter.getDescription());

            // For potential UI purposes, include the restore validation regular expression with the newly
            // created adapter lite object.
            Operation restoreOperation = adapter.getOperation("restore"); //$NON-NLS-1$
            if (restoreOperation != null)
            {
                adapterLite.setRestoreValidationRegex(restoreOperation.getRestoreValidationRegex());
            }

            // Add the created AdapterLite object to our list to pass over the wire
            result.add(adapterLite);
        }

        return result;
    }

    /** {@inheritDoc} */
    public synchronized List<CredentialKey> getCredentialKeys()
    {
        if (credentialKeys != null)
        {
            return credentialKeys;
        }

        InputStream credentialKeysResource = IAdapterService.class.getResourceAsStream('/' + CREDENTIAL_KEY_FILE);
        try
        {
            credentialKeys = CredentialKeyElf.loadCredentialKeys(credentialKeysResource);
        }
        catch (IOException e)
        {
            Logger.getLogger(getClass()).error(e.getMessage(), e);
            return Collections.emptyList();
        }
        catch (SAXException e)
        {
            Logger.getLogger(getClass()).error(e.getMessage(), e);
            return Collections.emptyList();
        }

        return credentialKeys;
    }
}
