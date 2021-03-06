/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.richtext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;
import org.mmbase.datatypes.processors.ParameterizedProcessorFactory;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.security.Rank;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.finalist.cmsc.mmbase.ResourcesUtil;

@SuppressWarnings("serial")
public class RichTextGetProcessor implements ParameterizedProcessorFactory {

   /** MMbase logging system */
   static final Logger log = Logging.getLoggerInstance(RichTextGetProcessor.class.getName());
   protected static final Parameter[] PARAMS = new Parameter[] { new Parameter("dynamicDescriptions", String.class,
         "false") };


   public Parameters createParameters() {
      return new Parameters(PARAMS);
   }


   public Processor createProcessor(Parameters parameters) {
      final boolean dynamicDescriptions = Boolean.parseBoolean(parameters.get("dynamicDescriptions").toString());
      return new Processor() {
         public Object process(Node node, Field field, Object value) {
            String out = "";
            String in = (String) value;
            if (StringUtils.isNotEmpty(in) && RichText.hasRichtextItems(in)) {
               try {
                  Document doc = RichText.getRichTextDocument(in);
                  if (doc == null) {
                     out = in;
                  }
                  else {
                     resolve(node, doc, dynamicDescriptions);
                     out = RichText.getRichTextString(doc);
                  }
               }
               catch (Exception e) {
                  log.error("resolve failed " + node.getNumber() + " " + field.getName(), e);
                  throw new IllegalStateException(e);
               }
            }
            else {
               out = in;
            }
            return out;
         }
      };
   }


   /**
    * Transform given richtext field data links to frontend links using given
    * navigation bean.
    *
    * @param node
    *           MMbase node
    * @param doc
    *           richtext field data
    * @param dynamicDescriptions
    *           dynamic Descriptions
    * @return transformed field data DOM object
    */
   public Document resolve(Node node, Document doc, boolean dynamicDescriptions) {
      Cloud cloud = node.getCloud();

      Map<String, String> inlineLinks = new HashMap<String, String>();
      RelationList links = node.getRelations(RichText.INLINEREL_NM, null, "DESTINATION");
      for (Iterator<Relation> iter = links.iterator(); iter.hasNext();) {
         Relation inlineRel = iter.next();
         inlineLinks.put(inlineRel.getStringValue(RichText.REFERID_FIELD), inlineRel.getStringValue("dnumber"));
      }

      // transform links
      NodeList linklist = doc.getElementsByTagName(RichText.LINK_TAGNAME);
      if (linklist.getLength() > 0) {
         resolveLinks(cloud, linklist, inlineLinks, dynamicDescriptions);
      }

      Map<String, Relation> inlineImages = new HashMap<String, Relation>();
      RelationList images = node
            .getRelations(RichText.IMAGEINLINEREL_NM, cloud.getNodeManager("images"), "DESTINATION");
      for (Iterator<Relation> iter = images.iterator(); iter.hasNext();) {
         Relation inlineRel = iter.next();
         inlineImages.put(inlineRel.getStringValue(RichText.REFERID_FIELD), inlineRel);
      }
      
      // transform all images
      NodeList imglist = doc.getElementsByTagName(RichText.IMG_TAGNAME);
      log.debug("" + imglist.getLength() + " images found in richtext.");
      if (imglist.getLength() > 0) {
         resolveImages(cloud, imglist, inlineImages, dynamicDescriptions);
      }

      return doc;
   }


   /**
    * Find image tags in the text and replace them with marked up blocks
    * including the corrected image tag.
    */
   private void resolveImages(Cloud cloud, NodeList nl, Map<String, Relation> inlineImages, boolean dynamicDescriptions) {
      for (int j = nl.getLength() - 1; j >= 0; --j) {
         Element image = (Element) nl.item(j);

         if (image.hasAttribute(RichText.DESCRIPTION_ATTR)
               && !(image.hasAttribute(RichText.TITLE_ATTR) && image.hasAttribute(RichText.ALT_ATTR))) {
            String desc = image.getAttribute(RichText.DESCRIPTION_ATTR);
            image.removeAttribute(RichText.DESCRIPTION_ATTR);
            image.setAttribute(RichText.TITLE_ATTR, desc);
            image.setAttribute(RichText.ALT_ATTR, desc);
         }

         if (image.hasAttribute(RichText.RELATIONID_ATTR)) {
            String imgidrel = image.getAttribute(RichText.RELATIONID_ATTR);
            log.debug("Creating image by relation " + imgidrel);

            if (!inlineImages.containsKey(imgidrel)) {
               if (isAnonymousVisitor(cloud)) {
                  org.w3c.dom.Node parentNode = image.getParentNode();
                  parentNode.removeChild(image);
               }
               continue;
            }
            Relation inlineRel = inlineImages.get(String.valueOf(imgidrel));
            String imageId = inlineRel.getStringValue("dnumber");

            Node imageNode = cloud.getNode(imageId);
            if (dynamicDescriptions) {
               String description = imageNode.getStringValue("description");
               if (StringUtils.isBlank(description)) {
                  description = imageNode.getStringValue(RichText.TITLE_ATTR);
               }
               image.setAttribute(RichText.ALT_ATTR, description);
               image.setAttribute(RichText.TITLE_ATTR, description);
            }
            int height = inlineRel.getIntValue("height");
            int width = inlineRel.getIntValue("width");

            Node scaledImageNode = ResourcesUtil.getImageNode(imageNode, height, width);
            String servletPath = ResourcesUtil
                  .getServletPath(scaledImageNode, scaledImageNode.getStringValue("number"));

            image.setAttribute(RichText.SRC_ATTR, servletPath);
            if (width > 0) {
               image.setAttribute(RichText.WIDTH_ATTR, String.valueOf(width));
            }
            if (height > 0) {
               image.setAttribute(RichText.HEIGHT_ATTR, String.valueOf(height));
            }

            if (image.hasAttribute("align")) {
               String align = image.getAttribute("align");
               if (image.hasAttribute("class")) {
                  String oldClass = image.getAttribute("class");
                  String classStr = oldClass + " inline-" + align;
                  image.removeAttribute("class");
                  image.setAttribute("class", classStr);
               }
               else {
                  String classStr = "inline-" + align;
                  image.setAttribute("class", classStr);
               }
            }

            if (isAnonymousVisitor(cloud)) {
               image.removeAttribute(RichText.RELATIONID_ATTR);
               if (image.hasAttribute(RichText.DESTINATION_ATTR)) {
                  image.removeAttribute(RichText.DESTINATION_ATTR);
               }
            }

         }
         else {
            log.debug("ImageTag does not contain relationId attribute. Skipping this image.");
         }
      }
   }

   /**
    * Find a tags in the text and replace them with valid links
    */
   private void resolveLinks(Cloud cloud, NodeList nl, Map<String, String> inlineLinks, boolean dynamicDescriptions) {
      for (int j = nl.getLength() - 1; 0 <= j; --j) {
         Element aElement = (Element) nl.item(j);

         if (aElement.hasAttribute(RichText.RELATIONID_ATTR)) {
            log.debug("Creating link to node by inlinerel");
            String idrel = aElement.getAttribute(RichText.RELATIONID_ATTR);

            if (!inlineLinks.containsKey(String.valueOf(idrel))) {
               if (isAnonymousVisitor(cloud)) {
                  org.w3c.dom.Node parentNode = aElement.getParentNode();
                  org.w3c.dom.Node nextSibling = aElement.getNextSibling();
                  while (nextSibling != null && nextSibling.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE) {
                     nextSibling = nextSibling.getNextSibling();
                  }

                  parentNode.removeChild(aElement);
                  org.w3c.dom.NodeList children = aElement.getChildNodes();
                  for (int i = 0; i < children.getLength(); i++) {
                     org.w3c.dom.Node child = children.item(i);
                     if (child.getNodeType() != org.w3c.dom.Node.ATTRIBUTE_NODE) {
                        parentNode.insertBefore(child, nextSibling);
                     }
                  }
               }
               continue;
            }
            String aElementId = inlineLinks.get(String.valueOf(idrel));

            Node destinationNode = cloud.getNode(aElementId);

            String name = null;
            String url = null;
            String builderName = destinationNode.getNodeManager().getName();
            if ("attachments".equals(builderName)) {
               name = destinationNode.getStringValue(RichText.DESCRIPTION_ATTR);
               if (StringUtils.isBlank(name)) {
                  name = destinationNode.getStringValue(RichText.TITLE_FIELD);
               }
               url = ResourcesUtil.getServletPath(destinationNode, destinationNode.getStringValue("number"));
            }
            else {
               if ("urls".equals(builderName)) {
                  name = destinationNode.getStringValue(RichText.TITLE_FIELD);
                  url = destinationNode.getStringValue("url");
                  url = url.replaceAll("&(?!amp;)", "&amp;");
               }
               else {
                  if (destinationNode.getNodeManager().hasField(RichText.TITLE_FIELD)) {
                     name = destinationNode.getStringValue(RichText.TITLE_FIELD);
                  }
                  else {
                     if (destinationNode.getNodeManager().hasField("name")) {
                        name = destinationNode.getStringValue("name");
                     }
                  }
                  url = getContentUrl(destinationNode);
               }
            }

            if (aElement.hasAttribute(RichText.HREF_ATTR)) {
               aElement.removeAttribute(RichText.HREF_ATTR);
            }
            aElement.setAttribute(RichText.HREF_ATTR, url);

            if (StringUtils.isBlank(name)) {
               name = url;
            }
            if (!aElement.hasAttribute(RichText.TITLE_ATTR) || dynamicDescriptions) {
               aElement.setAttribute(RichText.TITLE_ATTR, name);
            }

            if (isAnonymousVisitor(cloud)) {
               aElement.removeAttribute(RichText.RELATIONID_ATTR);
               if (aElement.hasAttribute(RichText.DESTINATION_ATTR)) {
                  aElement.removeAttribute(RichText.DESTINATION_ATTR);
               }
            }
         }
      }
   }

   protected String getContentUrl(Node node) {
      String title = null;

      //Check for the existence of title field of the node
      if (node.getNodeManager().hasField(RichText.TITLE_FIELD)) {
         title = node.getStringValue(RichText.TITLE_FIELD);
      }

      String id = node.getStringValue("number");
      return ResourcesUtil.getServletPathWithAssociation("content", "/content/*", id, title);
   }

   protected boolean isAnonymousVisitor(Cloud cloud) {
      return cloud.getUser().getRank() == Rank.ANONYMOUS;
   }


}
