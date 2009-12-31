package com.finalist.cmsc.community.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.acegisecurity.context.SecurityContextHolder;

public class IsLoggedInTag extends CommunityTagSupport  {

   private String var;
   
   @Override
   protected void doTagLogic() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication != null) {
         if (var != null) {
            if (authentication.getName() != null) {
               req.setAttribute(var,"true");
            } else {
               req.removeAttribute(var);
            }
         }
         else {
            ctx.getOut().print("true");
         }
      }
      else {
         ctx.getOut().print("false");
      }
   }

}
