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

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.Community;

public class PortalFilter implements Filter
{
   protected String guest = "guest";
   protected static final String ACEGI_SECURITY_FORM_USERNAME_KEY = "j_username";
   protected static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "j_password";
   protected static final Log log = LogFactory.getLog(PortalFilter.class);
   
   private static final String SESSION_KEY_SUBJECT = "session_key_subject";
   
   
   public void init(FilterConfig filterConfig) throws ServletException{
               
   }
   
   public void doFilter(ServletRequest sRequest,
          ServletResponse sResponse, FilterChain filterChain)
          throws IOException, ServletException
   {
      if (sRequest instanceof HttpServletRequest) {
         HttpServletRequest request = (HttpServletRequest)sRequest;
         String username = request.getParameter(ACEGI_SECURITY_FORM_USERNAME_KEY);
         String password = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);            
            if (username != null) {   
                Community.login(username, password);
                if (Community.isAuthenticated())
                {
                   Principal principal = Community.getPrincipal();
                   
                   Set<Principal> principals = new HashSet();
                   principals.add(principal);
                   Subject subject = new Subject(true, principals, new HashSet(), new HashSet());

                   sRequest = wrapperRequest(request, subject, principal);
                   HttpSession session = request.getSession(true);
                  session.setAttribute(SESSION_KEY_SUBJECT, subject);
               }
            else
            {
                log.debug("######################## Eerror happens when the user logs in");
            }
         }
         else
         {
          //HttpSession session = request.getSession();
          //System.out.println("*** session = " + session);
             Subject subject = (Subject)request.getSession().getAttribute(SESSION_KEY_SUBJECT);
             if (subject != null)
             {
                 Principal principal = Community.getPrincipal();
                 if (principal != null && principal.getName().equals(this.guest))
                 {                        
                 }
                 else
                 {
                    sRequest = wrapperRequest(request, subject, principal);
                 }
             }                
         }   
      }
        
      if (filterChain != null)
      {
          filterChain.doFilter(sRequest, sResponse);
      }
   }
    
   private ServletRequest wrapperRequest(HttpServletRequest request, Subject subject, Principal principal)
   {
       PortalRequestWrapper wrapper = new PortalRequestWrapper(request, subject, principal);
       return wrapper;
   }
   
   public void destroy()
   {
   }
}
