package com.finalist.cmsc.ehcache;

import java.util.ArrayList;

import java.util.List;


import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;

public abstract class SelfPopulatingCacheManager extends BlockingCacheManager {

   /**
    * Constructor. Caches are set up here.
    */
   public SelfPopulatingCacheManager() throws CacheException {
       super();
       setupCaches();
   }

   /**
    * Constructor. Caches are set up here.
    */
   public SelfPopulatingCacheManager(CacheManager mgr) throws CacheException {
       super(mgr);
       setupCaches();
   }

   /**
    * Gets a self-populating cache.
    *
    * @param name the name of the cache
    * @throws CacheException If the cache does not exist.
    */
   public SelfPopulatingCache getSelfPopulatingCache(final String name) throws CacheException {
       // Create the cache
       final SelfPopulatingCache cache = (SelfPopulatingCache) caches.get(name);
       if (cache == null) {
           throw new CacheException("Cache " + name + " cannot be retrieved. Please check ehcache.xml");
       }
       return cache;
   }

   /**
    * Refreshes all caches.
    */
   public void refreshAll() throws Exception {
       final List<SelfPopulatingCache> caches = getCaches();
       for (int i = 0; i < caches.size(); i++) {
           final SelfPopulatingCache cache = caches.get(i);
           cache.refresh();
       }
   }

   /**
    * Refreshes a SelfPopulatingCache. The cache will repopulate itself.
    *
    * @param name the name of the cache
    * @throws CacheException
    */
   public void refresh(final String name) throws CacheException {
       final SelfPopulatingCache cache = getSelfPopulatingCache(name);
       cache.refresh();
   }


   /**
    * Creates a self-populating cache.
    */
   protected synchronized SelfPopulatingCache createSelfPopulatingCache(final String name,
                                                                        final CacheEntryFactory factory) throws CacheException {
       if (caches.containsKey(name)) {
           throw new CacheException("A cache with name \"" + name + "\" already exists.");
       }

       // Create the cache
       SelfPopulatingCache cache = null;
       cache = new SelfPopulatingCache(name, factory);
       caches.put(name, cache);
       return cache;
   }

   /**
    * Creates a self-populating cache with an UpdatingCacheEntryFactory
    */
   protected synchronized SelfPopulatingCache createUpdatingSelfPopulatingCache(final String name,
           final UpdatingCacheEntryFactory factory) throws CacheException {
       if (caches.containsKey(name)) {
           throw new CacheException("A cache with name \"" + name + "\" already exists.");
       }

       // Create the cache
       SelfPopulatingCache cache = null;
       cache = new SelfPopulatingCache(name, factory);
       caches.put(name, cache);
       return cache;
   }

   /**
    * Builds the set of caches. Returns a copy so that the monitor can be released.
    */
   protected synchronized List<SelfPopulatingCache> getCaches() {
       final ArrayList<SelfPopulatingCache> caches = new ArrayList<SelfPopulatingCache>();
       caches.addAll(this.caches.values());
       return caches;
   }

   /**
    * Sets up the caches used by the manager.
    */
   protected synchronized void setupCaches() throws CacheException {
       doSetupCaches();
   }

   /**
    * A template method to set up caches. It is wrapped by {@link #setupCaches}
    * to ensure that caches are created within a synchronized method.
    * <p/>
    * Implementations of this method should typically call {@link #createSelfPopulatingCache(java.lang.String, net.sf.ehcache.constructs.blocking.CacheEntryFactory)},
    * or {@link #createUpdatingSelfPopulatingCache(java.lang.String, net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory)}
    * for each required cache.
    */
   protected abstract void doSetupCaches() throws CacheException;
}
