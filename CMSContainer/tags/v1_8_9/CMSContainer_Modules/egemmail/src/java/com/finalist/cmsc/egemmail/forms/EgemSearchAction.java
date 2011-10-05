package com.finalist.cmsc.egemmail.forms;

import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.SearchQuery;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.util.KeywordUtil;

public class EgemSearchAction extends MMBaseAction {

   public static final String RESULTS = "results";
   protected static final String OFFSET = "offset";
   protected static final String RESULTS_PER_PAGE = "resultsPerPage";
   protected static final String TOTAL_RESULTS = "totalNumberOfResults";
   protected static final int MAX_RESULTS = 500;
   protected static final int RESULTSPERPAGE = 50;


   protected ActionForward doSearch(ActionMapping mapping, EgemSearchForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      NodeManager nodeManager = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
      NodeQuery query = nodeManager.createQuery();

      if (StringUtils.isNotEmpty(form.getTitle())) {
         Field field = nodeManager.getField(ContentElementUtil.TITLE_FIELD);
         SearchUtil.addLikeConstraint(query, field, form.getTitle());
      }

      if (StringUtils.isNotEmpty(form.getAuthor())) {
         SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIER_FIELD, form.getAuthor());
      }

      if (StringUtils.isNotEmpty(form.getKeywords())) {
         List<String> keywords = KeywordUtil.getKeywords(form.getKeywords());
         Field field = nodeManager.getField(ContentElementUtil.KEYWORD_FIELD);
         for (String string : keywords) {
            SearchUtil.addLikeConstraint(query, field, string);
         }
      }

      if (form.isLimitToLastWeekModified()) {
         Field field = nodeManager.getField(ContentElementUtil.LASTMODIFIEDDATE_FIELD);
         long theDurationOfAWeek = 7 * 24 * 60 * 60 * 1000; // milliseconds
         long now = System.currentTimeMillis();
         long aWeekAgo = now - theDurationOfAWeek;

         SearchUtil.addDatetimeConstraint(query, field, aWeekAgo, now);
      } 
      else if (form.isLimitToLastWeekCreated()) {
            Field field = nodeManager.getField(ContentElementUtil.CREATIONDATE_FIELD);
            long theDurationOfAWeek = 7 * 24 * 60 * 60 * 1000; // milliseconds
            long now = System.currentTimeMillis();
            long aWeekAgo = now - theDurationOfAWeek;

            SearchUtil.addDatetimeConstraint(query, field, aWeekAgo, now);
      }

      SearchUtil.addLimitConstraint(query, SearchQuery.DEFAULT_OFFSET, MAX_RESULTS);

      NodeList results = nodeManager.getList(query);
      if (results.size() > 0) {
         removeUnpublished(cloud, results);
      }

      int offset = StringUtils.isBlank(form.getPage()) ? 0 : Integer.parseInt(form.getPage());
      int numberOfResults = results.size();

      int from = offset * RESULTSPERPAGE;
      int to = from + RESULTSPERPAGE;
      if (to > numberOfResults) {
         to = numberOfResults;
      }

      form.getNodesOnScreen().clear();
      for (int i = from; i < to; i++) {
         form.getNodesOnScreen().add(results.getNode(i).getNumber());
      }

      request.setAttribute(OFFSET, offset);
      request.setAttribute(RESULTS, results);
      request.setAttribute(RESULTS_PER_PAGE, RESULTSPERPAGE);
      request.setAttribute(TOTAL_RESULTS, numberOfResults);

      return mapping.findForward(EgemSearchForm.SEARCH);
   }


   /*
    * @see com.finalist.cmsc.struts.MMBaseAction#execute(org.apache.struts.action.ActionMapping,
    *      org.apache.struts.action.ActionForm,
    *      javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse, org.mmbase.bridge.Cloud)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (!(form instanceof EgemSearchForm)) {
         throw new IllegalArgumentException("The form is not an " + EgemSearchForm.class);
      }

      return doSearch(mapping, (EgemSearchForm) form, request, response, cloud);
   }


   private void removeUnpublished(Cloud cloud, NodeList results) {
      StringBuffer constraints = new StringBuffer("sourcenumber in (");
      for (NodeIterator ni = results.nodeIterator(); ni.hasNext();) {
         Node next = ni.nextNode();
         constraints.append(next.getNumber());
         if (ni.hasNext()) {
            constraints.append(",");
         }
      }
      constraints.append(")");

      NodeList remoteNodes = cloud.getNodeManager("remotenodes").getList(constraints.toString(), null, null);

      HashSet<Integer> remoteNumbers = new HashSet<Integer>();
      for (NodeIterator ni = remoteNodes.nodeIterator(); ni.hasNext();) {
         Node next = ni.nextNode();
         remoteNumbers.add(Integer.valueOf(next.getStringValue("sourcenumber")));
      }

      int found = 0;
      for (NodeIterator ni = results.nodeIterator(); ni.hasNext();) {
         Node next = ni.nextNode();
         if (found > MAX_RESULTS || !remoteNumbers.contains(next.getNumber())) {
            ni.remove();
         }
         else {
            found++;
         }
      }
   }
}
