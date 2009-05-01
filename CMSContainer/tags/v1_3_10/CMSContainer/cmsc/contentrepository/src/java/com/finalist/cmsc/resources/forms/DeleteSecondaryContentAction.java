package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

public class DeleteSecondaryContentAction extends MMBaseAction {

    private static transient Logger log = Logging.getLoggerInstance(DeleteSecondaryContentAction.class.getName());

	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {
		DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;
		
		String number = deleteForm.getObjectnumber();
        try {
			log.debug("deleting secondary content: "+number);
            Node objectNode = cloud.getNode(number);
            
            Publish.remove(objectNode);
            Publish.unpublish(objectNode);
            
            objectNode.delete(true);
        } catch (NotFoundException nfe) {
            log.info("Failed to delete secondaryContent with number " + number + ", node not found");
        }
		
		String returnurl = deleteForm.getReturnurl();
		return new ActionForward(returnurl);
	}

	public String getRequiredRankStr() {
	    return MMBaseAction.SITEADMIN;
	}
	
}
