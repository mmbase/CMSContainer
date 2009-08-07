/**
 * 
 */
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.security.implementation.cloudcontext.User;

import com.finalist.cmsc.security.SecurityUtil;
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
      NodeList channelList = cloud.createNodeList();
      NodeList groups = SecurityUtil.getGroups(cloud.getNode(Integer.valueOf(((User)cloud.getUser()).getNode().getNumber())));
      for(Object obj : groups){
         Node group = (Node)obj;
         NodeList channels = group.getRelatedNodes("contentchannel", "mmbasegrouprel", "DESTINATION");
         if(channels != null && channels.size() > 0){
            for(Object channel : channels){
               if(!channelList.contains(channel)) {
                  channelList.add(channel);
               }
            }
         }
      }
      addToRequest(request, "channelList", channelList);
      addToRequest(request, "returnpath", request.getParameter("returnpath"));
      
      return mapping.findForward(SUCCESS);
   }

}
