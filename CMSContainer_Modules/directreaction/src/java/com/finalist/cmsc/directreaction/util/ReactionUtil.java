package com.finalist.cmsc.directreaction.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.ServerUtil;

public class ReactionUtil {

   private ReactionUtil() {
      //Utility only
   }
   
   private static final String REACTION = "reaction";


   /**
    * Warning, this method is untested
    * 
    * @param number
    * @return
    */
   public static List<Reaction> getReactions(int number) {
      ArrayList<Reaction> result = new ArrayList<Reaction>();

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList nodeList = cloud.getList("" + number, "contentelement,reaction",
            "reaction.number,reaction.title,reaction.body,reaction.name,reaction.email", "", "reaction.number", null,
            null, true);
      for (NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         result.add(new Reaction(node.getIntValue("reaction.number"), node.getStringValue("reaction.title"), node
               .getStringValue("reaction.body"), node.getStringValue("reaction.name"), node
               .getStringValue("reaction.email"), null, null));
      }

      return result;
   }


   public static void addReaction(int number, String title, String body, String name, String email, String link) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager nodeManager = cloud.getNodeManager("reaction");

      Node message = nodeManager.createNode();
      message.setStringValue("title", title);
      message.setStringValue("body", body);
      message.setStringValue("name", name);
      if (email != null) {
         message.setStringValue("email", email);
      }
      if (link != null) {
         message.setStringValue("link", link);
      }
      message.commit();

      Node element = cloud.getNode(number);
      Relation posrel = element.createRelation(message, cloud.getRelationManager("posrel"));
      posrel.commit();
   }
   
   public static Cloud getRemoteCloud() {
      return getRemoteCloud(null);
   }
   
   public static Cloud getRemoteCloud(Cloud cloud) {
      /* It should use the staging cloud if it runs in single-war mode.
       * At live, the local cloud can be used.
       */
      if (cloud == null) {
         cloud = CloudProviderFactory.getCloudProvider().getCloud();
      }
      
      if (ServerUtil.isSingle() || ServerUtil.isLive()) {
         return cloud;         
      }
      
      return Publish.getRemoteCloud(cloud);
   }


   public static boolean isReaction(String node) {
      Node reacion = CloudProviderFactory.getCloudProvider().getCloud().getNode(node);
      return isReaction(reacion);
   }


   public static boolean isReaction(Node node) {
      return REACTION.equals(node.getNodeManager().getName());
   }
}
