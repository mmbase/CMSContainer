package com.finalist.cmsc.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class InsertPagePropertyTag extends CmscTag {

   private static Log log = LogFactory.getLog(InsertPagePropertyTag.class);

   // tag parameters
   private String var;
   private String key;
   private String inherit;
   private String page;
   
   
   public String getKey() {
      return key;
   }


   public void setKey(String key) {
      this.key = key;
   }


   public String getPage() {
      return page;
   }


   public void setPage(String page) {
      this.page = page;
   }


   public String getVar() {
      return var;
   }


   public String getInherit() {
      return inherit;
   }

   public void setVar(String var) {
      this.var = var;
   }


   public void setInherit(String inherit) {
      this.inherit = inherit;
   }

   @Override
   public void doTag() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      String value = null;
      Map<String,String> properties = null;
      if (StringUtils.isNotEmpty(page)) {
         properties = getPagePropertiesForPage(page);
      }
      else {
         properties = getPagePropertiesFromPath(getPath());
      }
      if (properties != null && !properties.isEmpty()) {
         value = properties.get(key);
      }
      // handle result
      if (value != null) {
         if (var != null) {
            request.setAttribute(var, value);
         }
         else {
            try {
               ctx.getOut().print(value);
            }
            catch (IOException e) {
               log.error("Unable to write property value to the output: " + e);
            }
         }
      }
      else {
         log.debug("property with name " + key + " not found for path: " + getPath());
      }
   }
   
   private Map<String,String> getPagePropertiesForPage(String pageId) {

      String path = SiteManagement.getPath(Integer.parseInt(pageId), true);
      return getPagePropertiesFromPath(path);
   }
   
   private Map<String,String> getPagePropertiesFromPath(String path) {
      boolean directly = StringUtils.equals(this.inherit, "directly");
      boolean override = StringUtils.equals(this.inherit, "override");
      boolean noInherit = StringUtils.equals(this.inherit, "false");
      Map<String,String> properties = getCurrentPageProperties(path);
      
      if (!noInherit &&((override && properties.size() == 0) || directly)) { // inherit from parent.
         properties.putAll(getPropertiesOfParent(path));
      }
      return properties;
   }
   
   /**
    * Search for images of parent pages and return an image when found.
    * @return the image of a parent page
    */
   private Map<String,String> getPropertiesOfParent(String path) {
      List<Page> pages = SiteManagement.getPagesFromPath(path);
      Map<String, String> properties =  new HashMap<String, String>();
      if (pages.size() > 1) {
         for (int i = pages.size() - 2; i >= 0; i--) {
            if (pages.get(i).getPageProperties().size() > 0)
               properties.putAll(pages.get(i).getPageProperties());
         }
      }
      return properties;
   }


   private Map<String,String> getCurrentPageProperties(String path) {
      List<Page> pages = SiteManagement.getPagesFromPath(path);
      return (pages.get(pages.size() - 1)).getPageProperties();
   }
}
