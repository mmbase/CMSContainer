package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.storage.search.SortOrder;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class SearchInitAction extends MMBaseAction {

   private static final String SEARCHOPTIONS = "searchoptions";
   private static final String TYPES_LIST = "typesList";
   private static final String PORTLET_ID = "portletId";
   private static final String DESTINATION = "destination";
   private static final String ALLOWREL = "allowrel";
   private static final String TYPEDEF = "typedef";

@Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      SearchForm searchForm = (SearchForm) form;

      String portletId = request.getParameter(PORTLET_ID);
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
         searchForm.setOrder("title");
      }

      if (searchForm.getDirection() != SortOrder.ORDER_DESCENDING) {
         searchForm.setDirection(SortOrder.ORDER_ASCENDING);
      }
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

      List<NodeManager> types;
      if(StringUtils.isEmpty(portletId)){
    	  types = ContentElementUtil.getContentTypes(cloud);
      } else {
          types = cloud.getNode(portletId).getRelatedNodes(TYPEDEF, ALLOWREL, DESTINATION);
          if(types.size() == 0){
        	  types = ContentElementUtil.getContentTypes(cloud);
          }
      }
      List<String> hiddenTypes = ContentElementUtil.getHiddenTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      addToRequest(request, TYPES_LIST, typesList);
      addToRequest(request, PORTLET_ID, portletId);

      return mapping.findForward(SEARCHOPTIONS);
   }

}
