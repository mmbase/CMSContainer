package com.finalist.cmsc.community.taglib;

import java.io.IOException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

public abstract class AbstractSSOTag extends CommunityTagSupport{
  
   private static final Log log = LogFactory.getLog(UseSSOTag.class);
   private static final String BASE = "java:comp/env";
   private String var;    
   private Context env;
   
   public void setVar(String var) {
      this.var = var;
   }
   
   @Override
   protected void doTagLogic() throws JspException, IOException{
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

      Context context;
      try {
         context = new InitialContext();
         env = (Context) context.lookup(BASE);
      } 
      catch (NamingException e) {
         log.error(e);
      }
      
      String value = getValue();
      if (var != null) {
         if (value != null) {
            req.setAttribute(var, value);
         }
         else {
            req.removeAttribute(var);
         }
      }
      else {
         ctx.getOut().print(value);
      }
   }
   /**
    * Get environment from context.xml
    * @param name
    * @return value
    */
   protected String getEnvironment(String name) {
      String value = null;
      try {
         Map<?, ?> environment = this.env.getEnvironment();
         if (environment != null) {
            value = (String) environment.get(name);
         }
         if (value == null) {
            value = (String) env.lookup(name);
         }
      }
      catch (NamingException e) {
         log.error(e);
      }
      return value;
   }
   
   protected String getPath() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
      PortalURL currentURL = env.getRequestedPortalURL();
      String path = currentURL.getGlobalNavigationAsString();
      if (ServerUtil.useServerName()) {
         path = request.getServerName() + "/" + path;
      }
      return path;
   }
   
   /**
    * Get parameter from context.xml
    * @param name
    * @return value
    */
   protected String getParameter(String name) {
      String value = null;
      PageContext ctx = (PageContext) getJspContext();
      value = ctx.getServletContext().getInitParameter(name);
      return value;
   }
   protected abstract String getValue();
}
