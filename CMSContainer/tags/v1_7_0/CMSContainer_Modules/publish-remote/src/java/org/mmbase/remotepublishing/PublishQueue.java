/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remotepublishing;

import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.module.core.MMBase;
import org.mmbase.remotepublishing.builders.PublishingQueueBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class PublishQueue implements Runnable {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(PublishQueue.class.getName());
    
    PublishingQueueBuilder queueBuilder;
    
    private BlockingQueue<PublishQueueItem> nodesToBePublishedQueue = new LinkedBlockingQueue<PublishQueueItem>();
    private boolean initialized = false;
    private boolean active = true;

    private int remoteCloudNumber;

    private int maxtrying = 10;

    public PublishQueue(PublishingQueueBuilder queueBuilder, int remoteCloudNumber) {
        this.queueBuilder = queueBuilder;
        this.remoteCloudNumber = remoteCloudNumber;
    }
    
    public void init() {
        // Wait for mmbase to be up and running.
        MMBase.getMMBase();
        
        try {
           linkNodes("typedef", "name");
           linkNodes("mmbaseranks", "name");
           linkNodes("mmbaseusers", "username");
        }
        catch (Exception e) {
            log.error("Problem with linking nodes together", e);
        }
    }
    
    public void run() {
        if (!initialized) {
            init();
        }
        
        while (this.active) {
            try {
                PublishQueueItem item = nodesToBePublishedQueue.take();
                log.debug("Queue take " + item);
                CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
                // check if node is not removed from the cloud
                if (localCloudInfo.getCloud().hasNode(item.getNumber())) {
                    publishItem(localCloudInfo, item);
                }
            } catch (InterruptedException e1) {
                log.warn("Failed to take node from queue ", e1);
                // ignore current node, continue on next one.
            }
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void addToPublishQueue(PublishQueueItem item) {
        try {
            log.debug("Queue add " + item);
            nodesToBePublishedQueue.put(item);
        } catch (InterruptedException e) {
            log.debug("Failed to put node into queue " + item.getNumber());
        }
    }
    
    private void publishItem(CloudInfo localCloudInfo, PublishQueueItem publishItem) {
        try {
            if (publishItem.isUpdateAction()) {
                try {
                    boolean finished = false;
                    int trytimes = 0;
                    while (!finished) {
                        try {
                            update(localCloudInfo, publishItem);
                            finished = true;
                        }
                        catch (BridgeException e) {
                            if (handleRmiException(e)
                                    && ++trytimes <= maxtrying ) {
                                // if it was caused rmi connection exception
                                // and still within
                                // the maximal trying times limitation,
                                // continue to try publishing it again
                            }
                            else {
                                // otherwise, throw the runtime exception
                                throw e;
                            }
                        }
                    }
                    queueBuilder.publishDone(publishItem.getNumber());
                }
                catch (BridgeException e) {
                    log.error("Nodenumber : " + publishItem.getNumber()
                            + ", " + e, e);
                    queueBuilder.publishFailed(localCloudInfo, publishItem, e);
                }
            }
            else {
                if (publishItem.isRemoveAction()) {
                    try {
                        removeNode(localCloudInfo, publishItem);
                        queueBuilder.publishDone(publishItem.getNumber());
                    }
                    catch (BridgeException e) {
                        log.error("Removing published node ("
                                + publishItem.getNumber() + ") failed", e);
                        queueBuilder.publishFailed(localCloudInfo, publishItem, e);
                    }
                }
            }
        }
        catch (Throwable e) {
            log.error("Throwable error with nodenumber: "
                    + publishItem.getNumber() + ", " + e.getMessage());
            log.debug(Logging.stackTrace(e));
        }
    }

    /**
     * handle exception, if the root cause is RMI's connection broken, try to set cached cloud
     * information invalid.
     * 
     * @param e
     *            the exception which we should handled.
     * @return true, if the exception is caused by invalid RMI connect; false, otherwise.
     */
    private boolean handleRmiException(BridgeException e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
            if (rootCause.getClass().getPackage().getName().startsWith("java.rmi")) {
                break;
            }
        }

        if (rootCause instanceof ConnectException) {
            // if root cause is rmi connection exception
            String message = e.getCause().getMessage();
            log.debug("rmi connection exception:" + message);
            int beginIndex = message.indexOf("rmi://");
            if (beginIndex >= 0) {
                // if we know exactly what cloud is died, we just reset this one
                int endIndex = message.indexOf(" ", beginIndex);
                String url = message.substring(beginIndex, endIndex);
                CloudInfo.setCloudInvalid(url);
                return true;
            }
            else {
                // if we aren't sure which cloud is died, reset all cached remote cloud
                CloudInfo.setRemoteCloudsInvalid();
                return true;
            }

        }
        else
            if (rootCause instanceof NoSuchObjectException) {
                CloudInfo.setRemoteCloudsInvalid();
                return true;
            }
        return false;
    }

    private void update(CloudInfo localCloudInfo, PublishQueueItem item) {
        CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(item.getRemoteCloudNumber());
        Node localNode = localCloudInfo.getCloud().getNode(item.getLocalNodeNumber());
        String nodeManagerName = localNode.getNodeManager().getName();

        try {
            if (!PublishManager.isImported(localCloudInfo, localNode)) {

                if (PublishManager.isPublished(localCloudInfo, localNode)) {
                    if (localNode instanceof Relation) {
                        log.debug(nodeManagerName + " update relation with number " + item.getLocalNodeNumber());
                    }
                    else {
                        log.debug(nodeManagerName + " update node with number " + item.getLocalNodeNumber());
                    }
                    if (item.isUpdateNodeOnly()) {
                        PublishManager.updateNodeOnly(localCloudInfo, localNode);
                    }
                    if (item.isUpdateNodeAndRelations()) {
                        PublishManager.updateNodeAndRelations(localCloudInfo, localNode);
                    }
                    if (item.isUpdateRelationsOnly()) {
                        String relatedNodes = item.getRelatedNodes();
                        if (relatedNodes != null && relatedNodes.length() > 0) {
                            List<Integer> related = new ArrayList<Integer>();
                            StringTokenizer tokenizer = new StringTokenizer(relatedNodes, ",");
                            while (tokenizer.hasMoreTokens()) {
                                int relatedNodeNumber = Integer.valueOf(tokenizer.nextToken());
                                related.add(relatedNodeNumber);
                            }
                            PublishManager.updateRelations(localCloudInfo, localNode, related);
                        }
                        else {
                            PublishManager.updateNodesAndRelations(localCloudInfo, localNode,
                                    false, true);
                        }
                    }
                }
                else {
                    if (localNode instanceof Relation) {
                        log.debug(nodeManagerName + " publish relation with number "
                                + item.getLocalNodeNumber());
                    }
                    else {
                        log.debug(nodeManagerName + " publish node with number " + item.getLocalNodeNumber());
                    }

                    if (item.isUpdateNodeOnly()) {
                        PublishManager.createNodeAndRelations(localCloudInfo, localNode,
                                remoteCloudInfo, false);
                    }
                    if (item.isUpdateNodeAndRelations()) {
                        PublishManager.createNodeAndRelations(localCloudInfo, localNode,
                                remoteCloudInfo, true);
                    }
                }

                queueBuilder.publishSuccessful(localNode);
            }
            else { // if it is imported from other cloud, ignore this node
                log.debug("imported node in publishqueue " + localNode.getNumber());
            }
        }
        catch (Exception e) {
            throw new BridgeException("PublishManager could not publish " + localNode.getNumber(),
                    e);
        }
        finally {
            log.debug("Published one node(" + localNode.getNumber() + ")");
        }
    }

    private void removeNode(CloudInfo localCloudInfo, PublishQueueItem item) {
        int number = item.getLocalNodeNumber();
        PublishManager.deletePublishedNode(localCloudInfo, number);
    }

   private void linkNodes(String managerName, String compareFieldName) {
      CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
      CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
      
      NodeList nodeList = SearchUtil.findNodeList(localCloudInfo.getCloud(), managerName);
        NodeIterator nodeIterator = nodeList.nodeIterator();
        while (nodeIterator.hasNext()) {
            Node localNode = nodeIterator.nextNode();
            String localFieldValue = localNode.getStringValue(compareFieldName);
            
            if (!PublishManager.isPublished(localCloudInfo, localNode)) {
                // not published
                Node remoteNode = SearchUtil.findNode(remoteCloudInfo.getCloud(), managerName, compareFieldName, localFieldValue);
                if (remoteNode != null) {
                    PublishManager.createPublishingInfo(localCloudInfo, localNode, remoteCloudInfo, remoteNode);

                    log.info("Added remoteinfo for " + managerName + " with " + compareFieldName + " : " + localFieldValue);
                }
                else {
                    log.debug("No remote node found to link with for " + managerName + " with " + compareFieldName + " : " + localFieldValue);
                }
            }
            else {
                log.debug("Found remoteinfo for " + managerName + " with " + compareFieldName + " : " + localFieldValue);
            }
        }
   }
}
