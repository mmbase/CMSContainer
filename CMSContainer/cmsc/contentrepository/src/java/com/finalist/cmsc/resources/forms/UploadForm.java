package com.finalist.cmsc.resources.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

@SuppressWarnings("serial")
public class UploadForm extends ActionForm {

   private FormFile file;

   public FormFile getFile() {
      return file;
   }

   public void setFile(FormFile file) {
      this.file = file;
   }

}
