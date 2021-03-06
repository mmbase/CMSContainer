package com.finalist.cmsc.module.glossary;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Node;
import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.ServerUtil;

public class GlossaryEventListener implements NodeEventListener {

   private Glossary glossary;
   
   public GlossaryEventListener(Glossary glossary) {
      this.glossary = glossary;
   }
   
   public void notify(NodeEvent event) {
      switch(event.getType()) {
      case Event.TYPE_DELETE:
         String oldTerm = (String)event.getOldValue("term");
         if(oldTerm != null) {
            glossary.removeTerm(oldTerm);
            Node node = CloudProviderFactory.getCloudProvider().getCloud().getNode(event.getNodeNumber());
            Publish.unpublish(node);
         }
         break;
      case Event.TYPE_NEW:
         String newTerm = (String)event.getNewValue("term");
         if(newTerm != null) {
            glossary.addTerm(newTerm, (String)event.getNewValue("definition"));
            if(ServerUtil.isStaging()) {
               Node node = CloudProviderFactory.getCloudProvider().getCloud().getNode(event.getNodeNumber());
               Publish.publish(node);
            }
         }
         break;
      case Event.TYPE_CHANGE:
         String oldChangeTerm = (String)event.getOldValue("term");
         String newChangeTerm = (String)event.getNewValue("term");

         boolean changed = false;
         if(oldChangeTerm == null || newChangeTerm == null ) {
            Node node = CloudProviderFactory.getCloudProvider().getCloud().getNode(event.getNodeNumber());
            newChangeTerm = node.getStringValue("term"); 
            String newDefinition = (String)event.getNewValue("definition");
            if(newDefinition != null) {
               glossary.addTerm(newChangeTerm, newDefinition);
               changed = true;
            }
         }
         else {
            glossary.removeTerm(oldChangeTerm);
            String definition = (String)event.getNewValue("definition");
            if(definition == null) {
               Node node = CloudProviderFactory.getCloudProvider().getCloud().getNode(event.getNodeNumber());
               definition = node.getStringValue("definition"); 
            }
            glossary.addTerm(newChangeTerm, definition);
            changed = true;
         }
         
         if(changed) {
            if(ServerUtil.isStaging()) {
               Node node = CloudProviderFactory.getCloudProvider().getCloud().getNode(event.getNodeNumber());
               Publish.publish(node);
            }
         }
         break;
      
      }
   }

}
