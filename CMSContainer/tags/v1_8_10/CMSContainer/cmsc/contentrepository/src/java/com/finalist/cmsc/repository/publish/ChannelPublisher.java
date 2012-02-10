package com.finalist.cmsc.repository.publish;

import java.util.Date;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publisher;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class ChannelPublisher extends Publisher {

   public ChannelPublisher(Cloud cloud) {
      super(cloud);
   }

   @Override
   public boolean isPublishable(Node node) {
      return RepositoryUtil.isChannel(node);
   }

   @Override
   public void addNodesToPublish(Node node, Map<Node, Date> nodes) {
      nodes.put(node, new Date());
   }

}
