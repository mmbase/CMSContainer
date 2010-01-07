/**
 * 
 */
package com.finalist.cmsc.community.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.finalist.cmsc.services.community.security.AuthorityService;


/**
 * @author Billy
 *
 */
public class AddAuthorityTag extends SimpleTagSupport {
   
   private String value;

   
   public String getValue() {
      return value;
   }

   
   public void setValue(String value) {
      this.value = value;
   }
   
   @Override
   public void doTag() throws JspException, IOException {
      PageContext pctx = (PageContext) getJspContext();
      WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(pctx.getServletContext());
      ctx.getAutowireCapableBeanFactory().autowireBeanProperties(this, Autowire.BY_NAME.value(), false);

      AuthorityService as = getAuthorityService();
      boolean exist = as.authorityExists(getValue());
      if(!exist){
         as.createAuthority("", getValue());
      }      
   }
   
   private AuthorityService authorityService;

   public AuthorityService getAuthorityService() {
      return authorityService;
   }

   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

}
