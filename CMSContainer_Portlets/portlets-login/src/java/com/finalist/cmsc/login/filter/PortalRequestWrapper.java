/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finalist.cmsc.login.filter;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.finalist.cmsc.services.community.Community;

public class PortalRequestWrapper extends HttpServletRequestWrapper
{
    private Principal userPrincipal = null;
    private Subject subject ;
    
    public PortalRequestWrapper(HttpServletRequest request, Subject subject,
            Principal userPrincipal)
    {
        super(request);
        this.subject = subject;
        this.userPrincipal = userPrincipal;
    }

    public boolean isUserInRole(String roleName)
    {
        if (subject == null)
        {
            return false;
        }
        List<String> roles = Community.getAuthorities();
        Iterator<String> ir = roles.iterator();
        while (ir.hasNext()) {
           String role = ir.next();
           if (roleName.equals(role)) {
              return true;
           }
        }
       
        return false;
    }
    
    public void setUserPrincipal(Principal userPrincipal)
    {
        this.userPrincipal = userPrincipal;
    }

    public Principal getUserPrincipal()
    {
        return userPrincipal;
    }

    /**
     * Return the name of the remote user that has been authenticated
     * for this Request.
     */
    public String getRemoteUser()
    {
        if (userPrincipal != null)
        {
            return userPrincipal.getName();
        }
        else
        {
            return null;
        }
    }

}
