package com.finalist.cmsc.repository.forms;

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.SimpleContentUtil;
import com.finalist.cmsc.util.ServerUtil;

public class SimpleContentDraftAction extends SimpleContentAction{

   static final Logger log = Logging.getLoggerInstance(SimpleContentDraftAction.class.getName());
   
   @Override
   public void addConstraints(NodeQuery query, ActionForm form, HttpServletRequest request, Cloud cloud) {
      request.setAttribute("statustype", "draft");
      NodeManager nodeManager = cloud.getNodeManager(SimpleContentUtil.CONTENT_TYPE);
      if ( !ServerUtil.isSingle() && ServerUtil.isStaging()) {
         SortedSet<Integer> draftSet = SimpleContentUtil.getDraftWorkFlowItem(cloud);  
         SearchUtil.addInConstraint(query, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), draftSet);
         //Judge it is staging/live model
         request.setAttribute("single", "false");
      }
      // cmsc-1479
      bindTypeList(request, cloud);
      bindChannelList(request, cloud);
      // end of cmsc-1479
   }
}
