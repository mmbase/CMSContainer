/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.publish;

import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;


public abstract class Publisher {
    
    protected static final String SOURCE = "SOURCE";
    protected static final String DESTINATION = "DESTINATION";

    protected Cloud cloud;

    public Publisher(Cloud cloud) {
        this.cloud = cloud;
    }

    public abstract boolean isPublishable(Node node);

    public abstract void addNodesToPublish(Node node, Map<Node, Date> nodes);

    public final Set<Node> remove(Node node) {
        Map<Node, Date> nodes = new LinkedHashMap<Node, Date>();
        addNodesToPublish(node, nodes);
        return nodes.keySet();
    }

    public final Set<Node> unpublish(Node node) {
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(node);
        return nodes;
    }

    protected boolean isPublished(Node node) {
        return Publish.isPublished(node);
    }

}
