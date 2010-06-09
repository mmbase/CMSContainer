/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

@SuppressWarnings("serial")
public class ContentUrlTag extends NodeReferrerTag {

   /** Holds value of property number. */
   private Attribute number = Attribute.NULL;
   private boolean absolute = false;


   public void setNumber(String t) throws JspTagException {
      number = getAttribute(t);
   }


   public void setAbsolute(String absolute) {
      this.absolute = Boolean.valueOf(absolute);
   }


   @Override
   public int doStartTag() throws JspException {
      Node node = null;
      int nr = number.getInt(this, -1);
      if (nr == -1) {
         node = getNode();
      }
      else {
         node = getCloudVar().getNode(nr);
      }
      if (node == null) {
         throw new JspTagException("Node not found for content url tag");
      }

      String url = null;
      String builderName = node.getNodeManager().getName();
      if ("attachments".equals(builderName)) {
         url = ResourcesUtil.getServletPath(node, node.getStringValue("number"));
         if (absolute) {
            url = makeAbsolute(url);
         }
      }
      else {
         if ("urls".equals(builderName)) {
            url = node.getStringValue("url");
         }
         else {
            url = getContentUrl(node);
            if (absolute) {
               url = makeAbsolute(url);
            }
            if(!ServerUtil.useServerName()) {
               PortalEnvironment env = PortalEnvironment.getPortalEnvironment((HttpServletRequest)pageContext.getRequest());
               if(env != null) {
	               PortalURL currentURL = env.getRequestedPortalURL();
	               String path = currentURL.getGlobalNavigationAsString();
	               String server = (path.indexOf("/") != -1)?(path.substring(0, path.indexOf("/"))):path;
	            	url += "?server="+server;
               }
            }
         }
      }

      if (url != null) {
         helper.setValue(url);
      }

      if (getId() != null) {
         getContextProvider().getContextContainer().register(getId(), helper.getValue());
      }

      return EVAL_BODY_BUFFERED;
   }


   public String makeAbsolute(String url) {
      return HttpUtil.makeAbsolute((HttpServletRequest) pageContext.getRequest(), url);
   }


   private String getContentUrl(Node node) {
      return ResourcesUtil.getServletPathWithAssociation("content", "/content/*", node.getStringValue("number"), node
            .getStringValue("title"));
   }


   @Override
   public int doEndTag() throws JspTagException {
      helper.doEndTag();
      return super.doEndTag();
   }

}
