package com.finalist.cmsc.community.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.services.community.security.AuthorityLDAPService;
import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class ListLDAPGroupsTag extends SimpleTagSupport {
   
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

      AuthorityService as = getAuthorityLDAPService();
      ArrayList<String> list = new ArrayList<String>(as.getAuthorityNames());
      Collections.sort(list, new Comparator<String>() {
    	  public int compare(String o1, String o2) {
    		  return o1.compareToIgnoreCase(o2);
    	  }
      });

      if (var != null) {
         if (list != null) {
            req.setAttribute(var, list);
         } else {
            req.removeAttribute(var);
         }
      } else {
         pctx.getOut().print(list);
      }
   }

   private AuthorityLDAPService authorityLDAPService;

   public AuthorityLDAPService getAuthorityLDAPService() {
      return authorityLDAPService;
   }

   public void setAuthorityLDAPService(AuthorityLDAPService authorityLDAPService) {
      this.authorityLDAPService = authorityLDAPService;
   }

}
