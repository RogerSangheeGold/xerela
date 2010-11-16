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

import org.xerela.nio.common.FileIn;
import org.xerela.nio.common.FileOut;
import org.xerela.nio.common.SystemLogger;


public class FileGenerator implements SystemLogger.Injector
{

    // -- member fields
    private byte[] testPattern;
    private int numPatternRepetitions;

    // -- constructors
    public FileGenerator(String dir, String filename, byte[] testPattern, int numPatternRepetitions)
    {
        this.testPattern = testPattern;
        this.numPatternRepetitions = numPatternRepetitions;
        generateFile(dir, filename);
        verifyFile(dir, filename, false);
    }

    // -- public methods    
    public void verifyFile(String dir, String filename, boolean delete)
    {
        FileIn fileIn = new FileIn(dir, filename, logger);
        byte[] verifyArray = new byte[testPattern.length];
        for (int i = 0; i < numPatternRepetitions; i++)
        {
            fileIn.read(verifyArray, 0, verifyArray.length);
            for (int j = 0; j < testPattern.length; j++)
            {
                if (testPattern[j] != verifyArray[j])
                {
                    throw new RuntimeException("byte not equal, expected " + (char) testPattern[j] + " but found " + (char) verifyArray[j] + " in " + dir + "/"
                            + filename + " (i=" + i + ", j=" + j + ")");
                }
            }
        }
        fileIn.close();
        if (delete)
        {
            new File(dir + File.separatorChar + filename).delete();
        }
    }

    public int fileSize()
    {
        return testPattern.length * numPatternRepetitions;
    }

    // -- private methods
    private void generateFile(String dir, String filename)
    {
        FileOut fileOut = new FileOut(dir, filename, logger);
        for (int i = 0; i < numPatternRepetitions; i++)
        {
            fileOut.write(testPattern, 0, testPattern.length);
        }
        fileOut.close();
    }

}
