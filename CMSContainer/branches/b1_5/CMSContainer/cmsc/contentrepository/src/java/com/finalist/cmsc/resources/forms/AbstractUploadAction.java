package com.finalist.cmsc.resources.forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.util.http.BulkUploadUtil;

/**
 * @author Billy
 * 
 */
public abstract class AbstractUploadAction extends MMBaseAction {

   public static final String UPLOADED_FILE_MAX_SIZE = "uploaded.file.max.size";

   public static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";

   protected static Set<String> supportedImages;

   protected static final Log log = LogFactory.getLog(AbstractUploadAction.class);

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception{

      boolean fileIsEmpty = false;
      boolean fileTooBig = false;
      boolean fileTypeInvalid = false;
      String uploadedNodes = "";
      int numberOfUploadedNodes = -1;
      int numberOfFailedFiles = -1;

      UploadForm uploadForm = (UploadForm) form;
      FormFile file = uploadForm.getFile();
      
      int fileSize = file.getFileSize();
      if(StringUtil.isEmpty(file.getFileName()) || fileSize == 0 ){
         fileIsEmpty = true;
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?fileIsEmpty=" + fileIsEmpty, true);
      }
      if(!maxFileSizeBiggerThan(fileSize)){
         fileTooBig = true;
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?fileTooBig=" + fileTooBig, true);
      }
      
      NodeManager manager = cloud.getNodeManager(getNodeManagerName());
      if(validFileType(file)){
         List<Integer> nodes = new ArrayList<Integer>();
         List<String> uploadedFileList = new ArrayList<String>();
         List<String> failedFileList = new ArrayList<String>();
         BulkUploadUtil.store(cloud, manager, file, nodes, uploadedFileList, failedFileList);
         uploadedNodes = BulkUploadUtil.convertToCommaSeparated(nodes);
         numberOfUploadedNodes = nodes.size();    
         numberOfFailedFiles = failedFileList.size();
         
         request.getSession().setAttribute("uploadedFiles", uploadedFileList);
         request.getSession().setAttribute("failedFiles", failedFileList);
      } else {
         fileTypeInvalid = true;
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?fileTypeInvalid=" + fileTypeInvalid, true);
      }

      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadedNodes="
            + uploadedNodes + "&numberOfUploadedNodes="
            + numberOfUploadedNodes + "&numberOfFailedFiles="
            + numberOfFailedFiles, true);
   }
   
   protected abstract String getNodeManagerName();
   
   protected abstract boolean validFileType(FormFile file);

   protected static void initSupportedImages() {
      supportedImages = new HashSet<String>();
      Properties properties = new Properties();
      String images = ".bmp,.jpg,.jpeg,.gif,.png,.svg,.tiff,.tif";
      try {
         properties.load(BulkUploadUtil.class.getResourceAsStream(CONFIGURATION_RESOURCE_NAME));
         images = (String) properties.get("supportedImages");
      }
      catch (IOException ex) {
         log.warn("Could not load properties from " + CONFIGURATION_RESOURCE_NAME
               + ", using defaults", ex);
      }
      for (String image : images.split(",")) {
         supportedImages.add(image.trim());
      }
   }

   protected static boolean isImage(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return false;
      }
      if (supportedImages == null) {
         initSupportedImages();
      }
      return supportedImages.contains(getFilenameExtension(fileName).toLowerCase());
   }

   protected static String getFilenameExtension(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return null;
      }
      int index = fileName.lastIndexOf('.');
      if (index < 0) {
         return null;
      }
      return fileName.substring(index);
   }

   public boolean maxFileSizeBiggerThan(int fileSize) {
      int maxFileSize = 8 * 1024 * 1024; // Default value of 16MB
      try {
         if(!StringUtil.isEmpty(PropertiesUtil.getProperty(UPLOADED_FILE_MAX_SIZE))){
            maxFileSize = Integer.parseInt(PropertiesUtil.getProperty(UPLOADED_FILE_MAX_SIZE)) * 1024 * 1024;
         }         
         // check invalid value of UPLOADED_FILE_MAX_SIZE
         if (maxFileSize <= 0) {
            // PropertiesUtil.setProperty(UPLOADED_FILE_MAX_SIZE, "8");
            maxFileSize = 8 * 1024 * 1024; // set default value of 16MB
         }
      }
      catch (NumberFormatException e) {
         log.warn("System property '" + UPLOADED_FILE_MAX_SIZE
               + "' is not set. Please add it (units = MB).");
      }
      return (fileSize <= maxFileSize);
   }
   
}
