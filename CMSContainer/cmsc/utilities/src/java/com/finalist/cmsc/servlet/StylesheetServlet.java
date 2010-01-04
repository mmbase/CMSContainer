/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.servlet.BridgeServlet;

/**
 * @author
 */
@SuppressWarnings( { "serial", "unused" })
public class StylesheetServlet extends BridgeServlet {

   @Override
   public void init(ServletConfig config) throws ServletException {
      super.init(config);
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      doRedirect(request, response);
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      doRedirect(request, response);
   }

   private void doRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String text = null;
      text = request.getParameter("text");

      response.setContentType("text/css");
      response.getWriter().print(text);
   }
}
