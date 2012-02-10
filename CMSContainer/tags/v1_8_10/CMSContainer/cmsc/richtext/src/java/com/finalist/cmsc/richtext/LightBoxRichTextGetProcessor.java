package com.finalist.cmsc.richtext;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.richtext.RichText;  
import com.finalist.cmsc.richtext.RichTextGetProcessor;

public class LightBoxRichTextGetProcessor extends RichTextGetProcessor {
   
   private static Logger log = Logging.getLoggerInstance(LightBoxRichTextGetProcessor.class.getName());

   @Override
   public Document resolve(Node node, Document doc, boolean dynamicDescriptions) {
       Cloud cloud = node.getCloud();           

       Map<String,Relation> inlineImages = new HashMap<String,Relation>();
       RelationList images = node.getRelations(RichText.IMAGEINLINEREL_NM, cloud.getNodeManager("images"), "DESTINATION");
       for (Iterator<Relation> iter = images.iterator(); iter.hasNext();) {
           Relation inlineRel =  iter.next();
           inlineImages.put(inlineRel.getStringValue(RichText.REFERID_FIELD), inlineRel);
       }

       // transform all images
       NodeList imglist = doc.getElementsByTagName(RichText.IMG_TAGNAME);
       log.debug("" + imglist.getLength() + " images found in richtext.");
       if (imglist.getLength() > 0) {
           resolveLightBoxImages(cloud, imglist, inlineImages, doc);
       }

      doc = super.resolve(node, doc, dynamicDescriptions);       
      return doc;
   }

   private void resolveLightBoxImages(Cloud cloud, NodeList nl, Map<String, Relation> inlineImages, Document doc) {
       for (int j = nl.getLength() - 1; j >= 0; --j) {
           Element image = (Element) nl.item(j);
           if (image.hasAttribute(RichText.LIGHTBOX_ATTR)) {
               if (image.hasAttribute(RichText.RELATIONID_ATTR)) {
                   String imgidrel = image.getAttribute(RichText.RELATIONID_ATTR);
                   log.debug("Creating image by relation " + imgidrel);

                   if (!inlineImages.containsKey(imgidrel)) {
                       if (cloud.getUser().getRank() == Rank.ANONYMOUS) {
                           org.w3c.dom.Node parentNode = image.getParentNode();
                           parentNode.removeChild(image);
                       }
                       continue;
                   }
                   Relation inlineRel = inlineImages.get(String.valueOf(imgidrel));
                   String originalImageNodeId = inlineRel.getStringValue("dnumber");

                   Element link = createLightboxLink(cloud, originalImageNodeId, image, doc);

                  /*
                    * Remove the image and an old (if it is there) href around the lightbox
                    */
                  org.w3c.dom.Node parentNode = image.getParentNode();             
    
                  org.w3c.dom.Node replaceNode = image;
                  if (parentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && parentNode.getNodeName().equals(RichText.LINK_TAGNAME)) {
                     replaceNode = parentNode;
                     parentNode = parentNode.getParentNode();
                  }
    
                  parentNode.replaceChild(link, replaceNode);                        
                  link.appendChild(image);
                  
                  if (isAnonymousVisitor(cloud)) {
                     image.removeAttribute(RichText.LIGHTBOX_ATTR);
                     if (image.hasAttribute(RichText.LIGHTBOXGROUP_ATTR)) {
                        image.removeAttribute(RichText.LIGHTBOXGROUP_ATTR);
                     }
                  }
               }
           }
       }
   }

   /**
    * Need to construct a link around the current image: <a href="originalimage"
    * title="description" rel="lightbox"> <img src="currentimage"/></a>.
    * 
    * @param originalImageNodeId 
    * @param cloud 
    * @param image 
    * @param originalImageNode 
    */ 
   protected Element createLightboxLink(Cloud cloud, String originalImageNodeId, Element image, Document doc) {
      Node originalImageNode = cloud.getNode(originalImageNodeId);

      // construct a link element
      Element link = doc.createElement(RichText.LINK_TAGNAME);               

      link.setAttribute(RichText.TITLE_ATTR, image.getAttribute(RichText.TITLE_ATTR));
      String servletPath = ResourcesUtil.getServletPath(originalImageNode, originalImageNodeId);
      
      // Image and flash file will both be parsed the same way.
      link.setAttribute(RichText.HREF_ATTR, servletPath);
      log.debug("The href attribute is " + link.getAttribute(RichText.HREF_ATTR));
      if (!image.hasAttribute(RichText.LIGHTBOXGROUP_ATTR)) {
         // simple lightbox
         link.setAttribute(RichText.LIGHTBOX_ATTR, RichText.LIGHTBOX_VALUE); 
      } else {
         // group lightbox
         link.setAttribute(RichText.LIGHTBOX_ATTR, RichText.LIGHTBOX_VALUE + "[" + image.getAttribute(RichText.LIGHTBOXGROUP_ATTR) + "]");                  
      }
      return link;
   }

}
