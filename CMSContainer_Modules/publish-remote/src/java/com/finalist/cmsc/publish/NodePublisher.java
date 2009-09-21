package com.finalist.cmsc.publish;

import java.util.Date;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.services.publish.Publisher;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class NodePublisher extends Publisher{

   public NodePublisher(Cloud cloud) {
      super(cloud);
   }

   @Override
   public boolean isPublishable(Node node) {
      String name = node.getNodeManager().getName();
      return !TypeUtil.isMmbaseType(name) && !TypeUtil.isPublishType(name);
   }

   @Override
   public void addNodesToPublish(Node node, Map<Node, Date> nodes) {
      nodes.put(node, new Date());
   }

}
