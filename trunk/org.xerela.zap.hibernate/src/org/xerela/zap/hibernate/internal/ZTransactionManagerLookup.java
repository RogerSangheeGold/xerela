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

package org.xerela.zap.hibernate.internal;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * ZTransactionManagerLookup
 * 
 * This class implements the Hibernate TransactionManagerLookup interface to
 * provide a custom OSGi-based service lookup of a Transaction Manager.
 *
 */
public class ZTransactionManagerLookup implements TransactionManagerLookup
{
    private static ServiceTracker tmTracker;

    /** {@inheritDoc} */
    public TransactionManager getTransactionManager(Properties props)
    {
        try
        {
            return getTM();
        }
        catch (Exception e)
        {
            throw new HibernateException("Unable to lookup JTA Transaction Manager", e); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public String getUserTransactionName()
    {
        return null;
    }

    static void init(BundleContext context)
    {
        tmTracker = new ServiceTracker(context, TransactionManager.class.getName(), null);
        tmTracker.open();
    }

    static void destroy()
    {
        tmTracker.close();
    }

    /**
     * Get the TransactionManager that was registered as a Service.
     *
     * @return a TransactionManager
     */
    private TransactionManager getTM()
    {
        Object object = tmTracker.getService();
        return (TransactionManager) object;
    }
}
