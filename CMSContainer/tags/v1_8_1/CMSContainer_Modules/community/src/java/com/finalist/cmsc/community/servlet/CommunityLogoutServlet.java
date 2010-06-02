package com.finalist.cmsc.community.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.finalist.cmsc.services.community.Community;

@SuppressWarnings("serial")
public class CommunityLogoutServlet extends HttpServlet {

   private static final String CAS_SERVER_LOGOUT_URL = "casServerLogoutUrl";
   private static final String CAS_LOGIN_LOCALE = "cas_login_locale";
   private static final Logger log = Logger.getLogger(CommunityLogoutServlet.class);
   @Override
   public void init(ServletConfig config) throws ServletException {
      super.init(config);
   }
   
   @Override
   public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
      String locale = null;
      if (request.getSession().getAttribute(CAS_LOGIN_LOCALE) != null) {
         locale = (String)request.getSession().getAttribute(CAS_LOGIN_LOCALE);       
      }
     // request.getSession(false).removeAttribute(HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY);
      HttpSession session = request.getSession(false);
      if (session != null) {
         session.invalidate();
      }
     
      Community.logout();
      
      String defaultLogoutUrl = CAS_SERVER_LOGOUT_URL;
      if (locale != null) {
         defaultLogoutUrl += "_"+locale;
      }
      log.debug("Community logout url : "+defaultLogoutUrl);
      response.sendRedirect(getServletContext().getInitParameter(defaultLogoutUrl));
   }
}
