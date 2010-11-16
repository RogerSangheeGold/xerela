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
 *   $Author: brettw $
 *     $Date: 2007/04/19 21:42:08 $
 * $Revision: 1.1 $
 *   $Source: /usr/local/cvsroot/org.xerela.net.tftp/src/org/xerela/nio/common/ILogger.java,v $
 */

package org.xerela.nio.common;

public interface ILogger
{

    public void error(Object msg);

    public void error(Object msg, Throwable t);

    public void warn(Object msg);

    public void warn(Object msg, Throwable t);

    public void info(Object msg);

    public void info(Object msg, Throwable t);

    public void debug(Object msg);

    public void debug(Object msg, Throwable t);

    public static enum Level
    {
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }

}

// -------------------------------------------------
// $Log: ILogger.java,v $
// Revision 1.1  2007/04/19 21:42:08  brettw
// Genesis for NIO TFTP server.
//
// Revision 1.2  2006/11/06 21:08:21  BEdwards
// use throwable instead of exception
//
// Revision 1.1  2006/10/05 23:16:14  BEdwards
// initial commit
//
// Revision 1.3  2006/09/11 17:32:45  BEdwards
// lazy evaluation of hex dump when logging debug messages
//
// Revision 1.2  2006/08/22 03:26:04  BEdwards
// collapse injectors into parent classes file.  synch up common classes with latest changes in blackrat
//
// Revision 1.1  2006/08/18 18:48:09  BEdwards
// logging helper classes
//
// Revision 1.0 Aug 17, 2006 bedwards
// Initial revision
// --------------------------------------------------
