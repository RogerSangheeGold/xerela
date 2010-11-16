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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.xerela.adaptertool.AtConfigElf;

/**
 * Some statics around tools bundles
 * ToolBundleElf
 */
public final class ToolBundleElf
{

    /**
     * hidden
     */
    private ToolBundleElf()
    {
    }

    /**
     * 
     * Find script tool bundle directories
     * @return a list of bundle directories
     * @throws IOException if there is an error reading files
     */
    public static List<ScriptBundle> getScriptToolBundles() throws IOException
    {
        List<ScriptBundle> bundleList = new ArrayList<ScriptBundle>();
        File adapterDir = AtConfigElf.getAdapterDir();
        File[] files = adapterDir.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            File manifest = new File(files[i], AtConfigElf.MANIFEST);
            if (manifest.exists())
            {
                FileInputStream in = new FileInputStream(manifest);
                try
                {
                    Manifest mf = new Manifest(in);
                    Attributes attrs = mf.getMainAttributes();
                    String toolsDir = attrs.getValue("ZTool-Directory"); //$NON-NLS-1$
                    if (toolsDir != null)
                    {
                        bundleList.add(new ScriptBundle(files[i], toolsDir));
                    }
                }
                finally
                {
                    in.close();
                }
            }
        }
        return bundleList;
    }

}
