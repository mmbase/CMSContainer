package com.finalist.cmsc.community.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CommunityLogoutServlet extends HttpServlet {

   @Override
   public void init(ServletConfig config) throws ServletException {
      super.init(config);
   }
   
   @Override
   public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
      request.getSession().invalidate();
      response.sendRedirect(getServletContext().getInitParameter("casServerLogoutUrl"));
   }
}
