/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.*;
import org.mmbase.remotepublishing.builders.PublishingQueueBuilder;
import org.mmbase.remotepublishing.util.PublishUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.publish.NodePublisher;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.publish.*;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.services.workflow.Workflow;

public class PublishServiceMMBaseImpl extends PublishService implements PublishListener {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(PublishServiceMMBaseImpl.class);

   public PublishServiceMMBaseImpl() {
      PublishingQueueBuilder.addPublishListener(this);
   }


   @Override
   public boolean isPublished(Node node) {
      return PublishManager.isPublished(node);
   }


   @Override
   public void publish(Node node) {
      Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
      getPublisher(node).addNodesToPublish(node, nodes);
      if (!nodes.isEmpty()) {
          publishNodes(node.getCloud(), nodes);
       }
       else {
           Workflow.complete(node);
       }
   }

   protected void publishNodes(Cloud cloud, Map<Node, Date> nodes) {
       for (Map.Entry<Node, Date> entry : nodes.entrySet()) {
           Node pnode = entry.getKey();
           Date publish = entry.getValue();
           PublishUtil.publishOrUpdateNode(cloud, pnode.getNumber(), publish);
       }
   }
   
   @Override
   public void publishRelations(Node node, NodeList nodes) {
      List<Integer> relatedNodes = new ArrayList<Integer>();
      for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
          Node content = iterator.next();
          if (isPublished(content)) {
              relatedNodes.add(content.getNumber());
          }
      }
      if (!relatedNodes.isEmpty()) {
          PublishUtil.publishOrUpdateRelations(node.getCloud(), node.getNumber(), relatedNodes);
      }
      else {
          if (Workflow.isWorkflowElement(node)) {
              Workflow.complete(node);
           }
      }
   }


   @Override
   public void remove(Node node) {
       Set<Node> removeNodes = getPublisher(node).remove(node);
       removeNodes(removeNodes);
   }
   
   protected void removeNodes(Collection<Node> removeNodes) {
       for (Node pnode : removeNodes) {
           PublishUtil.removeFromQueue(pnode);
       }
   }

   @Override
   public void unpublish(Node node) {
      if (isPublished(node)) {
         Set<Node> nodes = getPublisher(node).unpublish(node);
         for (Node pnode : nodes) {
             PublishUtil.removeNode(node.getCloud(), pnode.getNumber());
         }
      }
   }


   @Override
   public boolean isPublishable(Node node) {
      Cloud cloud = node.getCloud();
      String nodeName = node.getNodeManager().getName();
      return !TypeUtil.isSystemType(nodeName)
            && (getAssetPublisher(cloud).isPublishable(node) || getContentPublisher(cloud).isPublishable(node) || getNavigationPublisher(cloud, nodeName).isPublishable(node) || getChannelPublisher(
                  cloud).isPublishable(node));
   }


   private Publisher getPublisher(Node node) {
      Publisher publisher = getAssetPublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getContentPublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getChannelPublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getNavigationPublisher(node.getCloud(), node.getNodeManager().getName());
      if (publisher != null && publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getNodePublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
    	 return publisher;
      }
      throw new IllegalArgumentException("Node was not publishable " + node);
   }

   private Publisher getNodePublisher(Cloud cloud) {
      return new NodePublisher(cloud);
   }

   private Publisher getAssetPublisher(Cloud cloud) {
      return new AssetPublisher(cloud);
   }
   
   private Publisher getContentPublisher(Cloud cloud) {
      return new ContentPublisher(cloud);
   }


   private Publisher getChannelPublisher(Cloud cloud) {
      return new ChannelPublisher(cloud);
   }


   private Publisher getNavigationPublisher(Cloud cloud, String type) {
      for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
         Publisher publisher = manager.getPublisher(cloud, type);
         if (publisher != null) {
            return publisher;
         }
      }
      return null;
   }


   public void published(Node publishedNode) {
      if (Workflow.isWorkflowElement(publishedNode)) {
         Workflow.complete(publishedNode);
      }
   }


   public void publishedFailed(Node publishedNode, String systemMessage) {
      if (Workflow.isWorkflowElement(publishedNode) && Workflow.hasWorkflow(publishedNode)) {
         Workflow.reject(publishedNode,systemMessage);
      }
   }


   @Override
   public int getRemoteNumber(Node node) {
       Map<Integer,Integer> numbers = PublishManager.getPublishedNodeNumbers(node);
       Iterator<Integer> iter = numbers.values().iterator();
       if (iter.hasNext()) {
           return iter.next();
       }
       return -1;
   }

   @Override
   public Node getRemoteNode(Node node) {
       Map<Integer,Node> numbers = PublishManager.getPublishedNodes(node);
       Iterator<Node> iter = numbers.values().iterator();
       if (iter.hasNext()) {
           return iter.next();
       }
       return null;
   }

   @Override
   public Cloud getRemoteCloud(Cloud cloud) {
      return CloudManager.getCloudByAlias(cloud, "cloud.remote");
   }
   
   public String getRemoteUrl(Node node) {
      if (node == null) return null;
      
      if (PagesUtil.isPageType(node) || SiteUtil.isSite(node)) {

         //Retrieve the site to create its server name
         Node site = NavigationUtil.getPathToRoot(node).get(0); //Get the site of the node.
         String path = NavigationUtil.getPathToRootString(node, false);
         String serverName = site.getStringValue(SiteUtil.REMOTE_FIELD);
         
         return getRemoteNavigationUrl(serverName, path);
      }
      
      if (ContentElementUtil.isContentElement(node)) {
         if (Publish.isPublished(node) && Search.hasContentPages(node)) {
            if (ContentElementUtil.isContentElement(node) && !Search.hasContentPages(node)) {
               return null;
            }
            
            Cloud cloud = node.getCloud();
            int pageNumber = Search.findDetailPageForContent(node).getPageNumber();
            Node pageNode = cloud.getNode(pageNumber);
            
            Node site = NavigationUtil.getPathToRoot(pageNode).get(0);
            String serverName = site.getStringValue(SiteUtil.REMOTE_FIELD);
            
            int remoteNumber = Publish.getRemoteNumber(node);
            String appPath = "/content/" + remoteNumber;
            return getRemoteResourceUrl(serverName, appPath);
            
         }
         return null;
      }
      
      throw new IllegalArgumentException("Node is not a page or a content element; can not proceed.");
   }
   
   @Override
   public boolean inPublishQueue(Node node) {
      return PublishUtil.inPublishQueue(node);
   }

   @Override
   public void updateUser(Node userNode, String password) {
      CloudManager.updateLocalUser(userNode, password);
      if (isPublished(userNode)) {
         try {
            CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
            PublishManager.updateNodeOnly(localCloudInfo, userNode);
            CloudManager.updateRemoteUser(userNode, password);
         }
         catch (PublishException e) {
            log.error("Failed to update user in remote cloud", e);
         }
      }
   }
}
