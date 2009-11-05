package com.finalist.pluto.portalImpl.aggregation.portletcache;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheSettings {
    private int cacheTime = 0;
    private ReentrantReadWriteLock cacheLock = null;
    private String cachedVersion = null;
    private String cacheKey = null;
    
    
	public CacheSettings(int cacheTime, ReentrantReadWriteLock cacheLock, String cacheKey) {
		this.cacheTime = cacheTime;
		this.cacheLock = cacheLock;
		this.cacheKey = cacheKey;
	}

	public int getCacheTime() {
		return cacheTime;
	}

	public ReentrantReadWriteLock getCacheLock() {
		return cacheLock;
	}

	public String getCachedVersion() {
		return cachedVersion;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCachedVersion(String cachedVersion) {
		this.cachedVersion = cachedVersion;
	}
}
