package com.finalist.cmsc.repository.forms;

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.SimpleContentUtil;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.util.ServerUtil;

public class SimpleContentAction extends PagerAction {

   private static final Logger log = Logging
         .getLoggerInstance(SimpleContentAction.class.getName());
   private static final String SYSTEM_SIMPLEEDITOR_CONTENTTYPES = "system.simpleeditor.contenttypes";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {

      log.debug("Starting the simple editor");
      SimpleContentActionForm simpleContentForm = (SimpleContentActionForm) form;
    

     // Sorting on TYPE should not sort of the otype value but the title of the
      // type
      NodeManager nodeManager = cloud.getNodeManager(SimpleContentUtil.CONTENT_TYPE);
      NodeQuery draftQuery = SimpleContentUtil.createQuery(cloud,nodeManager);

      if ( !ServerUtil.isSingle() && ServerUtil.isStaging()) {
      // Add the title constraint:
         NodeQuery finishedQuery = SimpleContentUtil.createQuery(cloud,nodeManager);
         if (StringUtils.isNotEmpty(simpleContentForm.getTitle())) {

            Field field = nodeManager.getField(ContentElementUtil.TITLE_FIELD);
            Constraint titleConstraint = SearchUtil.createLikeConstraint(draftQuery,
                  field, simpleContentForm.getTitle());
            SearchUtil.addConstraint(draftQuery, titleConstraint);
         }
         String finishedOrder = simpleContentForm.getOrderFinished();
         String order = simpleContentForm.getOrder();
         if (StringUtils.isNotEmpty(simpleContentForm.getType())) {
            
            SimpleContentUtil.setOrder(finishedQuery,nodeManager,finishedOrder, simpleContentForm.getDirectionFinished());
            SimpleContentUtil.setOrder(draftQuery,nodeManager,order, 1);
            
            SimpleContentUtil.setMaxNumber(draftQuery);
            SimpleContentUtil.setMaxNumber(finishedQuery);
            
            SimpleContentUtil.setOffset(finishedQuery,simpleContentForm.getOffsetFinished());
            // Set the offset (used for paging).
            SimpleContentUtil.setOffset(draftQuery,"0");
           // request.setAttribute("offsetFinished", offset);
         }
         else {
            SimpleContentUtil.setOrder(finishedQuery, nodeManager, finishedOrder, 1);
            SimpleContentUtil.setOrder(draftQuery,nodeManager, order, simpleContentForm.getDirection());
            
            SimpleContentUtil.setMaxNumber(draftQuery);
            SimpleContentUtil.setMaxNumber(finishedQuery);
            
            SimpleContentUtil.setOffset(finishedQuery,"0");
            // Set the offset (used for paging).
            SimpleContentUtil.setOffset(draftQuery,simpleContentForm.getOffset());
         }

      SortedSet<Integer> set = SimpleContentUtil.getDraftWorkFlowItem(cloud);  
      SortedSet<Integer> contentSet = SimpleContentUtil.getAllContent(cloud);
      contentSet.removeAll(set);
      SearchUtil.addInConstraint(draftQuery, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), set);
      SearchUtil.addInConstraint(finishedQuery, nodeManager.getField(ContentElementUtil.NUMBER_FIELD), contentSet);
      int finisedResultCount = Queries.count(finishedQuery);
      NodeList finishedResults = finishedQuery.getNodeManager().getList(finishedQuery);
      // Set everything on the request.

      request.setAttribute("finishedResultCount", finisedResultCount);
      request.setAttribute("finishedResults", finishedResults);
      }
      else {
         SimpleContentUtil.setOrder(draftQuery, nodeManager, simpleContentForm.getOrder(), simpleContentForm.getDirection());

         SimpleContentUtil.setMaxNumber(draftQuery);
         
         SimpleContentUtil.setOffset(draftQuery,simpleContentForm.getOffset());
      }         

      int resultCount = Queries.count(draftQuery);
      NodeList results = draftQuery.getNodeManager().getList(draftQuery);
      simpleContentForm.setResultCount(resultCount);
      simpleContentForm.setResults(results);   
	  
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

      return super.execute(mapping, form, request, response, cloud);
   }
   

}
