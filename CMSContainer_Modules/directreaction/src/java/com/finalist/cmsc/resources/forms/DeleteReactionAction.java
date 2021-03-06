package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.directreaction.util.ReactionUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class DeleteReactionAction extends DeleteSecondaryContentAction {

   private static final Logger log = Logging.getLoggerInstance(DeleteReactionAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;

      String number = deleteForm.getObjectnumber();
      if (MMBaseAction.SITEADMIN.equals(cloud.getUser().getRank().toString()) ||
          MMBaseAction.ADMINISTRATOR.equals(cloud.getUser().getRank().toString())) {
         log.debug("deleting secondary content: " + number);
         Cloud remoteCloud = ReactionUtil.getRemoteCloud(cloud);
         
         /* Only remove the relations because the an event listener will remove the reaction 
          * @see com.finalist.cmsc.directreaction.ContentElementEventListener
          */
         remoteCloud.getNode(number).deleteRelations();
      }
      else {
         log.warn("Could not delete direct reaction, because user was not siteadmin or admin: " + number + " ("
               + cloud.getUser() + ":" + cloud.getUser().getRank() + ")");
      }

      String returnurl = deleteForm.getReturnurl();
      return new ActionForward(returnurl);

   }

   @Override
   public String getRequiredRankStr() {
      return SITEADMIN;
   }

}
