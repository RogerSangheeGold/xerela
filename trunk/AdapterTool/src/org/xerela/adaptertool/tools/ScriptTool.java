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

package org.xerela.adaptertool.tools;

import java.io.File;
import java.util.Properties;

/**
 * Contains the script properties and the file reference
 * 
 * ScriptTool
 */
public class ScriptTool
{
    private Properties properties;
    private File perlScript;

    /**
     * Build a ScriptTool reference
     * 
     * @param properties the props of the tools
     * @param perlScript a reference to the Perl script
     */
    public ScriptTool(Properties properties, File perlScript)
    {
        this.properties = properties;
        this.perlScript = perlScript;
    }

    /**
     * Get the displayable name of this tool.
     * 
     * @return the name
     */
    public String getToolName()
    {
        return properties.getProperty("menu.label");
    }

    /**
     * @return the perlScript
     */
    public File getPerlScript()
    {
        return perlScript;
    }

    /**
     * @param perlScript the perlScript to set
     */
    public void setPerlScript(File perlScript)
    {
        this.perlScript = perlScript;
    }

    /**
     * @return the properties
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }
}
