/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.*;
import org.mmbase.remotepublishing.builders.PublishingQueueBuilder;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.publish.*;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.services.workflow.Workflow;

public class PublishServiceMMBaseImpl extends PublishService implements PublishListener {


   public PublishServiceMMBaseImpl() {
      PublishingQueueBuilder.addPublishListener(this);
   }


   @Override
   public boolean isPublished(Node node) {
      return PublishManager.isPublished(node);
   }


   @Override
   public void publish(Node node) {
      getPublisher(node).publish(node);
   }


   @Override
   public void publish(Node node, NodeList nodes) {
      getPublisher(node).publish(node, nodes);
   }


   @Override
   public void remove(Node node) {
      getPublisher(node).remove(node);
   }


   @Override
   public void unpublish(Node node) {
      if (isPublished(node)) {
         getPublisher(node).unpublish(node);
      }
   }


   @Override
   public boolean isPublishable(Node node) {
      Cloud cloud = node.getCloud();
      return !TypeUtil.isSystemType(node.getNodeManager().getName())
            && (getContentPublisher(cloud).isPublishable(node) || getPagePublisher(cloud).isPublishable(node) || getChannelPublisher(
                  cloud).isPublishable(node));
   }


   private Publisher getPublisher(Node node) {
      Publisher publisher = getContentPublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getOptionalPublisher(node.getCloud(), node.getNodeManager().getName());
      if (publisher != null && publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getPagePublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getChannelPublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
         return publisher;
      }
      publisher = getNodePublisher(node.getCloud());
      if (publisher.isPublishable(node)) {
    	 return publisher;
      }
      throw new IllegalArgumentException("Node was not publishable " + node);
   }


   private Publisher getPagePublisher(Cloud cloud) {
      return new PagePublisher(cloud);
   }

   private Publisher getNodePublisher(Cloud cloud) {
      return new NodePublisher(cloud);
   }

   private Publisher getContentPublisher(Cloud cloud) {
      return new ContentPublisher(cloud);
   }


   private Publisher getChannelPublisher(Cloud cloud) {
      return new ChannelPublisher(cloud);
   }


   private Publisher getOptionalPublisher(Cloud cloud, String type) {
      for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
         Publisher publisher = (Publisher) manager.getPublisher(cloud, type);
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
      return getPublisher(node).getRemoteNumber(node);
   }

   @Override
   public Node getRemoteNode(Node node) {
      return getPublisher(node).getRemoteNode(node);
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
   
}