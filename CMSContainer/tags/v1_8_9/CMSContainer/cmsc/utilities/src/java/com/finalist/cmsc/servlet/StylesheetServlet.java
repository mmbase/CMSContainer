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

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
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
      renderText(request, response);
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      renderText(request, response);
   }

   private void renderText(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String pathInfo = request.getPathInfo();
      response.setContentType("text/css");
      String number = null;
      if (pathInfo != null) {
         number = pathInfo.replaceAll("/|.css", "");
      }
      if (number != null) {
         Cloud cloud = getAnonymousCloud();
         if (number.matches("[0-9]+") && cloud != null && cloud.hasNode(number)) {
            Node stylesheet= getAnonymousCloud().getNode(number);
            String text = stylesheet.getStringValue("text");
            response.getWriter().print(text);
         }
         else {
            response.getWriter().print("404 NOT FOUND page");
         }
      }

   }
}
