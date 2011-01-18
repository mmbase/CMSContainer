package com.finalist.cmsc.community.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.services.community.person.PersonLDAPService;
import com.finalist.cmsc.services.community.person.PersonService;

public class GenderTag extends SimpleTagSupport {

   private String var;
   
   public void setVar(String var) {
      this.var = var;
   }

   @Override
   public void doTag() throws JspException, IOException {
      PageContext pctx = (PageContext) getJspContext();
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(pctx.getServletContext());
      ctx.getAutowireCapableBeanFactory().autowireBeanProperties(this, Autowire.BY_NAME.value(), false);
      HttpServletRequest req = (HttpServletRequest) pctx.getRequest();

      PersonService ps = getPersonLDAPService();
      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication != null) {
         String value = ps.getGenderByUserId(authentication.getName());
         if (var != null) {
            if (value != null) {
               req.setAttribute(var, value);
            } else {
               req.removeAttribute(var);
            }
         } else {
            pctx.getOut().print(value);
         }
      }      
   }
   
   private PersonService personLDAPService;

   public PersonService getPersonLDAPService() {
      return personLDAPService;
   }

   public void setPersonLDAPService(PersonService personLDAPService) {
      this.personLDAPService = personLDAPService;
   }

}
