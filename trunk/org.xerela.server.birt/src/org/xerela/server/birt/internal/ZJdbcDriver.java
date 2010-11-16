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

package org.xerela.server.birt.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * ZJdbcDriver
 */
public class ZJdbcDriver extends OdaJdbcDriver
{
    /**
     * Default Constructor
     */
    public ZJdbcDriver()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public IConnection getConnection(String connectionClassName) throws OdaException
    {
        return new ZBirtConnection();
    }

    /**
     * ZBirtConnection
     */
    private class ZBirtConnection extends org.eclipse.birt.report.data.oda.jdbc.Connection
    {
        /** {@inheritDoc} */
        public void open(Properties connProperties) throws OdaException
        {
            try
            {
                Connection connection = BirtActivator.getDataSource().getConnection();
                super.jdbcConn = connection;
            }
            catch (SQLException se)
            {
                throw new OdaException(se);
            }
        }

        /** {@inheritDoc} */
        public void close() throws OdaException
        {
            if (jdbcConn == null)
            {
                return;
            }

            try
            {
                jdbcConn.close();
            }
            catch (SQLException se)
            {
                throw new OdaException(se);
            }
        }
    }
}
