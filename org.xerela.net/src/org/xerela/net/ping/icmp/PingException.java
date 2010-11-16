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

/* Alterpoint, Inc.
*
* The contents of this source code are proprietary and confidential
* All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2005
*
*   $Author: lbayer $
*     $Date: 2007/04/03 23:51:09 $
* $Revision: 1.3 $
*  $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/net/ping/icmp/PingException.java,v $
*/

package org.xerela.net.ping.icmp;

/**
 * 
 */
public class PingException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -6395726805276550716L;

    /**
     * 
     * @param message the message
     */
    public PingException(String message)
    {
        super(message);
    }

    /**
     * @param message the message
     * @param cause the cause
     */
    public PingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause the cause
     */
    public PingException(Throwable cause)
    {
        super(cause);
    }

}
