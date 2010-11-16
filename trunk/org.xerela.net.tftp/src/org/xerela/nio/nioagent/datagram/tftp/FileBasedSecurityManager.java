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

package org.xerela.nio.nioagent.datagram.tftp;

import java.io.File;
import java.net.SocketAddress;

/**
 * This implementation of the {@link SecurityManager} requires 
 * that a file exist on the filesystem before allowing a write request.
 * This makes it so that remote users wouldn't be able to fill
 * the tftp filesystem.
 * 
 * @author rkruse
 */
public class FileBasedSecurityManager implements SecurityManager
{
    private String tftpRootDir;
    
    /**
     * Create an instance
     * @param tftpRootDir the tftp directory
     */
    public FileBasedSecurityManager(String tftpRootDir)
    {
        this.tftpRootDir = tftpRootDir + File.separator;
    }

    // -- public methods
    public boolean denyRead(SocketAddress remote, String filename, String mode)
    {
        return denyMode(mode);
    }

    public boolean denyWrite(SocketAddress remote, String filename, String mode)
    {
        File newWrite = new File(tftpRootDir + filename);
        if (!newWrite.exists())
        {
           return true; 
        }
        return denyMode(mode);
    }

    // -- private methods
    private boolean denyMode(String mode)
    {
        return !("octet".equalsIgnoreCase(mode) || "netascii".equalsIgnoreCase(mode));
    }
}
