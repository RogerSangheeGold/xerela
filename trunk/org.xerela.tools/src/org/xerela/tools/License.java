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

package org.xerela.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * License
 */
@SuppressWarnings("nls")
public class License
{
    private static final String PASS_PHRASE = "XerelaPassPhrase";
    private static final String ALGORITHM = "Blowfish";

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            FileInputStream fis = new FileInputStream("license.xml");
            FileOutputStream fos = new FileOutputStream("license.enc");

            SecretKeySpec secretKeySpec = new SecretKeySpec(PASS_PHRASE.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] bytes = new byte[1024];
            while (true)
            {
                int rc = fis.read(bytes);
                if (rc == -1)
                {
                    break;
                }
                cos.write(bytes, 0, rc);
            }
            fis.close();
            cos.flush();
            cos.close();

            System.out.println("License file encrypted.\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
