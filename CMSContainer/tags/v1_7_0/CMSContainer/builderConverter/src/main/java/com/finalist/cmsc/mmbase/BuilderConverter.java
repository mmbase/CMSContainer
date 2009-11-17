package com.finalist.cmsc.mmbase;

import java.io.*;

import java.util.*;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.*;

import com.finalist.cmsc.util.XmlUtil;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class BuilderConverter {
   
   
   public static void main(String[] args) {
      String baseDirectory;
      if (args.length >= 1) {
         baseDirectory = args[0];
      }
      else {
         throw new IllegalArgumentException("baseDirectory not provided");
      }
      
      
      String newBaseDirectory = baseDirectory;
      convertBuilders(baseDirectory, newBaseDirectory);
      
//      copyBuilders(baseDirectory, "c:\\projects\\CMSTest");
//
//      deleteEmptyBuilderDirectories(newBaseDirectory);
      System.out.println("DONE!!");
   }

   private static void deleteEmptyBuilderDirectories(String newBaseDirectory) {
      DirectoryScanner scanner = new DirectoryScanner();

      String[] includes = new String[]{"**/config/builders"};
      String[] excludes = new String[]{"**/target/**",
                                       "**/CMSContainer_Demo/**"};
      
      scanner.setBasedir(newBaseDirectory);
      scanner.setIncludes( includes );
      scanner.setExcludes( excludes );
      scanner.addDefaultExcludes();
      scanner.scan();
      List<String> includedFiles = Arrays.asList(scanner.getIncludedDirectories());
      
      for (Iterator<String> j = includedFiles.iterator(); j.hasNext();) {
         String name = j.next();

         File file = new File(newBaseDirectory, name);
         System.out.println(file.getPath());
         
         
         File[] children = file.listFiles();
         for (int i = 0; i < children.length; i++) {
            File child = children[i];
            if (child.list().length == 0) {
               child.delete();
            }
         }
         if (file.list().length == 0) {
            file.delete();
         }
         
      }
   }
   
   private static void copyBuilders(String baseDirectory, String newBaseDirectory) {
      DirectoryScanner scanner = new DirectoryScanner();

      String[] includes = new String[]{"**/config/builders/**/*.xml"};
      String[] excludes = new String[]{"**/target/**",
                                       "**/CMSContainer_Demo/**"};
      
      scanner.setBasedir(baseDirectory);
      scanner.setIncludes( includes );
      scanner.setExcludes( excludes );
      scanner.addDefaultExcludes();
      scanner.scan();
      List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());
      
      for (Iterator<String> j = includedFiles.iterator(); j.hasNext();) {
         String name = j.next();
         try {
            String newName = name.replaceAll(".*config\\\\(.*)", "config\\\\$1");
            
            File file = new File(baseDirectory, name);
            FileUtils.copyFile(file, new File(newBaseDirectory, newName));
            
            file.delete();
            
            System.out.println(name);
         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private static void convertBuilders(String baseDirectory, String newBaseDirectory) {
      DirectoryScanner scanner = new DirectoryScanner();

      String[] includes = new String[]{"**/config/**/builders/**/*.xml"};
      String[] excludes = new String[]{"**/target/**",
                                       "**/config/applications/Resources/**",
                                       "**/config/builders/**",
                                       "**/CMSContainer_Demo/**"};
      
      scanner.setBasedir(baseDirectory);
      scanner.setIncludes( includes );
      scanner.setExcludes( excludes );
      scanner.addDefaultExcludes();
      scanner.scan();
      List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());

      System.out.println("Copying " + +includedFiles.size() + " resource"
                  + (includedFiles.size() != 1 ? "s" : "")
                  );

      for (Iterator<String> j = includedFiles.iterator(); j.hasNext();) {
         String name = j.next();
         String newName = name.replaceAll("config\\\\applications\\\\(.*)\\\\builders",
                                          "config\\\\builders\\\\$1");
         
         processFile(baseDirectory, newBaseDirectory, name, newName);
         System.out.println(newName);
      }
   }

   private static void processFile(String baseDirectory, String newBaseDirectory, String name,
         String newName) {
      try {
         File file = new File(baseDirectory,name);
         Document doc = XmlUtil.toDocument(new FileInputStream(file), false, false);
         
         convert(doc);
         
         File newFile = new File(newBaseDirectory, newName);
         if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
         }
         if (newFile.exists()) {
            newFile.delete();
         }
         
         FileOutputStream fos = new FileOutputStream(newFile);
         OutputFormat of = new OutputFormat("XML","UTF-8",true);
         of.setIndent(1);
         of.setIndenting(true);
         of.setLineWidth(80);
         XMLSerializer serializer = new XMLSerializer(fos,of);
         // As a DOM Serializer
         serializer.asDOMSerializer();
         serializer.serialize( doc.getDocumentElement() );
         fos.close();
         
         file.delete();
         String[] files = file.getParentFile().list();
         if (files == null || files.length == 0) {
            file.getParentFile().delete();
         }
         
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void convert(Document doc) {
      removeStatus(doc);
      removeSearchAge(doc);
      removeComments(doc);
      removeEmptyProperties(doc);
      removeEmptyDescriptions(doc.getDocumentElement());
      convertFields(doc);

      doc.getDocumentElement().setAttribute("xsi:schemaLocation", "http://www.mmbase.org/xmlns/builder http://www.mmbase.org/xmlns/builder.xsd");
   }

   private static void removeComments(Document doc) {
      Element element = doc.getDocumentElement();
      removeComments(element);
   }

   private static void removeComments(Element element) {
      NodeList children = element.getChildNodes();
      for (int i = children.getLength()-1; i >=0 ; i--) {
         Node node = children.item(i);
         if (node.getNodeType() == Node.COMMENT_NODE) {
            String comment = node.getNodeValue().trim();
            if ("".equals(comment)
                  || comment.contains("<class>")
                  || comment.contains("<classfile>")
                  || comment.contains("Status of the builder")
                  || comment.contains("The default maximum age of objects in a search")
                  || comment.contains("Names defines the different names used in user visible parts")
                  || comment.contains("per language as defined by ISO 639")
                  || comment.contains("Descriptions are meant to provide some additional clarification")
                  || comment.contains("They are useful for GUI editors to provide helptexts.")
                  || comment.contains("You can define properties to be used by the classfile")
                  || comment.contains("gui related")
                  || comment.contains("editor related")
                  || comment.contains("database related")
                  || comment.contains("name of the field in the database")
                  || comment.contains("MMBase datatype and demands on it")
                  || (comment.contains("position in") && comment.contains("area of the editor"))) {
               node.getParentNode().removeChild(node);
            }
            else {
               System.err.println(comment);
            }
         }
         if (node.getNodeType() == Node.ELEMENT_NODE) {
            removeComments((Element) node);
         }
      }
   }

   private static void removeStatus(Document doc) {
      NodeList status = doc.getElementsByTagName("status");
      if (status.getLength() == 1) {
         Element e = (Element) status.item(0);
         String value = getNodeTextValue(e, true);
         if (value == null || "".equals(value) || "active".equalsIgnoreCase(value)) {
            e.getParentNode().removeChild(e);
         }
      }
   }
   
   private static void removeSearchAge(Document doc) {
      NodeList searchage = doc.getElementsByTagName("searchage");
      if (searchage.getLength() == 1) {
         Element e = (Element) searchage.item(0);
         String value = getNodeTextValue(e, true);
         if (value == null || "".equals(value) || "70".equalsIgnoreCase(value)) {
            e.getParentNode().removeChild(e);
         }
      }
      
   }

   private static void removeEmptyDescriptions(Element e) {
      NodeList descriptionList = e.getElementsByTagName("descriptions");
      for (int i = descriptionList.getLength()-1; i >=0 ; i--) {
         Element descriptions = (Element) descriptionList.item(i);
         NodeList desc = descriptions.getElementsByTagName("description");
         for (int j = desc.getLength()-1; j >=0 ; j--) {
            Element d = (Element) desc.item(j);
            String descValue = getNodeTextValue(d, true);
            if (descValue == null || "".equals(descValue)) {
               d.getParentNode().removeChild(d);
            }
         }
         if (descriptions.getElementsByTagName("description").getLength() == 0) {
            descriptions.getParentNode().removeChild(descriptions);
         }
      }
   }

   private static void removeEmptyProperties(Document doc) {
      NodeList properties = doc.getElementsByTagName("properties");
      for (int i = 0; i < properties.getLength(); i++) {
         Element p = (Element) properties.item(i); 
         NodeList props = p.getElementsByTagName("property");
         if (props.getLength() == 0) {
            p.getParentNode().removeChild(p);
         }
      }
   }

   private static void convertFields(Document doc) {
      NodeList fields = doc.getElementsByTagName("field");
      for (int i = 0; i < fields.getLength(); i++) {
         Element field = (Element) fields.item(i);
         if (!field.hasAttribute("name")) {
            Element dbName = getElementByPath(field, "field.db.name");
            String fieldName = getNodeTextValue(dbName, true);
            field.setAttribute("name", fieldName);
            dbName.getParentNode().removeChild(dbName);
         }
         
         Element dbType = getElementByPath(field, "field.db.type");
         if (dbType != null) {
            String state = dbType.getAttribute("state");
            if (!"persistent".equalsIgnoreCase(state)) {
               field.setAttribute("state", state);
            }
            dbType.removeAttribute("state");
            
            String readonly = dbType.getAttribute("readonly");
            if (readonly != null && !"".equals(readonly)) {
               field.setAttribute("readonly", readonly);
            }
            dbType.removeAttribute("readonly");
            
            String type = getNodeTextValue(dbType, true);
            Element datatype = getElementByPath(field, "field.datatype");
            if (datatype == null) {
               datatype = createDatatype(field, type);
            }
            else {
               String base = datatype.getAttribute("base");
               if (base == null || "".equals(base)) {
                  base = getBaseType(type);
                  datatype.setAttribute("base", base);
               }
            }
            
            convertUnique(field, dbType, datatype);
            convertRequired(field, dbType, datatype);
            convertMaxLength(field, dbType, datatype, type);
            
            if (dbType.getAttributes().getLength() == 0) {
               dbType.getParentNode().removeChild(dbType);
            }
            else {
               System.err.println("FIX THIS: field " + field.getAttribute("name") + " contains unprocessed attributes");
               for (int j = 0; j < dbType.getAttributes().getLength(); j++) {
                  Node node = dbType.getAttributes().item(j);
                  System.err.println("attribute: " + node.getNodeName());
               }
            }
         }
         Element db = getElementByPath(field, "field.db");
         if(db != null) {
            db.getParentNode().removeChild(db);
         }
         
         Element editorPositions = getElementByPath(field, "field.editor.positions");
         if (editorPositions != null) {
            List<Element> positions = XmlUtil.getElements(editorPositions);
            for (Element pos : positions) {
               String posValue = getNodeTextValue(pos, true);
               if (posValue == null || "".equals(posValue) || "-1".equals(posValue)) {
                  pos.getParentNode().removeChild(pos);
               }
            }
         }
      }
   }

   private static void convertMaxLength(Element field, Element dbType, Element datatype,
         String type) {
      String size = dbType.getAttribute("size");
      if (size != null && !"".equals(size)) {
         if ("STRING".equalsIgnoreCase(type) || "BYTE".equalsIgnoreCase(type)) {
            Element datatypeMaxLength = getElementByPath(field, "field.datatype.maxLength");
            if (datatypeMaxLength == null) {
               Element e = XmlUtil.createElement(datatype.getOwnerDocument(), "maxLength");
               XmlUtil.createAttribute(e, "value", size);
               
               addChild(datatype, e,  "default", "unique", "required", "minLength");
            }
         }
      }
      dbType.removeAttribute("size");
   }

   private static void convertRequired(Element field, Element dbType, Element datatype) {
      String required = dbType.getAttribute("required");
      if (Boolean.valueOf(required)) {
         Element datatypeUnique = getElementByPath(field, "field.datatype.required");
         if (datatypeUnique == null) {
            Element e = XmlUtil.createElement(datatype.getOwnerDocument(), "required");
            XmlUtil.createAttribute(e, "value", true);
            addChild(datatype, e,  "default", "unique");
         }
      }
      else {
         String notnull = dbType.getAttribute("notnull");
         if (Boolean.valueOf(notnull)) {
            Element datatypeUnique = getElementByPath(field, "field.datatype.required");
            if (datatypeUnique == null) {
               Element e = XmlUtil.createElement(datatype.getOwnerDocument(), "required");
               XmlUtil.createAttribute(e, "value", true);
               addChild(datatype, e,  "default", "unique");
            }
         }
      }
      dbType.removeAttribute("required");
      dbType.removeAttribute("notnull");
   }

   private static void convertUnique(Element field, Element dbType, Element datatype) {
      String key = dbType.getAttribute("key");
      if (Boolean.valueOf(key)) {
         Element datatypeUnique = getElementByPath(field, "field.datatype.unique");
         if (datatypeUnique == null) {
            Element e = XmlUtil.createElement(datatype.getOwnerDocument(), "unique");
            e.setAttribute("value", "true");
            addChild(datatype, e,  "default");
         }
      }
      dbType.removeAttribute("key");
   }

   private static Element createDatatype(Element field, String type) {
      Element datatype = XmlUtil.createChild(field, "datatype", "http://www.mmbase.org/xmlns/datatypes");
      String base = getBaseType(type);
      datatype.setAttribute("base", base);
      datatype.setAttribute("xmlns", "http://www.mmbase.org/xmlns/datatypes");
      return datatype;
   }

   private static String getBaseType(String type) {
      String base = "line";
      if ("INTEGER".equalsIgnoreCase(type)) {
         base = "integer";
      }
      if ("BOOLEAN".equalsIgnoreCase(type)) {
         base = "boolean";
      }
      if ("DATETIME".equalsIgnoreCase(type)) {
         base = "datetime";
      }
      if ("BYTE".equalsIgnoreCase(type)) {
         base = "binary";
      }

      return base;
   }

   private static void addChild(Element parent, Element child, String ... tags) {
      List<String> afterTags = Arrays.asList(tags);
      Element refNode = null;
      List<Element> eList = XmlUtil.getElements(parent);
      if(!eList.isEmpty()) {
         for (Iterator<Element> iterator = eList.iterator(); iterator.hasNext();) {
            Element element = iterator.next();
            if (!afterTags.contains(element.getTagName())) {
               refNode = element;
               break;
            }
         }
      }
      parent.insertBefore(child, refNode);
   }
   
   public static Element getElementByPath(Element e, String path) {
       StringTokenizer st = new StringTokenizer(path,".");
       if (!st.hasMoreTokens()) {
           // faulty path
           System.err.println("No tokens in path");
           return null;
       } else {
           String root = st.nextToken();
           if (!e.getNodeName().equals(root)) {
               // path should start with root element
              System.err.println("path [" + path + "] with root (" + root + ") doesn't start with root element (" + e.getLocalName() + "): incorrect xml file" +
                         "(" + e.getOwnerDocument().getDocumentURI() + ")");
               return null;
           }
           OUTER:
           while (st.hasMoreTokens()) {
               String tag = st.nextToken();
               NodeList nl = e.getChildNodes();
               for(int i = 0; i < nl.getLength(); i++) {
                   if (! (nl.item(i) instanceof Element)) continue;
                   e = (Element) nl.item(i);
                   String tagName = e.getNodeName();
                   if (tagName == null || tagName.equals(tag) || "*".equals(tag)) continue OUTER;
               }
               // Handle error!
               return null;
           }
           return e;
       }
   }

   public static String getNodeTextValue(Node n, boolean trim) {
      NodeList nl = n.getChildNodes();
      StringBuilder res = new StringBuilder();
      for (int i = 0; i < nl.getLength(); i++) {
          Node textnode = nl.item(i);
          if (textnode.getNodeType() == Node.TEXT_NODE) {
              String s = textnode.getNodeValue();
              if (trim) s = s.trim();
              res.append(s);
          } else if (textnode.getNodeType() == Node.CDATA_SECTION_NODE) {
              res.append(textnode.getNodeValue());
          }
      }
      return res.toString();
  }
}
