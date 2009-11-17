/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.knownvisitor;

import java.io.IOException;

import java.util.*;

import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.util.Encode;

public class VisitorFilter implements Filter {

   private static final Log log = LogFactory.getLog(VisitorFilter.class);
   
   public void init(FilterConfig filterConfig) {
      // nothing
   }

   public void destroy() {
      // nothing
   }

   /**
    * This method simply calls <tt>negotiate( req, resp, false )</tt> and then
    * <tt>chain.doFilter</tt>. You can override and call negotiate manually
    * to achive a variety of different behavior.
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {
      final HttpServletRequest req = (HttpServletRequest) request;
      final HttpServletResponse resp = (HttpServletResponse) response;

      if (KnownVisitorModule.getInstance().isEnabled() && !alreadyLoggedIn(req)) {
         List<String> exceptions = KnownVisitorModule.getInstance().getIpExceptions();
         if (!exceptions.isEmpty()) {
            
            List<String> ips = resolveRemoteAddresses(request);

            for (Iterator<String> iterator = ips.iterator(); iterator.hasNext();) {
               String addr = iterator.next();
               if (exceptions.contains(addr)) {
                  log.debug("Ip " + addr + " allowed.");
                  chain.doFilter(request, response);
                  return;
               }
            }
         }

         if (!negotiate(req, resp)) {
            return;
         }
      }
      chain.doFilter(req, resp);
   }

   private List<String> resolveRemoteAddresses(ServletRequest request) {
      List<String> ips = new ArrayList<String>();
      ips.add(request.getRemoteAddr());
      
      String ip = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
      if (StringUtils.isBlank(ip)) {
         // not behind a proxy or mod_proxy
         log.debug("Incoming ip, remote address = " + request.getRemoteAddr());
      }
      else {
         log.debug("Incoming ip, remote address = " + request.getRemoteAddr() + " X-Forwarded-For =" + ip);
         StringTokenizer token = new StringTokenizer(ip, ",");
         while(token.hasMoreTokens()) {
            ips.add(token.nextToken().trim());
         }
      }
      return ips;
   }

   /**
    * Negotiate password hashes with MSIE clients using NTLM SSP
    * 
    * @param req The servlet request
    * @param resp The servlet response
    * @return True if the negotiation is complete, otherwise false
    */
   protected boolean negotiate(HttpServletRequest req, HttpServletResponse resp)
         throws IOException, ServletException {
      String msg = req.getHeader("Authorization");
      boolean offerBasic = KnownVisitorModule.getInstance().offerBasic(req);
      log.debug("Message: " + msg);
      if (msg != null && offerBasic && msg.startsWith("Basic ")) {
         log.debug("Message starts with Basic.");
         String base64str = msg.substring(6);
         String auth = Encode.decode("BASE64", base64str);
         int index = auth.indexOf(':');
         String user = (index != -1) ? auth.substring(0, index) : auth;
         String password = (index != -1) ? auth.substring(index + 1) : "";
         index = user.indexOf('\\');
         if (index == -1)
            index = user.indexOf('/');
         
         String domain;
         if (index == -1) {
             domain = KnownVisitorModule.getInstance().getRealm();
         } else {
             domain = user.substring(0, index);
         }

         user = (index != -1) ? user.substring(index + 1) : user;
         
         
         try {
            log.debug("user " + user + " domain " + domain + " password " + password);
            KnownVisitorModule.getInstance().ldapLogin(user, password);
         }
         catch (NamingException e) {
            log.debug("" + e.getMessage(), e);
            // lets try mmbase authentication now
            return true;
         }
         
         req.getSession().setAttribute("NtlmHttpAuth", user);
         justLoggedIn(req, user);

         return true;
      }
      else {
         HttpSession ssn = req.getSession(false);
         if (offerBasic && (ssn == null || ssn.getAttribute("NtlmHttpAuth") == null)) {
            log.debug("Not BASIO authenticated, starting authentication.");
            String realm = KnownVisitorModule.getInstance().getRealm();
            if (realm == null) {
               realm = "CMS Container";
            }
            resp.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
         }
         resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         resp.setContentLength(0);
         resp.flushBuffer();
         return false;
      }
   }

   protected boolean alreadyLoggedIn(HttpServletRequest req) {
      return KnownVisitorModule.getInstance().getVisitor(req) != null;
   }

   public void justLoggedIn(HttpServletRequest request, String username) {
      Visitor visitor = new Visitor();
      visitor.setIdentifier(username);
      KnownVisitorModule.getInstance().readLdapInfo(visitor);
      KnownVisitorModule.getInstance().setVisitor(request, visitor);
   }
}
