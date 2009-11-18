package com.finalist.pluto.portalImpl.aggregation.portletcache;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;

public class PortletCache {

	private static final Log log = LogFactory.getLog(PortletCache.class);

	private static HashMap<String, ReentrantReadWriteLock> cacheLocks = new HashMap<String, ReentrantReadWriteLock>();

	private static ReentrantLock cacheLockLock = new ReentrantLock();
	
	private static Cache cache;
	
	/**
	 * initialize the cache
	 */
	static {
		try {
			CacheManager manager = CacheManager.create();
			cache = manager.getCache("PortletCache");
		} catch (CacheException e) {
			log.error("Unable to create portlet cache!",e);
		}
	}
	
	/**
	 * Get a cache lock, needed for concurrency
	 * 
	 * @param cacheKey
	 * @return
	 */
	public static ReentrantReadWriteLock getCacheLock(String cacheKey) {
		cacheLockLock.lock();
		try {
			ReentrantReadWriteLock lock = cacheLocks.get(cacheKey);
			if (lock == null) {
				lock = new ReentrantReadWriteLock();
				cacheLocks.put(cacheKey, lock);
			}
			return lock;
		}
		finally {
			cacheLockLock.unlock();
		}
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

	/**
	 * Get the cache version of a portlet for certain settings
	 * 
	 * @param settings
	 * @return
	 */
	public static String getCachedVersion(CacheSettings settings) {
		int cacheTime = settings.getCacheTime();
		String cacheKey = settings.getCacheKey();
		Cache cache = PortletCache.getCache();
		
		String value = null;
		try {
			Element element = cache.get(cacheKey);
			if(element != null) {
				value = (String)element.getValue();
			}
		} catch (IllegalStateException e) {
			log.error("Unable to read from portlet cache", e);
		} catch (CacheException e) {
			log.error("Unable to read from portlet cache", e);
		}
		
		if(log.isDebugEnabled()) {
			log.debug("Serving out from cache("+cacheTime+") "+cacheKey+": "+((value == null)?null:(value.length()+" bytes")));
		}
		return value;
	}

	/**
	 * Get the cache 
	 * @param cacheTime
	 * @return
	 */
	public static synchronized Cache getCache() {
		return cache;
	}
	
	/**
	 * Store a rendered version of a portlet in the cache
	 * 
	 * @param settings
	 * @param value
	 */
	public static void cacheRenderedPortlet(CacheSettings settings, String value) {
		int cacheTime = settings.getCacheTime();
		String cacheKey = settings.getCacheKey();
		
		if(log.isDebugEnabled()) {
			log.debug("Storing in cache("+cacheTime+") "+cacheKey+": "+((value == null)?null:(value.length()+" bytes")));
		}
		Element element = new Element(cacheKey, value, false, cacheTime, cacheTime);
		PortletCache.getCache().put(element);
	}

	
	/** 
	 * Create the CacheSettings object for a certain portlet request
	 * 
	 * @param fragment
	 * @param request
	 * @param wrappedRequest
	 * @return
	 */
	public static CacheSettings getCacheSettings(PortletFragment fragment, HttpServletRequest wrappedRequest) {

		if (ServerUtil.isLive()) {
		
			int cacheTime = fragment.getExpirationCache();
			boolean isPortletCacheable = (cacheTime > 0);

			if (isPortletCacheable) {
				String cacheKey = PortletCache.getCacheKey(fragment, wrappedRequest);
				ReentrantReadWriteLock cacheLock = PortletCache.getCacheLock(cacheKey);
				CacheSettings settings = new CacheSettings(cacheTime, cacheLock, cacheKey);
				cacheLock.writeLock().lock();
				cacheLock.readLock().lock();
				return settings;
			}
		} 

		return null;
	}
}
