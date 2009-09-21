/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.Service;

public abstract class PublishService extends Service {

   private static final String SYSTEM_LIVEPATH = "system.livepath";

   public abstract boolean isPublished(Node node);


   public abstract void publish(Node node);


   public abstract void publishRelations(Node node, NodeList nodes);


   public abstract boolean isPublishable(Node node);


   public abstract void remove(Node node);


   public abstract void unpublish(Node node);


   public abstract int getRemoteNumber(Node node);


   public abstract Node getRemoteNode(Node node);


   public abstract Cloud getRemoteCloud(Cloud cloud);


   protected String getRemoteContentUrl(Node node) {
      return getRemoteUrl(node);
   }

   public abstract String getRemoteUrl(Node node);
   
   /**
    * @See com.finalist.cmsc.services.publish.Publish#getRemoteResourceUrl
    */
   protected String getRemoteResourceUrl(String serverName, String appPath) {
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
   protected String getRemoteNavigationUrl(String serverName, String appPath) {
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


   public abstract void updateUser(Node userNode, String password);
   
}