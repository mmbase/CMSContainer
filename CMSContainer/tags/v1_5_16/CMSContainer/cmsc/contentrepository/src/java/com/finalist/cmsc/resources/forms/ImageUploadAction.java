/**
 * 
 */
package com.finalist.cmsc.resources.forms;

import org.apache.struts.upload.FormFile;

import com.finalist.util.http.BulkUploadUtil;



/**
 * @author Billy
 *
 */
public class ImageUploadAction extends AbstractUploadAction {

   private static final String IMAGES = "images";

   @Override
   protected String getNodeManagerName() {
      return IMAGES;
   }

   @Override
   protected boolean validFileType(FormFile file) {
      return isImage(file.getFileName()) || BulkUploadUtil.isZipFile(file.getContentType(), file.getFileName());
   }

}

