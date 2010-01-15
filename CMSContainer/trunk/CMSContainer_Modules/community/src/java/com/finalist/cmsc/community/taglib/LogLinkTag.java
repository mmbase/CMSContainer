package com.finalist.cmsc.community.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.acegisecurity.context.SecurityContextHolder;

import com.finalist.cmsc.util.HttpUtil;

public class LogLinkTag extends AbstractSSOTag  {

   private static final String CAS_LOGIN_LOCALE = "cas_login_locale";
   private String referurl;    

   private String locale;
   
   public void setReferurl(String referurl) {
      this.referurl = referurl;
   }
   
   public void setLocale(String locale) {
      this.locale = locale;
   }
   
   @Override
   protected String getValue() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
      
      String link = "";
      StringBuffer backUrl = new StringBuffer();
      if (referurl != null) {
         backUrl = backUrl.append(ctx.getAttribute(referurl));
      }
      else {
         backUrl.append(HttpUtil.getWebappUri(req));
         backUrl.append(getPath());
      }
      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication == null) {
         link = getParameter("casServerLoginUrl"+"_"+locale)+"?service="+backUrl;
         req.getSession().setAttribute(CAS_LOGIN_LOCALE, locale);
      }
      else {
         link = HttpUtil.getWebappUri(req)+"LogoutServlet";
      }
      return link;
   }  

}
