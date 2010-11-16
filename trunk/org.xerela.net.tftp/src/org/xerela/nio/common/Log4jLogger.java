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

package org.xerela.nio.common;

import org.apache.log4j.Logger;

public class Log4jLogger implements ILogger
{
    // -- member fields
    private final Logger logger;

    // -- constructors
    public Log4jLogger(String name)
    {
        logger = Logger.getLogger(name);
    }

    // public methods

    public void error(Object msg)
    {
        possiblyLog(org.apache.log4j.Level.ERROR, msg);
    }

    public void error(Object msg, Throwable t)
    {
        possiblyLog(org.apache.log4j.Level.ERROR, msg, t);
    }

    public void warn(Object msg)
    {
        possiblyLog(org.apache.log4j.Level.WARN, msg);
    }

    public void warn(Object msg, Throwable t)
    {
        possiblyLog(org.apache.log4j.Level.WARN, msg, t);
    }

    public void info(Object msg)
    {
        possiblyLog(org.apache.log4j.Level.INFO, msg);
    }

    public void info(Object msg, Throwable t)
    {
        possiblyLog(org.apache.log4j.Level.INFO, msg, t);
    }

    public void debug(Object msg)
    {
        possiblyLog(org.apache.log4j.Level.DEBUG, msg);
    }

    public void debug(Object msg, Throwable t)
    {
        possiblyLog(org.apache.log4j.Level.DEBUG, msg, t);
    }

    // -- private methods
    private void possiblyLog(org.apache.log4j.Level level, Object msg)
    {
        if (logger.isEnabledFor(level))
        {
            logger.log(level, msg);
        }
    }

    private void possiblyLog(org.apache.log4j.Level level, Object msg, Throwable t)
    {
        if (logger.isEnabledFor(level))
        {
            logger.log(level, msg, t);
        }
    }
}
