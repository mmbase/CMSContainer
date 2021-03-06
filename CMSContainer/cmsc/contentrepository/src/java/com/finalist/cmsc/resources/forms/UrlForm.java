package com.finalist.cmsc.resources.forms;

@SuppressWarnings("serial")
public class UrlForm extends SearchForm {

   private String title;
   private String description;
   private String url;
   private String valid;


   public UrlForm() {
      super("urls", "title");
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String name) {
      this.title = name;
   }


   public String getUrl() {
      return url;
   }


   public void setUrl(String url) {
      this.url = url;
   }


   public String getValid() {
      return valid;
   }


   public void setValid(String valid) {
      this.valid = valid;
   }

}
