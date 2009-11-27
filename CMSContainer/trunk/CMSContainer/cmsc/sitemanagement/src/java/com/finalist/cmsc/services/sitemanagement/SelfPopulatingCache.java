package com.finalist.cmsc.services.sitemanagement;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.concurrent.*;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;


public class SelfPopulatingCache extends net.sf.ehcache.constructs.blocking.SelfPopulatingCache {

   private final CacheLockProvider cacheLockProvider;
   
   public SelfPopulatingCache(Ehcache cache, CacheEntryFactory factory) throws CacheException {
      super(cache, factory);
      this.cacheLockProvider = new UniqueKeyReadWriteLockSync();
   }
   
   @Override
   protected Sync getLockForKey(Object key) {
      return cacheLockProvider.getSyncForKey(key);
   }

}
