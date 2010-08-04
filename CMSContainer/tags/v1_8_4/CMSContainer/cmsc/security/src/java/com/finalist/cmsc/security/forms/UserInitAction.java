package com.finalist.cmsc.security.forms;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * @author Nico Klasens
 */
public class UserInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      String id = request.getParameter("id");
      UserForm userForm = (UserForm) form;
      if (id != null) {
         Node node = cloud.getNode(id);
         userForm.setUsername(node.getStringValue("username"));
         userForm.setSurname(node.getStringValue("surname"));
         userForm.setId(Integer.parseInt(id));
         userForm.setFirstname(node.getStringValue("firstname"));
         userForm.setPrefix(node.getStringValue("prefix"));
         userForm.setCompany(node.getStringValue("company"));
         userForm.setDepartment(node.getStringValue("department"));
         userForm.setFunction(node.getStringValue("function"));
         userForm.setNote(node.getStringValue("note"));
         userForm.setEmail(node.getStringValue("emailaddress"));
         userForm.setLanguage(node.getStringValue("language"));
         userForm.setEmailSignal(node.getBooleanValue("emailsignal"));
         userForm.setWebsite(node.getStringValue("website"));
         userForm.setStatus(node.getIntValue("status"));
         userForm.setValidfrom(node.getDateValue("validfrom"));
         userForm.setValidto(node.getDateValue("validto"));

         NodeList ranks = node.getRelatedNodes("mmbaseranks", "rank", "DESTINATION");
         if (ranks.size() > 0) {
            userForm.setRank(ranks.getNode(0).getStringValue("number"));
         }
         Node defaultContextNode = node.getNodeValue("defaultcontext");
         if (defaultContextNode != null) {
            userForm.setDefaultcontext(defaultContextNode.getStringValue("number"));
         }
      }
      else {
         // new
         userForm.setId(-1);
      }

      userForm.resetRanks();
      NodeList ranks = cloud.getNodeManager("mmbaseranks").getList(null, "rank", "down");
      for (NodeIterator iter = ranks.nodeIterator(); iter.hasNext();) {
         Node rankNode = iter.nextNode();
         String name = rankNode.getStringValue("name");
         String number = rankNode.getStringValue("number");
         if("basic user".equalsIgnoreCase(name) && userForm.getId() == -1){
            userForm.setRank(number);
         }
         userForm.addRank(number, name);
      }      
      NodeList contexts = cloud.getNodeManager("mmbasecontexts").getList(null, "name", "down");
      for (NodeIterator iter = contexts.nodeIterator(); iter.hasNext();) {
         Node contextNode = iter.nextNode();
         userForm.addContext(contextNode.getStringValue("number"), contextNode.getStringValue("name"));
      }
      Node userNode = SecurityUtil.getUserNode(cloud);
      String language = userNode.getStringValue("language");
      Locale locale;
      if(StringUtils.isEmpty(language)){
         locale = request.getLocale();
      }else{
         locale = new Locale(language);
      }
      MessageResources resources = getResources(request, "SECURITY");
      userForm.resetStatuses();
      userForm.addStatus("1", resources.getMessage(locale, "user.status.active"));
      userForm.addStatus("-1", resources.getMessage(locale, "user.status.disabled"));
      return mapping.findForward(SUCCESS);
   }
}