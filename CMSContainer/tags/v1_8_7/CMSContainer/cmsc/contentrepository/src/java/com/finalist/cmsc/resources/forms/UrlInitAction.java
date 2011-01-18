package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.storage.search.SortOrder;

public class UrlInitAction extends SearchInitAction {

   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      UrlForm searchForm = (UrlForm) actionForm;

      searchForm.setOrder("title");
      searchForm.setDirection(SortOrder.ORDER_DESCENDING);
      searchForm.setContenttypes("urls");
      return super.execute(actionMapping, actionForm, httpServletRequest, httpServletResponse);
   }
}
