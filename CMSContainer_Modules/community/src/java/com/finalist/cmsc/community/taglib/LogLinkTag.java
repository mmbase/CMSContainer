package com.finalist.cmsc.community.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class LogLinkTag extends AbstractSSOTag  {

   @Override
   protected String getValue() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
      
      String link = "";
      StringBuffer backUrl = req.getRequestURL();
      if(req.getQueryString() != null) {
         backUrl.append("?"+req.getQueryString());
      }
      org.acegisecurity.Authentication authentication = (org.acegisecurity.Authentication)req.getSession().getAttribute("session_key_subject");
      if(authentication == null) {
         link = getParameter("casServerLoginUrl")+"?servie="+backUrl;
      }
      else {
         link = getParameter("casServerLogoutUrl");
      }
      return link;
   }  

}
