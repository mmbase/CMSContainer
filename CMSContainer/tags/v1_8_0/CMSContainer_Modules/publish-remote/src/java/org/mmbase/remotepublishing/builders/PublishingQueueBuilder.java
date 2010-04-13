/*
 * MMBase Remote Publishing
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.remotepublishing.builders;

import java.util.*;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Node;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.remotepublishing.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Main class that publishes or removes nodes to or from remote clouds.
 */
public class PublishingQueueBuilder extends MMObjectBuilder {

    public static final String STATUS_DONE = "done";
    public static final String STATUS_FAIL = "fail";
    public static final String STATUS_SCHEDULED = "scheduled";
    public static final String STATUS_INIT = "init";

    public static final String FIELD_STATUS = "status";
    public static final String FIELD_PUBLISHDATE = "publishdate";

    protected static Logger log = Logging.getLoggerInstance(PublishingQueueBuilder.class.getName());

    /** thread performing the task */
    private PublishQueueFillTask publishFillTask = null;

    private static Map<Integer, PublishQueue> queuesMap = new HashMap<Integer, PublishQueue>();

    int remoteCloudNumber = -1;

    /** List of cloud names where to publish to */
    String remoteCloudName;


    private static List<PublishListener> publishListeners = new ArrayList<PublishListener>();

    /**
     * MMBase builder init method. 
     * This method looks for the remotecloud property in the builder xml. 
     */
    @Override
    public boolean init() {
        Map<String, String> params = getInitParameters("mmbase/remotepublishing");

        remoteCloudName = params.get("remotecloud");
        if ((remoteCloudName != null) && (!"".equals(remoteCloudName))) {
            log.info("remote cloud will be " + remoteCloudName);

            /* Milliseconds how long the thread will sleep */
            long interval = 60 * 1000;
            String intervalStr = params.get("interval");
            if (intervalStr != null) { 
                interval = Integer.parseInt(intervalStr) * 1000;
            }

            /* Milliseconds how long the task will sleep before starting */
            long startDelay = 60 * 1000;
            String startDelayStr = params.get("startDelay");
            if (startDelayStr != null) {
                startDelay = Long.parseLong(startDelayStr) * 1000;
            }
            
            if (publishFillTask == null) {
                Timer publishQueueTimer = new Timer(true);
                publishFillTask = new PublishQueueFillTask();
                publishQueueTimer.schedule(publishFillTask, startDelay, interval);
            }
            else {
                log.warn("init() method of the PublishingQueueBuilder was called multiple times");
            }
        }
        else {
            remoteCloudName = null;
            log.warn("cloudlist parameter missing publishing disabled");
        }

        return super.init();
    }

    @Override
    public void shutdown() {
        publishFillTask.cancel();
        publishFillTask = null;
        super.shutdown();
    }
    
    @Override
    public void setDefaults(MMObjectNode node) {
        super.setDefaults(node);
        if ((remoteCloudNumber == -1) && (remoteCloudName != null)) {
            remoteCloudNumber = CloudManager.getLocalCloudNumber(remoteCloudName);
        }
        node.setValue(PublishQueueItem.FIELD_DESTINATIONCLOUD, remoteCloudNumber);
    }

    @Override
    public int insert(String owner, MMObjectNode node) {
        updateStatus(node);
        int number = super.insert(owner, node);
        addItemToQueue(node);
        return number;
    }

    
    @Override
    public boolean commit(MMObjectNode node) {
       updateStatus(node);
       boolean retval = super.commit(node);
       addItemToQueue(node);
       return retval;
    }

    private void updateStatus(MMObjectNode node) {
       String status = node.getStringValue(FIELD_STATUS);
       if (STATUS_INIT.equals(status)) {
           Date now = new Date();
           Date publishDate =  node.getDateValue(FIELD_PUBLISHDATE);
           if (now.before(publishDate)) {
               node.setValue(FIELD_STATUS, STATUS_SCHEDULED);
           }
       }
   }

    private void addItemToQueue(MMObjectNode node) {
       String status = node.getStringValue(FIELD_STATUS);
       if (STATUS_INIT.equals(status)) {
            PublishQueueItem publishItem = new PublishQueueItem(node);
            addItemToQueue(publishItem);
       }
    }

    protected void addItemToQueue(PublishQueueItem publishItem) {
        PublishQueue queue = getQueue(publishItem.getRemoteCloudNumber());
        if (queue.isActive()) { 
            queue.addToPublishQueue(publishItem);
        }
    }

    private void updateNodeStatus(int queueNumber, String status) {
        MMObjectNode mmNode = getNode(queueNumber);
        updateNodeStatus(mmNode, status);
    }

    protected void updateNodeStatus(MMObjectNode mmNode, String status) {
        mmNode.setValue(FIELD_STATUS, status);
        mmNode.commit();
    }

    public NodeSearchQuery createQuery(String status) {
        NodeSearchQuery query = new NodeSearchQuery(this);
        
        StepField statusField = query.getField(this.getField(FIELD_STATUS));
        
        BasicFieldValueConstraint statusConstraint = new BasicFieldValueConstraint(statusField, status);
        statusConstraint.setOperator(FieldCompareConstraint.EQUAL);
        statusConstraint.setCaseSensitive(true);

        if (!this.hasField(PublishQueueItem.FIELD_PUBLISHDATE)) {
            query.setConstraint(statusConstraint);
        }
        else {
            StepField publishDateField = query.getField(this.getField(PublishQueueItem.FIELD_PUBLISHDATE));
            Constraint publishNull = new BasicFieldNullConstraint(publishDateField);

            BasicFieldValueConstraint publishNow = new BasicFieldValueConstraint(publishDateField, new Date());
            publishNow.setOperator(FieldCompareConstraint.LESS_EQUAL);

            Constraint publishDateComposite = createConstraint(publishNow,
                    CompositeConstraint.LOGICAL_OR, publishNull);
            Constraint composite = createConstraint(statusConstraint,
                    CompositeConstraint.LOGICAL_AND, publishDateComposite);
            query.setConstraint(composite);
        }
        StepField numberField = query.getField(this.getField(FIELD_NUMBER));
        BasicSortOrder o = query.addSortOrder(numberField);
        o.setDirection(SortOrder.ORDER_ASCENDING);

        return query;
    }
    
    public CompositeConstraint createConstraint(Constraint c1, int operator, Constraint c2) {
        BasicCompositeConstraint c = new BasicCompositeConstraint(operator);
        if (c1 != null) c.addChild(c1);
        if (c2 != null) c.addChild(c2);
        return c;
    }


    public static void addPublishListener(PublishListener publishListener) {
        publishListeners.add(publishListener);
    }

    public static void removePublishListener(PublishListener publishListener) {
        publishListeners.remove(publishListener);
    }

    public void publishDone(int number) {
        updateNodeStatus(number, STATUS_DONE);
    }
    
    public void publishSuccessful(Node localNode) {
        for (PublishListener listener : publishListeners) {
            listener.published(localNode);
        }
    }

    public void publishFailed(CloudInfo localCloudInfo, PublishQueueItem item, BridgeException e) {
        updateNodeStatus(item.getNumber(), STATUS_FAIL);

        for (PublishListener listener : publishListeners) {
            int number = item.getLocalNodeNumber();
            if (localCloudInfo.getCloud().hasNode(number)) {
                StringBuffer message = new StringBuffer();
                Throwable t = e;
                while (t != null) {
                    message.append(t.getMessage());
                    t = t.getCause();
                    if (t != null) {
                        message.append(" | ");
                    }
                }

                listener.publishedFailed(localCloudInfo.getCloud().getNode(number), message
                        .toString());
            }
        }
    }

    private PublishQueue getQueue(int remoteCloudNumber) {
        if (queuesMap.containsKey(remoteCloudNumber) == false) {
            // not exists, create one instance
            PublishQueue instance = new PublishQueue(this, remoteCloudNumber);
            queuesMap.put(remoteCloudNumber, instance);

            String threadName = "Publishing Queue for cloud "+remoteCloudNumber;
            Thread thread = new Thread(instance,threadName);
            thread.setDaemon(true);
            thread.start();
        }
        return queuesMap.get(remoteCloudNumber);
    }
    
    public class PublishQueueFillTask extends TimerTask {

        @Override
        public void run() {
            try {
                NodeSearchQuery query = createQuery(STATUS_SCHEDULED);
                List<MMObjectNode> list = getStorageConnector().getNodes(query, false);
                if (!list.isEmpty()) {
                    log.debug("Add nodes to the queue: " + list.size());
                }
                for (MMObjectNode queueNode : list) {
                    PublishQueueItem publishItem = new PublishQueueItem(queueNode);
                    log.debug("Queue scheduled " + publishItem);
                    
                    addItemToQueue(publishItem);
                    updateNodeStatus(queueNode, STATUS_INIT);
                }
            }
            catch (SearchQueryException e) {
                log.info("Failed to execute publishqueue fill query: " + e.getMessage(), e);
            }
        }
    }

}
