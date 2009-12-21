package com.finalist.cmsc.services.sitemanagement;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.concurrent.*;

public class UniqueKeyReadWriteLockSync implements CacheLockProvider {

   private final Map<Object, Sync> locks = new HashMap<Object, Sync>();

   public Sync getSyncForKey(Object key) {
      return checkLockExistsForKey(key);
   }

   private synchronized Sync checkLockExistsForKey(Object key) {
      Sync lock = locks.get(key);
      if (lock == null) {
         lock = new ReadWriteLockSync();
         locks.put(key, lock);
      }
      return lock;
   }

}
