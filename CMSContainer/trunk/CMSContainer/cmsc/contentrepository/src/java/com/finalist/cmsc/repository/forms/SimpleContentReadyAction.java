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

public class SimpleContentReadyAction extends SimpleContentAction{

   private static final Logger log = Logging.getLoggerInstance(SimpleContentReadyAction.class.getName());
   
   @Override
   public void doAction(NodeQuery query, ActionForm form, HttpServletRequest request, Cloud cloud) {
      
      NodeManager nodeManager = cloud.getNodeManager(SimpleContentUtil.CONTENT_TYPE);
      SortedSet<Integer> set = SimpleContentUtil.getDraftWorkFlowItem(cloud);  
      SortedSet<Integer> contentSet = SimpleContentUtil.getAllContent(cloud);
      contentSet.removeAll(set);
      SearchUtil.addInConstraint(query, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), contentSet);
      request.setAttribute("statustype", "ready");
   }
}
