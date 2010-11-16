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

package org.xerela.provider.configstore;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public final class Messages extends NLS
{
    public static String ConfigBackupPersister_unableToWriteOrClose;
    public static String ConfigBackupPersister_unableToRead;
    public static String ConfigSearch_errorAccessingLucene;
    public static String ConfigSearch_errorParsingLastChangedDate;
    public static String ConfigSearch_errorParsingSearchExpression;
    public static String ConfigSearch_luceneCorrupt;
    public static String ConfigSearch_luceneLockFailure;
    public static String ConfigSearch_unableToIndexConfig;
    public static String ConfigStore_clientFactoryException;
    public static String ConfigStore_configError;
    public static String ConfigStore_creatingWorkingCopy;
    public static String ConfigStore_createdFileRepository;
    public static String ConfigStore_errorAccessingRevision;
    public static String ConfigStore_failureCreatingRepository;
    public static String ConfigStore_failureCreatingWorkingCopy;
    public static String ConfigStore_invalidClientType;
    public static String ConfigStore_repositoryBinding;
    public static String ConfigStoreDelegate_serviceUnavailable;
    public static String ConfigSearchDelegate_serviceUnavailable;
    public static String ConfigStoreActivator_registered;
    public static String ConfigStoreActivator_serviceFailed;
    public static String ConfigStoreActivator_starting;
    public static String ConfigStoreActivator_stopped;
    public static String RepositoryConfig_reposRootNotDefined;

    private static final String BUNDLE_NAME = "org.xerela.provider.configstore.messages"; //$NON-NLS-1$
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
