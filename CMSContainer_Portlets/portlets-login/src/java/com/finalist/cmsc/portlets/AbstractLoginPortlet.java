package com.finalist.cmsc.portlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;

public abstract class AbstractLoginPortlet extends CmscPortlet{
   
   protected String DEFAULT_EMAIL_CONFIRM_TEMPLATE = "/WEB-INF/templates/view/login/confirmation.txt";
   protected static final String EMAIL_SUBJECT = "emailSubject";
   protected static final String EMAIL_TEXT = "emailText";
   protected static final String EMAIL_FROMEMAIL = "emailFromEmail";
   protected static final String EMAIL_FROMNAME = "emailFromName";
   protected static final String REGISTRATIONPAGE = "registrationpage";
   protected static final String PAGE = "page";

   private static final Log log = LogFactory.getLog(AbstractLoginPortlet.class);

   
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
   PortletException {
      PortletPreferences preferences = req.getPreferences();
      setAttribute(req, EMAIL_SUBJECT, preferences.getValue(EMAIL_SUBJECT,""));
     // setAttribute(req, EMAIL_TEXT, preferences.getValue(EMAIL_TEXT,getConfirmationTemplate()));
      setAttribute(req, EMAIL_TEXT, preferences.getValue(EMAIL_TEXT,getConfirmationTemplate()));
      setAttribute(req, EMAIL_FROMEMAIL, preferences.getValue(EMAIL_FROMEMAIL,""));
      setAttribute(req, EMAIL_FROMNAME, preferences.getValue(EMAIL_FROMNAME,""));
      String pageid = preferences.getValue(PAGE, null);
      if (StringUtils.isNotEmpty(pageid)) {
         String pagepath = SiteManagement.getPath(Integer.valueOf(pageid), true);
         if (pagepath != null) {
            setAttribute(req, "pagepath", pagepath);
         }
      }
      super.doEditDefaults(req, res);
   }
   
   @Override
   public void processEditDefaults(ActionRequest request,
         ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      if (portletId != null) {
         // get the values submitted with the form
         setPortletNodeParameter(portletId, PAGE, request.getParameter(PAGE));
         String registrationpage = request.getParameter(REGISTRATIONPAGE);
         setPortletNodeParameter(portletId, REGISTRATIONPAGE, registrationpage);
         setPortletParameter(portletId, EMAIL_SUBJECT, request.getParameter(EMAIL_SUBJECT));
         setPortletParameter(portletId, EMAIL_TEXT, request.getParameter(EMAIL_TEXT));
         setPortletParameter(portletId, EMAIL_FROMEMAIL, request.getParameter(EMAIL_FROMEMAIL));
         setPortletParameter(portletId, EMAIL_FROMNAME, request.getParameter(EMAIL_FROMNAME));
      }
      super.processEditDefaults(request, response);
   }
   
   protected String getEmailBody(String emailText,ActionRequest request, 
         Authentication authentication, Person person) {

      Cloud cloud = getCloudForAnonymousUpdate(false);
      String pageId = request.getParameter("page");

      if (cloud.hasNode(pageId)) {
         String url = getConfirmationLink(cloud, Integer.parseInt(pageId));
         String confirmUrl = HttpUtil.getWebappUri((HttpServletRequest) request)
               + "login/confirm.do?s=" + authentication.getId() + url;
         
         return formatConfirmationText(emailText, authentication, person, confirmUrl);
      }
      return null;
   }

   protected String formatConfirmationText(String emailText, Authentication authentication, Person person, String confirmUrl) {
      String text = emailText == null?getConfirmationTemplate():emailText;
      text = text.replaceAll("%EMAIL", authentication.getUserId() ==null?"":authentication.getUserId());
      text = text.replaceAll("%PASSWORD", authentication.getPassword()==null?"":authentication.getPassword());
      text = text.replaceAll("%FIRSTNAME", person.getFirstName()==null?"":person.getFirstName());
      text = text.replaceAll("%INFIX", person.getInfix()==null?"":person.getInfix());
      text = text.replaceAll("%LASTNAME", person.getLastName()==null?"":person.getLastName());
      text = text.replaceAll("%URL", confirmUrl==null?"":confirmUrl);
      return text;
   }

   protected Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
   }

   protected String getConfirmationLink(Cloud cloud, int pageNode) {
      String link = null;
      NodeList portletDefinations = SearchUtil.findNodeList(cloud,
            "portletdefinition", "definition", this.getPortletName());
      Node regiesterPortletDefination = portletDefinations.getNode(0);
      if (portletDefinations.size() > 1) {
         log.error("found " + portletDefinations.size()
               + " registerPortlet nodes; first one will be used");
      }
      NodeList portlets = regiesterPortletDefination.getRelatedNodes("portlet",
            "definitionrel", SearchUtil.SOURCE);
      Node portlet = null;
      Relation relation = null;
      Node page = cloud.getNode(pageNode);
      
      if (page == null) {
         return null;
      }
      
      NodeList nodeList = page.getRelatedNodes("portlet", "portletrel", SearchUtil.DESTINATION);
      for (int i =0 ; i < nodeList.size() ; i++) {
         for (int j = 0 ; j < portlets.size() ; j++) {
            if (nodeList.getNode(i).getNumber() == portlets.getNode(j).getNumber()) {
               portlet = nodeList.getNode(i);
            }
         }
      }
      
      relation = RelationUtil.getRelation(cloud.getRelationManager("portletrel"), page.getNumber(), portlet.getNumber());
      link = "&pn=" + page.getNumber();  
      if (relation != null) {
         String name = relation.getStringValue("name");
         if (name != null) {
            link += "&nm=" + name;
         }
      }
      
      return link;
   }
   
   protected String getConfirmationTemplate() {
      InputStream is = getPortletContext().getResourceAsStream(getEmailConfirmTemplate());

      if (is == null) {
         log.warn("The confirmation template file '" + getEmailConfirmTemplate() + "' does not exist while loading EditDefaults. Using the default empty String for now.");
         return ""; 
      }
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      
      try {
         String strLine;
         while ((strLine = reader.readLine()) != null) {
            sb.append(strLine + "\n");
         }
         is.close();
      } catch (IOException e) {
         log.error("error happened when reading email template file", e);
      }

      return sb.toString();
   }
   
   /**
    * @return path and filename, starting in context root. E.g. /WEB-INF/templates/hello.txt
    */
   protected String getEmailConfirmTemplate() {
      return DEFAULT_EMAIL_CONFIRM_TEMPLATE;
   }
   
}
