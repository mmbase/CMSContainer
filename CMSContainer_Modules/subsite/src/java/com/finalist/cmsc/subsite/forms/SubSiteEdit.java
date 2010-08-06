/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class SubSiteEdit extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = getParameter(request, "action");
      String subsite = request.getParameter("subsite");

      if (StringUtils.isBlank(action)) {
         String objectnumber = getParameter(request, "number", true);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber="
               + objectnumber + "&returnurl=" + mapping.findForward("returnurl").getPath() + URLEncoder.encode("?from=" + request.getParameter("from") + "&subsite=" + subsite));
         ret.setRedirect(true);
         return ret;
      }
      else {
         String ewnodelastedited = getParameter(request, "ewnodelastedited");
         addToRequest(request, "showsubsite", ewnodelastedited);
		if ("site".equalsIgnoreCase(getParameter(request, "from"))) {
         return mapping.findForward(SUCCESS);
      } else {
         return new ActionForward(mapping.findForward("modulesuccess").getPath() + "?from=" + request.getParameter("from") + "&subsite=" + subsite);
      }
   }
  }
}
