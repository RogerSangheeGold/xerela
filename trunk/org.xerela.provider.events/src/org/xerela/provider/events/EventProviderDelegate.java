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

package org.xerela.provider.events;

import java.util.List;

import javax.jws.WebService;

import org.xerela.provider.events.internal.EventsActivator;

/**
 * EventProviderDelegate
 */
@WebService(endpointInterface = "org.xerela.provider.events.IEventProvider", //$NON-NLS-1$
            serviceName = "EventsService", portName = "EventsPort")
public class EventProviderDelegate implements IEventProvider
{

    /** {@inheritDoc} */
    public void subscribe(String queue)
    {
        getProvider().subscribe(queue);
    }

    /** {@inheritDoc} */
    public void unsubscribe(String queue)
    {
        getProvider().unsubscribe(queue);
    }

    public List<Event> poll()
    {
        return getProvider().poll();
    }

    /**
     * This is an accessor to get the 'true' provider as a service.  If the bundle
     * has been restarted, this may return a different provider than previous
     * invocations.
     * 
     * @return the provider to which to delegate
     */
    private IEventProvider getProvider()
    {
        IEventProvider provider = EventsActivator.getEventProvider();
        if (provider == null)
        {
            throw new RuntimeException("EventsProvider unavailable."); //$NON-NLS-1$
        }

        return provider;
    }
}
