/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
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
import org.mmbase.util.transformers.ByteToCharTransformer;
import org.mmbase.util.transformers.ChecksumFactory;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningException;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.util.http.BulkUploadUtil;

public final class AssetElementUtil {

   private static final String DESTINATION = "DESTINATION";

   public static final String NUMBER_FIELD = "number";
   public static final String TITLE_FIELD = "title";
   public static final String NAME_FIELD = "name";
   public static final String CREATIONDATE_FIELD = "creationdate";
   public static final String PUBLISHDATE_FIELD = "publishdate";
   public static final String EXPIREDATE_FIELD = "expiredate";
   public static final String USE_EXPIRY_FIELD = "use_expirydate";
   public static final String LASTMODIFIEDDATE_FIELD = "lastmodifieddate";
   public static final String CREATOR_FIELD = "creator";
   public static final String LASTMODIFIER_FIELD = "lastmodifier";
   public static final String ARCHIVEDATE_FIELD = "archivedate";

   public static final String ASSETELEMENT = "assetelement";
   public static final String USER = SecurityUtil.USER;

   public static final String OWNERREL = "ownerrel";

   private static final String PROPERTY_HIDDEN_ASSET_TYPES = "system.assettypes.hide";

   public static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";
   protected static Set<String> supportedImages;
   protected static final Log log = LogFactory.getLog(AssetElementUtil.class);

   private AssetElementUtil() {
      // utility
   }

   public static NodeManager getNodeManager(Cloud cloud) {
      return cloud.getNodeManager(ASSETELEMENT);
   }

   public static List<NodeManager> getAssetTypes(Cloud cloud) {
      List<NodeManager> result = new ArrayList<NodeManager>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      while (v.hasNext()) {
         NodeManager nm = v.next();
         if (AssetElementUtil.isAssetType(nm)) {
            result.add(nm);
         }
      }
      Collections.sort(result, new Comparator<NodeManager>() {
         public int compare(NodeManager o1, NodeManager o2) {
            return o1.getGUIName().compareTo(o2.getGUIName());
         }
      });

      return result;
   }

   /**
    * Is element from one of the asset types
    * 
    * @param element
    *           node to check
    * @return is asset type
    */
   public static boolean isAssetElement(Node element) {
      NodeManager nm = element.getNodeManager();
      return isAssetType(nm);
   }

   /**
    * Is ModeManager of the asset types
    * 
    * @param nm
    *           NodeManager to check
    * @return is content type
    */
   public static boolean isAssetType(NodeManager nm) {
      if (ASSETELEMENT.equals(nm.getName())) {
         // assetelement manager is not a assettent type
         return false;
      }
      try {
         NodeManager nmTemp = nm.getParent();
         while (!ASSETELEMENT.equals(nmTemp.getName())) {
            nmTemp = nmTemp.getParent();
         }
         return true;
      } catch (NotFoundException nfe) {
         // Ran out of NodeManager parents
      }
      return false;
   }

   /**
    * Is type of asset type
    * 
    * @param type
    *           to check
    * @return is asset type
    */
   public static boolean isAssetType(String type) {
      NodeManager nm = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNodeManager(type);
      return isAssetType(nm);
   }

   /**
    * Add owner
    * 
    * @param asset
    *           - asset
    */
   public static void addOwner(Node asset) {
      Cloud cloud = asset.getCloud();
      Node user = SecurityUtil.getUserNode(cloud);
      RelationManager author = cloud.getRelationManager(ASSETELEMENT, USER, OWNERREL);
      Relation ownerrel = asset.createRelation(user, author);
      ownerrel.commit();
   }

   /**
    * Check if a assetnode has an owner
    * 
    * @param asset
    *           - Asset Node
    * @return true if the node has a related workflowitem
    */
   public static boolean hasOwner(Node asset) {
      int count = asset.countRelatedNodes(asset.getCloud().getNodeManager(USER), OWNERREL, DESTINATION);
      return count > 0;
   }

   public static void addLifeCycleConstraint(NodeQuery query, long date) {
      NodeManager assetManager = query.getCloud().getNodeManager(ASSETELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, assetManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, assetManager, true);
      Constraint publishdate = getPublishConstraint(query, date, assetManager, false);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_AND, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }

   public static void addLifeCycleInverseConstraint(NodeQuery query, long date) {
      NodeManager assetManager = query.getCloud().getNodeManager(ASSETELEMENT);

      Constraint useExpire = getUseExpireConstraint(query, assetManager, Boolean.TRUE);
      Constraint expirydate = getExpireConstraint(query, date, assetManager, false);
      Constraint publishdate = getPublishConstraint(query, date, assetManager, true);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_OR, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_AND, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }

   public static Constraint getUseExpireConstraint(NodeQuery query, NodeManager assetManager, Boolean value) {
      Field useExpireField = assetManager.getField(USE_EXPIRY_FIELD);
      Constraint useExpire = query.createConstraint(query.getStepField(useExpireField), FieldCompareConstraint.EQUAL,
            value);
      return useExpire;
   }

   public static Constraint getExpireConstraint(NodeQuery query, long date, NodeManager assetManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field expireField = assetManager.getField(EXPIREDATE_FIELD);
      Object expireDateObj = (expireField.getType() == Field.TYPE_DATETIME) ? new Date(date) : Long.valueOf(date);
      Constraint expirydate = query.createConstraint(query.getStepField(expireField), operator, expireDateObj);
      return expirydate;
   }

   public static Constraint getPublishConstraint(NodeQuery query, long date, NodeManager assetManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field publishField = assetManager.getField(PUBLISHDATE_FIELD);
      Object publishDateObj = (publishField.getType() == Field.TYPE_DATETIME) ? new Date(date) : Long.valueOf(date);
      Constraint publishdate = query.createConstraint(query.getStepField(publishField), operator, publishDateObj);
      return publishdate;
   }

   public static void addArchiveConstraint(Node channel, NodeQuery query, Long date, String archive) {
      if (StringUtils.isEmpty(archive) || "all".equalsIgnoreCase(archive)) {
         return;
      }
      NodeManager contentManager = channel.getCloud().getNodeManager(ASSETELEMENT);

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
    * Helper method to get all hidden asset types
    * 
    * @return List of hidden types
    */
   public static List<String> getHiddenAssetTypes() {
      String property = PropertiesUtil.getProperty(PROPERTY_HIDDEN_ASSET_TYPES);
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

   public static List<Node> findAssetRelatedNodes(Node node) {

      List<Node> nodes = new ArrayList<Node>();
      RelationIterator childs = node.getRelations(null, null, DESTINATION).relationIterator();
      while (childs.hasNext()) {
         Relation childNode = childs.nextRelation();
         if (isAssetElement(childNode.getDestination())) {
            nodes.add(childNode.getDestination());
         }
      }
      return nodes;
   }

   protected static void initSupportedImages() {
      supportedImages = new HashSet<String>();
      Properties properties = new Properties();
      String images = ".bmp,.jpg,.jpeg,.gif,.png,.svg,.tiff,.tif";
      try {
         properties.load(BulkUploadUtil.class.getResourceAsStream(CONFIGURATION_RESOURCE_NAME));
         images = (String) properties.get("supportedImages");
      } catch (IOException ex) {
         log.warn("Could not load properties from " + CONFIGURATION_RESOURCE_NAME + ", using defaults", ex);
      }
      for (String image : images.split(",")) {
         supportedImages.add(image.trim());
      }
   }

   /**
    * Determine whether the file is an image file
    * 
    * @param fileName
    * @return
    */
   protected static boolean isImage(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return false;
      }
      if (supportedImages == null) {
         initSupportedImages();
      }
      return supportedImages.contains(getFilenameExtension(fileName).toLowerCase());
   }

   /**
    * 
    * @param fileName
    * @return
    */
   protected static String getFilenameExtension(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return null;
      }
      int index = fileName.lastIndexOf('.');
      if (index < 0) {
         return null;
      }
      return fileName.substring(index);
   }

   /**
    * Determine whether the file is already in the system
    * 
    * @param file
    * @param manager
    * @return
    */
   public static boolean isNewFile(FormFile file, NodeManager manager) {
      ChecksumFactory checksumFactory = new ChecksumFactory();
      ByteToCharTransformer transformer = (ByteToCharTransformer) checksumFactory.createTransformer(checksumFactory
            .createParameters());
      String checkSum = null;
      try {
         checkSum = transformer.transform(file.getFileData());
      } catch (FileNotFoundException e) {
         log.warn("Uploading file is not found!");
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      NodeQuery query = manager.createQuery();
      SearchUtil.addEqualConstraint(query, manager.getField("checksum"), checkSum);
      NodeList assets = query.getList();
      return (assets.size() == 0);
   }

   /**
    * 
    * @param nodes
    * @param cloud
    * @throws NotFoundException
    * @throws VersioningException
    */
   public static void addRelationsForNodes(List<Integer> nodes, Cloud cloud) throws NotFoundException,
         VersioningException {
      if (nodes != null && nodes.size() > 0) {
         for (Integer node : nodes) {
            Node assetNode = cloud.getNode(node);
            if (!Workflow.hasWorkflow(assetNode)) {
               Workflow.create(assetNode, "");
            } else {
               Workflow.addUserToWorkflow(assetNode);
            }
            Versioning.addVersion(cloud.getNode(node));
         }
      }
   }

   /**
    * determine the type of an asset file
    * 
    * @param fileName
    * @return
    */
   public static String judgeAssetType(String fileName) {
      String assetType = "";
      if (isImage(fileName)) {
         assetType = "images";
      } else {
         assetType = "attachments";
      }
      return assetType;
   }
}
