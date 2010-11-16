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

package org.xerela.zap.web.internal;

import java.util.Arrays;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.mortbay.jetty.plus.jaas.callback.ObjectCallback;
import org.mortbay.jetty.plus.jaas.spi.AbstractLoginModule;
import org.mortbay.jetty.plus.jaas.spi.UserInfo;
import org.mortbay.jetty.security.Credential;

/**
 * NoopLoginModule
 */
public class NoopLoginModule extends AbstractLoginModule
{
    private Object webCredential;

    /**
     * Constructor
     */
    public NoopLoginModule()
    {
    }

    /** {@inheritDoc} */
    @Override
    public UserInfo getUserInfo(String username) throws Exception
    {
        String[] roles = { "nobody" }; //$NON-NLS-1$
        UserInfo userInfo = new UserInfo(username, Credential.getCredential(webCredential.toString()), Arrays.asList(roles));

        return userInfo;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public boolean login() throws LoginException
    {
        try
        {
            if (getCallbackHandler() == null)
            {
                throw new LoginException("No callback handler");
            }

            setAuthenticated(true);

            Callback[] callbacks = configureCallbacks();
            getCallbackHandler().handle(callbacks);

            String webUserName = ((NameCallback) callbacks[0]).getName();
            webCredential = ((ObjectCallback) callbacks[1]).getObject();

            if ((webUserName == null) || (webCredential == null))
            {
                setAuthenticated(false);
                return isAuthenticated();
            }

            UserInfo userInfo = getUserInfo(webUserName);

            setCurrentUser(new JAASUserInfo(userInfo));
            setAuthenticated(getCurrentUser().checkCredential(webCredential));
            return isAuthenticated();
        }
        catch (UnsupportedCallbackException e)
        {
            throw new LoginException(e.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new LoginException(e.toString());
        }
    }
}
