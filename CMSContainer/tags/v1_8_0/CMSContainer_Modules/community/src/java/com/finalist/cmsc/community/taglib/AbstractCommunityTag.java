package com.finalist.cmsc.community.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.acegisecurity.context.SecurityContextHolder;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;

public abstract class AbstractCommunityTag extends CommunityTagSupport {
   
   private String var;
   
   @Override
   protected void doTagLogic() throws JspException, IOException {

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

      PersonService ps = getPersonService();
      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication != null) {
         Person person = ps.getPersonByUserId(authentication.getName());
         String value = getValue(person); 
         if (var != null) {
            if (person != null && value != null) {
               req.setAttribute(var, value);
            } else {
               req.removeAttribute(var);
            }
         } else {
            ctx.getOut().print(value);
         }
      }      
   }
   
   public void setVar(String var) {
      this.var = var;
   }
   
   protected abstract String getValue(Person person);
}
