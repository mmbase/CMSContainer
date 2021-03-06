/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class PersonalPageCreate extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String parentpage = getParameter(request, "parentpage", true);
      String action = getParameter(request, "action");
      String from = getParameter(request, "from");

      if (StringUtils.isBlank(action)) {
         request.getSession().setAttribute("parentpage", parentpage);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?action=create"
               + "&contenttype=personalpage" + "&returnurl=" + mapping.findForward("returnurl").getPath() + "?from=" + from);
         ret.setRedirect(true);
         return ret;
      }
      else {
         if ("save".equals(action)) {
            String ewnodelastedited = getParameter(request, "ewnodelastedited");
            NavigationUtil.appendChild(cloud, parentpage, ewnodelastedited);
            
            Node newPage = cloud.getNode(ewnodelastedited);
            Node layoutNode = PagesUtil.getLayout(newPage);
            PagesUtil.linkPortlets(newPage, layoutNode);
            
            SubSiteUtil.createPersonalPageContentChannel(newPage);
            
            addToRequest(request, "showsubsite", ewnodelastedited);
            ActionForward ret = new ActionForward(mapping.findForward(SUCCESS).getPath() + "?from=" + from + "&subsite=" + parentpage);
            return ret;
         }
         request.getSession().removeAttribute("parentpage");
         ActionForward ret = new ActionForward(mapping.findForward(CANCEL).getPath() + "?from=" + from + "&subsite=" + parentpage);
         return ret;
      }
   }

}
