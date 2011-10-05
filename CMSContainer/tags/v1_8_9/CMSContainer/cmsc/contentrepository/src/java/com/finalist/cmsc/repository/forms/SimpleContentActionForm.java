package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class SimpleContentActionForm extends PagerForm {
   
   private String title;

   
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

}
