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
/*
  File: Mutex.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
*/

package com.finalist.cmsc.ehcache;

/**
 * @version $Id: Mutex.java,v 1.1 2004/11/17 00:59:02 gregluck Exp $
 * @author Doug Lea
 * A simple non-reentrant mutual exclusion lock.
 * The lock is free upon construction. Each acquire gets the
 * lock, and each release frees it. Releasing a lock that
 * is already free has no effect.
 * <p/>
 * This implementation makes no attempt to provide any fairness
 * or ordering guarantees. If you need them, consider using one of
 * the Semaphore implementations as a locking mechanism.
 * <p/>
 * <b>Sample usage</b><br>
 * <p/>
 * Mutex can be useful in constructions that cannot be
 * expressed using java synchronized blocks because the
 * acquire/release pairs do not occur in the same method or
 * code block. For example, you can use them for hand-over-hand
 * locking across the nodes of a linked list. This allows
 * extremely fine-grained locking,  and so increases
 * potential concurrency, at the cost of additional complexity and
 * overhead that would normally make this worthwhile only in cases of
 * extreme contention.
 * <pre>
 * class Node {
 *   Object item;
 *   Node next;
 *   Mutex lock = new Mutex(); // each node keeps its own lock
 * <p/>
 *   Node(Object x, Node n) { item = x; next = n; }
 * }
 * <p/>
 * class List {
 *    protected Node head; // pointer to first node of list
 * <p/>
 *    // Use plain java synchronization to protect head field.
 *    //  (We could instead use a Mutex here too but there is no
 *    //  reason to do so.)
 *    protected synchronized Node getHead() { return head; }
 * <p/>
 *    boolean search(Object x) throws InterruptedException {
 *      Node p = getHead();
 *      if (p == null) return false;
 * <p/>
 *      //  (This could be made more compact, but for clarity of illustration,
 *      //  all of the cases that can arise are handled separately.)
 * <p/>
 *      p.lock.acquire();              // Prime loop by acquiring first lock.
 *                                     //    (If the acquire fails due to
 *                                     //    interrupt, the method will throw
 *                                     //    InterruptedException now,
 *                                     //    so there is no need for any
 *                                     //    further cleanup.)
 *      for (;;) {
 *        if (x.equals(p.item)) {
 *          p.lock.release();          // release current before return
 *          return true;
 *        }
 *        else {
 *          Node nextp = p.next;
 *          if (nextp == null) {
 *            p.lock.release();       // release final lock that was held
 *            return false;
 *          }
 *          else {
 *            try {
 *              nextp.lock.acquire(); // get next lock before releasing current
 *            }
 *            catch (InterruptedException ex) {
 *              p.lock.release();    // also release current if acquire fails
 *              throw ex;
 *            }
 *            p.lock.release();      // release old lock now that new one held
 *            p = nextp;
 *          }
 *        }
 *      }
 *    }
 * <p/>
 *    synchronized void add(Object x) { // simple prepend
 *      // The use of `synchronized'  here protects only head field.
 *      // The method does not need to wait out other traversers
 *      // who have already made it past head.
 * <p/>
 *      head = new Node(x, head);
 *    }
 * <p/>
 *    // ...  other similar traversal and update methods ...
 * }
 * </pre>
 * <p/>
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 */
public class Mutex {
    /**
     * The lock status *
     */
    protected boolean inUse;

    /**
     * Wait (possibly forever) until successful passage.
     * Fail only upon interuption. Interruptions always result in
     * `clean' failures. On failure,  you can be sure that it has not
     * been acquired, and that no
     * corresponding release should be performed. Conversely,
     * a normal return guarantees that the acquire was successful.
     * @see Sync#acquire() 
     */
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            try {
                while (inUse) {
                    wait();
                }
                inUse = true;
            } catch (InterruptedException ex) {
                notify();
                throw ex;
            }
        }
    }

    /**
     * @param msecs the number of milleseconds to wait.
     *              An argument less than or equal to zero means not to wait at all.
     *              However, this may still require
     *              access to a synchronization lock, which can impose unbounded
     *              delay if there is a lot of contention among threads.
     * @return true if acquired
     * @see Sync#attempt(long)
     */
    public boolean attempt(long msecs) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (!inUse) {
                inUse = true;
                return true;
            } else if (msecs <= 0) {
                return false;
            } else {
                long waitTime = msecs;
                long start = System.currentTimeMillis();
                try {
                    for (;;) {
                        wait(waitTime);
                        if (!inUse) {
                            inUse = true;
                            return true;
                        } else {
                            waitTime = msecs - (System.currentTimeMillis() - start);
                            if (waitTime <= 0) {
                                return false;
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
            }
        }
    }

    /**
     * Potentially enable others to pass.
     * <p/>
     * Because release does not raise exceptions,
     * it can be used in `finally' clauses without requiring extra
     * embedded try/catch blocks. But keep in mind that
     * as with any java method, implementations may
     * still throw unchecked exceptions such as Error or NullPointerException
     * when faced with uncontinuable errors. However, these should normally
     * only be caught by higher-level error handlers.
     * @see Sync#release()
     */
    public synchronized void release() {
        inUse = false;
        notify();
    }
}

