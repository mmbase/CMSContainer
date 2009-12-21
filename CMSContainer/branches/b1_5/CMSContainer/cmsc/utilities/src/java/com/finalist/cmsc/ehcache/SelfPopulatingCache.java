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
import java.util.Collection;
import java.util.Iterator;

import net.sf.ehcache.*;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A {@link net.sf.ehcache.Cache} backed cache that creates entries on demand.
 * <p/>
 * Clients of the cache simply call it without needing knowledge of whether
 * the entry exists in the cache.
 * <p/>
 * The cache is designed to be refreshed. Refreshes operate on the backing cache, and do not
 * degrade performance of {@link #get(java.io.Serializable)} calls.
 *
 * Thread safety depends on the factory being used. The UpdatingCacheEntryFactory should be made
 * thread safe. In addition users of returned values should not modify their contents.
 *
 * For values which are {@link Collection}s, extra safety has been built in.
 * Use the {@link SelfPopulatingCollectionCache}
 *
 * @author Greg Luck
 * @version $Id: SelfPopulatingCache.java,v 1.1.1.1 2004/11/15 06:15:58 gregluck Exp $
 * @see SelfPopulatingCollectionCache
 */
public class SelfPopulatingCache extends BlockingCache {
    private static final Log LOG = LogFactory.getLog(SelfPopulatingCache.class.getName());

    /**
     * A factory for creating entries, given a key
     */
    protected final CacheEntryFactory factory;

    /**
     * Creates a SelfPopulatingCache.
     */
    public SelfPopulatingCache(final String name, final CacheEntryFactory factory)
        throws CacheException {
        super(name);
        this.factory = factory;
    }

    public SelfPopulatingCache(Cache cache, final CacheEntryFactory factory)
       throws CacheException {
       super(cache);
       this.factory = factory;
    }

    
    /**
     * Looks up an object, creating it if not found.
     */
    public Element get(final Object key) throws CacheException {
        String oldThreadName = Thread.currentThread().getName();
        setThreadName("get", key);

        try {
            //if null will lock here
            Element element = super.get(key);

            if (element == null) {
                // Value not cached - fetch it
                Object value = factory.createEntry(key);
                setThreadName("put", key);
                put(key, value);
                Thread.currentThread().setName(oldThreadName);
                
                element = new Element(key, value);
            }

            return element;
        } catch (final Throwable throwable) {
            // Could not fetch - Ditch the entry from the cache and rethrow
            setThreadName("put", key);
            put(key, null);

            try {
                throw new CacheException("Could not fetch object for cache entry \"" + key + "\".", throwable);
            } catch (NoSuchMethodError e) {
                //Running 1.3 or lower
                throw new CacheException("Could not fetch object for cache entry \"" + key + "\".");
            }
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

    /**
     * Rename the thread for easier thread dump reading.
     *
     * @param method the method about to be called
     * @param key    the key being operated on
     */
    protected void setThreadName(String method, final Object key) {
        StringBuffer threadName = new StringBuffer(getName()).append(": ").append(method).append("(").append(key).append(")");
        Thread.currentThread().setName(threadName.toString());
    }

    /**
     * Refresh the elements of this cache.
     * <p/>
     * Refreshes bypass the {@link BlockingCache} and act directly on the backing {@link Cache}.
     * This way, {@link BlockingCache} gets can continue to return stale data while the refresh, which
     * might be expensive, takes place.
     * <p/>
     * Quiet methods are used, so that statistics are not affected.
     * <p/>
     * Threads entering this method are temporarily renamed, so that a Thread Dump will show
     * meaningful information.
     * <p/>
     * Configure ehcache.xml to stop elements from being refreshed forever:
     * <ul>
     * <li>use timeToIdle to discard elements unused for a period of time
     * <li>use timeToLive to discard elmeents that have existed beyond their allotted lifespan
     * </ul>
     */
    public void refresh() throws CacheException {
        final String oldThreadName = Thread.currentThread().getName();
        Exception exception = null;

        // Refetch the entries
        final Collection keys = getKeys();

        if (LOG.isTraceEnabled()) {
            LOG.trace(getName() + ": found " + keys.size() + " keys to refresh");
        }

        // perform the refresh
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            final Serializable key = (Serializable) iterator.next();

            try {
                Cache backingCache = getCache();
                final Element element = backingCache.getQuiet(key);

                if (element == null) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(getName() + ": entry with key " + key + " has been removed - skipping it");
                    }

                    continue;
                }

                refreshElement(element, backingCache);
            } catch (final Exception e) {
                // Collect the exception and keep going.
                // Throw the exception once all the entries have been refreshed
                // If the refresh fails, keep the old element. It will simply become staler.
                LOG.warn(getName() + "Could not refresh element " + key, e);
                exception = e;
            } finally {
                Thread.currentThread().setName(oldThreadName);
            }
        }

        if (exception != null) {
            throw new CacheException(exception.getMessage());
        }
    }

    /**
     * Refresh a single element.
     *
     * @param element      the Element to refresh
     * @param backingCache the underlying {@link Cache}.
     * @throws Exception
     */
    protected void refreshElement(final Element element, Cache backingCache)
        throws Exception {
        Serializable key = element.getKey();

        if (LOG.isTraceEnabled()) {
            setThreadName("refreshElement", key);
            LOG.trace(getName() + ": refreshing element with key " + key);
        }

        final Element replacementElement;

        if (factory instanceof UpdatingCacheEntryFactory) {
            //update the value of the cloned Element in place
            replacementElement = element;
            ((UpdatingCacheEntryFactory) factory).updateEntryValue(key, replacementElement.getValue());

            //put the updated element back into the backingCache, without updating stats
            //It is not usually necessary to do this. We do this in case the element expired
            //or idles out of the backingCache. In that case we hold a reference to it but the
            // backingCache no longer does.
        } else {
            final Object value = factory.createEntry(key);
            replacementElement = new Element(key, value);
        }

        backingCache.putQuiet(replacementElement);
    }

}
