/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.util.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.transformers.ByteToCharTransformer;
import org.mmbase.util.transformers.ChecksumFactory;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.util.UploadUtil;

public class BulkUploadUtil {

   private static final Log log = LogFactory.getLog(BulkUploadUtil.class);

   private static final int MAXSIZE = 16 * 1024 * 1024;

   public static final String UPLOADED_FILE_MAX_SIZE = "uploaded.file.max.size";
   
   private static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";

   private static final String ZIP_MIME_TYPES[] = new String[] { "application/x-zip-compressed", "application/zip",
         "\"application/octet-stream", "application/x-zip" };

   private static Set<String> supportedImages;

   private static void initSupportedImages() {
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

   public static String convertToCommaSeparated(List<?> values) {
      StringBuffer buffer = new StringBuffer();
      for (Object obj : values) {
         if (obj != null) {
            buffer.append(obj.toString());
            buffer.append(",");
         }
      }
      return buffer.toString();
   }

   public static List<Integer> uploadAndStore(NodeManager manager, HttpServletRequest request) {
      List<UploadUtil.BinaryData> binaries = UploadUtil.uploadFiles(request, MAXSIZE);
      ArrayList<Integer> nodes = new ArrayList<Integer>();

      for (UploadUtil.BinaryData binary : binaries) {
         if (log.isDebugEnabled()) {
            log.debug("originalFileName: " + binary.getOriginalFileName());
            log.debug("contentType: " + binary.getContentType());
         }

         if (isZipFile(binary.getContentType(), binary.getOriginalFileName())) {
            log.debug("unzipping content");
            nodes.addAll(createNodesInZip(manager, new ZipInputStream(binary.getInputStream())));
         } else {
            Node node = createNode(manager, binary.getOriginalFileName(), binary.getInputStream(), binary.getLength());
            if (node != null) {
               nodes.add(node.getNumber());
            }
         }
      }
      return nodes;
   }

   public static void store(Cloud cloud, NodeManager manager, FormFile file, List<Integer> nodes, List<String> uploadedFileList, List<String> failedFileList) {
      try {
         if (isZipFile(file.getContentType(), file.getFileName())) {

            InputStream in = file.getInputStream();
            InputStream is = new BufferedInputStream(in);
            ZipInputStream zip = new ZipInputStream(is);

            createNodesInZip(manager, zip, cloud, nodes, uploadedFileList, failedFileList);
         } else {
            if(isNewFile(manager, file)){
               Node node = createNode(manager, file.getFileName(), file.getInputStream(), file.getFileSize());
               if (node != null) {
                  nodes.add(node.getNumber());
                  uploadedFileList.add(file.getFileName());
               } else {
                  failedFileList.add(file.getFileName());
               }
             } else {
                failedFileList.add(file.getFileName());
             }
         }
      } catch (Exception ex) {
         log.error("Failed to read uploaded file", ex);
      } finally {
         file.destroy();
      }
   }

   public static boolean validFileSize(int fileSize) {
      int maxFileSize = MAXSIZE;
      try {
         if(StringUtils.isNotBlank(PropertiesUtil.getProperty(UPLOADED_FILE_MAX_SIZE))){
            maxFileSize = Integer.parseInt(PropertiesUtil.getProperty(UPLOADED_FILE_MAX_SIZE)) * 1024 * 1024;
         }         
         // check invalid value of UPLOADED_FILE_MAX_SIZE
         if (maxFileSize <= 0) {
            maxFileSize = MAXSIZE; // set default value of 16MB
         }
      }
      catch (NumberFormatException e) {
         log.warn("System property '" + UPLOADED_FILE_MAX_SIZE + "' is not set. Please add it (units = MB).");
      }
      return (fileSize <= maxFileSize);
   }
   
   public static boolean isZipFile(String contentType, String fileName) {

      for (String element : ZIP_MIME_TYPES) {
         if (element.equalsIgnoreCase(contentType)) {
            return true;
         }
      }
      
      //Sometimes browsers don't return a nice mime-type (for example application/octet-stream)
      //So checking on extension might be a good idea too.
      if (getExtension(fileName).equalsIgnoreCase(".zip")) {
         return true;
      }
      
      return false;
   }

   private static Node createNode(NodeManager manager, String fileName, InputStream in, long length) {
      if (length > manager.getField("handle").getMaxLength()) {
         return null;
      }
      Node node = manager.createNode();
      node.setValue("title", fileName);
      node.setValue("filename", fileName);
      node.setInputStreamValue("handle", in, length);
      node.commit();

      return node;
   }

   private static ArrayList<Integer> createNodesInZip(NodeManager manager, ZipInputStream zip) {
      ZipEntry entry = null;
      int count = 0;
      ArrayList<Integer> nodes = new ArrayList<Integer>();

      try {
         while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
               continue;
            }
            if ("images".equals(manager.getName()) && !isImage(entry.getName())) {
               if (log.isDebugEnabled()) {
                  log.debug("Skipping " + entry.getName() + " because it is not an image");
               }
               continue;
            }
            if (log.isDebugEnabled()) {
               log.debug("reading file (from ZIP): '" + entry.getName() + "'");
            }
            count++;
            // create temp file for zip entry, create a node from it and
            // remove the temp file
            File tempFile = File.createTempFile("cmsc", null);
            FileOutputStream out = new FileOutputStream(tempFile);
            copyStream(zip, out);
            zip.closeEntry();
            out.close();
            FileInputStream in = new FileInputStream(tempFile);
            Node node = createNode(manager, entry.getName(), in, tempFile.length());
            if (node != null) {
               nodes.add(node.getNumber());
            }
            in.close();
            tempFile.delete();
         }

      } 
      catch (IOException ex) {
         log.info("Failed to read uploaded zipfile, skipping it" + ex.getMessage());
      }
      finally {
         close(zip);
      }
      return nodes;
   }

   private static void createNodesInZip(NodeManager manager, ZipInputStream zip,
         Cloud cloud, List<Integer> nodes, List<String> uploadedFileList, List<String> failedFileList) {

      ZipEntry entry = null;
      int count = 0;

      try {
         while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
               continue;
            }
            if ("images".equals(manager.getName()) && !isImage(entry.getName())) {
               if (log.isDebugEnabled()) {
                  log.debug("Skipping " + entry.getName() + " because it is not an image");
               }
               continue;
            }
            count++;
            long size = entry.getSize();
            if(!validFileSize((int) size)){
               failedFileList.add(entry.getName());
               continue;
            }
            
            ByteArrayOutputStream fileData = readFromZip(zip);
            boolean isNewFile = isNewFile(manager, fileData);
            
            InputStream is = new ByteArrayInputStream(fileData.toByteArray());
            if (isNewFile) {
               Node node = createNode(manager, entry.getName(), is, size);
               if (node != null) {
                  nodes.add(node.getNumber());
               }
               is.close();
               uploadedFileList.add(entry.getName());
            }
            else {
               failedFileList.add(entry.getName());
            }
            
            zip.closeEntry();
         }
      } catch (IOException ex) {
         log.error("IOException--Failed to read uploaded zipfile, skipping it", ex);
      } catch (Exception e) {
         log.error("Failed to read uploaded zipfile, skipping it", e);
      } finally {
         close(zip);
      }
   }
   
   private static boolean isNewFile(NodeManager manager, ByteArrayOutputStream fileData){
      ChecksumFactory checksumFactory = new ChecksumFactory();
      ByteToCharTransformer transformer = (ByteToCharTransformer) checksumFactory
            .createTransformer(checksumFactory.createParameters());
      String checkSum = transformer.transform(fileData.toByteArray());
      NodeQuery query = manager.createQuery();
      SearchUtil.addEqualConstraint(query, manager.getField("checksum"), checkSum);
      NodeList assets = query.getList();
      return (assets.size() == 0);
   }
   
   public static boolean isNewFile(NodeManager manager, FormFile file) {
      ChecksumFactory checksumFactory = new ChecksumFactory();
      ByteToCharTransformer transformer = (ByteToCharTransformer) checksumFactory
            .createTransformer(checksumFactory.createParameters());
      String checkSum = null;
      try {
         checkSum = transformer.transform(file.getFileData());
      }
      catch (FileNotFoundException e) {
         log.warn("Uploading file is not found!");
         e.printStackTrace();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      NodeQuery query = manager.createQuery();
      SearchUtil.addEqualConstraint(query, manager.getField("checksum"), checkSum);
      NodeList assets = query.getList();
      return (assets.size() == 0);
   }
   
   private static ByteArrayOutputStream readFromZip(ZipInputStream zip){
      ByteArrayOutputStream fileData = new ByteArrayOutputStream();
      byte[] buffer = new byte[2048 * 1024];
      int len = 0;
      try {
         while ((len = zip.read(buffer)) > 0) {
            fileData.write(buffer, 0, len);
         }
      }
      catch (IOException ex) {
         log.error("IOException--Failed to read uploaded zipfile, skipping it", ex);
      }
      return fileData;
   }
   
   private static void close(InputStream stream) {
      try {
         stream.close();
      } catch (IOException ignored) {
         // ignored
      }
   }

   public static boolean isImage(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return false;
      }
      if (supportedImages == null) {
         initSupportedImages();
      }
      
      return supportedImages.contains(getExtension(fileName).toLowerCase());
   }

   public static String getExtension(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return null;
      }
      int index = fileName.lastIndexOf('.');
      if (index < 0) {
         return null;
      }
      return fileName.substring(index);
   }

   public static long copyStream(final InputStream ins, final OutputStream outs) throws IOException {
      final int bufferSize = 8 * 1024;
      byte[] writeBuffer = new byte[bufferSize];
      long size = 0;
      BufferedOutputStream bos = new BufferedOutputStream(outs, bufferSize);
      int bufferRead;
      while ((bufferRead = ins.read(writeBuffer)) != -1) {
         bos.write(writeBuffer, 0, bufferRead);
         size += bufferRead;
      }
      bos.flush();
      bos.close();
      outs.flush();
      return size;
   }

   public static void main(String[] args) {
      System.out.println(isImage(getExtension("test.jpg")));
      System.out.println(isImage(getExtension(".jpg")));
      System.out.println(isImage(getExtension("test")));
      System.out.println(isImage(getExtension("test.")));
      System.out.println(isImage(getExtension("")));
      System.out.println(isImage(getExtension("test.jpeg")));
      System.out.println(isImage(getExtension("test.gif")));
      System.out.println(isImage(getExtension("test.txt")));
      System.out.println(isImage(getExtension("test.bummer")));
      System.out.println(isImage(""));
      System.out.println(isImage(" "));

      //Also test the isZipFile method
      System.out.println(isZipFile("content","helloworld.zip")); //Should be true
      System.out.println(isZipFile("application/x-zip-compressed","helloworld.zipper")); //Should be true
      System.out.println(isZipFile("content","helloworld.zipper")); //Should be false
   }
}