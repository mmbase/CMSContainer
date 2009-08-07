package com.finalist.cmsc.repository.forms;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.NodeGUITypeComparator;
import com.finalist.cmsc.repository.SimpleContentUtil;
import com.finalist.cmsc.struts.PagerAction;

public abstract class SimpleContentAction extends PagerAction {

   private static final Logger log = Logging
         .getLoggerInstance(SimpleContentAction.class.getName());
  

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {

      log.debug("Starting the simple editor");
      SimpleContentActionForm simpleContentForm = (SimpleContentActionForm) form;
      if(StringUtil.isEmptyOrWhitespace(request.getParameter("title"))){
         simpleContentForm.setTitle("");
      } else {
         simpleContentForm.setTitle(request.getParameter("title"));
      }
      int resultCount = 0;
      NodeList results = cloud.createNodeList();
      NodeManager nodeManager = cloud.getNodeManager(SimpleContentUtil.CONTENT_TYPE);
      NodeQuery query = SimpleContentUtil.createQuery(cloud,nodeManager);
      
      doAction(query,form,request,cloud);
      
      if (StringUtils.isNotEmpty(simpleContentForm.getTitle())) {

         Field field = nodeManager.getField(ContentElementUtil.TITLE_FIELD);
         Constraint titleConstraint = SearchUtil.createLikeConstraint(query,
               field, simpleContentForm.getTitle());
         SearchUtil.addConstraint(query, titleConstraint);
      }
      
      int offset = 0;
      if (StringUtils.isNotEmpty(simpleContentForm.getOffset())) {
         offset = Integer.valueOf(simpleContentForm.getOffset());
      }
      
      String resultsPerPage = PropertiesUtil.getProperty(SimpleContentUtil.REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
         resultsPerPage = SimpleContentUtil.DEFAULT_SIZE_PER_PAGE;
      }
      int maxNumber = Integer.parseInt(resultsPerPage);
      
      String order = simpleContentForm.getOrder();
      
      if (StringUtils.isNotEmpty(order) && "otype".equalsIgnoreCase(order)) {
         SearchUtil.addLimitConstraint(query, SearchQuery.DEFAULT_OFFSET, SearchQuery.DEFAULT_MAX_NUMBER);
         results = query.getNodeManager().getList(query);
         boolean reverse = false;
         if (simpleContentForm.getDirection() == 1) {
            reverse = true;
         }
         resultCount = results.size();
         Collections.sort(results, new NodeGUITypeComparator(query.getCloud().getLocale(), reverse));
         int toIndex = results.size()<(offset+maxNumber)? results.size(): (offset+maxNumber);
         results = results.subNodeList(offset, toIndex);
      }
      else {
         SimpleContentUtil.setOrder(query, nodeManager, simpleContentForm.getOrder(), simpleContentForm.getDirection());
        // query.setMaxNumber(maxNumber);
         SimpleContentUtil.setOffset(query,simpleContentForm.getOffset());
         resultCount = Queries.count(query);
         results = query.getNodeManager().getList(query);
      }
      
      resultCount = Queries.count(query);
      results = query.getNodeManager().getList(query);
      simpleContentForm.setResultCount(resultCount);
      simpleContentForm.setResults(results);   
	  
      return super.execute(mapping, form, request, response, cloud);
   }
   
   protected abstract void doAction(NodeQuery query, ActionForm form, HttpServletRequest request, Cloud cloud);

}
