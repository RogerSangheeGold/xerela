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

package org.xerela.provider.devices.internal;

import static org.xerela.provider.devices.DeviceResolutionElf.populatePageData;

import java.io.IOException;
import java.io.StringReader;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.xerela.net.adapters.AdapterMetadata;
import org.xerela.provider.devices.IDeviceResolutionScheme;
import org.xerela.provider.devices.PageData;
import org.xerela.provider.devices.ServerDeviceElf;
import org.xerela.provider.devices.ZDeviceLite;
import org.xerela.zap.jta.TransactionElf;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Device resolver for OS Version queries.
 */
public class OsVersionResolutionScheme implements IDeviceResolutionScheme
{
    /** {@inheritDoc} */
    public PageData resolve(String scheme, String data, PageData pageData, String sortColumn, boolean descending)
    {
        CSVReader reader = new CSVReader(new StringReader(data));
        String[] split;
        try
        {
            split = reader.readNext();
        }
        catch (IOException e)
        {
            // reading from a string should never fail.
            throw new RuntimeException(e);
        }
        String osType = split[0];
        String operator = (split.length > 1 ? split[1] : null);
        String version = (split.length > 2 ? split[2] : null);

        boolean own = TransactionElf.beginOrJoinTransaction();
        boolean success = false;
        try
        {
            PageData result = search(osType, operator, version, pageData, sortColumn, descending);
            success = true;
            return result;
        }
        finally
        {
            if (own)
            {
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

    private PageData search(String osType, String operator, String version, PageData pageData, String sortColumn, boolean descending)
    {

        AdapterMetadata adapterMetadata = DeviceProviderActivator.getAdapterService().getAdapterMetadata(osType);
        if (adapterMetadata != null)
        {
            Session session = DeviceProviderActivator.getSessionFactory().getCurrentSession();
            Criteria criteria = session.createCriteria(ZDeviceLite.class)
                .setFirstResult(pageData.getOffset())
                .setMaxResults(pageData.getPageSize());

            criteria.add(Restrictions.eq(ATTR_ADAPTER_ID, osType));
            if (version != null && version.trim().length() > 0)
            {
                String canonicalVersion = ServerDeviceElf.computeCononicalVersion(version, adapterMetadata.getSoftwareVersionRegEx());
                if (">".equals(operator)) //$NON-NLS-1$
                {
                    criteria.add(Restrictions.gt(ATTR_CANONICAL_OS_VERSION, canonicalVersion));
                }
                else if ("<".equals(operator)) //$NON-NLS-1$
                {
                    criteria.add(Restrictions.lt(ATTR_CANONICAL_OS_VERSION, canonicalVersion));
                }
                else if ("=".equals(operator)) //$NON-NLS-1$
                {
                    criteria.add(Restrictions.eq(ATTR_CANONICAL_OS_VERSION, canonicalVersion));
                }
                else if ("<=".equals(operator)) //$NON-NLS-1$
                {
                    criteria.add(Restrictions.le(ATTR_CANONICAL_OS_VERSION, canonicalVersion));
                }
                else if (">=".equals(operator)) //$NON-NLS-1$
                {
                    criteria.add(Restrictions.ge(ATTR_CANONICAL_OS_VERSION, canonicalVersion));
                }
                else
                {
                    throw new RuntimeException(String.format("Invalid operator '%s'supplied to search method.", operator)); //$NON-NLS-1$
                }
            }

            return populatePageData(pageData, criteria, sortColumn, descending);
        }

        return new PageData();
    }
}
