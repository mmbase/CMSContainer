package com.finalist.cmsc.forms.definition;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public final class GuiImage {

   private String title;
   private String url;
   private String status;

   public void render(Element root) {
      toXml(root);
   }
   
   public void setTitle(String title) {
      this.title = title;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setUrl(String titleUrl) {
      this.url = titleUrl;
   }

   public Element toXml(Element root) {
      Element image = XmlUtil.createChild(root, "image");
      XmlUtil.createAttribute(image, "title", title);
      XmlUtil.createAttribute(image, "url", url);
      XmlUtil.createAttribute(image, "status", status);
      return image;
   }
}
