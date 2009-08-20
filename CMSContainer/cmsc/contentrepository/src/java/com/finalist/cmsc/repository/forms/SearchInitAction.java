package com.finalist.cmsc.repository.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.storage.search.SortOrder;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class SearchInitAction extends MMBaseAction {

   private static final String TYPES_LIST = "typesList";
   private static final String TITLE = "title";
   private static final String ONLYTYPE = "onlytype";
   private static final String POSITION = "position";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      SearchForm searchForm = (SearchForm) form;

      String position = request.getParameter(POSITION);
      String onlytype = request.getParameter(ONLYTYPE);

      if (StringUtils.isEmpty(searchForm.getExpiredate())) {
         searchForm.setExpiredate("0");
      }

      if (StringUtils.isEmpty(searchForm.getPublishdate())) {
         searchForm.setPublishdate("0");
      }

      if (StringUtils.isEmpty(searchForm.getOffset())) {
         searchForm.setOffset("0");
      }

      if (StringUtils.isEmpty(searchForm.getOrder())) {
         searchForm.setOrder(TITLE);
      }

      if (searchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         searchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
      addToRequest(request, "typesList", ContentElementUtil.getValidTypesList(cloud, types));
      addToRequest(request, POSITION, position);
      addToRequest(request, TITLE, searchForm.getTitle());
      addToRequest(request, ONLYTYPE, onlytype);

      return mapping.findForward("searchoptions");
   }

}
