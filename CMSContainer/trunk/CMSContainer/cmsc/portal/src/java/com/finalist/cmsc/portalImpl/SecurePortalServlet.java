package com.finalist.cmsc.portalImpl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.CloudUtil;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.community.Community;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;

@SuppressWarnings("serial")
public class SecurePortalServlet extends PortalServlet {
   
   private static Log log = LogFactory.getLog(SecurePortalServlet.class);
   
   private static final String CAS_LOGIN_LOCALE = "cas_login_locale";
   
   private static final String DEFAULT_LOGIN_URL = "casServerLoginUrl";
   	
	protected boolean doRender(HttpServletRequest request,
			HttpServletResponse response, String path) throws IOException {
	   String useSSO = SecureUtil.getEnvironment("useSSO", "false");
	   if(useSSO == null || "false".equalsIgnoreCase(useSSO)) {
	      return super.doRender(request, response, path);
	   }
      Cloud cloud = CloudUtil.getCloudFromThread();
      //In the staging side. deny community user and guest. redirect to 403 page.
	   if (!ServerUtil.isSingle() && ServerUtil.isStaging() && cloud == null) {
	      String noRightPage = SiteManagement.getSiteFromPath(path).getUrlfragment()+"/403";	     
	      if (path != null && !path.endsWith("/403")) {
   	      response.sendRedirect(HttpUtil.getWebappUri(request)+noRightPage);
   	      return true;
	      }
	   }
		NavigationItem item = SiteManagement.getNavigationItemFromPath(path);
		if (SecureUtil.isAllowedToSee(item)) {
			log.debug("Page: allowed to see");
			return super.doRender(request, response, path);
		}		
      
	   if (cloud != null) {
		   Node node = cloud.getNode(item.getId());
		   UserRole role = NavigationUtil.getRole(cloud, node, false);
		   if (SecurityUtil.isWriter(role)) {
			 return super.doRender(request, response, path);
		   }
	   }
	   if (Community.isAuthenticated()) {
	      String noRightPage = SiteManagement.getSiteFromPath(path).getUrlfragment()+"/403";
	      response.sendRedirect(HttpUtil.getWebappUri(request)+noRightPage);
	      return true;
	   }
	   String locale = null;
      if (request.getSession().getAttribute(CAS_LOGIN_LOCALE) != null) {
         locale = (String)request.getSession().getAttribute(CAS_LOGIN_LOCALE);       
      }       
      String loginUrl = DEFAULT_LOGIN_URL;
      if (locale != null) {
         loginUrl += "_"+locale;
      }
      else {
         Site site = SiteManagement.getSiteFromPath(path);
         if(site != null && site.getLanguage() != null) {
            loginUrl += "_"+site.getLanguage();
            request.getSession().setAttribute(CAS_LOGIN_LOCALE, site.getLanguage());
         }
      }
      String redirectUrl = getServletContext().getInitParameter(loginUrl)==null?getServletContext().getInitParameter(DEFAULT_LOGIN_URL):getServletContext().getInitParameter(loginUrl)+"?service="+HttpUtil.getWebappUri(request)+path;
      log.debug("Community logout url : "+redirectUrl);
      response.sendRedirect(redirectUrl);
	   return true;
	}

}
