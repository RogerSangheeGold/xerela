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

package org.xerela.provider.configstore;

/**
 * Revision
 */
public class Revision extends RevisionInfo
{
    private String content;

    /**
     * Default constructor.
     */
    public Revision()
    {
        // default constructor
    }

    /**
     * Get the contents of the configuration.  If the mime-type indicates that
     * it is a text type then the value returned is the raw text of the config,
     * otherwise it is a Base64 encoded binary.
     *
     * @return either raw text or a Base64 encoded binary 
     */
    public String getContent()
    {
        return content;
    }

    /**
     * Set the content of this revision.  Depending on the mime-type it could be
     * raw text or a Base64 encoded binary.
     *
     * @param content the content to set
     */
    public void setContent(String content)
    {
        this.content = content;
    }
}
