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

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedNonblockingResolver;
import org.xbill.DNS.LookupAsynch;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import uk.nominet.dnsjnio.Response;
import uk.nominet.dnsjnio.ResponseQueue;

/**
 * DnsService
 */
public class DnsService implements IDnsService
{
    private static final String ORG_XERELA_DNS_TIMEOUT = "org.xerela.dns.timeout"; //$NON-NLS-1$
    private static final String ORG_XERELA_DNS_RETRIES = "org.xerela.dns.retries"; //$NON-NLS-1$

    private ResponseQueue responseQueue;
    private Map<Object, IDnsResolveListener> listenerMap;

    /**
     * Default constructor.
     */
    public DnsService()
    {
        int dnsTimeout = Integer.getInteger(ORG_XERELA_DNS_TIMEOUT, 10);
        int dnsRetries = Integer.getInteger(ORG_XERELA_DNS_RETRIES, 0);

        LookupAsynch.getDefaultResolver().setTimeout(dnsTimeout);
        LookupAsynch.getDefaultResolver().setRetries(dnsRetries);

        responseQueue = new ResponseQueue();
        listenerMap = new ConcurrentHashMap<Object, IDnsResolveListener>();

        Thread t = new Thread(new DnsCallbackHandler(), "DNS Resolver Thread"); //$NON-NLS-1$
        t.setDaemon(true);
        t.start();
    }

    /** {@inheritDoc} */
    public void resolveHost(String host, IDnsResolveListener listener)
    {
        reverseDns(host, listener);
    }

    /** {@inheritDoc} */
    public void reverseDns(String hostIp, IDnsResolveListener listener)
    {
        try
        {
            ExtendedNonblockingResolver res = LookupAsynch.getDefaultResolver();

            Name name = ReverseMap.fromAddress(hostIp);
            int type = Type.PTR;
            int dclass = DClass.IN;
            Record rec = Record.newRecord(name, type, dclass);
            Message query = Message.newQuery(rec);

            synchronized (this)
            {
                Object sendAsyncKey = res.sendAsync(query, responseQueue);
                listenerMap.put(sendAsyncKey, listener);
            }
        }
        catch (UnknownHostException uhe)
        {
            listener.resolvedName(null);
        }
    }

    private class DnsCallbackHandler implements Runnable
    {
        public void run()
        {
            while (true)
            {
            	try
            	{
	                Response response = responseQueue.getItem();

	                String host = null;
	                IDnsResolveListener dnsResolveListener = null;
	                try
	                {
		                synchronized (DnsService.this)
		                {
		                    Object key = response.getId();
		                    dnsResolveListener = listenerMap.remove(key);
		                }
		
		                if (!response.isException())
		                {
		                	Record[] answers = response.getMessage().getSectionArray(Section.ANSWER);
		                	if (answers.length > 0)
		                	{
		                		host = answers[0].rdataToString();
		                	}
		                }
	                }
	                finally
	                {
	                	dnsResolveListener.resolvedName(host);
	                }
            	}
            	catch (Exception e)
            	{
                	if (e instanceof InterruptedException)
                	{
                		break;
                	}

            		continue;
            	}
            }
        }
    }
}
