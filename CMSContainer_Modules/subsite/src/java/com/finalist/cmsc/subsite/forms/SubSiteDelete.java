/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class SubSiteDelete extends MMBaseFormlessAction {

   /** name of submit button in jsp to confirm removal */
   private static final String ACTION_REMOVE = "remove";

   /** name of submit button in jsp to cancel removal */
   private static final String ACTION_CANCEL = "cancel";


   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String from = request.getParameter("from");
      if (from != null) {
         request.setAttribute("from", from);
      }
      String subsite = getParameter(request, "subsite");
      if (isRemoveAction(request)) {
         String objectnumber = getParameter(request, "number", true);
         Node subsiteNode = cloud.getNode(objectnumber);
         Node pageNode = subsiteNode.getRelatedNodes("page").getNode(0);

         UserRole role = NavigationUtil.getRole(pageNode.getCloud(), pageNode, false);
         boolean isEditor = (role != null && SecurityUtil.isEditor(role));

         if (isEditor) {
            NavigationUtil.deleteItem(subsiteNode);
         }
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?from=" + from + "&subsite=" + subsite);
      }

      if (isCancelAction(request)) {
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?from=" + from + "&subsite=" + subsite);
      }

      // neither remove or cancel, show confirmation page
      return new ActionForward(mapping.findForward("delete").getPath() + "?from=" + from + "&subsite=" + subsite);
   }


   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }


   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

}
