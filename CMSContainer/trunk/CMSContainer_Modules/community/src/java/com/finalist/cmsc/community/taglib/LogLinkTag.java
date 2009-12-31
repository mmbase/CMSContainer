package com.finalist.cmsc.community.taglib;

import org.acegisecurity.context.SecurityContextHolder;

public class LogLinkTag extends AbstractSSOTag  {

   @Override
   protected String getValue() {
      String link = "";
      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication == null) {
         link = getParameter("casServerLoginUrl");
      }
      else {
         link = getParameter("casServerLogoutUrl");
      }

      return link;
   }  

}
