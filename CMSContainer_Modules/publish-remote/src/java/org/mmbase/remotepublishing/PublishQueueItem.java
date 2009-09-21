/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remotepublishing;

import java.util.Date;

import org.mmbase.module.core.MMObjectNode;

public class PublishQueueItem {

    public static final String FIELD_ACTION = "action";
    public static final String FIELD_SOURCENUMBER = "sourcenumber";
    public static final String FIELD_DESTINATIONCLOUD = "destinationcloud";
    public static final String FIELD_RELATEDNODES = "relatednodes";
    public static final String FIELD_PUBLISHDATE = "publishdate";

    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_UPDATE_NODE = "update-node";
    public static final String ACTION_UPDATE_RELATIONS = "update-relations";
    public static final String ACTION_REMOVE = "remove";

    private int number;
    private String action;
    private int localNodeNumber;
    private int remoteCloudNumber;
    private String relatedNodes;
    private Date publishDate;
    
    public PublishQueueItem(MMObjectNode queueNode) {
        number = queueNode.getNumber();
        action = queueNode.getStringValue(FIELD_ACTION);
        localNodeNumber = queueNode.getIntValue(FIELD_SOURCENUMBER);
        remoteCloudNumber = queueNode.getIntValue(FIELD_DESTINATIONCLOUD);
        relatedNodes = queueNode.getStringValue(FIELD_RELATEDNODES);
        publishDate =  queueNode.getDateValue(FIELD_PUBLISHDATE);
    }

    public boolean isRemoveAction() {
        return action.equalsIgnoreCase(ACTION_REMOVE);
    }

    public boolean isUpdateAction() {
        return action.equalsIgnoreCase(ACTION_UPDATE)
                || action.equalsIgnoreCase(ACTION_UPDATE_NODE)
                || action.equalsIgnoreCase(ACTION_UPDATE_RELATIONS);
    }

    public boolean isUpdateNodeAndRelations() {
        return action.equalsIgnoreCase(ACTION_UPDATE);
    }

    public boolean isUpdateNodeOnly() {
        return action.equalsIgnoreCase(ACTION_UPDATE_NODE);
    }

    public boolean isUpdateRelationsOnly() {
        return action.equalsIgnoreCase(ACTION_UPDATE_RELATIONS);
    }
    
    public int getNumber() {
        return number;
    }
    
    public String getAction() {
        return action;
    }
    
    public int getLocalNodeNumber() {
        return localNodeNumber;
    }
    
    public int getRemoteCloudNumber() {
        return remoteCloudNumber;
    }
    
    public String getRelatedNodes() {
        return relatedNodes;
    }



    public Date getPublishDate() {
        return publishDate;
    }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + number;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      PublishQueueItem other = (PublishQueueItem) obj;
      if (number != other.number) return false;
      return true;
   }
   
   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(number).append(' ').append(action).append(' ').append(localNodeNumber).append(' ')
         .append(remoteCloudNumber).append(' ').append(publishDate);
      return buf.toString();
   }
}