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
 * All code, patterns, and comments are Copyright Alterpoint, Inc. 2003-2006
 *
 *   $Author: rkruse $
 *     $Date: 2007/06/15 17:33:59 $
 * $Revision: 1.3 $
 *   $Source: /usr/local/cvsroot/org.xerela.net/src/org/xerela/discovery/NoFutureException.java,v $e
 */

package org.xerela.discovery;

/**
 * Thrown by the <code>DiscoveryEngine</code> if a request to discover doesn't
 * make it into the thread pool.
 * 
 * @author rkruse
 */
public class NoFutureException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 8367484682688918349L;

    /**
     * 
     */
    public NoFutureException()
    {
    }

    /**
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public NoFutureException(String message)
    {
        super(message);
    }

    /**
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public NoFutureException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public NoFutureException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
