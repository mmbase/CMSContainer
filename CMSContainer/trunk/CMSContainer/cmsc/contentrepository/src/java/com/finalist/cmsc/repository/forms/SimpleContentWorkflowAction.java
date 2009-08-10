/**
 * 
 */
package com.finalist.cmsc.repository.forms;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.struts.StrutsUtil;


/**
 * @author Billy
 *
 */
public class SimpleContentWorkflowAction extends MMBaseFormlessAction {

   /* (non-Javadoc)
    * @see com.finalist.cmsc.struts.MMBaseFormlessAction#execute(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest, org.mmbase.bridge.Cloud)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud)
         throws Exception {
      String content = request.getParameter("content");

      Locale locale = StrutsUtil.getLocale(request);
      MessageResources resources = getResources(request, "REPOSITORY");
      String workflowmessage;
      if(!StringUtil.isEmptyOrWhitespace(content)){
         Workflow.finish(cloud.getNode(content), "");    
         workflowmessage = resources.getMessage(locale, "simple.editor.finish.success");
      } else {
         workflowmessage = resources.getMessage(locale, "simple.editor.finish.failed");
      }
      
      String path = mapping.findForward(SUCCESS).getPath(); 
      path += "?workflowmessage=" + workflowmessage;
      ActionForward actionForward = new ActionForward(path);
      actionForward.setRedirect(false);
      return actionForward;

   }

}
