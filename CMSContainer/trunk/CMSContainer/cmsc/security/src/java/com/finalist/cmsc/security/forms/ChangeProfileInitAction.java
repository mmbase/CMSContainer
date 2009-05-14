package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * @author Nico Klasens
 */
public class ChangeProfileInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      Cloud userCloud = getCloudFromSession(request);
      Node node = SecurityUtil.getUserNode(userCloud);
      ChangeProfileInforForm changeMyprofileInforForm = (ChangeProfileInforForm) form;
      changeMyprofileInforForm.setUsername(node.getStringValue("username"));
      changeMyprofileInforForm.setSurname(node.getStringValue("surname"));
      changeMyprofileInforForm.setFirstname(node.getStringValue("firstname"));
      changeMyprofileInforForm.setPrefix(node.getStringValue("prefix"));
      changeMyprofileInforForm.setCompany(node.getStringValue("company"));
      changeMyprofileInforForm.setDepartment(node.getStringValue("department"));
      changeMyprofileInforForm.setEmail(node.getStringValue("emailaddress"));
      changeMyprofileInforForm.setEmailSignal(node.getBooleanValue("emailsignal"));
      return mapping.findForward(SUCCESS);
   }
}