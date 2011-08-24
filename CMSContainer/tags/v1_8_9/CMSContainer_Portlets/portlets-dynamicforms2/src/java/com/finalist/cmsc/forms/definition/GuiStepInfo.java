package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.util.XmlUtil;

public final class GuiStepInfo {

   private String title;
   private GuiDescription description;
   private List<GuiImage> images = new ArrayList<GuiImage>();

   public void addImage(GuiImage image) {
      images.add(image);
   }

   public GuiDescription getDescription() {
      return description;
   }

   public List<GuiImage> getImages() {
      return images;
   }

   public String getTitle() {
      return title;
   }

   public void render(Element root) {
      Element stepinfo = toXml(root);
      stepinfo.setAttribute("title", title);
      if (description != null) {
         description.render(stepinfo);
      }
      if (!images.isEmpty()) {
         Element imagesElement = XmlUtil.createChild(stepinfo, "images");
         for (GuiImage image : images) {
            image.render(imagesElement);
         }
      }
   }


   public void setDescription(GuiDescription description) {
      this.description = description;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Element toXml(Element root) {
      return XmlUtil.createChild(root, "stepinfo");
   }
}
