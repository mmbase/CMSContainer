/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.publish;

import java.util.*;

import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.repository.*;
import com.finalist.cmsc.services.publish.Publisher;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.workflow.Workflow;

public class ContentPublisher extends Publisher {

    public ContentPublisher(Cloud cloud) {
        super(cloud);
    }

    @Override
    public boolean isPublishable(Node node) {
        return ContentElementUtil.isContentElement(node);
    }

    @Override
    public void addNodesToPublish(Node node, Map<Node, Date> nodes) {
        boolean channelPublished = false;
        
        if (isPublished(node)) {
            channelPublished = true;
        }
        else {
            NodeList channels = RepositoryUtil.getContentChannelsForContent(node);
            for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
                Node channel = iter.next();
                if (isPublished(channel)) {
                    channelPublished = true;
                    break;
                }
            }
        }
    
        if (channelPublished) {
            addContentNodes(node, nodes);
        }
    }

    private void addContentNodes(Node node, Map<Node, Date> nodes) {
        Date publishDate = node.getDateValue(ContentElementUtil.PUBLISHDATE_FIELD);
        List<Node> contentnodes = findContentBlockNodes(node);
        for (Node pnode : contentnodes) {
            nodes.put(pnode, publishDate);
            Versioning.setPublishVersion(pnode);
        }
    }
    
    
    public static List<Node> findContentBlockNodes(Node node) {
       List<Node> nodes = new ArrayList<Node>();
       findContentBlockNodes(node, nodes);
       return nodes;
   }
   
   private static void findContentBlockNodes(Node node, List<Node> nodes) {
       if (nodes.contains(node) || TypeUtil.isSystemType(node.getNodeManager().getName())) {
           return;
       }

       nodes.add(node);
       RelationManagerList rml = node.getNodeManager().getAllowedRelations((NodeManager) null, null, DESTINATION);        
       if (!rml.isEmpty()) {
           NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
           while (childs.hasNext()) {
              Node childNode = childs.nextNode();
              if (ContentElementUtil.isContentElement(childNode)) {
                  if (!RepositoryUtil.hasContentChannel(childNode)) {
                      findContentBlockNodes(childNode, nodes);
                  }
              }
              else if(AssetElementUtil.isAssetElement(childNode)){
                 nodes.add(childNode);
              }
              else {
                  if (!RepositoryUtil.isContentChannel(childNode) &&
                          !Workflow.isWorkflowElement(childNode)) {
                     findContentBlockNodes(childNode, nodes);
                  }
              }
           }
       }
   }

}
