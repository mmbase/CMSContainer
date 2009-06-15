/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NotFoundException;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class ChannelRetrieve extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      String quicksearch = request.getParameter("quicksearch");
      if (quicksearch != null && quicksearch.trim().length() > 0) {
         Integer intValue = null;
         try {
            intValue = Integer.valueOf(quicksearch);
         } catch (Exception e) {
            // not an integer then it is a path
         }

         String channel = "notfound";
         if (intValue != null && isValidChannel(cloud, intValue)) {
            channel = intValue.toString();
         } else {
            Node node = getChannelFromPath(cloud, quicksearch);
            String path = quicksearch;
            int index = path.lastIndexOf(TreeUtil.PATH_SEPARATOR);
            while (node == null && index != -1) {
               path = path.substring(0, index);
               node = getChannelFromPath(cloud, path);
               index = path.lastIndexOf(TreeUtil.PATH_SEPARATOR);
            }

            if (node != null) {
               channel = node.getStringValue("number");
            }
         }
         response.getWriter().print(channel);
      }
      return null;
   }

   protected boolean isValidChannel(Cloud cloud, int channelNumber) {
      try {
         Node node = cloud.getNode(channelNumber);
         if (node != null) {
            String nodeManagerName = node.getNodeManager().getName();
            return RepositoryUtil.getTreeManagers().containsKey(nodeManagerName);
         }
      } catch (NotFoundException nfe) {
         // when not found, it is not a valid number
      }
      return false;
   }

   protected Node getChannelFromPath(Cloud cloud, String quicksearch) {
      if (quicksearch.equals("/")) {
         quicksearch = "repository";
      }
      return RepositoryUtil.getChannelFromPath(cloud, quicksearch);
   }
}
