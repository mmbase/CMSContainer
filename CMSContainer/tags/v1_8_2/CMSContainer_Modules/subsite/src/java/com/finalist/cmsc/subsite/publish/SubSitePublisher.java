package com.finalist.cmsc.subsite.publish;

import java.util.Date;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.navigation.publish.PagePublisher;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.subsite.util.SubSiteUtil;

public class SubSitePublisher extends PagePublisher {

   public SubSitePublisher(Cloud cloud) {
      super(cloud);
   }

   @Override
   public void addNodesToPublish(Node node, Map<Node, Date> nodes) {
       addPageNodes(node, nodes);
       addSubSiteChannel(node,nodes);
   }

   protected void addSubSiteChannel(Node node, Map<Node, Date> nodes) {
      // Publish content channel of PersonalPage or SubSite-object

      if (SubSiteUtil.isSubSiteType(node)) {
         Node subsiteNode = SubSiteUtil.getSubsiteChannel(node);
         if (subsiteNode != null) {
            addChannels(nodes, subsiteNode);
         }
      } else if (SubSiteUtil.isPersonalPageType(node)) {
         // the root channel from this personal page
         Node ppNode = SubSiteUtil.getPersonalpageChannel(node);

         if (ppNode != null) {
            addChannels(nodes, ppNode);

            // add any children channels as well
            NodeList children = RepositoryUtil.getChildren(ppNode);
            for (NodeIterator ni = children.nodeIterator(); ni.hasNext();) {
               addChannels(nodes, ni.nextNode());
            }
         }
      }
   }
   
   @Override
   protected void findPageNodes(Node node, Map<Node, Date> nodes, Date publishDate) {
       NodeManager nodeManager = node.getNodeManager();
       if ( !ContentElementUtil.isContentType(nodeManager.getName())) {
           super.findPageNodes(node, nodes, publishDate);    
       }       
   }

   @Override
   public boolean isPublishable(Node node) {
      return (SubSiteUtil.isSubSiteType(node) || SubSiteUtil.isPersonalPageType(node));
   }
}
