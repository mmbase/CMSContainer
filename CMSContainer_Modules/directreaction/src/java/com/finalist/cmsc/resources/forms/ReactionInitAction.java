package com.finalist.cmsc.resources.forms;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReactionInitAction extends SearchInitAction {

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
      ReactionForm searchForm = (ReactionForm) actionForm;

      if (StringUtils.isEmpty(searchForm.getOrder())) {
         searchForm.setOrder("title");
      }
      return super.execute(actionMapping, actionForm, httpServletRequest, httpServletResponse);
   }
}
