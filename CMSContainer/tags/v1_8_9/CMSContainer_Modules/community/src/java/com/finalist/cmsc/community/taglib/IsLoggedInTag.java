package com.finalist.cmsc.community.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.acegisecurity.context.SecurityContextHolder;

public class IsLoggedInTag extends CommunityTagSupport  {

   private String var;
   
   public void setVar(String var) {
      this.var = var;
   }

   @Override
   protected void doTagLogic() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String value = "false";
      if(authentication != null) {
         value = "true";
      }
      if (var != null) {
         req.setAttribute(var, value);
      }
      else {
         ctx.getOut().print(value);
      }
   }

}
