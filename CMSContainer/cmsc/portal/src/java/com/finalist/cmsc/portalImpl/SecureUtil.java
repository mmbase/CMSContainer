package com.finalist.cmsc.portalImpl;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.services.community.Community;

public class SecureUtil {

	public static boolean isAllowedToSee(NavigationItem item) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node node = cloud.getNode(item.getId());
		NodeList groups = node.getRelatedNodes("pagegroup");
		if(groups.size() == 0) {
			return true;
		}
		
		for(NodeIterator i = groups.nodeIterator(); i.hasNext(); ) {
			Node group = i.nextNode();
			if(Community.hasAuthority(group.getStringValue("name"))) {
				return true;
			}
		}
		return false;
	}
}
