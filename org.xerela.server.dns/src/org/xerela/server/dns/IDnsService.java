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

package org.xerela.server.dns;

/**
 * IDnsService
 */
public interface IDnsService
{
    /**
     * This method uses the non-blocking I/O DNS resolver to resolve a host
     * against a DNS.  The host provided can either be a symbolic DNS name
     * or an IP address.
     *
     * @param host a host name or IPv4 or IPv6 address
     * @param listener a callback listener that is called by the resolver
     *    when name resolution is complete
     */
    void resolveHost(String host, IDnsResolveListener listener);

    /**
     * This method uses the non-blocking I/O DNS resolver to resolve a
     * host name against a DNS.
     * 
     * @param hostname the symbolic name to resolve
     * @param listener a callback listener that is called by the resolver
     *    when name resolution is complete
     */
    void reverseDns(String hostname, IDnsResolveListener listener);
}
