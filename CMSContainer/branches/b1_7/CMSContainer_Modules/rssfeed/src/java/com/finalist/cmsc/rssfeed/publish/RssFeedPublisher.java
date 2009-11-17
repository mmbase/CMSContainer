package com.finalist.cmsc.rssfeed.publish;

import java.util.Date;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;
import com.finalist.cmsc.services.publish.Publisher;

public class RssFeedPublisher extends Publisher {

	public RssFeedPublisher(Cloud cloud) {
		super(cloud);
	}

    @Override
	public boolean isPublishable(Node node) {
        return RssFeedUtil.isRssFeedType(node);
	}
    
   @Override
   public void addNodesToPublish(Node node, Map<Node, Date> nodes) {
      addRssFeedNodes(node, nodes);
   }

   private void addRssFeedNodes(Node node, Map<Node, Date> nodes) {
      Date publishDate = node.getDateValue(PagesUtil.PUBLISHDATE_FIELD);
      nodes.put(node, publishDate);
   }
}
