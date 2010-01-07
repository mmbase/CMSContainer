package com.finalist.cmsc.community.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.acegisecurity.context.SecurityContextHolder;

import com.finalist.cmsc.util.HttpUtil;

public class LogLinkTag extends AbstractSSOTag  {

   private String referurl;    

   
   public void setReferurl(String referurl) {
      this.referurl = referurl;
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
         link = getParameter("casServerLoginUrl")+"?service="+backUrl;
      }
      else {
         link = HttpUtil.getWebappUri(req)+"LogoutServlet";
      }
      return link;
   }  

}
