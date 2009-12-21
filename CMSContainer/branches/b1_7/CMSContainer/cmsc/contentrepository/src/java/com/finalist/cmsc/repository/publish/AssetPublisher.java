/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.publish;

import java.util.*;

import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publisher;
import com.finalist.cmsc.services.versioning.Versioning;

public class AssetPublisher extends Publisher {

    public AssetPublisher(Cloud cloud) {
        super(cloud);
    }

    @Override
    public boolean isPublishable(Node node) {
        return AssetElementUtil.isAssetElement(node);
    }

    @Override
    public void addNodesToPublish(Node node, Map<Node, Date> nodes) {
        boolean channelPublished = false;
        
        if (isPublished(node)) {
            channelPublished = true;
        }
        else {
           Node channel = RepositoryUtil.getCreationChannel(node);
           if (isPublished(channel)) {
              channelPublished = true;
           }
        }
    
        if (channelPublished) {
            addAssetNodes(node, nodes);
        }
    }

    private void addAssetNodes(Node node, Map<Node, Date> nodes) {
       Date publishDate = node.getDateValue(AssetElementUtil.PUBLISHDATE_FIELD);
       nodes.put(node, publishDate);
       
       Versioning.setPublishVersion(node);
    }
}
