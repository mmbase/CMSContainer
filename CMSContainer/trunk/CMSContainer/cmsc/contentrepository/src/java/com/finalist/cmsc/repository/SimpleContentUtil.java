package com.finalist.cmsc.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.FieldValueInConstraint;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.security.SecurityUtil;

public class SimpleContentUtil {
   

   private static final Logger log = Logging.getLoggerInstance(SimpleContentUtil.class.getName());
   
   public static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";
   
   public static final String DEFAULT_SIZE_PER_PAGE = "25";
   
   private static final String MMBASEGROUPREL = "mmbasegrouprel";

   private static final String CONTENTCHANNEL = "contentchannel";

   public static final String CONTENT_TYPE = "contentelement";
   
   private static final String NAME_FIELD = "name";

   private static final String TITLE_FIELD = "title";

   private static final String NUMBER_FIELD = "number";

   private static final String STATUS_FIELD = "status";

   private static final String TYPE_FIELD = "type";

   private static final String WORKFLOWREL = "workflowrel";

   private static final String WORKFLOWITEM_TYPE = "workflowitem";
   

   public  static SortedSet<Integer>  getDraftWorkFlowItem(Cloud cloud) {
      SortedSet<Integer>  set = new TreeSet<Integer> ();
      NodeManager nodeManager = cloud.getNodeManager(WORKFLOWITEM_TYPE);
      
      NodeManager contentManager = cloud.getNodeManager(CONTENT_TYPE);
      NodeQuery query = cloud.createNodeQuery();

      Step parameterStep = query.addStep(contentManager);
      query.setNodeStep(parameterStep);
      
      Step itemStep = query.addRelationStep(nodeManager, WORKFLOWREL, SearchUtil.SOURCE).getNext();
      StepField typeField = query.createStepField(itemStep, TYPE_FIELD);
      FieldValueConstraint typeConstraint =query.createConstraint(typeField, FieldCompareConstraint.EQUAL,"content" );
      
      SearchUtil.addConstraint(query, typeConstraint);
      
      StepField statusField = query.createStepField(itemStep, STATUS_FIELD);
      FieldValueConstraint statusConstraint =query.createConstraint(statusField, FieldCompareConstraint.EQUAL,"draft" );
      
      SearchUtil.addConstraint(query, statusConstraint);
      
      set = SearchUtil.createNodeNumberSet(query.getList());
      return set;
   }
   
   
   public static SortedSet<Integer> getAllContent(Cloud cloud) {
      SortedSet<Integer> set = new TreeSet<Integer>();
      NodeManager nodeManager = cloud.getNodeManager(CONTENT_TYPE);
      NodeQuery query = cloud.createNodeQuery();
      // First add the proper step to the query.
      NodeManager channelNodeManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      Step channelStep = query.addStep(channelNodeManager);
      Step contentStep = query.addRelationStep(nodeManager, RepositoryUtil.CONTENTREL, "DESTINATION").getNext();
      //Add channel constraint
      addChannelConstraint(cloud, query, channelNodeManager, channelStep);
      
      // Add type constraint
      addTypeConstraint(cloud,  query, nodeManager,contentStep);
      
      
      query.setNodeStep(contentStep);
      SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.CREATOR_FIELD, cloud.getUser().getIdentifier());
      set = SearchUtil.createNodeNumberSet(query.getList());
      return set;
   }


   private static void addChannelConstraint(Cloud cloud, NodeQuery query,
         NodeManager channelNodeManager, Step channelStep) {
      List<Node> channelNodes = getRelatedChannelsByUser(cloud);
      TreeSet<Integer> channelNumbers = new TreeSet<Integer>();
      for (Node channelNode : channelNodes) {
         channelNumbers.add(channelNode.getNumber());
      }
      
      StepField channelStepField = query.createStepField(channelStep, channelNodeManager.getField(NUMBER_FIELD));
      FieldValueInConstraint channelConstraint = query.createConstraint(channelStepField, channelNumbers);
      SearchUtil.addConstraint(query, channelConstraint);
   }
   
   public static NodeQuery createQuery(Cloud cloud, NodeManager nodeManager) {
      NodeQuery query = cloud.createNodeQuery();
      // Order the result by:
      // First add the proper step to the query.
      NodeManager channelNodeManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      Step channelStep = query.addStep(channelNodeManager);
      Step contentStep = query.addRelationStep(nodeManager, RepositoryUtil.CONTENTREL, SearchUtil.DESTINATION).getNext();
      
     // add channel Constraint
      addChannelConstraint(cloud, query, channelNodeManager, channelStep);
      
      // add content types Constraint
      addTypeConstraint(cloud, query, nodeManager, contentStep);
      
      // set default order field
      query.setNodeStep(contentStep);

      SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.CREATOR_FIELD, cloud.getUser().getIdentifier());
      query.setDistinct(true);
      return query;
   }


   private static void addTypeConstraint(Cloud cloud, NodeQuery query, NodeManager nodeManager, Step contentStep) {
      TreeSet<Integer> validTypes = new TreeSet<Integer>();
      List<NodeManager> types = new ArrayList<NodeManager>();
      List<String> simpleEditorTypes = ContentElementUtil.getSimpleEditorTypes(cloud);
      for (int i = 0; i < simpleEditorTypes.size(); i++) {
         if (StringUtils.isNotEmpty(simpleEditorTypes.get(i))
               && ContentElementUtil.isContentType(simpleEditorTypes.get(i))) {
            types.add(cloud.getNodeManager(simpleEditorTypes.get(i)));
         }
      }
      List<String> hiddenTypes = ContentElementUtil.getHiddenTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            validTypes.add(manager.getNumber());
         }
      }

      StepField contentStepField = query.createStepField(contentStep, nodeManager.getField("otype"));
      FieldValueInConstraint contentConstraint = query.createConstraint(contentStepField, validTypes);
      SearchUtil.addConstraint(query, contentConstraint);
   }
   
   public static NodeQuery createQuery(Cloud cloud, NodeManager nodeManager,String order,int direction,int offset) {
      NodeQuery query = createQuery(cloud,nodeManager);
      if (StringUtils.isEmpty(order)) {
         if (nodeManager.hasField(TITLE_FIELD)) {
            order = TITLE_FIELD;
         }
         if (nodeManager.hasField(NAME_FIELD)) {
            order = NAME_FIELD;
         }
      }
      if (StringUtils.isNotEmpty(order)) {
         query.addSortOrder(query.getStepField(nodeManager.getField(order)),direction);
      }
      query.setOffset(offset);
      return query;
   }
   
   public static void setOrder(NodeQuery query,NodeManager nodeManager,String order,int direction) {
      if (StringUtils.isEmpty(order)) {
         if (nodeManager.hasField(TITLE_FIELD)) {
            order = TITLE_FIELD;
         }
         if (nodeManager.hasField(NAME_FIELD)) {
            order = NAME_FIELD;
         }
      }
      if (StringUtils.isNotEmpty(order)) {
         query.addSortOrder(query.getStepField(nodeManager.getField(order)), direction);
       }
   }

   public static void setMaxNumber(NodeQuery query) {
     String resultsPerPage = PropertiesUtil
     .getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
     if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
        resultsPerPage = DEFAULT_SIZE_PER_PAGE;
     }
     // Sorting on TYPE should not sort of the otype value but the title of the
     // type
     query.setMaxNumber(Integer.parseInt(resultsPerPage));
  }
  
   public static void setOffset(NodeQuery query, String offset) {
     if (offset != null && offset.matches("\\d+")) {
        // Sorting on TYPE should not sort of the otype value but the title of
        // the type
        query.setOffset(query.getMaxNumber() * Integer.parseInt(offset));
     }
     else {
        query.setOffset(0);
     }
  }
   
   public static List<Node> getRelatedChannelsByUser(Cloud cloud) {
      List<Node> channels = new ArrayList<Node>();
      NodeList groups = SecurityUtil.getGroups(SecurityUtil.getUserNode(cloud));
      for (int i = 0; i <groups.size() ; i++) {
         Node group = groups.getNode(i);
         NodeList relatedChannels = group.getRelatedNodes(CONTENTCHANNEL, MMBASEGROUPREL, SearchUtil.DESTINATION);
         if (relatedChannels.size() > 0 && !channels.contains(relatedChannels.getNode(0))) {
            channels.add(relatedChannels.getNode(0));
         }
      }
      return channels;
   }
}
