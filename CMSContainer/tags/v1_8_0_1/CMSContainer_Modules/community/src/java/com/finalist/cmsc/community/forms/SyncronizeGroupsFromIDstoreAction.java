/**
 * 
 */
package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.context.WebApplicationContext;

import com.finalist.cmsc.services.community.security.GroupsService;


/**
 * @author Billy
 *
 */
public class SyncronizeGroupsFromIDstoreAction extends AbstractCommunityAction {
   
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      List<String> results = new ArrayList<String>();
      results = getGroupsService().syncronizeGroupsFromIDstore();
      request.setAttribute("results", results);
      return mapping.findForward(SUCCESS);
   }

   public GroupsService getGroupsService() {
      WebApplicationContext ctx = getWebApplicationContext();
      return (GroupsService) ctx.getBean("groupsService");
   }

}
