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

package org.xerela.provider.scheduler;

import org.quartz.JobDetail;

/**
 * ISecureJob
 */
public interface ISecureJob
{
    /**
     * Validate whether the invoking user is allowed to create/update/delete this job.
     *
     * @param jobData the JobData describing the job to be created/updated/deleted
     * @return true if the user is allowed, false if they are not
     */
    boolean validateCudOperation(JobData jobData);

    /**
     * Validate whether the invoking user is allowed to run this job.
     *
     * @param jobDetail the JobDetail describing the job to be run
     * @return true if the user is allowed, false if they are not
     */
    boolean validateRunOperation(JobDetail jobDetail);
}
