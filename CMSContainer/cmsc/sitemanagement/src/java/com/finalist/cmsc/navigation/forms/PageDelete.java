/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class PageDelete extends MMBaseFormlessAction {

   /** porlet name **/
   private static final String FIELD_NAME = "name";
   /** page builder name **/
   private static final String PAGE = "page";

   /** name of submit button in jsp to confirm removal */
   private static final String ACTION_REMOVE = "remove";

   /** name of submit button in jsp to cancel removal */
   private static final String ACTION_CANCEL = "cancel";


   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String objectnumber = getParameter(request, "number", true);
      Node pageNode = cloud.getNode(objectnumber);
      if (isRemoveAction(request)) {

         UserRole role = NavigationUtil.getRole(pageNode.getCloud(), pageNode, false);
         boolean isWebMaster = (role != null && SecurityUtil.isWebmaster(role));

         if (NavigationUtil.getChildCount(pageNode) > 0 && !isWebMaster) {
            return mapping.findForward("pagedeletewarning");
         }
         NavigationUtil.deleteItem(pageNode);
         return mapping.findForward(SUCCESS);
      }

      if (isCancelAction(request)) {
         return mapping.findForward(SUCCESS);
      }
      //CMSC-1306
      if(PortletUtil.findLinkedPorlets(pageNode).size() > 0) {
         NodeList porlets = PortletUtil.findLinkedPorlets(pageNode);
         Map<String,Integer> hashMap = new HashMap<String,Integer>();
         for(int i = 0 ; i < porlets.size() ; i++) {
            Node portlet = porlets.getNode(i);
            Node page = portlet.getRelatedNodes(PAGE).getNode(0);
            Relation relation =RelationUtil.getRelation(portlet.getCloud().getNodeManager("portletrel"), page.getNumber(), portlet.getNumber());
            String portletName = relation.getStringValue(FIELD_NAME);
            String url = SiteManagement.getPath(page.getNumber(), true);
            if(!url.endsWith("/")) {
               url += "/";
            }
            url += "_md_"+portletName+"/edit_defaults";
            hashMap.put(url, page.getNumber());
         }

         request.setAttribute("pageMap", hashMap);
         return mapping.findForward("linkedlist");
      }
      
      // neither remove or cancel, show confirmation page
      return mapping.findForward("pagedelete");
   }

   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }


   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

}
