package com.finalist.pluto.portalImpl.aggregation.portletcache;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;

public class PortletCache extends BlockingCache {

	private static PortletCache cache = new PortletCache();

   
   public PortletCache() throws CacheException {
      super(CacheManager.create().getCache("PortletCache"));
   }
   
	/**
	 * The cache key is: page number, portlet position and parameters
	 * 
	 * @param request
	 * @param wrappedRequest 
	 * @return
	 */
	public static String getCacheKey(PortletFragment fragment, HttpServletRequest wrappedRequest) {
		StringBuilder key = new StringBuilder();
		key.append(((ScreenFragment) fragment.getParent()).getPage().getId());
		key.append("_");
		key.append(fragment.getPortletWindow().getId());
		for (Object paramName : wrappedRequest.getParameterMap().keySet()) {
			key.append("&");
			key.append(paramName);
			key.append("=");
			key.append(wrappedRequest.getParameter((String) paramName));
		}
		return key.toString();
	}

	public static PortletCache getPortletCache() {
		return cache;
	}
}
