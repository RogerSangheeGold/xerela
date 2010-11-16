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

package org.xerela.provider.security.internal;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import org.xerela.provider.security.License;

/**
 * LicenseElf
 */
@SuppressWarnings("nls")
public final class LicenseElf
{
    private static final String PASS_PHRASE = "XerelaPassPhrase";
    private static final String ALGORITHM = "Blowfish";
    private static License license;

    /**
     * Private default constructor
     */
    private LicenseElf()
    {
        // private constructor
    }

    public synchronized static License loadLicense()
    {
        if (license != null)
        {
            return license;
        }

        try
        {
            SecretKeySpec secretKeySpec = new SecretKeySpec(PASS_PHRASE.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            InputStream is = new FileInputStream("osgi-config/security/license.enc");
            CipherInputStream cis = new CipherInputStream(is, cipher);

            int offset = 0;
            byte[] bytes = new byte[2048];
            while (true)
            {
                int rc = cis.read(bytes, offset, bytes.length - offset);
                if (rc == -1)
                {
                    break;
                }
                offset += rc;
            }
            is.close();
            cis.close();

            Properties licenseProps = new Properties();
            licenseProps.loadFromXML(new ByteArrayInputStream(bytes, 0, offset));

            license = new License(licenseProps);
            return license;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load license file.", e);
        }
    }
}
