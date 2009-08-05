package com.finalist.cmsc.repository.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class SimpleContentActionForm extends PagerForm {
   
   private String title;
   private String type;
   private int directionFinished = 1;
   private String orderFinished;
   private String offsetFinished;
   
   public String getTitle() {
      return title;
   }

   public int getDirectionFinished() {
      return directionFinished;
   }

   public void setDirectionFinished(int directionFinished) {
      this.directionFinished = directionFinished;
   }

   public String getOrderFinished() {
      return orderFinished;
   }

   public void setOrderFinished(String orderFinished) {
      this.orderFinished = orderFinished;
   }

   public String getOffsetFinished() {
      return offsetFinished;
   }

   public void setOffsetFinished(String offsetFinished) {
      this.offsetFinished = offsetFinished;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }
}
