package com.finalist.cmsc.repository;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Relation;
import org.mmbase.core.event.Event;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.core.event.RelationEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.RelationUtil;

public class ContentElementEventListener implements RelationEventListener{
   
   protected final static String TYPE_CONTENT_ELEMENT = "contentelement";

   private static final Logger log = Logging.getLoggerInstance(ContentElementEventListener.class.getName());

   
   public ContentElementEventListener() {
      MMBase.getMMBase().addNodeRelatedEventsListener(TYPE_CONTENT_ELEMENT, this);
      log.info("registered listener for: " + TYPE_CONTENT_ELEMENT);
   }
   public void notify(RelationEvent event) {
      if("article".equalsIgnoreCase(event.getRelationSourceType()) && 
            "article".equalsIgnoreCase(event.getRelationDestinationType()) && 
            "posrel".equalsIgnoreCase(event.getNodeEvent().getBuilderName())) {
         
         int sourceNumber = event.getRelationSourceNumber();
         int detinationNumber = event.getRelationDestinationNumber();
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Relation  relation = RelationUtil.getRelation(cloud.getNodeManager("posrel"), detinationNumber, sourceNumber);
         
         if(event.getType() ==  Event.TYPE_NEW && relation == null) {
            RelationUtil.createRelation(cloud.getNode(detinationNumber), cloud.getNode(sourceNumber), "posrel");
         }
         else if (relation != null && event.getType() == Event.TYPE_DELETE) {
            relation.delete();
         }
      }
   }

}
