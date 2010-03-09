package com.finalist.cmsc.resources.forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.util.http.BulkUploadUtil;

/**
 * @author Billy
 * 
 */
public abstract class AbstractUploadAction extends MMBaseAction {

   public static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";

   protected static Set<String> supportedImages;

   protected static final Log log = LogFactory.getLog(AbstractUploadAction.class);
   
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception{

      boolean uploadError = false;
      String uploadedNodes = "";
      int numberOfUploadedNodes = -1;
      int numberOfFailedFiles = -1;

      UploadForm uploadForm = (UploadForm) form;
      FormFile file = uploadForm.getFile();
      
      int fileSize = file.getFileSize();
      if(StringUtils.isBlank(file.getFileName()) || fileSize == 0 ){
         uploadError = true;
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadError=" + uploadError, true);
      }
      if(!BulkUploadUtil.isZipFile(file.getContentType(), file.getFileName()) && !validFileSize(fileSize)){
         uploadError = true;
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadError=" + uploadError, true);
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
         uploadError = true;
         return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadError=" + uploadError, true);
      }

      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadedNodes="
            + uploadedNodes + "&numberOfUploadedNodes="
            + numberOfUploadedNodes + "&numberOfFailedFiles="
            + numberOfFailedFiles, true);
   }
   
   private boolean validFileSize(int fileSize) {
      return BulkUploadUtil.validFileSize(fileSize);
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


   
}