package com.finalist.cmsc.community.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class CommunityLogoutServlet extends HttpServlet {

   private static final Logger log = Logger.getLogger(CommunityLogoutServlet.class);
   @Override
   public void init(ServletConfig config) throws ServletException {
      super.init(config);
   }
   
   @Override
   public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
      String locale = null;
      if (request.getSession().getAttribute("cas_login_locale") != null) {
         locale = (String)request.getSession().getAttribute("cas_login_locale");       
      }
      request.getSession().invalidate();
      response.sendRedirect(getServletContext().getInitParameter("casServerLogoutUrl"+"_"+(locale==null?"":locale)));
    
      log.error("There is no right casServerLogoutUrl in the context.xml");
   }
}
