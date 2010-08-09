/**
 * 
 */
package com.finalist.cmsc.repository.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.SimpleContentUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


/**
 * @author Billy
 *
 */
public class SimpleEditorChannelAction extends MMBaseFormlessAction {

   /* (non-Javadoc)
    * @see com.finalist.cmsc.struts.MMBaseFormlessAction#execute(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest, org.mmbase.bridge.Cloud)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud)
         throws Exception {
      
      List<Node> channelList = SimpleContentUtil.getRelatedChannelsByUser(cloud);
      addToRequest(request, "channelList", channelList);
      addToRequest(request, "returnpath", request.getParameter("returnpath"));
      
      return mapping.findForward(SUCCESS);
   }

}
