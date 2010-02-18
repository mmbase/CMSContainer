/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NotFoundException;

import com.finalist.cmsc.navigation.NavigationUtil;

public class QuickSearchAction 
    extends com.finalist.cmsc.struts.QuickSearchAction {

    @Override
    protected Node getChannelFromPath(Cloud cloud, String quicksearch) {
        return NavigationUtil.getPageFromPath(cloud, quicksearch);
    }

	protected boolean isValidChannel(Cloud cloud, int channelNumber) {
		try {
			Node node = cloud.getNode(channelNumber);
			if(node != null) {
				NodeManager nodeManager = node.getNodeManager();
				for(String manager:NavigationUtil.treeManagers) {
					if(manager.equals(nodeManager.getName())) {
						return true;
					}
				}
			}
		}
		catch(NotFoundException nfe) {
			// when not found, it is not a valid number
		}
		return false;
	}

}