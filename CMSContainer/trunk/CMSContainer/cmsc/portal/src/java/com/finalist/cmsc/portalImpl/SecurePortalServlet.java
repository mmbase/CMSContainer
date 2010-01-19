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
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.community.Community;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;

@SuppressWarnings("serial")
public class SecurePortalServlet extends PortalServlet {

	private static Log log = LogFactory.getLog(SecurePortalServlet.class);
	
	protected boolean doRender(HttpServletRequest request,
			HttpServletResponse response, String path) throws IOException {
		NavigationItem item = SiteManagement.getNavigationItemFromPath(path);
		if (SecureUtil.isAllowedToSee(item)) {
			log.debug("Page: allowed to see");
			return super.doRender(request, response, path);
		}
		
      Cloud cloud = CloudUtil.getCloudFromThread();
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
	   }
	   String locale = null;
      if (request.getSession().getAttribute("cas_login_locale") != null) {
         locale = (String)request.getSession().getAttribute("cas_login_locale");       
      } 
      String defaultLoginUrl = "casServerLoginUrl";
      if (locale != null) {
         defaultLoginUrl += "_"+locale;
      }
	   response.sendRedirect(getServletContext().getInitParameter(defaultLoginUrl)+"?service="+HttpUtil.getWebappUri(request)+path);
       log.warn("Page: not allowed to see, no login page found!");
	   return false;
	}

}
