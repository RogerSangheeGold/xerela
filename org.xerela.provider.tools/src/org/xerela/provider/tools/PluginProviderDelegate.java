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

package org.xerela.provider.tools;

import java.util.List;

import javax.jws.WebService;

import org.xerela.provider.tools.internal.PluginsActivator;
import org.xerela.server.security.SecurityHandler;

/**
 * ToolsProviderDelegate
 */
@WebService(endpointInterface = "org.xerela.provider.tools.IPluginProvider", serviceName = "PluginsService", portName = "PluginsPort")
public class PluginProviderDelegate implements IPluginProvider
{
    /** {@inheritDoc} */
    public List<PluginDescriptor> getPluginDescriptors()
    {
        return getProvider().getPluginDescriptors();
    }

    /** {@inheritDoc} */
    public List<ToolRunDetails> getExecutionDetails(int executionId)
    {
        return getProvider().getExecutionDetails(executionId);
    }

    /** {@inheritDoc} */
    public PluginExecRecord getExecutionRecord(int executionId)
    {
        return getProvider().getExecutionRecord(executionId);
    }

    /** {@inheritDoc} */
    public List<String> getFileStoreEntries(String path)
    {
        return getProvider().getFileStoreEntries(path);
    }

    private IPluginProvider getProvider()
    {
        IPluginProvider toolsProvider = PluginsActivator.getToolsProvider();

        return (IPluginProvider) SecurityHandler.newProxy(toolsProvider);
    }
}
