package com.finalist.cmsc.repository.forms;

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.SimpleContentUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.util.ServerUtil;

public class SimpleContentInitAction extends MMBaseFormlessAction {

   private static final Logger log = Logging
   .getLoggerInstance(SimpleContentInitAction.class.getName());

   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {
      NodeManager nodeManager = cloud.getNodeManager(SimpleContentUtil.CONTENT_TYPE);
      NodeQuery draftContentQuery = SimpleContentUtil.createQuery(cloud,nodeManager,null,1,0);
      if ( !ServerUtil.isSingle() && ServerUtil.isStaging()) {
         SortedSet<Integer> set = SimpleContentUtil.getDraftWorkFlowItem(cloud);
         SortedSet<Integer> contentSet = SimpleContentUtil.getAllContent(cloud);

         SearchUtil.addInConstraint(draftContentQuery, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), set);
         contentSet.removeAll(set);
         
         NodeQuery finishedContentQuery = SimpleContentUtil.createQuery(cloud,nodeManager,null,1,0);
         SearchUtil.addInConstraint(finishedContentQuery, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), contentSet);
         
         int finishedResultCount = Queries.count(finishedContentQuery);
         NodeList finishedResults = finishedContentQuery.getNodeManager().getList(finishedContentQuery);
         
         request.setAttribute("finishedResultCount", finishedResultCount);
         request.setAttribute("finishedResults", finishedResults);
      }
      int resultCount = Queries.count(draftContentQuery);
      NodeList results = draftContentQuery.getNodeManager().getList(draftContentQuery);
      request.setAttribute("resultCount", resultCount);
      request.setAttribute("results", results);
      
      return mapping.findForward(SUCCESS);
   }
}
