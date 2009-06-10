package com.finalist.cmsc.repository.forms;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.PagerAction;

public class AbstractAssetSearch extends PagerAction {

   public static final String GETURL = "geturl";
   public static final String PERSONAL = "personal";
   public static final String MODE = "mode";
   public static final String STRICT = "strict";
   public static final String AUTHOR = "author";
   public static final String OBJECTID = "objectid";
   public static final String PARENTCHANNEL = "parentchannel";
   public static final String ASSETTYPES = "assettypes";
   public static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

   public AbstractAssetSearch() {
      super();
   }

   protected void massDeleteAsset(String deleteAsset, Cloud cloud) {
      if (StringUtils.isNotBlank(deleteAsset)) {
         String[] deleteAssets = deleteAsset.split(",");
         for (String asset : deleteAssets) {
            deleteAsset(asset, cloud);
         }
      }
   }

   protected void deleteAsset(String deleteAssetRequest, Cloud cloud) {
      StringTokenizer commandAndNumber = new StringTokenizer(deleteAssetRequest, ":");
      String command = commandAndNumber.nextToken();
      String number = commandAndNumber.nextToken();
   
      if ("moveToRecyclebin".equals(command)) {
         moveAssetToRecyclebin(number, cloud);
      }
   
      if ("permanentDelete".equals(command)) {
         deleteAssetPermanent(number, cloud);
      }
   
   }

   protected void deleteAssetPermanent(String objectnumber, Cloud cloud) {
      Node objectNode = cloud.getNode(objectnumber);
      if (Workflow.hasWorkflow(objectNode)) {
         // at this time complete is the same as remove
         Workflow.complete(objectNode);
      }
      objectNode.delete(true);
   
   }

   protected void moveAssetToRecyclebin(String number, Cloud cloud) {
      Node objectNode = cloud.getNode(number);
   
      // NodeList channels = RepositoryUtil.getDeletionChannels(objectNode);
      Node channelNode = RepositoryUtil.getCreationChannel(objectNode);
      if (channelNode != null) {
         RepositoryUtil.addAssetDeletionRelation(objectNode, channelNode);
         RepositoryUtil.removeCreationRelForAsset(objectNode);
      }
   
      RepositoryUtil.addAssetToChannel(objectNode, RepositoryUtil.getTrash(cloud));
   
      // unpublish and remove from workflow
      Publish.remove(objectNode);
      Workflow.remove(objectNode);
      Publish.unpublish(objectNode);
   }

}