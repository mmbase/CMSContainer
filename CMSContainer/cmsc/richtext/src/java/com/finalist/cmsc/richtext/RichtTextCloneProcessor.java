package com.finalist.cmsc.richtext;

import java.util.*;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.cmsc.mmbase.RelationUtil;


public class RichtTextCloneProcessor {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(RichtTextCloneProcessor.class
         .getName());
   
   public void resolve(Node sourceNode,Node destinationNode,Document doc,Map<Integer, Integer> copiedNodes) {
      resolveLinks(doc,sourceNode,destinationNode,copiedNodes);
      resolveImages(doc,sourceNode,destinationNode,copiedNodes);
   }
   
   /**
    *    To resolve the links in Richtext fields
    * @param doc
    * @param sourceNode
    * @param copiedNodes
    * @param channels
    */
   public static void resolveLinks(Document doc, Node sourceNode,Node destinationNode,Map<Integer, Integer> copiedNodes) {
      if (doc == null) {
         return;
      }
      // collect <A> tags
      org.w3c.dom.NodeList nl = doc.getElementsByTagName("a");
      List<org.w3c.dom.Node> links = new ArrayList<org.w3c.dom.Node>();
      
      int len = nl.getLength();
      for(int i = 0 ; i < len ; i++) {
         links.add(nl.item(i));
      }
      for (int i = 0; i < len; i++) {
         Element link = (Element) links.get(i);
         if (link.hasAttribute(RichText.DESTINATION_ATTR)
               && "undefined".equalsIgnoreCase(link.getAttribute(RichText.DESTINATION_ATTR))) {
            link.removeAttribute(RichText.DESTINATION_ATTR);
         }
         if (link.hasAttribute(RichText.RELATIONID_ATTR)
               && "undefined".equalsIgnoreCase(link.getAttribute(RichText.RELATIONID_ATTR))) {
            link.removeAttribute(RichText.RELATIONID_ATTR);
         }
         // handle relations to other objects
         if (link.hasAttribute(RichText.DESTINATION_ATTR) && link.hasAttribute(RichText.RELATIONID_ATTR)) {
            // get id of the link
            //String id = link.getAttribute("relationID");
            int source = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));
            org.w3c.dom.Node parentNode = link.getParentNode();
            if (source > 0) {
               Node node = sourceNode.getCloud().getNode(source);
               Object number = copiedNodes.get(node.getNumber());
               if (number == null) {
                  Element newNode = doc.createElement("span");
                  newNode.appendChild(link.getFirstChild());
                  parentNode.replaceChild(newNode,link);
               }
               else {
                  Integer destination = copiedNodes.get(source);
                  if (destination  != null && destination > 0 && sourceNode.getCloud().hasNode(destination)) {
                     Relation rel = RelationUtil.getRelation(sourceNode.getCloud().getNodeManager( RichText.INLINEREL_NM), destinationNode.getNumber(), destination);
                     if(rel == null) {
                        rel = RelationUtil.createRelation(destinationNode, sourceNode.getCloud().getNode(destination), RichText.INLINEREL_NM);
                     }
                     link.setAttribute(RichText.DESTINATION_ATTR, String.valueOf(destination));
                     link.setAttribute(RichText.RELATIONID_ATTR, String.valueOf(rel.getNumber()));
                  }
               }
            }
         }
      }
   }


   /**
    *   To resolve the images used in the richtext fileds
    * @param doc
    * @param sourceNode
    * @param copiedNodes
    * @param channels
    */
   public static  void resolveImages(Document doc,Node sourceNode,Node destinationNode,Map<Integer, Integer> copiedNodes) {
      if (doc == null) {
         return;
      }
      org.w3c.dom.NodeList nl = doc.getElementsByTagName("img");
      log.debug("number of images: " + nl.getLength());
      List<org.w3c.dom.Node> links = new ArrayList<org.w3c.dom.Node>();
      int len = nl.getLength();
      for(int i = 0 ; i < len ; i++) {
         links.add(nl.item(i));
      }
      for (int i = 0; i < len; i++) {
         Element image = (Element) links.get(i);

         if (image.hasAttribute(RichText.DESTINATION_ATTR)
               && "undefined".equalsIgnoreCase(image.getAttribute(RichText.DESTINATION_ATTR))) {
            image.removeAttribute(RichText.DESTINATION_ATTR);
         }
         if (image.hasAttribute(RichText.RELATIONID_ATTR)
               && "undefined".equalsIgnoreCase(image.getAttribute(RichText.RELATIONID_ATTR))) {
            image.removeAttribute(RichText.RELATIONID_ATTR);
         }

         if (image.hasAttribute(RichText.DESTINATION_ATTR) && image.hasAttribute(RichText.RELATIONID_ATTR)) {
            // get id of the image
            int source = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));
           
            org.w3c.dom.Node parentNode = image.getParentNode();
           // parentNode.removeChild(image);
           // MMObjectNode imagerel = getImageInlineRel(id);
            if (source > 0 ) {
               Node node = sourceNode.getCloud().getNode(source);
               Object number = copiedNodes.get(node.getNumber());
               if (number == null) {
                  parentNode.removeChild(image);
               }
               else {
                  Integer destination = copiedNodes.get(source);
                  if (destination  != null && destination > 0 && sourceNode.getCloud().hasNode(destination)) {
                     Relation rel = RelationUtil.getRelation(sourceNode.getCloud().getNodeManager( RichText.IMAGEINLINEREL_NM), destinationNode.getNumber(), destination);
                     if(rel == null) {
                        rel = RelationUtil.createRelation(destinationNode, sourceNode.getCloud().getNode(destination), RichText.IMAGEINLINEREL_NM);
                     }
                   
                     image.setAttribute(RichText.DESTINATION_ATTR, String.valueOf(destination));
                     image.setAttribute(RichText.RELATIONID_ATTR, String.valueOf(rel.getNumber()));
                  }
               }
            }
         }

      }
   }

}
