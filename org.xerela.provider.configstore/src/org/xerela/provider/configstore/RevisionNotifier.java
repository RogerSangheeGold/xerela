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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xerela.provider.configstore.internal.ConfigStoreActivator;
import org.xerela.provider.devices.ZDeviceCore;

/**
 * RevisionNotifier
 */
public class RevisionNotifier
{
    private static final Logger LOGGER = Logger.getLogger(RevisionNotifier.class);

    private static final int THREAD_KEEP_ALIVE = 10;
    private static final int QUEUE_CAPACITY = 100;

    private static final String EXTENSION_NAMESPACE = "org.xerela.provider.configstore"; //$NON-NLS-1$
    private static final String EXTENSION_POINT_ID = EXTENSION_NAMESPACE + ".newRevision"; //$NON-NLS-1$

    private Lock extensionLoadLock;
    private Class<?>[] observerExtensions;

    private ThreadPoolExecutor executor;

    /**
     * Default constructor.
     */
    public RevisionNotifier()
    {
        extensionLoadLock = new ReentrantLock();
        NotifierThreadFactory threadFactory = new NotifierThreadFactory();
        executor = new ThreadPoolExecutor(1, 5, THREAD_KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_CAPACITY), threadFactory);
    }

    /**
     * Notify revision observers that the specified device has new/changed revisions.
     *
     * @param device a device that has new revisions
     * @param configs a list of ConfigHolder's of changed configurations
     */
    public void notifyRevisionObservers(ZDeviceCore device, List<ConfigHolder> configs)
    {
        executor.execute(new NotifierRunnable(device, configs));
    }

    // ----------------------------------------------------------------------
    //                     P R I V A T E   M E T H O D S
    // ----------------------------------------------------------------------

    /**
     * 
     */
    private List<IRevisionObserver> createObserverExtensions()
    {
        extensionLoadLock.lock();
        try
        {
            if (observerExtensions == null)
            {
                IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
                IConfigurationElement[] configElements = extensionRegistry.getConfigurationElementsFor(EXTENSION_POINT_ID);

                observerExtensions = new Class[configElements.length];
                if (configElements.length == 0)
                {
                    LOGGER.warn("No Backup Persist extensions discovered."); //$NON-NLS-1$
                }
                else
                {
                    int i = 0;
                    for (IConfigurationElement element : configElements)
                    {
                        String className = element.getAttribute("class"); //$NON-NLS-1$
                        try
                        {
                            String targetBundle = element.getContributor().getName();
                            Bundle bundle = ConfigStoreActivator.getBundle(targetBundle);
                            Class<?> clazz = bundle.loadClass(className);
                            observerExtensions[i++] = clazz;
                        }
                        catch (ClassNotFoundException cnfe)
                        {
                            LOGGER.error(String.format("ConfigStore bundle unable to load extension class '%s'", className), cnfe); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        finally
        {
            extensionLoadLock.unlock();
        }

        ArrayList<IRevisionObserver> extensionList = new ArrayList<IRevisionObserver>();
        for (Class<?> clazz : observerExtensions)
        {
            try
            {
                extensionList.add((IRevisionObserver) clazz.newInstance());
            }
            catch (Exception e)
            {
                LOGGER.warn(String.format("Unable to create Revision Observer extension %s", clazz.getName())); //$NON-NLS-1$
            }
        }

        return extensionList;
    }

    // ----------------------------------------------------------------------
    //                       I N N E R   C L A S S E S
    // ----------------------------------------------------------------------

    /**
     * NotifierRunnable
     */
    private class NotifierRunnable implements Runnable
    {
        private ZDeviceCore device;
        private List<ConfigHolder> configs;

        public NotifierRunnable(ZDeviceCore device, List<ConfigHolder> configs)
        {
            this.device = device;
            this.configs = configs;
        }

        public void run()
        {
            List<IRevisionObserver> observers = createObserverExtensions();
            for (IRevisionObserver observer : observers)
            {
                try
                {
                    observer.revisionChange(device, configs);
                }
                catch (Exception e)
                {
                    // Don't let anyone interrupt us from calling all observers.
                    // They better log their own errors, etc. because we're going
                    // to keep going.
                    continue;
                }
            }
        }
    }

    /**
     * NotifierThreadFactory
     */
    private class NotifierThreadFactory implements ThreadFactory
    {
        public Thread newThread(Runnable r)
        {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("Revision Notifier Thread"); //$NON-NLS-1$
            return t;
        }
    }
}
