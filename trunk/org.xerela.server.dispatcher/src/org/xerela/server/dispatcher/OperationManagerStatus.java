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

package org.xerela.server.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the current status of a <code>OperationManager</code>.
 * Call <code>OperationManager.getStatus()</code> to retrieve one.
 * 
 * @author chamlett
 */
public class OperationManagerStatus
{
    /** A list with one entry per batch, representing the status of that batch */
    private ArrayList<BatchStatus> batchStatuses;

    // ----------------------------------------------------------------
    //                    C O N S T R U C T O R S
    // ----------------------------------------------------------------

    /**
     * The only constructor; initializes internal variables.
     */
    public OperationManagerStatus()
    {
        batchStatuses = new ArrayList<BatchStatus>();
    }

    // ----------------------------------------------------------------
    //                   P U B L I C   M E T H O D S
    // ----------------------------------------------------------------

    /** 
     * Get the list of batch statuses 
     * @return a List of status objects, one per batch still in the schedule
     * */
    public List<BatchStatus> getBatchStatuses()
    {
        return batchStatuses;
    }

    /**
     * @return A String with a summary line followed by 0 or more lines with a
     *    summary of one batch per line.
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(batchStatuses.size()).append(" batches\n");

        for (BatchStatus bs : batchStatuses)
        {
            sb.append('\t').append(bs).append('\n');
        }

        return sb.toString();
    }

    // ----------------------------------------------------------------
    //                   P A C K A G E   M E T H O D S
    // ----------------------------------------------------------------

    /** 
     * Add a new batch status to the list.  
     * @param batch the OperationBatch to add.
     */
    void addBatchStatus(OperationBatch batch)
    {
        batchStatuses.add(new BatchStatus(batch));
    }
}
