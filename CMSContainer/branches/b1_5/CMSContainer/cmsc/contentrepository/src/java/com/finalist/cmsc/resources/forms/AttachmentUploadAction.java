/**
 * 
 */
package com.finalist.cmsc.resources.forms;

import org.apache.struts.upload.FormFile;


/**
 * @author Billy
 *
 */
public class AttachmentUploadAction extends AbstractUploadAction {

   private static final String ATTACHMENTS = "attachments";

   @Override
   protected String getNodeManagerName() {
      return ATTACHMENTS;
   }

   @Override
   protected boolean validFileType(FormFile file) {
      return true;
   }

}

