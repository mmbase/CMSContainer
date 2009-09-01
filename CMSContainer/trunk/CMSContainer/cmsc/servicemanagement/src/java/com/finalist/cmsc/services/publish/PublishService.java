/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.services.Service;
import com.finalist.cmsc.services.search.Search;

public abstract class PublishService extends Service {

   private static final String SYSTEM_LIVEPATH = "system.livepath";

   public abstract boolean isPublished(Node node);


   public abstract void publish(Node node);


   public abstract void publish(Node node, NodeList nodes);


   public abstract boolean isPublishable(Node node);


   public abstract void remove(Node node);


   public abstract void unpublish(Node node);


   public abstract int getRemoteNumber(Node node);


   public abstract Node getRemoteNode(Node node);


   public abstract Cloud getRemoteCloud(Cloud cloud);


   public String getRemoteContentUrl(Node node) {
      return getRemoteUrl(node);
   }

   public String getRemoteUrl(Node node) {
      if (node == null) return null;
      
      if (PagesUtil.isPageType(node) || SiteUtil.isSite(node)) {

         //Retrieve the site to create its server name
         Node site = NavigationUtil.getPathToRoot(node).get(0); //Get the site of the node.
         String path = NavigationUtil.getPathToRootString(node, false);
         String serverName = site.getStringValue(SiteUtil.REMOTE_FIELD);
         
         return getRemoteNavigationUrl(serverName, path);
      }
      
      if (ContentElementUtil.isContentElement(node)) {
         if (Publish.isPublished(node) && Search.hasContentPages(node)) {
            if (ContentElementUtil.isContentElement(node) && !Search.hasContentPages(node)) {
               return null;
            }
            
            Cloud cloud = node.getCloud();
            int pageNumber = Search.findDetailPageForContent(node).getPageNumber();
            Node pageNode = cloud.getNode(pageNumber);
            
            Node site = NavigationUtil.getPathToRoot(pageNode).get(0);
            String serverName = site.getStringValue(SiteUtil.REMOTE_FIELD);
            
            int remoteNumber = Publish.getRemoteNumber(node);
            String appPath = "/content/" + remoteNumber;
            return getRemoteResourceUrl(serverName, appPath);
            
         }
         return null;
      }
      
      throw new IllegalArgumentException("Node is not a page or a content element; can not proceed.");
   }

   /**
    * @See com.finalist.cmsc.services.publish.Publish#getRemoteResourceUrl
    */
   public String getRemoteResourceUrl(String serverName, String appPath) {
      String livePath = PropertiesUtil.getProperty(SYSTEM_LIVEPATH);
      if (appPath == null) {
         appPath = "";
      }
      
      if (!appPath.startsWith("/")) {
         appPath = "/" + appPath;  
      }

      //Two situations possible:
      //livePath = "http://$servername/application-live/" / "http://www.china.nl/application-live/";
      if (livePath.contains("$")) {
         livePath = livePath.replaceAll("\\$servername", serverName); 
      }
      
      return livePath + appPath;
   }

   /**
    * @See com.finalist.cmsc.services.publish.Publish#getRemoteNavigationUrl(String, String)
    */
   public String getRemoteNavigationUrl(String serverName, String appPath) {
//      String livePath = "http://${servername}/app-live/"; or
//      String livePath = "http://www.4en5mei.nl/app-live/";
      String livePath = PropertiesUtil.getProperty(SYSTEM_LIVEPATH);
      if (appPath == null) {
         appPath = "";
      }
      
      if (!appPath.startsWith("/")) {
         appPath = "/" + appPath;  
      }
      
      String result;
      if (livePath.contains("$")) {
         result = livePath.replaceAll("\\$servername", serverName) + appPath;
      }
      else {
         result = livePath + serverName + appPath;
      }
      return result;
   }


   public abstract boolean inPublishQueue(Node node);
   
}