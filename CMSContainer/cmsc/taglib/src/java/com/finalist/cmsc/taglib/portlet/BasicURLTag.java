package com.finalist.cmsc.taglib.portlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * Supporting class for the <CODE>actionURL</CODE> and <CODE>renderURL</CODE>
 * tag. Creates a url that points to the current Portlet and triggers an action
 * request with the supplied parameters.
 */
public abstract class BasicURLTag extends TagSupport {

   public static class TEI extends TagExtraInfo {
      @Override
      public VariableInfo[] getVariableInfo(TagData tagData) {
         VariableInfo vi[] = null;
         String var = tagData.getAttributeString("var");
         if (var != null) {
            vi = new VariableInfo[1];
            vi[0] = new VariableInfo(var, "java.lang.String", true, VariableInfo.AT_END);
         }
         return vi;
      }
   }

   protected String page;
   protected String window;
   protected String elementId;

   protected String portletMode;
   protected String secure;
   protected Boolean secureBoolean;
   protected boolean absolute;
   protected String windowState;
   protected String var;

   protected PortletURL url;
   protected String contenturl;


   /*
    * (non-Javadoc)
    * 
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   @Override
   public int doStartTag() throws JspException {
      if (var != null) {
         pageContext.removeAttribute(var, PageContext.PAGE_SCOPE);
      }
      // make sure we start with an unset url variable;
      setUrl(null);

      if (StringUtils.isEmpty(page) && StringUtils.isEmpty(window) && StringUtils.isNotEmpty(elementId)) {
         contenturl = ResourcesUtil.getServletPathWithAssociation("content", "/content/*", elementId, null);
         return EVAL_PAGE;
      }

      PortletURL renderUrl = getRenderUrl();
      if (renderUrl == null) {
         throw new JspException("RenderUrl is not found");
      }
      else {
         setUrl(renderUrl);
         if (portletMode != null) {
            try {
               PortletMode mode = new PortletMode(portletMode);
               url.setPortletMode(mode);
            }
            catch (PortletModeException e) {
               throw new JspException(e);
            }
         }
         if (windowState != null) {
            try {
               WindowState state = new WindowState(windowState);
               url.setWindowState(state);
            }
            catch (WindowStateException e) {
               throw new JspException(e);
            }
         }
         if (secure != null) {
            try {
               url.setSecure(getSecureBoolean());
            }
            catch (PortletSecurityException e) {
               throw new JspException(e);
            }
         }
         if (elementId != null) {
            url.setParameter("elementId", elementId);
         }
      }
      return EVAL_PAGE;
   }


   protected abstract PortletURL getRenderUrl();


   /**
    * @return int
    */
   @Override
   public int doEndTag() throws JspException {
      String urlString;
      if (url == null) {
         urlString = contenturl;
      }
      else {
         urlString = url.toString();
      }
      if (absolute) {
         urlString = HttpUtil.makeAbsolute((HttpServletRequest) pageContext.getRequest(), urlString);
      }
      if(!ServerUtil.useServerName() && urlString.contains("/content/")) {
         PortalEnvironment env = PortalEnvironment.getPortalEnvironment((HttpServletRequest)pageContext.getRequest());
         PortalURL currentURL = env.getRequestedPortalURL();
         String path = currentURL.getGlobalNavigationAsString();
         String server = (path.indexOf("/") != -1)?(path.substring(0, path.indexOf("/"))):path;
         if(urlString.contains("?")) {
         	urlString += "&server="+server;
         }
         else {
         	urlString += "?server="+server;
         }
      }
      
      if (var == null) {
         try {
            JspWriter writer = pageContext.getOut();
            writer.print(urlString);
         }
         catch (IOException ioe) {
            throw new JspException("actionURL/renderURL Tag Exception: cannot write to the output writer.", ioe);
         }
      }
      else {
         pageContext.setAttribute(var, urlString, PageContext.PAGE_SCOPE);
      }
      page = null;
      window = null;
      elementId = null;

      portletMode = null;
      secure = null;
      secureBoolean = null;
      windowState = null;
      var = null;

      url = null;
      contenturl = null;

      return EVAL_PAGE;
   }


   public String getLink() {
      String link = "";
      NavigationItem item = SiteManagement.convertToNavigationItem(page);
      if (item != null) {
         link = SiteManagement.getPath(item, !ServerUtil.useServerName());
      }
      else {
         //Throw error, should use this function with full path!
         throw new IllegalArgumentException("item == null; getLink should be called with full page path!");
      }

      return link;
   }

   
   public String getHost() {
      String host = null;
      if (ServerUtil.useServerName()) {
         NavigationItem item = SiteManagement.convertToNavigationItem(page);
         host = SiteManagement.getSite(item);
      }
      return host;
   }


   /**
    * Returns the portletMode.
    * 
    * @return String
    */
   public String getPortletMode() {
      return portletMode;
   }


   /**
    * @return secure as String
    */
   public String getSecure() {
      return secure;
   }


   /**
    * @return secure as Boolean
    */
   public boolean getSecureBoolean() {
      return this.secureBoolean.booleanValue();
   }


   /**
    * Returns the windowState.
    * 
    * @return String
    */
   public String getWindowState() {
      return windowState;
   }


   /**
    * @return PortletURL
    */
   public PortletURL getUrl() {
      return url;
   }


   /**
    * Returns the var.
    * 
    * @return String
    */
   public String getVar() {
      return var;
   }


   /**
    * Sets the portletMode.
    * 
    * @param portletMode
    *           The portletMode to set
    */
   public void setPortletMode(String portletMode) {
      this.portletMode = portletMode;
   }


   /**
    * Sets secure to boolean value of the string
    * 
    * @param secure
    */
   public void setSecure(String secure) {
      this.secure = secure;
      this.secureBoolean = Boolean.valueOf(secure);
   }


   /**
    * Sets the windowState.
    * 
    * @param windowState
    *           The windowState to set
    */
   public void setWindowState(String windowState) {
      this.windowState = windowState;
   }


   /**
    * Sets the url.
    * 
    * @param url
    *           The url to set
    */
   public void setUrl(PortletURL url) {
      this.url = url;
   }


   /**
    * Sets the var.
    * 
    * @param var
    *           The var to set
    */
   public void setVar(String var) {
      this.var = var;
   }


   public String getPage() {
      return page;
   }


   public void setPage(String page) {
      this.page = page;
   }


   public String getWindow() {
      return window;
   }


   public void setWindow(String window) {
      this.window = window;
   }


   public String getElementId() {
      return elementId;
   }


   public void setElementId(String elementId) {
      this.elementId = elementId;
   }


   public void setAbsolute(boolean absolute) {
      this.absolute = absolute;
   }


   public boolean isAbsolute() {
      return absolute;
   }
}
