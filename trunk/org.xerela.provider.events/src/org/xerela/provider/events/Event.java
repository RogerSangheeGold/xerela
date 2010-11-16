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

/**
 * Event
 */
public class Event
{
    private String queue;
    private String text;
    private long originTime;
    private String type;

    /**
     * Constructor.
     */
    public Event()
    {
        // empty
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setOriginTime(long originTime)
    {
        this.originTime = originTime;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setQueue(String queue)
    {
        this.queue = queue;
    }

    public String getQueue()
    {
        return queue;
    }
    
    public String getText()
    {
        return text;
    }

    public long getOriginTime()
    {
        return originTime;
    }

    public String getType()
    {
        return type;
    }
}
