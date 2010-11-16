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

package org.xerela.provider.tools;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.xerela.provider.tools.internal.PluginsActivator;
import org.xerela.zap.jta.TransactionElf;

/**
 * ScriptPluginDetailStreamer
 */
public class ScriptPluginDetailStreamer implements IPluginDetailStreamer
{
    private static final String HTTP_RECORD_ID = "recordId"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String recordId = req.getParameter(HTTP_RECORD_ID);

        boolean ownTransaction = TransactionElf.beginOrJoinTransaction();

        try
        {
            Session session = PluginsActivator.getSessionFactory().getCurrentSession();
            Criteria criteria = session.createCriteria(ToolRunDetails.class);
            criteria.add(Restrictions.idEq(Integer.valueOf(recordId)));
            ToolRunDetails uniqueResult = (ToolRunDetails) criteria.uniqueResult();

            String details = uniqueResult.getDetails();
            if (details != null)
            {
                resp.setHeader("Content-type", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
                PrintWriter writer = resp.getWriter();
                writer.print(details);
            }
        }
        finally
        {
            if (ownTransaction)
            {
                TransactionElf.commit();
            }
        }
    }
}
