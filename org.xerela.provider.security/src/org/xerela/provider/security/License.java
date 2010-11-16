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

package org.xerela.provider.security;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * The license properties originate from a Java properties file in XML
 * format that look like this:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
 * &lt;properties>
 *    &lt;comment>Xerela License File&lt;/comment>
 *    &lt;entry key="organization">None&lt;/entry>
 *    &lt;entry key="expires">2020/01/01&lt;/entry>
 *    &lt;entry key="nodes">0&lt;/entry>
 * &lt;/properties>
 * </pre>
 */
public class License
{
    private int nodes;
    private Date expiration;
    private String organization;

    /**
     * Default constructor.
     */
    public License()
    {
        // Default constructor
    }

    /**
     * Construct from Properties.
     *
     * @param props a properties set
     */
    @SuppressWarnings("nls")
    public License(Properties props)
    {
        try
        {
            nodes = Integer.parseInt(props.getProperty("nodes", "0"));
            expiration = (new SimpleDateFormat("yyyy/MM/dd")).parse(props.getProperty("expires", "2020/01/01"));
            setOrganization(props.getProperty("organization", "None"));
        }
        catch (ParseException pe)
        {
            throw new RuntimeException("Unable to construct License.", pe);
        }
    }

    /**
     * Get the maximum supported node count.
     *
     * @return the maximum supported nodes
     */
    public int getNodes()
    {
        return nodes;
    }

    /**
     * Set the maximum supported node count.
     *
     * @param nodes
     */
    public void setNodes(int nodes)
    {
        this.nodes = nodes;
    }

    /**
     * Get the expiration date of the support license.
     *
     * @return the expiration date of support
     */
    public Date getExpiration()
    {
        return expiration;
    }

    /**
     * Set the expiration date of the support license.
     *
     * @param expiration the expiration date of support
     */
    public void setExpiration(Date expiration)
    {
        this.expiration = expiration;
    }

    /**
     * Get the organization the license is granted to.
     *
     * @return the name of the organization
     */
    public String getOrganization()
    {
        return organization;
    }

    /**
     * Set the organization the license is granted to.
     *
     * @param organization the name of the organization
     */
    public void setOrganization(String organization)
    {
        this.organization = organization;
    }
}
