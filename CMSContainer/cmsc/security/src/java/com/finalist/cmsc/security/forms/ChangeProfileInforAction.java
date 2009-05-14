package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * ChangePasswordAction
 * 
 * @author Nico Klasens
 */
public class ChangeProfileInforAction extends MMBaseAction {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(ChangeProfileInforAction.class.getName());


   /**
    * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
    *      org.apache.struts.action.ActionForm,
    *      javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (!isCancelled(request)) {
         // Make sure we have the logged in user and not a user from a
         // cloudprovider
         Cloud userCloud = getCloudFromSession(request);

         log.debug("ChangePasswordAction - doPerform()");
         if (!isCancelled(request)) {
            ChangeProfileInforForm changeMyprofileInforForm = (ChangeProfileInforForm) form;
            Node userNode = SecurityUtil.getUserNode(userCloud);
            userNode.setStringValue("username", changeMyprofileInforForm.getUsername());
            userNode.setStringValue("password", changeMyprofileInforForm.getNewpassword());
            userNode.setStringValue("firstname", changeMyprofileInforForm.getFirstname());
            userNode.setStringValue("prefix", changeMyprofileInforForm.getPrefix());
            userNode.setStringValue("surname", changeMyprofileInforForm.getSurname());
            userNode.setStringValue("company", changeMyprofileInforForm.getCompany());
            userNode.setStringValue("department", changeMyprofileInforForm.getDepartment());
            userNode.setBooleanValue("emailsignal", changeMyprofileInforForm.isEmailSignal());
            userNode.setStringValue("emailaddress", changeMyprofileInforForm.getEmail());
            userNode.commit();
         }
         ActionForward af = mapping.findForward(SUCCESS);
         af = new ActionForward(af.getPath() + "?succeeded=true");
         return af;
      }
      else {
         return mapping.findForward("cancel");
      }
   }

}