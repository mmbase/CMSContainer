package com.finalist.cmsc.workflow.forms;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningService;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class WorkflowItemDelete extends MMBaseFormlessAction{

   private static final String VERSION = "version";
   private static final String SOURCE = "SOURCE";
   private static final String WORKFLOWREL = "workflowrel";
   private static final String WORKFLOWITEM = "workflowitem";
   private static final String ARCHIVENUMBER = "archivenumber";
   private static final String ACTION = "action";
   private static final String RETURNURL = "returnurl";
   private static final String NUMBER = "number";
   private static Log log = LogFactory.getLog(WorkflowItemDelete.class);
   
   
   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {
      String number = getParameter(request, NUMBER, "");
      String returnPath = getParameter(request, RETURNURL, "");
      String action = getParameter(request, ACTION, null);
      String archivenumber = getParameter(request, ARCHIVENUMBER, "");
      
      Node elementNode = cloud.getNode(number);
      if(action != null && "delete".equals(action)) {
         if (cloud.hasNode(archivenumber)) {
            Node archiveNode = cloud.getNode(archivenumber);
            UserRole role = RepositoryUtil.getRole(cloud, RepositoryUtil.getCreationChannel(elementNode), false);
            boolean isWriter = SecurityUtil.isWriter(role);
            if (isWriter) {
               Versioning.restoreVersion(archiveNode);
            }
         }
         Node workflowItem = elementNode.getRelatedNodes(cloud.getNodeManager(WORKFLOWITEM), WORKFLOWREL, SOURCE).getNode(0);
         workflowItem.deleteRelations();
         workflowItem.delete();
         return new ActionForward(returnPath,true);
      }
      else {
         if(!Publish.isPublished(elementNode)) {
            returnPath += "&errMessage=workflow.item.delete.error";
            return new ActionForward(returnPath,true);
         }
         Node workflowItem = elementNode.getRelatedNodes(cloud.getNodeManager(WORKFLOWITEM), WORKFLOWREL, SOURCE).getNode(0);
         if(Versioning.isOnLive(elementNode)) {
            workflowItem.deleteRelations();
            workflowItem.delete();
         }
         else {
            NodeManager manager = cloud.getNodeManager(VersioningService.ARCHIVE);
            NodeQuery query = manager.createQuery();
            SearchUtil.addEqualConstraint(query, manager, VersioningService.ORIGINAL_NODE, Integer.parseInt(number));
            SearchUtil.addSortOrder(query, manager, VersioningService.DATE, "UP");
            NodeList archiveNodeList = manager.getList(query);
            request.setAttribute("archiveNodesSize", archiveNodeList.size());
            request.setAttribute("archiveNodes", archiveNodeList);
   
            UserRole role = RepositoryUtil.getRole(cloud, RepositoryUtil.getCreationChannel(elementNode), false);
            request.setAttribute("isAllowed", SecurityUtil.isWriter(role));
            request.setAttribute(ACTION,"workflow");
            request.setAttribute(NUMBER,number);
            request.setAttribute("returnUrl",returnPath);
            return mapping.findForward(VERSION);
         }
      }
      return new ActionForward(returnPath,true);
   }   
}
