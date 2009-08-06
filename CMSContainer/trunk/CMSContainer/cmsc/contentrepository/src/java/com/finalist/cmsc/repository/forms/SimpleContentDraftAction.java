package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.SimpleContentUtil;
import com.finalist.cmsc.util.ServerUtil;

public class SimpleContentDraftAction extends SimpleContentAction{

   private static final Logger log = Logging.getLoggerInstance(SimpleContentDraftAction.class.getName());
   
   private static final String SYSTEM_SIMPLEEDITOR_CONTENTTYPES = "system.simpleeditor.contenttypes";
     @Override
   public void doAction(NodeQuery query, ActionForm form, HttpServletRequest request, Cloud cloud) {
      request.setAttribute("statustype", "draft");
      NodeManager nodeManager = cloud.getNodeManager(SimpleContentUtil.CONTENT_TYPE);
      if ( !ServerUtil.isSingle() && ServerUtil.isStaging()) {
         SortedSet<Integer> set = SimpleContentUtil.getDraftWorkFlowItem(cloud);  
         SearchUtil.addInConstraint(query, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), set);
         //Judge it is staging/live model
         request.setAttribute("single", "false");
      }
      // cmsc-1479
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();
      if (PropertiesUtil.getProperty(SYSTEM_SIMPLEEDITOR_CONTENTTYPES) != null) {
         String[] rawTypes = PropertiesUtil.getProperty(SYSTEM_SIMPLEEDITOR_CONTENTTYPES).split(",");
         List<String> hiddenTypes = ContentElementUtil.getHiddenTypes();
         for (String sType : rawTypes) {
            if (!hiddenTypes.contains(sType)) {
               LabelValueBean bean = new LabelValueBean(sType, sType);
               typesList.add(bean);
            }
         }
      } else {
         log.warn("System property '" + SYSTEM_SIMPLEEDITOR_CONTENTTYPES + "' is not set. Please add it.");
      }
      request.setAttribute("typesList", typesList);

      List<Node> channelNodessList = SimpleContentUtil.getRelatedChannelsByUser(cloud);
      List<LabelValueBean> channelsList = new ArrayList<LabelValueBean>();
      for (Node channel : channelNodessList) {
         LabelValueBean bean = new LabelValueBean(channel.getStringValue("name"), channel.getStringValue("number"));
         channelsList.add(bean);
      }
      request.setAttribute("channelsList", channelsList);
      // end of cmsc-1479
   }
}
