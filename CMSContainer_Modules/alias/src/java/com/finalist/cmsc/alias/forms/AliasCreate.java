/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.alias.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class AliasCreate extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String parentpage = getParameter(request, "parentpage", true);
      String action = getParameter(request, "action");
      boolean stacked=(request.getParameter("stacked") != null && request.getParameter("stacked").equals("true"));

      if (StringUtils.isBlank(action)) {
         request.getSession().setAttribute("parentpage", parentpage);

         
         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?action=create"
               + "&contenttype=pagealias" + "&returnurl=" + mapping.findForward("returnurl").getPath() + "?stacked="+stacked);
         ret.setRedirect(true);
         return ret;
      }
      else {
         if ("save".equals(action)) {
            String ewnodelastedited = getParameter(request, "ewnodelastedited");
            NavigationUtil.appendChild(cloud, parentpage, ewnodelastedited);

            addToRequest(request, "showalias", ewnodelastedited);
            addToRequest(request, "refreshChannels", "true");
            if(!stacked) {
	            return mapping.findForward(SUCCESS);
            }
            else {
            	return new ActionForward(mapping.findForward("stacked").getPath()+"?parent="+parentpage);
            }
         }
         request.getSession().removeAttribute("parentpage");
         ActionForward ret = mapping.findForward(CANCEL);
         return ret;
      }
   }

}
