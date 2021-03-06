package com.finalist.cmsc.richtext.builders;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mmbase.applications.wordfilter.WordHtmlCleaner;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.util.CloudUtil;
import org.mmbase.core.CoreField;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.finalist.cmsc.richtext.RichText;

/**
 * @author Nico Klasens (Finalist IT Group)
 * @author Hillebrand Gelderblom
 * @author Cees Roele
 */
public class RichTextBuilder extends MMObjectBuilder {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(RichTextBuilder.class.getName());

   /** list of html text fields to clean */
   protected List<String> htmlFields = new ArrayList<String>();

   protected MMObjectBuilder inlinerelBuilder = null;
   protected int inlinerelNumber = -1;

   protected MMObjectBuilder imagerelBuilder = null;
   protected int imagerelNumber = -1;

   protected boolean downloadImages = true;
   protected boolean resolveIds = true;

   private List<String> htmlTypes = new ArrayList<String>();
   private List<String> lightboxTypes = new ArrayList<String>();


   /**
    * @see org.mmbase.module.core.MMObjectBuilder#init()
    */
   @Override
   public boolean init() {
      if (!super.init()) {
         return false;
      }

      Map<String, String> map = getInitParameters("cmsc/richtext");
      String download = map.get("downloadImages");
      if (StringUtils.isNotEmpty(download)) {
         downloadImages = Boolean.valueOf(download);
      }
      String resolve = map.get("resolveIds");
      if (StringUtils.isNotEmpty(resolve)) {
         resolveIds = Boolean.valueOf(resolve);
      }

      String htmltypesParam = map.get("htmltypes");
      if (StringUtils.isNotEmpty(htmltypesParam)) {
         StringTokenizer tokenizer = new StringTokenizer(htmltypesParam, ", \t\n\r\f");
         while(tokenizer.hasMoreTokens()) {
            htmlTypes.add(tokenizer.nextToken());
         }
      }
      String lightboxtypesParam = map.get("lightboxtypes");
      if (StringUtils.isNotEmpty(lightboxtypesParam)) {
         StringTokenizer tokenizer = new StringTokenizer(lightboxtypesParam, ", \t\n\r\f");
         while(tokenizer.hasMoreTokens()) {
            lightboxTypes.add(tokenizer.nextToken());
         }
      }
      
      Collection<CoreField> fields = getFields();
      for (CoreField field : fields) {
         if (isHtmlField(field)) {
            String fieldname = field.getName();
            log.debug("richtext field: " + fieldname.trim());
            htmlFields.add(fieldname);
         }
      }
      return true;
   }

   /**
    * override this method if you have your own rich text fields!
    *
    * @param name
    * @return
    */
   public final boolean isHtmlField(String name) {
      return RichText.isHtmlField(name) || htmlTypes.contains(name) || lightboxTypes.contains(name);
   }
   
   public final boolean isLightboxFIeld(String name) {
      return RichText.isLightboxFIeld(name) || lightboxTypes.contains(name);
   }

   public boolean isHtmlField(Field field) {
      String dataTypeName = RichText.getDataTypeName(field);
      return isHtmlField(dataTypeName);
   }

   public boolean isLightboxFIeld(Field field) {
      String dataTypeName = RichText.getDataTypeName(field);
      return isLightboxFIeld(dataTypeName);
   }
   
   protected void initInlineBuilders() {
      if (inlinerelBuilder == null) {
         inlinerelBuilder = mmb.getMMObject(RichText.INLINEREL_NM);
         if (inlinerelBuilder == null) {
            throw new IllegalStateException("Builder '" + RichText.INLINEREL_NM + "' does not exist.");
         }
         inlinerelNumber = mmb.getRelDef().getNumberByName(RichText.INLINEREL_NM);
      }

      if (imagerelBuilder == null) {
         imagerelBuilder = mmb.getMMObject(RichText.IMAGEINLINEREL_NM);
         if (imagerelBuilder == null) {
            throw new IllegalStateException("Builder '" + RichText.IMAGEINLINEREL_NM + "' does not exist.");
         }
         imagerelNumber = mmb.getRelDef().getNumberByName(RichText.IMAGEINLINEREL_NM);
      }
   }


   @Override
   public int insert(String owner, MMObjectNode node) {
      initInlineBuilders();

      int number = super.insert(owner, node);
      if (!resolveIds) {
         return number;
      }
      List<CoreField> fields = getFields(NodeManager.ORDER_EDIT);
      List<String> idsList = new ArrayList<String>();
      resolve(node, idsList, fields, true);
      if (!idsList.isEmpty()) {
         super.commit(node);
      }

      return number;
   }


   @Override
   public boolean commit(MMObjectNode node) {
      initInlineBuilders();

      log.debug("committing node " + node);
      if (!resolveIds) {
         return super.commit(node);
      }

      // Resolve images
      List<CoreField> fields = getFields(NodeManager.ORDER_EDIT);

      boolean htmlFieldChanged = false;

      Iterator<CoreField> checkFields = fields.iterator();
      while (checkFields.hasNext()) {
         Field field = checkFields.next();
         if (field != null) {
            String fieldName = field.getName();
            if (node.getChanged().contains(fieldName)) {
               if (htmlFields.contains(fieldName)) {
                  htmlFieldChanged = true;
               }

               if (fieldName.equals("title")) {
                  String value = node.getStringValue("title");
                  if (value.length() != value.trim().length()) {
                     node.setValue("title", value.trim());
                  }
               }
               
               if (fieldName.equals("subtitle")) {
                  String value = node.getStringValue("subtitle");
                  if (value.length() != value.trim().length()) {
                     node.setValue("subtitle", value.trim());
                  }
               }
               
               if (fieldName.equals("description")) {
                  String value = node.getStringValue("description");
                  if (value.length() != value.trim().length()) {
                     node.setValue("description", value.trim());
                  }
               }
            }
         }
      }

      List<String> idsList = new ArrayList<String>();

      if (htmlFieldChanged) {
         resolve(node, idsList, fields, false);
      }

      boolean committed = super.commit(node);

      if (committed) {
         if (htmlFieldChanged) {
            // remove outdated inlinerel
            Enumeration<MMObjectNode> idrels = node.getRelations(RichText.INLINEREL_NM);
            while (idrels.hasMoreElements()) {
               MMObjectNode rel = idrels.nextElement();
               String referid = rel.getStringValue(RichText.REFERID_FIELD);
               if ((rel.getIntValue("snumber") == node.getNumber()) && !idsList.contains(referid)) {
                  inlinerelBuilder.removeNode(rel);
                  log.debug("removed unused inlinerel: " + referid);
               }
            }

            // remove outdated imageinlinerel
            Enumeration<MMObjectNode> imagerels = node.getRelations(RichText.IMAGEINLINEREL_NM);
            while (imagerels.hasMoreElements()) {
               MMObjectNode rel = imagerels.nextElement();
               String referid = rel.getStringValue(RichText.REFERID_FIELD);
               if (!idsList.contains(referid)) {
                  imagerelBuilder.removeNode(rel);
                  log.debug("removed unused imageinlinerel: " + referid);
               }
            }
         }
      }

      return committed;
   }


   protected void resolve(MMObjectNode node, List<String> idsList, List<CoreField> fields, boolean isInsert) {
      Iterator<CoreField> iFields = fields.iterator();
      while (iFields.hasNext()) {
         Field field = iFields.next();

         if (field != null) {
            String fieldName = field.getName();
            if (log.isDebugEnabled()) {
               log.debug("Got field " + fieldName + " : persistent = " + (!field.isVirtual()) + ", stringtype = "
                     + (field.getType() == Field.TYPE_STRING) + ", isHtmlField = " + htmlFields.contains(fieldName));
            }

            if (htmlFields.contains(fieldName)) {
               log.debug("Evaluating " + fieldName);
               if (isInsert || node.getChanged().contains(fieldName)) {
                  // Persistent string field.
                  String fieldValue = (String) node.getValues().get(fieldName);
                  if (StringUtils.isNotEmpty(fieldValue)) {
                     try {
                        if (RichText.hasRichtextItems(fieldValue)) {
                           Document doc = RichText.getRichTextDocument(fieldValue);

                           if (isLightboxFIeld(field)) {
                              resolveLightboxResources(node, idsList, doc);
                           }
                           
                           resolveResources(node, idsList, doc);

                           String out = RichText.getRichTextString(doc);
                           out = WordHtmlCleaner.fixEmptyAnchors(out);
                           log.debug("final richtext text = " + out);

                           node.setValue(fieldName, out);
                        }
                     }
                     catch (Exception e) {
                        log.error("An error occured while resolving inline resources!", e);
                     }
                  }
               }
               else {
                  String fieldValue = (String) node.getValues().get(fieldName);
                  if (StringUtils.isNotEmpty(fieldValue) && RichText.hasRichtextItems(fieldValue)) {
                     Document doc = RichText.getRichTextDocument(fieldValue);
                     fillIdFromLinks(doc, idsList);
                     fillIdFromImages(doc, idsList);
                  }
               }
            }
         }
      }
   }

   protected void resolveResources(MMObjectNode node, List<String> idsList, Document doc) {
      // resolve links. Links in document are inline links from richtext functionality.
      // links created for the lightbox are removed above.
      resolveLinks(doc, idsList, node);
      // resolve images as normal richtext functionality.
      resolveImages(doc, idsList, node);
   }

   private void resolveLightboxResources(MMObjectNode node, List<String> idsList, Document doc) {
      // remove all invalid lightbox links (lightbox checkbox turned off)
      resolveLightBoxLinks(doc, idsList, node);
      // remove all lightbox links around lightbox images
      resolveLightBoxImages(doc, idsList, node);
   }


   protected void fillIdFromImages(Document doc, List<String> idsList) {
      fillIds(doc, idsList, RichText.IMG_TAGNAME);
   }


   protected void fillIdFromLinks(Document doc, List<String> idsList) {
      fillIds(doc, idsList, RichText.LINK_TAGNAME);
   }


   protected void fillIds(Document doc, List<String> idsList, String tagname) {
      NodeList nl = doc.getElementsByTagName(tagname);
      for (int i = 0, len = nl.getLength(); i < len; i++) {
         Element link = (Element) nl.item(i);
         if (link.hasAttribute(RichText.RELATIONID_ATTR)) {
            String id = link.getAttribute(RichText.RELATIONID_ATTR);
            idsList.add(id);
         }
      }
   }


   /**
    * resolve links in the richtextfield and make inlinerel of it. Add the id to
    * the anchortag so that the link can be resolved in the frontend and point to
    * the correct article.
    */
   protected void resolveLinks(Document doc, List<String> idsList, MMObjectNode mmObj) {
      if (doc == null) {
         return;
      }
      // collect <A> tags
      NodeList nl = doc.getElementsByTagName(RichText.LINK_TAGNAME);
      log.debug("number of links: " + nl.getLength());

      for (int i = 0, len = nl.getLength(); i < len; i++) {
         Element link = (Element) nl.item(i);

         if (link.hasAttribute(RichText.DESTINATION_ATTR)
               && "undefined".equalsIgnoreCase(link.getAttribute(RichText.DESTINATION_ATTR))) {
            link.removeAttribute(RichText.DESTINATION_ATTR);
         }
         
         //Check for broken and bad destinations and clean them, so the user can try it again.
         if (link.hasAttribute(RichText.DESTINATION_ATTR)
               && !NumberUtils.isNumber(link.getAttribute(RichText.DESTINATION_ATTR))) {
            log.info("While searching for a destinationId, no number was found. Found:" + link.getAttribute(RichText.DESTINATION_ATTR));
            link.removeAttribute(RichText.DESTINATION_ATTR);
            if (link.hasAttribute(RichText.HREF_ATTR)) { //Also remove href, preventing bad URLs being created
               link.removeAttribute(RichText.HREF_ATTR);
            }
            continue; //Continue to next element; nothing to fix the link.
         }
         if (link.hasAttribute(RichText.RELATIONID_ATTR)
               && "undefined".equalsIgnoreCase(link.getAttribute(RichText.RELATIONID_ATTR))) {
            link.removeAttribute(RichText.RELATIONID_ATTR);
         }

         // handle relations to other objects
         if (isInlineAttributesComplete(link)) {
            // get id of the link
            String id = link.getAttribute(RichText.RELATIONID_ATTR);
            int destination = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));

            MMObjectNode inlinerel = getInlineRel(id);
            if (inlinerel != null) {
               // check if destination is still the same
               int dnumber = inlinerel.getIntValue("dnumber");
               if (destination == dnumber) {
                  idsList.add(id);
               }
               else {
                  link.removeAttribute(RichText.RELATIONID_ATTR);
                  String referid = createInlineRel(mmObj, destination);
                  // update richtext
                  link.setAttribute(RichText.RELATIONID_ATTR, referid);
                  idsList.add(referid);
               }
            }
            else {
               // check if destination still exists
               if (getNode(destination) != null) {
                  // create inlinerel to related object
                  String referid = createInlineRel(mmObj, destination);
                  // update richtext
                  link.setAttribute(RichText.RELATIONID_ATTR, referid);

                  idsList.add(referid);
               }
               else {
                  // destination does also not exist!
                  link.setAttribute("title", "Link, element of bestemming is verwijderd !!!");
               }
            }
            if (link.hasAttribute(RichText.HREF_ATTR)) {
               link.removeAttribute(RichText.HREF_ATTR);
            }
         }
         else {
            if (isNewInline(link)) {
               // create inlinerel to related object
               int objId = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));

               String referid = createInlineRel(mmObj, objId);
               link.setAttribute(RichText.RELATIONID_ATTR, referid);

               idsList.add(referid);

               if (link.hasAttribute(RichText.HREF_ATTR)) {
                  link.removeAttribute(RichText.HREF_ATTR);
               }
            }
            else {
               if (link.hasAttribute(RichText.RELATIONID_ATTR)) {
                  String id = link.getAttribute(RichText.RELATIONID_ATTR);
                  idsList.add(id);
               }
               else {
                  if (link.hasAttribute(RichText.HREF_ATTR) && !(link.getAttribute(RichText.HREF_ATTR).startsWith("#")) ) {
                     String href = link.getAttribute(RichText.HREF_ATTR);
                     String name = link.getFirstChild().getNodeValue();       
                     String owner = mmObj.getStringValue("owner");
                     MMObjectNode urlNode = createUrl(owner, href, name);

                     String referid = createInlineRel(mmObj, urlNode.getNumber());

                     link.setAttribute(RichText.RELATIONID_ATTR, referid);
                     link.setAttribute(RichText.DESTINATION_ATTR, String.valueOf(urlNode.getNumber()));
                     link.removeAttribute(RichText.HREF_ATTR);

                     idsList.add(referid);
                     log.debug("imported url " + urlNode.getNumber() + " and added idrel: " + referid);
                  }
               }
            }
         }
      }
   }


   protected void resolveImages(Document doc, List<String> idsList, MMObjectNode mmObj) {
      if (doc == null) {
         return;
      }

      NodeList nl = doc.getElementsByTagName(RichText.IMG_TAGNAME);
      log.debug("number of images: " + nl.getLength());
      for (int i = 0, len = nl.getLength(); i < len; i++) {
         Element image = (Element) nl.item(i);

         if (image.hasAttribute(RichText.DESTINATION_ATTR)
               && "undefined".equalsIgnoreCase(image.getAttribute(RichText.DESTINATION_ATTR))) {
            image.removeAttribute(RichText.DESTINATION_ATTR);
         }
         if (image.hasAttribute(RichText.RELATIONID_ATTR)
               && "undefined".equalsIgnoreCase(image.getAttribute(RichText.RELATIONID_ATTR))) {
            image.removeAttribute(RichText.RELATIONID_ATTR);
         }

         if (isInlineAttributesComplete(image)) {
            // get id of the image
            String id = image.getAttribute(RichText.RELATIONID_ATTR);
            int destination = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));

            MMObjectNode imagerel = getImageInlineRel(id);
            if (imagerel != null) {
               String width = null;
               if (image.hasAttribute(RichText.WIDTH_ATTR)) {
                  width = image.getAttribute(RichText.WIDTH_ATTR);
                  image.removeAttribute(RichText.WIDTH_ATTR);
               }
               String height = null;
               if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
                  height = image.getAttribute(RichText.HEIGHT_ATTR);
                  image.removeAttribute(RichText.HEIGHT_ATTR);
               }
               String legend = null;
               if (image.hasAttribute(RichText.LEGEND)) {
                  legend = image.getAttribute(RichText.LEGEND);
                  image.removeAttribute(RichText.LEGEND);
               }

               // check if destination is still the same
               int dnumber = imagerel.getIntValue("dnumber");
               if (destination == dnumber) {
                  setImageIdRelFields(imagerel, height, width, legend);
                  imagerel.commit();
                  idsList.add(id);
               }
               else {
                  String referid = createImageIdRel(mmObj, destination, height, width, legend);
                  image.setAttribute(RichText.RELATIONID_ATTR, referid);
                  idsList.add(referid);
               }
            }
            else {
               // check if destination still exists
               if (getNode(destination) != null) {
                  // update richtext
                  String width = null;
                  if (image.hasAttribute(RichText.WIDTH_ATTR)) {
                     width = image.getAttribute(RichText.WIDTH_ATTR);
                     image.removeAttribute(RichText.WIDTH_ATTR);
                  }
                  String height = null;
                  if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
                     height = image.getAttribute(RichText.HEIGHT_ATTR);
                     image.removeAttribute(RichText.HEIGHT_ATTR);
                  }
                  String legend = null;
                  if (image.hasAttribute(RichText.LEGEND)) {
                     legend = image.getAttribute(RichText.LEGEND);
                     image.removeAttribute(RichText.LEGEND);
                  }
                  String referid = createImageIdRel(mmObj, destination, height, width, legend);
                  image.setAttribute(RichText.RELATIONID_ATTR, referid);

                  idsList.add(referid);
               }
               else {
                  // destination does also not exist!
                  // image.removeAttribute(RichText.SRC_ATTR);
                  // image.getParentNode().removeChild(image);

                  // Het verwijderen van de image tag bij het ontbreken van het
                  // plaatje is
                  // uitgecommentarieerd omdat
                  // dit fout ging bij publiceren. Eerst wordt het artikel
                  // gepubliceerd en
                  // vervolgens pas de secundaire
                  // elementen die eraan hangen. Bij het publiceren wordt deze
                  // klasse
                  // uitgevoerd voor het opslaan van het
                  // artikel, maar op dat moment is het plaatje nog niet
                  // gepubliceerd en
                  // bestaat dus nog niet. De img tag wordt
                  // dan vrolijk verwijderd.
               }
            }
            if (image.hasAttribute(RichText.SRC_ATTR)) {
               image.removeAttribute(RichText.SRC_ATTR);
            }
         }
         else {
            // handle relations to other objects
            if (isNewInline(image)) {
               // create inlinerel to related object
               int objId = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));

               String width = null;
               if (image.hasAttribute(RichText.WIDTH_ATTR)) {
                  width = image.getAttribute(RichText.WIDTH_ATTR);
                  image.removeAttribute(RichText.WIDTH_ATTR);
               }
               String height = null;
               if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
                  height = image.getAttribute(RichText.HEIGHT_ATTR);
                  image.removeAttribute(RichText.HEIGHT_ATTR);
               }
               String legend = null;
               if (image.hasAttribute(RichText.LEGEND)) {
                  legend = image.getAttribute(RichText.LEGEND);
                  image.removeAttribute(RichText.LEGEND);
               }
               String referid = createImageIdRel(mmObj, objId, height, width, legend);
               image.setAttribute(RichText.RELATIONID_ATTR, referid);

               idsList.add(referid);
            }
            else {
               if (!image.hasAttribute(RichText.RELATIONID_ATTR)) {
                  if (downloadImages && image.hasAttribute(RichText.SRC_ATTR)) {
                     importImage(image, mmObj, idsList);
                  }
               }
               else {
                  String referid = image.getAttribute(RichText.RELATIONID_ATTR);
                  idsList.add(referid);
               }
            }
         }
      }
   }
   
   protected void resolveLightBoxLinks(Document doc, List<String> idsList, MMObjectNode mmObj) {
      if (doc == null) {
          return;
      }
      // collect <A> tags
      NodeList nl = doc.getElementsByTagName(RichText.LINK_TAGNAME);

      for (int i = nl.getLength() - 1; i >= 0; --i) {
          Element link = (Element) nl.item(i);

          String ligthboxValue = link.getAttribute(RichText.LIGHTBOX_ATTR);
          if (StringUtils.isNotEmpty(ligthboxValue) && ligthboxValue.startsWith(RichText.LIGHTBOX_VALUE)) {
              NodeList nlImages = link.getElementsByTagName(RichText.IMG_TAGNAME);
              
              for (int j = nlImages.getLength() - 1; j >= 0; --j) {
                  Element image = (Element) nlImages.item(j);
                  if (!image.hasAttribute(RichText.LIGHTBOX_ATTR) ){
                      // Found lightbox link, but image has no lightbox attribute. Remove link
                      
                      // replace link node with image node
                      Node linkParentNode = link.getParentNode();
                      linkParentNode.replaceChild(image, link);
                      break; //continue with next link
                  }
              }
          }
      }
  }
  
  protected void resolveLightBoxImages(Document doc, List<String> idsList, MMObjectNode mmObj) {
      if (doc == null) {
          return;
      }

      NodeList nl = doc.getElementsByTagName(RichText.IMG_TAGNAME);
      
      for (int i = nl.getLength() - 1; i >= 0; --i) {
          Element image = (Element) nl.item(i);
          if (image.hasAttribute(RichText.LIGHTBOX_ATTR)) {
              org.w3c.dom.Node parentNode = image.getParentNode();             
              if (parentNode.getNodeType() == Node.ELEMENT_NODE &&
                      parentNode.getNodeName().equals(RichText.LINK_TAGNAME)) {
                  Element link = ((Element) parentNode);
                  String ligthboxValue = link.getAttribute(RichText.LIGHTBOX_ATTR);
                  if (StringUtils.isNotEmpty(ligthboxValue) && ligthboxValue.startsWith(RichText.LIGHTBOX_VALUE)) {
                      // Found lightbox link. Never save these links. Remove node from DOM.

                      Node linkParentNode = link.getParentNode();
                      linkParentNode.replaceChild(image, link);
                  }
              }
          }
      }
  }

   protected void importImage(Element image, MMObjectNode mmObj, List<String> idsList) {
      String src = image.getAttribute(RichText.SRC_ATTR);
      log.warn("Image found which is not linked " + src);
      String alt = image.getAttribute("alt");
      String owner = mmObj.getStringValue("owner");
      MMObjectNode imageNode = createImage(owner, src, alt);
      if (imageNode != null) {
         String width = null;
         if (image.hasAttribute(RichText.WIDTH_ATTR)) {
            width = image.getAttribute(RichText.WIDTH_ATTR);
            image.removeAttribute(RichText.WIDTH_ATTR);
         }
         String height = null;
         if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
            height = image.getAttribute(RichText.HEIGHT_ATTR);
            image.removeAttribute(RichText.HEIGHT_ATTR);
         }
         String legend = null;
         if (image.hasAttribute(RichText.LEGEND)) {
            legend = image.getAttribute(RichText.LEGEND);
            image.removeAttribute(RichText.LEGEND);
         }

         String referid = createImageIdRel(mmObj, imageNode.getNumber(), height, width, legend);

         image.setAttribute(RichText.RELATIONID_ATTR, referid);
         image.setAttribute(RichText.DESTINATION_ATTR, String.valueOf(imageNode.getNumber()));
         image.removeAttribute(RichText.SRC_ATTR);

         idsList.add(referid);

         log.debug("imported image " + imageNode.getNumber() + " and added imgidrel: " + referid);
      }
   }


   protected boolean isNewInline(Element element) {
      return element.hasAttribute(RichText.DESTINATION_ATTR) && !element.hasAttribute(RichText.RELATIONID_ATTR);
   }


   protected boolean isInlineAttributesComplete(Element element) {
      return element.hasAttribute(RichText.DESTINATION_ATTR) && element.hasAttribute(RichText.RELATIONID_ATTR);
   }


   protected MMObjectNode getInlineRel(String id) {
      return getRelation(id, inlinerelBuilder, RichText.REFERID_FIELD);
   }


   protected MMObjectNode getImageInlineRel(String id) {
      return getRelation(id, imagerelBuilder, RichText.REFERID_FIELD);
   }


   protected MMObjectNode getRelation(String id, MMObjectBuilder builder, String idField) {
      NodeSearchQuery query = getQuery(id, builder, idField);
      try {
         List<MMObjectNode> nodes = builder.getNodes(query);
         for (MMObjectNode imagerel : nodes) {
            return imagerel;
         }
      }
      catch (SearchQueryException e) {
         log.error(e);
      }
      return null;
   }


   protected NodeSearchQuery getQuery(String id, MMObjectBuilder builder, String idField) {
      // get all relations which are related to the node
      NodeSearchQuery query = new NodeSearchQuery(builder);
      StepField referidStepField = query.getField(builder.getField(idField));
      BasicFieldValueConstraint cReferid = new BasicFieldValueConstraint(referidStepField, id);
      cReferid.setOperator(FieldCompareConstraint.EQUAL);
      query.setConstraint(cReferid);
      return query;
   }


   /**
    * Creates a relation for an inline link
    */
   protected String createInlineRel(MMObjectNode mmObj, int objId) {
      String owner = mmObj.getStringValue("owner");
      MMObjectNode idrel = inlinerelBuilder.getNewNode(owner);
      idrel.setValue("snumber", mmObj.getNumber());
      idrel.setValue("dnumber", objId);
      idrel.setValue("rnumber", inlinerelNumber);
      idrel.insert(owner);

      return idrel.getStringValue(RichText.REFERID_FIELD);
   }


   /**
    * Creates a relation for an inline image
    */
   protected String createImageIdRel(MMObjectNode mmObj, int dnumber, String height, String width, String legend) {
      String owner = mmObj.getStringValue("owner");
      MMObjectNode imagerel = imagerelBuilder.getNewNode(owner);
      imagerel.setValue("snumber", mmObj.getNumber());
      imagerel.setValue("dnumber", dnumber);
      imagerel.setValue("rnumber", imagerelNumber);

      setImageIdRelFields(imagerel, height, width, legend);
      int insres = imagerel.insert(owner);
      log.debug("insert of imageidrel " + insres + "\r\n" + "   snumber:" + mmObj.getNumber() + "\r\n" + "   dnumber:"
            + dnumber + "\r\n" + "   rnumber:" + imagerelNumber);

      return imagerel.getStringValue(RichText.REFERID_FIELD);
   }


   protected void setImageIdRelFields(MMObjectNode imagerel, String height, String width, String legend) {
      if (StringUtils.isNotEmpty(height)) {
         imagerel.setValue("height", height);
      }
      if (StringUtils.isNotEmpty(width)) {
         imagerel.setValue("width", width);
      }
      if (StringUtils.isNotEmpty(legend)) {
         imagerel.setValue("legend", legend);
      }
   }


   protected MMObjectNode createImage(String owner, String src, String alt) {
      try {
         URL imageUrl = new URL(src);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         BufferedInputStream is = new BufferedInputStream(imageUrl.openStream());
         int bytesRead = 0;
         byte[] temp = new byte[4096];
         while (-1 != (bytesRead = is.read(temp))) {
            baos.write(temp, 0, bytesRead);
         }
         is.close();

         log.debug("image retrieved. " + src);
         int protocolIndex = src.indexOf("://") + "://".length();
         int imageUrlIndex = src.indexOf("/", protocolIndex);
         String title = "Imported: " + src.substring(imageUrlIndex);
         log.debug("Found image with title " + title + " and alt tekst " + alt);

         MMObjectNode imageNode = mmb.getMMObject("images").getNewNode(owner);
         imageNode.setValue("title", title.trim());
         imageNode.setValue("handle", baos.toByteArray());
         imageNode.setValue("description", alt.trim());
         imageNode.insert(owner);
         return imageNode;
      }
      catch (IOException t) {
         log.error("Failed to read " + src);
      }
      catch (Throwable t) {
         log.error(Logging.stackTrace(t));
      }
      return null;
   }


   protected MMObjectNode createUrl(String owner, String href, String name) {
      MMObjectNode urlNode = mmb.getMMObject("urls").getNewNode(owner);
      if (StringUtils.isNotBlank(name)) {
         urlNode.setValue("title", name.trim());
      }
      else {
         urlNode.setValue("title", href.trim());
      }
      urlNode.setValue("url", href.trim());
      setLastModifier(urlNode);
      urlNode.insert(owner);
      return urlNode;
   }
   
   private void setLastModifier(MMObjectNode node) {
      String username = CloudUtil.getCloudFromThread().getUser().getIdentifier();
      node.setValue("lastmodifier", username);
      node.setValue("lastmodifieddate", System.currentTimeMillis()/1000);
   } 
}