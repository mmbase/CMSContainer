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
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * ChangePasswordAction
 * 
 * @author Nico Klasens
 */
public class ChangeProfileAction extends MMBaseAction {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(ChangeProfileAction.class.getName());

   /**
    * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
    *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
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
            boolean passworChanged = false;
            
            ChangeProfileForm changeMyprofileForm = (ChangeProfileForm) form;
            Node userNode = SecurityUtil.getUserNode(userCloud);
            userNode.setStringValue("username", changeMyprofileForm.getUsername());
            if (changeMyprofileForm.getNewpassword().trim().length() != 0) {
               userNode.setStringValue("password", changeMyprofileForm.getNewpassword());
               passworChanged = true;
            }
            userNode.setStringValue("firstname", changeMyprofileForm.getFirstname());
            userNode.setStringValue("prefix", changeMyprofileForm.getPrefix());
            userNode.setStringValue("surname", changeMyprofileForm.getSurname());
            userNode.setStringValue("company", changeMyprofileForm.getCompany());
            userNode.setStringValue("department", changeMyprofileForm.getDepartment());
            userNode.setBooleanValue("emailsignal", changeMyprofileForm.isEmailSignal());
            userNode.setStringValue("emailaddress", changeMyprofileForm.getEmail());
            userNode.commit();
            
            if (passworChanged) {
               Publish.updateUser(userNode, changeMyprofileForm.getNewpassword());
            }
         }
         ActionForward af = mapping.findForward(SUCCESS);
         af = new ActionForward(af.getPath() + "?succeeded=true");
         return af;
      } else {
         return mapping.findForward("cancel");
      }
   }

}