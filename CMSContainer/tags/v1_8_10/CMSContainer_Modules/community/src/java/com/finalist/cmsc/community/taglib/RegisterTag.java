package com.finalist.cmsc.community.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class RegisterTag extends AbstractSSOTag  {

   private static final String CAS_REGISTOR_LOCALE = "cas_registor_locale";
//   private String referurl;    
   private String locale;
   
//   public void setReferurl(String referurl) {
//      this.referurl = referurl;
//   }
   
   public void setLocale(String locale) {
      this.locale = locale;
   }
   
   @Override
   protected String getValue() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

      String link = "";
//      StringBuffer backUrl = new StringBuffer();
//      if (referurl != null) {
//         backUrl = backUrl.append(ctx.getAttribute(referurl));
//      }
//      else {
//         backUrl.append(HttpUtil.getWebappUri(req));
//         backUrl.append(getPath());
//      }
      String defaultRegisterUrl = "casServerRegisterUrl";
      if (locale != null) {
         defaultRegisterUrl += "_" + locale;
      }
//      link = getParameter(defaultRegisterUrl) + "?service=" + backUrl;
      link = getParameter(defaultRegisterUrl);
      req.getSession().setAttribute(CAS_REGISTOR_LOCALE, locale);
      return link;
   }

}
