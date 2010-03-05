package com.finalist.cmsc.community.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class ListGroupsTag extends CommunityTagSupport {
   private String var;

   @Override
   protected void doTagLogic() throws IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

      AuthorityService as = getAuthorityService();
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
         ctx.getOut().print(list);
      }

   }

   public void setVar(String var) {
      this.var = var;
   }
}
