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

package org.xerela.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.xerela.nio.common.Log4jLogger;
import org.xerela.nio.nioagent.datagram.tftp.TftpServerImpl;

public class TftpServer
{

    /**
     * @param args
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException
    {
        TftpServer server = new TftpServer();
        server.setupLog4j();
        server.startTftpd();
        Thread.sleep(100000000);
    }
    
    private void setupLog4j()
    {
        Logger root = Logger.getRootLogger();
        root.addAppender(new ConsoleAppender(new PatternLayout("%d{ISO8601} %-5p: %X{metadata} %m%n"), ConsoleAppender.SYSTEM_ERR));
        root.setLevel(Level.DEBUG);
    }

    private void startTftpd() throws FileNotFoundException, IOException
    {
        File conf = new File("c:\\Dev\\xerela\\HEAD\\conf");
        File scratch = new File("c:\\Dev\\xerela\\HEAD\\conf\\scratch");
        URI configRoot = conf.toURI();
        URI scratchURI = scratch.toURI();

        Log4jLogger tftpLogger = new Log4jLogger("TFTPServer"); //$NON-NLS-1$

        // Grab the TFTP properties file
        // Create a new TftpProperties object using the TFTP properties file
        Properties tftpProps = new Properties();

        URI tftpConfigURI = URI.create(configRoot.toASCIIString() + "/network/tftp.properties"); //$NON-NLS-1$
        tftpProps.load(new FileInputStream(new File(tftpConfigURI)));

        File tftpRootDir;

        String dir = tftpProps.getProperty(TftpServerImpl.ROOT_DIRECTORY);
        if (dir != null)
        {
            tftpRootDir = new File(dir);
        }
        else
        {
            // Specifiy the root directory for the TFTP server if there is no property set for it,
            // which will be the "scratch/tftp" directory within the conf bundle.s
            //
            // If a different root directory has been specified within the TFTP properties file, use that.
            // Other wise, use the Xerela specific TFTP server root directory.
            URI tftpRootURI = URI.create(scratchURI.toASCIIString() + "/tftp"); //$NON-NLS-1$
            tftpRootDir = new File(tftpRootURI);
            if (!tftpRootDir.exists())
            {
                System.out.println("TFTP root directory, '" + tftpRootDir.getAbsolutePath() + "' doesn't exist....creating.");
                tftpRootDir.mkdirs();
            }

            // Make sure to set the TFTP root dir as a property
            tftpProps.setProperty(TftpServerImpl.ROOT_DIRECTORY, tftpRootDir.getAbsolutePath());
        }

        // If the TFTP scratch directory does not exist within the configuration root directory, then create it
        if (!tftpRootDir.exists() || !tftpRootDir.isDirectory())
        {
            tftpRootDir.mkdir();
        }

        // Create a basic TFTP server and start it
        TftpServerImpl tftpServer = new TftpServerImpl(tftpProps, tftpLogger);
        tftpServer.start();

        System.out.println("TFTP server started");
    }

}
