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

package org.xerela.server.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SecurityElf
 */
public final class SecurityElf
{
    private static final String SALT = "ZiPtIE"; //$NON-NLS-1$

    private SecurityElf()
    {
        // private constructor
    }

    /**
     * Calculate MD5 of username:password:salt
     *
     * @param username the username
     * @param password the password
     * @return a HEX version of the MD5
     */
    public static String calcMD5(String username, String password)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5"); //$NON-NLS-1$

            digest.update(String.format("%s:%s:%s", username, password, SALT).getBytes()); //$NON-NLS-1$
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            return bigInt.toString(16);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

}
