/*
 * Copyright (c) Rafael Steil
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor
 * the names of its contributors may be used to endorse
 * or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * Created on Mar 28, 2005 7:36:00 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.sso;

import javax.servlet.http.HttpServletRequest;

import net.jforum.ActionServletRequest;
import net.jforum.entities.UserSession;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Simple SSO authenticator.
 * This class will try to validate an user by simple
 * checking <code>request.getRemoteUser()</code> is not
 * null.
 *
 * @author Rafael Steil
 * @author Daniel Campagnoli
 * @version $Id: RemoteUserSSO.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class RemoteUserSSO implements SSO {
    public static final String JFORUM_USERNAME = "JFORUM_USERNAME";

    /**
     * @see net.jforum.sso.SSO#authenticateUser(net.jforum.ActionServletRequest)
     */
    public String authenticateUser(ActionServletRequest request) {
        String remoteUser = request.getRemoteUser();
        if (remoteUser == null) {
            remoteUser = (String) request.getSession().getAttribute(JFORUM_USERNAME);
        }
        return remoteUser;
    }


    public boolean isSessionValid(UserSession userSession, HttpServletRequest request) {
        String remoteUser = request.getRemoteUser();

        // user has since logged out
        if (remoteUser == null && userSession.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
            return false;

            // user has since logged in
        } else
        if (remoteUser != null && userSession.getUserId() == SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
            return false;

            // user has changed user
        } else if (remoteUser != null && !remoteUser.equals(userSession.getUsername())) {
            return false;
        }
        return false;
    }

}