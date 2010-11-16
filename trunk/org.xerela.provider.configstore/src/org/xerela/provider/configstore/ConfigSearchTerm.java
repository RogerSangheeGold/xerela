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
 * ConfigSearchTerm
 */
public class ConfigSearchTerm
{
    private String term;
    private int startOffset;
    private int endOffset;

    /**
     * Default constructor.
     */
    public ConfigSearchTerm()
    {
        // constructor
    }

    /**
     * Get the search term.
     *
     * @return the search term
     */
    public String getTerm()
    {
        return term;
    }

    /**
     * Set the search term.
     *
     * @param term the search term
     */
    public void setTerm(String term)
    {
        this.term = term;
    }

    /**
     * Get the start byte offset of the search term in the configuration.
     *
     * @return the start offset
     */
    public int getStartOffset()
    {
        return startOffset;
    }

    /**
     * Set the start byte offset of the search term i the configuration.
     *
     * @param startOffset the start offset
     */
    public void setStartOffset(int startOffset)
    {
        this.startOffset = startOffset;
    }

    /**
     * Get the end byte offset of the search term in the configuration.
     *
     * @return the end offset
     */
    public int getEndOffset()
    {
        return endOffset;
    }

    /**
     * Set the end byte offset of the search term in the configuration.
     *
     * @param endOffset the end offset
     */
    public void setEndOffset(int endOffset)
    {
        this.endOffset = endOffset;
    }
}
