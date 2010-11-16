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
 * This is a callback interface for a <code>OperationManager</code> as it executes
 * <code>ITask</code>s. The <code>eventOccurred()</code> method will be called
 * as work is performed on the list of <code>ITask</code>s submitted for execution.
 * <p>
 * This listener will potentially called by several different threads and, if the
 * batch is non-sequential those calls can be contemporaneous. It is important
 * that it be properly synchronized when handling the events.
 * <p>
 * 
 * @author chamlett
 */
public interface ITaskListener
{
    /**
     * Called as the <code>OperationManager</code> executes <code>ITasks</code>,
     * including when the run is complete.
     * <p>
     * 
     * @param pEvent An event describing the current status of an <code>ITask</code>.
     */
    void eventOccurred(TaskEvent pEvent);
}
