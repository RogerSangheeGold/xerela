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

/**
 * A class for specifying the outcome of a running <code>ITask.execute()</code>.
 * The listener will be notified of the completion of the task with the Outcome
 * that it produced.  If the ITask did not finish, the listener will still be
 * notified that the task is complete with an Outcome of either EXCEPTION or
 * CANCELLED.  See the discussion in <code>OperationManager</code> for how that
 * can happen.
 * 
 * @author chamlett
 */
public enum Outcome
{
    SUCCESS,
    FAILURE,
    WARNING,
    EXCEPTION,
    CANCELLED
}
