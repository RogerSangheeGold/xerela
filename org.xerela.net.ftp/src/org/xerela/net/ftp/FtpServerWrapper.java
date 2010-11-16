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

package org.xerela.net.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.interfaces.IFtpConfig;

/**
 * FtpServerWrapper.
 *
 * Wrap the Apache FTP Server just so we can name the stupid thread.
 */
public class FtpServerWrapper extends FtpServer
{
    /**
     * Constructor.
     * 
     * @param ftpConfig an ftp server configuration
     */
    public FtpServerWrapper(IFtpConfig ftpConfig)
    {
        super(ftpConfig);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        Thread.currentThread().setName("Apache FTP Server");
        super.run();
    }
}
