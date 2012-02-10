package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class UserBulkDeleteAction extends AbstractCommunityAction{

   @Override
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      SearchForm searchform = (SearchForm) actionForm;

         // no conditions search
         // need authId from the last jsp
         if (searchform.getChk_() != null) {
            for (String authId : searchform.getChk_()) {              
               getAuthenticationService().deleteAuthentication(Long.parseLong(authId));
            }                                                                                      // string[]
         }
      return actionMapping.findForward("success");

   }
}
