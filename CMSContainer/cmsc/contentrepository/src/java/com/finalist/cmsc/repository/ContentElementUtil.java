/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeManagerList;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.security.SecurityUtil;

public final class ContentElementUtil {

   private static final String SOURCE = "SOURCE";
   private static final String DESTINATION = "DESTINATION";

   private static final String ALLOWREL = "allowrel";
   private static final String TYPEDEF = "typedef";

   public static final String NUMBER_FIELD = "number";
   public static final String TITLE_FIELD = "title";
   public static final String DESCRIPTION_FIELD = "description";
   public static final String CREATIONDATE_FIELD = "creationdate";
   public static final String PUBLISHDATE_FIELD = "publishdate";
   public static final String EXPIREDATE_FIELD = "expiredate";
   public static final String USE_EXPIRY_FIELD = "use_expirydate";
   public static final String LASTMODIFIEDDATE_FIELD = "lastmodifieddate";
   public static final String CREATOR_FIELD = "creator";
   public static final String LASTMODIFIER_FIELD = "lastmodifier";
   public static final String NOTIFICATIONDATE_FIELD = "notificationdate";
   public static final String KEYWORD_FIELD = "keywords";
   public static final String ARCHIVEDATE_FIELD = "archivedate";

   public static final String CONTENTELEMENT = "contentelement";
   public static final String USER = SecurityUtil.USER;

   public static final String OWNERREL = "ownerrel";

   private static final String PROPERTY_HIDDEN_TYPES = "system.contenttypes.hide";

   private static final String SYSTEM_SIMPLEEDITOR_CONTENTTYPES = "system.simpleeditor.contenttypes";

   private ContentElementUtil() {
      // utility
   }

   public static NodeManager getNodeManager(Cloud cloud) {
      return cloud.getNodeManager(CONTENTELEMENT);
   }

   public static List<NodeManager> getContentTypes(Cloud cloud) {
      List<NodeManager> result = new ArrayList<NodeManager>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      while (v.hasNext()) {
         NodeManager nm = v.next();
         if (ContentElementUtil.isContentType(nm)) {
            result.add(nm);
         }
      }
      sortContentTypes(result);
      return result;
   }

   private static void sortContentTypes(List<NodeManager> result) {
      Collections.sort(result, new Comparator<NodeManager>() {
         public int compare(NodeManager o1, NodeManager o2) {
            return o1.getGUIName().compareTo(o2.getGUIName());
         }
      });
   }

   public static List<NodeManager> getAllowedContentTypes(Cloud cloud, String nodeNumber) {
      List<NodeManager> types = new ArrayList<NodeManager>();

      NodeList allowedTypes = cloud.getNode(nodeNumber).getRelatedNodes(TYPEDEF, ALLOWREL, DESTINATION);
      for (Iterator<Node> iterator = allowedTypes.iterator(); iterator.hasNext();) {
         Node node = iterator.next();
         if (node.isNodeManager()) {
            types.add(node.toNodeManager());
         }
      }
      sortContentTypes(types);
      return types;
   }

   public static boolean isContentElementField(Field field) {
      Cloud cloud = field.getNodeManager().getCloud();
      return cloud.getNodeManager(CONTENTELEMENT).hasField(field.getName());
   }

   /**
    * Is element from one of the content types
    * 
    * @param element
    *           node to check
    * @return is content type
    */
   public static boolean isContentElement(Node element) {
      if (element == null) return false;

      NodeManager nm = element.getNodeManager();
      return isContentType(nm);
   }

   /**
    * Is ModeManager of the content types
    * 
    * @param nm
    *           NodeManager to check
    * @return is content type
    */
   public static boolean isContentType(NodeManager nm) {
      if (CONTENTELEMENT.equals(nm.getName())) {
         // contentelement manager is not a content type
         return false;
      }
      try {
         NodeManager nmTemp = nm.getParent();
         while (!CONTENTELEMENT.equals(nmTemp.getName())) {
            nmTemp = nmTemp.getParent();
         }
         return true;
      } catch (NotFoundException nfe) {
         // Ran out of NodeManager parents
      }
      return false;
   }

   /**
    * Is type of content type
    * 
    * @param type
    *           to check
    * @return is content type
    */
   public static boolean isContentType(String type) {
      NodeManager nm = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNodeManager(type);
      return isContentType(nm);
   }

   public static void removeContentBlock(Node node) {
      List<Node> nodes = findContentBlockNodes(node);
      for (Node removeNode : nodes) {
         removeNode.delete(true);
      }
   }

   public static List<Node> findContentBlockNodes(Node node) {
      List<Node> nodes = new ArrayList<Node>();
      findContentBlockNodes(node, nodes, false, false);
      return nodes;
   }

   public static List<Node> findContentBlockNodesWithRelations(Node node) {
      List<Node> nodes = new ArrayList<Node>();
      findContentBlockNodes(node, nodes, true, false);
      return nodes;
   }

   public static void findContentBlockNodes(Node node, List<Node> nodes, boolean withRelation, boolean remove) {
      if (!remove) {
         if (!nodes.contains(node)) {
            nodes.add(node);
         }
      }

      NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
      while (childs.hasNext()) {
         Node childNode = childs.nextNode();
         if (isContentElement(childNode)) {
            if (!RepositoryUtil.hasContentChannel(childNode)) {
               findContentBlockNodes(childNode, nodes, withRelation, remove);
            }
         } else {
            if (!RepositoryUtil.isContentChannel(childNode)) {
               if (remove) {
                  if (childNode.countRelatedNodes(null, null, SOURCE) <= 1) {
                     findContentBlockNodes(childNode, nodes, withRelation, remove);
                  }
               } else {
                  if (!TypeUtil.isSystemType(childNode.getNodeManager().getName())) {
                     findContentBlockNodes(childNode, nodes, withRelation, remove);
                  }
               }
            }
         }
      }

      if (withRelation) {
         RelationIterator relations = node.getRelations().relationIterator();
         while (relations.hasNext()) {
            Relation rel = relations.next();
            if (!nodes.contains(rel)) {
               nodes.add(rel);
            }
         }
      }
      if (remove) {
         if (!nodes.contains(node)) {
            nodes.add(node);
         }
      }
   }

   public static Node createContentElement(Cloud cloud, String manager, String creationPath, boolean linkToChannel) {
      Node creationChannel = RepositoryUtil.getChannelFromPath(cloud, creationPath);
      if (creationChannel == null) {
         throw new IllegalArgumentException("Contentchannel '" + creationPath + "' does not exist");
      }
      String title = creationChannel.getStringValue(RepositoryUtil.TITLE_FIELD);
      return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
   }

   public static Node createContentElement(Cloud cloud, String manager, String creationPath, String title,
         boolean linkToChannel) {
      Node creationChannel = RepositoryUtil.getChannelFromPath(cloud, creationPath);
      if (creationChannel == null) {
         throw new IllegalArgumentException("Contentchannel '" + creationPath + "' does not exist");
      }
      return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
   }

   public static Node createContentElement(Cloud cloud, String manager, Node creationChannel, boolean linkToChannel) {
      String title = creationChannel.getStringValue(RepositoryUtil.TITLE_FIELD);
      return createContentElement(cloud, manager, creationChannel, title, linkToChannel);
   }

   public static Node createContentElement(Cloud cloud, String manager, Node creationChannel, String title,
         boolean linkToChannel) {
      NodeManager contentManager = cloud.getNodeManager(manager);
      if (contentManager == null) {
         throw new IllegalArgumentException("Manager '" + manager + "' does not exist");
      }
      Node content = contentManager.createNode();
      content.setStringValue(TITLE_FIELD, title);
      content.commit();
      addOwner(content);
      if (linkToChannel) {
         RepositoryUtil.addContentToChannel(content, creationChannel);
      } else {
         RepositoryUtil.addCreationChannel(content, creationChannel);
      }
      return content;
   }

   /**
    * Add owner
    * 
    * @param content
    *           - content
    */
   public static void addOwner(Node content) {
      Cloud cloud = content.getCloud();
      Node user = SecurityUtil.getUserNode(cloud);
      RelationManager author = cloud.getRelationManager(CONTENTELEMENT, USER, OWNERREL);
      Relation ownerrel = content.createRelation(user, author);
      ownerrel.commit();
   }

   /**
    * Check if a contentnode has an owner
    * 
    * @param content
    *           - Content Node
    * @return true if the node has a related workflowitem
    */
   public static boolean hasOwner(Node content) {
      int count = content.countRelatedNodes(content.getCloud().getNodeManager(USER), OWNERREL, DESTINATION);
      return count > 0;
   }

   /**
    * Get author of the content element
    * 
    * @param content
    *           - Content Node
    * @return Author node
    */
   public static Node getAuthor(Node content) {
      String creator = content.getStringValue(CREATOR_FIELD);
      return SecurityUtil.getUserNode(content.getCloud(), creator);
   }

   /**
    * Get owner of the content element
    * 
    * @param content
    *           - Content Node
    * @return Owner node
    */
   public static Node getOwner(Node content) {
      NodeList list = content.getRelatedNodes(USER, OWNERREL, DESTINATION);
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }

   public static void addNotExpiredConstraint(Node channel, NodeQuery query, long date) {
      NodeManager contentManager = channel.getCloud().getNodeManager(CONTENTELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, contentManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, contentManager, true);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, expirydate);
      SearchUtil.addConstraint(query, composite);
   }

   public static void addLifeCycleConstraint(NodeQuery query, long date) {
      NodeManager contentManager = query.getCloud().getNodeManager(CONTENTELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, contentManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, contentManager, true);
      Constraint publishdate = getPublishConstraint(query, date, contentManager, false);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_AND, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }

   public static void addLifeCycleInverseConstraint(NodeQuery query, long date) {
      NodeManager contentManager = query.getCloud().getNodeManager(CONTENTELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, contentManager, Boolean.TRUE);
      Constraint expirydate = getExpireConstraint(query, date, contentManager, false);
      Constraint publishdate = getPublishConstraint(query, date, contentManager, true);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_OR, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_AND, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }

   public static Constraint getUseExpireConstraint(NodeQuery query, NodeManager contentManager, Boolean value) {
      Field useExpireField = contentManager.getField(USE_EXPIRY_FIELD);
      Constraint useExpire = query.createConstraint(query.getStepField(useExpireField), FieldCompareConstraint.EQUAL,
            value);
      return useExpire;
   }

   public static Constraint getExpireConstraint(NodeQuery query, long date, NodeManager contentManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field expireField = contentManager.getField(EXPIREDATE_FIELD);
      Object expireDateObj = (expireField.getType() == Field.TYPE_DATETIME) ? new Date(date) : Long.valueOf(date);
      Constraint expirydate = query.createConstraint(query.getStepField(expireField), operator, expireDateObj);
      return expirydate;
   }

   public static Constraint getPublishConstraint(NodeQuery query, long date, NodeManager contentManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field publishField = contentManager.getField(PUBLISHDATE_FIELD);
      Object publishDateObj = (publishField.getType() == Field.TYPE_DATETIME) ? new Date(date) : Long.valueOf(date);
      Constraint publishdate = query.createConstraint(query.getStepField(publishField), operator, publishDateObj);
      return publishdate;
   }

   public static void addArchiveConstraint(Node channel, NodeQuery query, Long date, String archive) {
      if (StringUtils.isEmpty(archive) || "all".equalsIgnoreCase(archive)) {
         return;
      }
      NodeManager contentManager = channel.getCloud().getNodeManager(CONTENTELEMENT);

      Field archiveDateField = contentManager.getField(ARCHIVEDATE_FIELD);
      Object archiveDateObj = (archiveDateField.getType() == Field.TYPE_DATETIME) ? new Date(date) : Long.valueOf(date);

      Constraint archivedate = null;
      if ("old".equalsIgnoreCase(archive)) {
         archivedate = query.createConstraint(query.getStepField(archiveDateField), FieldCompareConstraint.LESS_EQUAL,
               archiveDateObj);
      } else {
         // "new".equalsIgnoreCase(archive)
         archivedate = query.createConstraint(query.getStepField(archiveDateField),
               FieldCompareConstraint.GREATER_EQUAL, archiveDateObj);
      }
      SearchUtil.addConstraint(query, archivedate);
   }

   /**
    * judge if the content's archive date is in the archive time scope
    * 
    * @param content
    *           the node to be match
    * @param archive
    *           spcifiy how to calculate the archive time scrop.accept "all" "old" or other String as parameter,
    * @return <li>true if archive is "all" or content is empty. <li>true if archive is "old" and content's archive time
    *         is before current time <li>true if archive is others and content's archive time is after or equels to
    *         current time
    */
   public static boolean matchArchive(Node content, String archive) {
      if (StringUtils.isEmpty(archive) || "all".equalsIgnoreCase(archive)) {
         return true;
      }
      // Precision of now is based on minutes.
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      long date = cal.getTimeInMillis();

      Date archiveDate = content.getDateValue(ARCHIVEDATE_FIELD);
      if ("old".equalsIgnoreCase(archive)) {
         return archiveDate.getTime() < date;
      }
      return archiveDate.getTime() >= date;
   }

   /**
    * Helper method to get all hidden types
    * 
    * @return List of hidden types
    */
   public static List<String> getHiddenTypes() {
      return getProperty(PROPERTY_HIDDEN_TYPES);
   }

   /**
    * Helper method to get all valid simple editor's types
    * 
    * @param cloud
    * 
    * @return List of the types
    */
   public static List<String> getSimpleEditorTypes(Cloud cloud) {
      List<String> simpleEditorTypes = new ArrayList<String>();
      if (PropertiesUtil.getProperty(SYSTEM_SIMPLEEDITOR_CONTENTTYPES) != null) {
         List<String> rawTypes = getProperty(SYSTEM_SIMPLEEDITOR_CONTENTTYPES);
         List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
         List<String> allTypes = new ArrayList<String>();
         for (NodeManager nodeManager : types) {
            allTypes.add(nodeManager.getName());
         }
         List<String> hiddenTypes = getHiddenTypes();
         for (String sType : rawTypes) {
            if (!hiddenTypes.contains(sType) && allTypes.contains(sType)) {
               simpleEditorTypes.add(sType);
            }
         }
      }
      return simpleEditorTypes;
   }

   /**
    * Helper method to get properties from System Property
    * 
    * @param propertyKey
    *           the key of property,
    * @return List of the value, if the value is separated by comma,return the list
    */
   public static List<String> getProperty(String propertyKey) {
      String property = PropertiesUtil.getProperty(propertyKey);
      if (property == null) {
         return new ArrayList<String>();
      }

      ArrayList<String> list = new ArrayList<String>();
      String[] values = property.split(",");
      for (String value : values) {
         list.add(value);
      }
      return list;
   }

   /**
    * Return a valid type list, not including those belong to hidden types
    * 
    * @param cloud
    * @param request
    * @param types
    */
   public static List<LabelValueBean> getValidTypesList(Cloud cloud, List<NodeManager> types) {
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();
      List<String> hiddenTypes = getHiddenTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      return typesList;
   }

   /**
    * 
    * @param cloud
    * @param types
    * @return
    */
   public static TreeSet<Integer> getValidTypes(Cloud cloud, List<NodeManager> types) {
      List<String> hiddenTypes = getHiddenTypes();
      TreeSet<Integer> validTypes = new TreeSet<Integer>();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            validTypes.add(manager.getNumber());
         }
      }
      return validTypes;
   }
}
