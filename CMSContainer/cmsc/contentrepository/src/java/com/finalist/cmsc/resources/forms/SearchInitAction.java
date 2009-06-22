package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class SearchInitAction extends MMBaseAction {

   private static final String STRICT = "strict";
   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(SearchInitAction.class.getName());
   
   private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";
   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest request, HttpServletResponse httpServletResponse, Cloud cloud) throws Exception {
      SearchForm searchForm = (SearchForm) actionForm;
      // Initialize

      NodeManager nodeManager = cloud.getNodeManager(searchForm.getContenttypes());
      NodeQuery query = cloud.createNodeQuery();

      // First add the proper step to the query.
      // CMSC-1260 Content search also finds elements in Recycle bin
      NodeManager channelNodeManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      Step channelStep = query.addStep(channelNodeManager);
      Step theStep = query.addRelationStep(nodeManager, RepositoryUtil.CREATIONREL, "SOURCE").getNext();
      query.setNodeStep(theStep);

      Integer trashNumber = Integer.parseInt(RepositoryUtil.getTrash(cloud));
      StepField stepField = query.createStepField(channelStep, channelNodeManager.getField("number"));
      FieldValueConstraint channelConstraint = query.createConstraint(stepField, FieldCompareConstraint.NOT_EQUAL,
            trashNumber);
      SearchUtil.addConstraint(query, channelConstraint);
      query.setNodeStep(theStep);
      // Order the result by:
      String order = searchForm.getOrder();
      // set default order field
      if (StringUtils.isEmpty(order)) {
         if (nodeManager.hasField("title")) {
            order = "title";
         }
         if (nodeManager.hasField("name")) {
            order = "name";
         }
      }
      if (StringUtils.isNotEmpty(order)) {
         query.addSortOrder(query.getStepField(nodeManager.getField(order)), searchForm.getDirection());
      }
      query.setDistinct(true);

      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
         query.setMaxNumber(25);
      } else {
         query.setMaxNumber(Integer.parseInt(resultsPerPage));
      }
      log.debug("QUERY: " + query);

      int resultCount = Queries.count(query);
      NodeList results = nodeManager.getList(query);
      request.setAttribute("results",results);
      request.setAttribute("resultCount",resultCount);
      if (StringUtils.isEmpty(searchForm.getOffset())) {
         searchForm.setOffset("0");
      }

      if (searchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         searchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      request.setAttribute(STRICT, searchForm.getStrict());
      return actionMapping.findForward("searchoptions");
   }
}
