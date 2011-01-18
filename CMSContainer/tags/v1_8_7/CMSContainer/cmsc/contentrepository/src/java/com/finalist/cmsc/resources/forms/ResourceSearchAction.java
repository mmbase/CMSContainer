/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.resources.forms;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;

import com.finalist.cmsc.repository.RepositoryUtil;

public abstract class ResourceSearchAction extends AbstractSearchAction {

   protected void addStepsToQuery(Cloud cloud, SearchForm searchForm, NodeManager nodeManager, NodeQuery query) {
      // First add the proper step to the query.
      // CMSC-1260 Content search also finds elements in Recycle bin
      NodeManager channelNodeManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      Step channelStep = query.addStep(channelNodeManager);
      Step theStep = query.addRelationStep(nodeManager, RepositoryUtil.CREATIONREL, "SOURCE").getNext();
      query.setNodeStep(theStep);

     if (StringUtils.isNotEmpty(searchForm.getContentChannel())) {
         Integer ContentChannelNumber = Integer.parseInt(searchForm.getContentChannel());
         StepField stepField = query.createStepField(channelStep, channelNodeManager.getField("number"));
         FieldValueConstraint channelConstraint = query.createConstraint(stepField, FieldCompareConstraint.EQUAL,
               ContentChannelNumber);
         SearchUtil.addConstraint(query, channelConstraint);
      } else {
         Integer trashNumber = Integer.parseInt(RepositoryUtil.getTrash(cloud));
         StepField stepField = query.createStepField(channelStep, channelNodeManager.getField("number"));
         FieldValueConstraint channelConstraint = query.createConstraint(stepField, FieldCompareConstraint.NOT_EQUAL,
               trashNumber);
         SearchUtil.addConstraint(query, channelConstraint);
      }
      query.setNodeStep(theStep);
   }
   
}
