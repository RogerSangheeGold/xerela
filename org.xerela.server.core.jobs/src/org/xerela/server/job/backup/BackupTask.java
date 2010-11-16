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
 */
package org.xerela.server.job.backup;

import java.io.File;

import javax.xml.stream.XMLStreamException;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.xerela.common.StringElf;
import org.xerela.credentials.CredentialSet;
import org.xerela.net.client.Backup;
import org.xerela.net.client.ConnectionPath;
import org.xerela.protocols.ProtocolSet;
import org.xerela.provider.credentials.CredentialsProvider;
import org.xerela.provider.devices.ZDeviceCore;
import org.xerela.server.dispatcher.Outcome;
import org.xerela.server.job.AbstractAdapterTask;
import org.xerela.server.job.AdapterEndpointElf;
import org.xerela.server.job.internal.CoreJobsActivator;
import org.xerela.zap.jta.TransactionElf;

/**
 * The {@link BackupTask} class provides functionality for backing up all of device's configuration files and parsing various
 * device response to build a normalized data set about that device.
 */
public class BackupTask extends AbstractAdapterTask
{
    /**
     * Creates a new {@link BackupTask} instance and associates the specified {@link ZDeviceCore} object with it.
     * 
     * @param device The device to be associated with this {@link BackupTask} instance.
     */
    BackupTask(ZDeviceCore device)
    {
        super("backup", device); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected Outcome performTask(CredentialSet credentialSet, ProtocolSet protocolSet, ConnectionPath connectionPath) throws Exception
    {
        ZDeviceCore device = getDevice();
        String ipAddress = device.getIpAddress();
        String adapterId = device.getAdapterId();
        String deviceId = Integer.toString(device.getDeviceId());
        SessionFactory sessionFactory = CoreJobsActivator.getSessionFactory();

        String backupOutput = null;
        File modelXmlFile = null;

        boolean success = false;
        TransactionElf.beginOrJoinTransaction();  // This thread OWNS this transaction

        try
        {
            // Execute the backup
            backupOutput = AdapterEndpointElf.getEndpoint(Backup.class, adapterId).backup(connectionPath);
            String filename = (ipAddress + "_backup.xml").replaceAll(":+", "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            modelXmlFile = StringElf.stringToTempFile(filename, backupOutput);
            backupOutput = null;

            Session currentSession = sessionFactory.getCurrentSession();
            StaxProcessor processor = new StaxProcessor();
            processor.process(device, modelXmlFile);

            currentSession.flush();

            // Save the protocol set and credential set that were both successfully used to backup the device
            // and map this information to the device itself.
            CredentialsProvider credProvider = CoreJobsActivator.getCredentialsProvider();
            credProvider.mapDeviceToProtocolSet(deviceId, protocolSet);
            credProvider.mapDeviceToCredentialSet(deviceId, credentialSet);

            success = true;

            return Outcome.SUCCESS;

        }
        // TODO dwhite: This is for checking to see the contents of a problematic XML related to
        // bug #635 (http://bugs.xerela.org/show_bug.cgi?id=635)
        catch (XMLStreamException xse)
        {
            throw new XMLStreamException(xse.getMessage() + "\nProblematic XML:\n" + (backupOutput != null ? backupOutput : "null"), xse);
        }
        finally
        {
            if (modelXmlFile != null && modelXmlFile.exists())
            {
                modelXmlFile.delete();
            }

            if (success)
            {
                TransactionElf.commit();
            }
            else
            {
                TransactionElf.rollback();
            }
        }
    }

}
