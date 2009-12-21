/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 - 2004 Greg Luck.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by Greg Luck
 *       (http://sourceforge.net/users/gregluck) and contributors.
 *       See http://sourceforge.net/project/memberlist.php?group_id=93232
 *       for a list of contributors"
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "EHCache" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For written
 *    permission, please contact Greg Luck (gregluck at users.sourceforge.net).
 *
 * 5. Products derived from this software may not be called "EHCache"
 *    nor may "EHCache" appear in their names without prior written
 *    permission of Greg Luck.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL GREG LUCK OR OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by contributors
 * individuals on behalf of the EHCache project.  For more
 * information on EHCache, please see <http://ehcache.sourceforge.net/>.
 *
 */

package com.finalist.cmsc.ehcache;

import java.io.Serializable;
import java.util.*;

import net.sf.ehcache.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A blocking cache, backed by {@link Cache}.
 * <p/>
 * It allows concurrent read access to elements already in the cache. If the element is null, other
 * reads will block until an element with the same key is put into the cache.
 * <p/>
 * This is useful for constructing read-through or self-populating caches.
 * <p/>
 * This implementation uses the {@link Mutex} class from Doug Lea's concurrency package. If you wish to use
 * this class, you will need the concurrent package in your class path.
 * <p/>
 * It features:
 * <ul>
 * <li>Excellent liveness.
 * <li>Fine-grained locking on each element, rather than the cache as a whole.
 * <li>Scalability to a large number of threads.
 * </ul>
 *
 * A version of this class is planned which will dynamically use JDK5's concurrency package, which is
 * based on Doug Lea's, so as to avoid a dependency on his package for JDK5 systems. This will not
 * be implemented until JDK5 is released on MacOSX and Linux, as JDK5 will be required to compile
 * it, though any version from JDK1.2 up will be able to run the code, falling back to Doug
 * Lea's concurrency package, if the JDK5 package is not found in the classpath.
 * <p/>
 * The <code>Mutex</code> class does not appear in the JDK5 concurrency package. Doug Lea has
 * generously offered the following advice:
 * <p/>
 * <pre>
You should just be able to use ReentrantLock here.  We supply
ReentrantLock, but not Mutex because the number of cases where a
non-reentrant mutex is preferable is small, and most people are more
familiar with reentrant seamantics. If you really need a non-reentrant
one, the javadocs for class AbstractQueuedSynchronizer include sample
code for them.

-Doug
 * </pre>
 * <p/>
 * @author Greg Luck
 * @version $Id: BlockingCache.java,v 1.2 2004/11/17 00:59:02 gregluck Exp $
 */
public class BlockingCache {

    private static final Log LOG = LogFactory.getLog(BlockingCache.class.getName());

    /**
     * The backing Cache
     */
    private final Cache cache;

    /**
     * A map of cache entry locks, one per key, if present
     */
    private final Map locks = new HashMap();

    /**
     * Creates a BlockingCache with the given name.
     *
     * @param name the name to give the cache
     * @throws CacheException
     */
    protected BlockingCache(final String name) throws CacheException {
        CacheManager manager = null;
        try {
            manager = CacheManager.create();
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException("CacheManager cannot be created");
        }
        cache = manager.getCache(name);
        if (cache == null || !cache.getName().equals(name)) {
            throw new CacheException("Cache " + name + " cannot be retrieved. Please check ehcache.xml");
        }
    }

    public BlockingCache(Cache cache) {
       this.cache = cache;
    }

   /**
     * Retrieve the EHCache backing cache
     */
    protected net.sf.ehcache.Cache getCache() {
        return cache;
    }

    /**
     * Returns this cache's name
     */
    public String getName() {
        return cache.getName();
    }

    /**
     * Looks up an entry.  Blocks if the entry is null.
     * Relies on the first thread putting an entry in, which releases the lock
     * If a put is not done, the lock is never released
     */
    public Element get(final Object key) throws CacheException {
        Mutex lock = checkLockExistsForKey(key);
        try {
            lock.acquire();
            final Element element = cache.get(key);
            if (element != null) {
                //ok let the other threads in
                lock.release();
                return element;
            } else {
                //don't release the read lock until we write
                return null;
            }
        } catch (InterruptedException e) {
            throw new CacheException("Interrupted");
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException("EHCache exception");
        }
    }

    private synchronized Mutex checkLockExistsForKey(final Object key) {
        Mutex lock;
        lock = (Mutex) locks.get(key);
        if (lock == null) {
            lock = new Mutex();
            locks.put(key, lock);
        }
        return lock;
    }

    /**
     * Adds an entry and unlocks it
     */
    public void put(final Object key, final Object value) {
        Mutex lock = checkLockExistsForKey(key);
        try {
            if (value != null) {
                final Element element = new Element(key, value);
                cache.put(element);
            } else {
                cache.remove(key);
            }
        } finally {
            //Release the readlock here. This will have been acquired in the get, where the element was null
            lock.release();
        }
    }

    /**
     * Returns the keys of this cache.
     *
     * @return The keys of this cache.  This is not a live set, so it will not track changes to the key set.
     */
    public Collection getKeys() throws CacheException {
        return cache.getKeys();
    }

    /**
     * Drops the contents of this cache.
     */
    public void clear() throws CacheException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cache " + cache.getName() + ": removing all entries");
        }
//        try {
            cache.removeAll();
//        } catch (IOException e) {
//            throw new CacheException("Failure clearing cache");
//        }
    }
    
    public void removeAll() throws CacheException {
       cache.removeAll();
    }
    
    public void remove(Serializable key) {
       put(key, null);
    }

    
    /**
     * Synchronized version of getName to test liveness of the object lock.
     * <p/>
     * The time taken for this method to return is a useful measure of runtime contention on the cache.
     */
    public synchronized String liveness() {
        return getName();
    }

    /**
     * Gets all entries from a blocking cache. Cache is not serializable. This
     * method provides a way of accessing the keys and values of a Cache in a Serializable way e.g.
     * to return from a Remote call.
     * <p/>
     * This method may take a long time to return. It does not lock the cache. The list of entries is based
     * on a copy. The actual cache may have changed in the time between getting the list and gathering the
     * KeyValuePairs.
     * <p/>
     * This method can potentially return an extremely large object, roughly matching the memory taken by the cache
     * itself. Care should be taken before using this method.
     * <p/>
     * By getting all of the entries at once this method can transfer a whole cache with a single method call, which
     * is important for Remote calls across a network.
     *
     * @return a Serializable {@link java.util.List} of {@link KeyValuePair}s, which implement the Map.Entry interface
     * @throws CacheException where there is an error in the underlying cache
     */
    public List getEntries() throws CacheException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting entries for the " + cache.getName() + " cache");
        }
        Collection keys = cache.getKeys();
        List keyValuePairs = new ArrayList(keys.size());
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Serializable key = (Serializable) iterator.next();
            Element element = cache.get(key);
            keyValuePairs.add(new KeyValuePair(key, element.getValue()));
        }
        return keyValuePairs;
    }
}



