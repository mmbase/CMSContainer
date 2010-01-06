package com.finalist.cmsc.portalImpl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.NavigationItem;
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
		
	   response.sendRedirect(this.getServletContext().getInitParameter("casServerLoginUrl")+"?service="+HttpUtil.getWebappUri(request)+path);

		log.warn("Page: not allowed to see, no login page found!");
		return false;
	}

}
