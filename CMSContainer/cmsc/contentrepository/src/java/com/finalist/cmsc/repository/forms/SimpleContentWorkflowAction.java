/**
 * 
 */
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


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
      if(!StringUtil.isEmptyOrWhitespace(content)){
         Workflow.finish(cloud.getNode(content), "");         
      }
      
      return mapping.findForward(SUCCESS); 
   }

}
