/*
 * MMBase Remote Publishing
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.remotepublishing;

import java.util.*;


import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * class using the cloud builder nodes to help identify clouds
 */
public final class CloudManager {

    static Logger log = Logging.getLoggerInstance(CloudManager.class.getName());

    private static final String FIELD_NAME = "name";
    private static final String FIELD_RMIURL = "rmiurl";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";

    private static final String CLOUD = "cloud";
    
    private static final String CLOUD_DEFAULT = "cloud.default";

    private CloudManager() {
        //Utility class
    }
    
    /**
     * Get a cloud number based on the node name
     * 
     * @param nameServerCloud the cloud containing the cloud nodes to use
     * @param name the name of the cloud ( in the cloud list)
     * @return cloud node number
     * @throws BridgeException if the cloud was not found
     */
    public static int getCloudNumber(Cloud nameServerCloud, String name) throws BridgeException {
        Node cloud = SearchUtil.findNode(nameServerCloud, CLOUD, FIELD_NAME, name);

        if (cloud == null) { 
            throw new BridgeException("can not find cloud with name("
                + name
                + ") in nameServerCloud");
        }
        return cloud.getNumber();
    }


    /**
     * gets the node-number of the remote cloud in the list in the cloud-builder this routine
     * depends on unique and consistent names for clouds in the cloud builder
     * 
     * see also applications/Publisher/builders/cloud.xml
     * 
     * @param nameServerCloud Cloud where the number shoud be found
     * @param cloud Cloud to determine the number from
     * @return Number of the remoteCloud as used in localCloud
     */
    public static int getCloudNumber(Cloud nameServerCloud, Cloud cloud) {
        Node cloudNode = getCloudNode(nameServerCloud, cloud);
        return cloudNode.getNumber();
    }

    public static Node getCloudNode(Cloud nameServerCloud, Cloud cloud) {
        //get the name of the cloud
        String cloudName = getDefaultCloudNode(cloud).getStringValue(FIELD_NAME);

        Node cloudNode = SearchUtil.findNode(nameServerCloud, CLOUD, FIELD_NAME, cloudName);
        if (cloudNode == null) {
            throw new BridgeException("remote cloud's number could not be established");
        }
        return cloudNode;
    }
    
    /**
     * Get a cloud object based on the node name
     * 
     * @param nameServerCloud the cloud containing the cloud nodes to use
     * @param cloudName the name of the cloud ( in the cloud list)
     * @return cloud instance
     * @throws BridgeException if the cloud was not found
     */
    public static Cloud getCloud(Cloud nameServerCloud, String cloudName) throws BridgeException {
        Node cloudNode = getCloudNodeByName(nameServerCloud, cloudName);
        return getCloudFromNode(cloudNode);
    }
    
    public static Node getCloudNodeByName(Cloud nameServerCloud, String name) throws BridgeException {
        Node cloudNode = SearchUtil.findNode(nameServerCloud, CLOUD, FIELD_NAME, name);
        if (cloudNode == null) {
            throw new BridgeException("can not find cloud with name("
                + name
                + ") in nameServerCloud");
        }
        return cloudNode;
    }
    
    /**
     * Get a cloud object based on the node alias
     * 
     * @param nameServerCloud name cloud containing the cloud nodes to use
     * @param cloudAlias node number of alias of the cloud node
     * @return cloud instance
     * @throws BridgeException if the node does not exist
     */
    public static Cloud getCloudByAlias(Cloud nameServerCloud, String cloudAlias) throws BridgeException {
        Node cloudNode = getCloudNodeByAlias(nameServerCloud, cloudAlias);
        return getCloudFromNode(cloudNode);
    }

    protected static Node getCloudNodeByAlias(Cloud nameServerCloud, String alias) throws BridgeException {
        Node cloudNode = nameServerCloud.getNode(alias);

        if (cloudNode == null) {
            throw new BridgeException("Can not find cloud with alias(" + alias
                    + ") in the nameServerCloud");
        }
        return cloudNode;
    }

    public static Cloud getCloudWithName(Cloud cloud, String name) {
        Node defaultCloud = cloud.getNode(CLOUD_DEFAULT);
        if (name.equals(defaultCloud.getStringValue(FIELD_NAME))) {
           return cloud;
        }
        int cloudNumber = CloudManager.getCloudNumber(cloud, name);
        return CloudManager.getCloud(cloud, cloudNumber);
     }

    /**
     * Get a cloud object based on the node number
     * 
     * @param nameServerCloud name cloud containing the cloud nodes to use
     * @param number the node number of the cloud in the nameServerCloud
     * @return cloud instance
     * @throws BridgeException if the node does not exist
     */
    public static Cloud getCloud(Cloud nameServerCloud, int number) throws BridgeException {
        Node node = nameServerCloud.getNode(number);
        if (node == null) {
            throw new BridgeException("Can not find cloud with number("
                + number
                + ") in the nameServerCloud"); 
        }
        return getCloudFromNode(node);
    }
    
    public static Node getCloudNodeByNumber(Cloud nameServerCloud, int number) throws BridgeException {
        Node cloudNode = nameServerCloud.getNode(number);
        if (cloudNode == null) {
            throw new BridgeException("Can not find cloud with number("
                + number
                + ") in the nameServerCloud"); 
        }
        return cloudNode;
    }

    /**
     * Get a cloud object based on the information in the node
     * cloud info consists of rmiurl, username and password
     * 
     * @param cloudNode node with cloud information stored
     * @return cloud instance
     */
    public static Cloud getCloudFromNode(Node cloudNode) {
        if (!cloudNode.getNodeManager().getName().equals(CLOUD)) { 
            throw new BridgeException("does not point to a cloud node");
        }

        String rmiUrl = cloudNode.getStringValue(FIELD_RMIURL);
        String username = cloudNode.getStringValue(FIELD_USERNAME);
        String password = cloudNode.getStringValue(FIELD_PASSWORD);

        CloudContext cloudContext = ContextProvider.getCloudContext(rmiUrl);

        if ((username != null) && !username.equals("")) {
            Map<String, String> user = new HashMap<String, String>();
            user.put(FIELD_USERNAME, username);
            user.put(FIELD_PASSWORD, password);
            return cloudContext.getCloud("mmbase", "name/password", user);
        }
        return cloudContext.getCloud("mmbase");
    }
    
    public static Cloud getAdminCloud() {
        return CloudProviderFactory.getCloudProvider().getAdminCloud();
    }
    
    public static String getDefaultCloudName() {
        MMObjectNode cloudNode = getLocalCloudNode();
        if (cloudNode != null) {
            return cloudNode.getStringValue(FIELD_NAME);
        }
        return null;
    }

    public static Node getDefaultCloudNode(Cloud cloud) {
        Node defaultCloudNode = cloud.getNodeByAlias(CLOUD_DEFAULT);
        return defaultCloudNode;
    }

    public static MMObjectNode getLocalCloudNode() {
        return MMBase.getMMBase().getBuilder(CLOUD).getNode(CLOUD_DEFAULT);
    }
    
    /**
     * Get number of cloud from local system
     * 
     * @param name
     *            the name of the cloud ( in the cloud list)
     * @return cloud node number
     * @throws BridgeException
     *             if the cloud was not found
     */
    public static int getLocalCloudNumber(String name) throws BridgeException {
        MMObjectBuilder builder = MMBase.getMMBase().getBuilder(CLOUD);
        NodeSearchQuery query = new NodeSearchQuery(builder);
        StepField nameStepField = query.getField(builder.getField(FIELD_NAME));
        BasicFieldValueConstraint cName = new BasicFieldValueConstraint(nameStepField, name);
        cName.setOperator(FieldCompareConstraint.EQUAL);
        query.setConstraint(cName);

        try {
            List<MMObjectNode> nodes = builder.getNodes(query);
            if (nodes.isEmpty()) { throw new BridgeException("can not find cloud with name(" + name
                    + ") in local cloud"); }
            return nodes.get(0).getNumber();
        }
        catch (SearchQueryException e) {
            throw new BridgeException("can not find cloud with name(" + name
                    + ") in nameServerCloud", e);
        }
    }

   public static void updateLocalUser(Node userNode, String password) {
      Cloud localCloud = userNode.getCloud();
      String username = userNode.getStringValue("username");

      Node localCloudNode = getDefaultCloudNode(localCloud);

      String localUsername = localCloudNode.getStringValue(FIELD_USERNAME);
      if (username.equals(localUsername)) {
         localCloudNode.setStringValue(FIELD_PASSWORD, password);
         localCloudNode.commit();
      }
      
      String nameOfCloud = localCloudNode.getStringValue(FIELD_NAME);
      NodeList localClouds = localCloud.getNodeManager(CLOUD).createQuery().getList();
      
      for (Node cloudNode : localClouds) {
         if (localCloudNode.getNumber() != cloudNode.getNumber()) {
            Cloud remoteCloud = getCloudFromNode(cloudNode);
            Node remoteCLoudNode = getCloudNodeByName(remoteCloud, nameOfCloud);
            String remoteUsername = remoteCLoudNode.getStringValue(FIELD_USERNAME);
            if (username.equals(remoteUsername)) {
               remoteCLoudNode.setStringValue(FIELD_PASSWORD, password);
               remoteCLoudNode.commit();
            }
         }
      }
   }

   public static void updateRemoteUser(Node userNode, String password) {
      Cloud localCloud = userNode.getCloud();
      String username = userNode.getStringValue("username");
      
      Node localCloudNode = getDefaultCloudNode(localCloud);
      
      NodeList localClouds = localCloud.getNodeManager(CLOUD).createQuery().getList();
      for (Node cloudNode : localClouds) {
         if (localCloudNode.getNumber() != cloudNode.getNumber()) {
            String localUsername = cloudNode.getStringValue(FIELD_USERNAME);
            if (username.equals(localUsername)) {
               cloudNode.setStringValue(FIELD_PASSWORD, password);
               cloudNode.commit();
            }
             
            Cloud remoteCloud = getCloudFromNode(cloudNode);
            
            Node remoteCLoudNode = getDefaultCloudNode(remoteCloud);
            String remoteUsername = remoteCLoudNode.getStringValue(FIELD_USERNAME);
            if (username.equals(remoteUsername)) {
               remoteCLoudNode.setStringValue(FIELD_PASSWORD, password);
               remoteCLoudNode.commit();
            }
         }
      }
   }

}
