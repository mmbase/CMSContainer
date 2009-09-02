/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.*;

import com.finalist.cmsc.services.ServiceManager;

public class Publish {

   private final static PublishService cService = (PublishService) ServiceManager.getService(PublishService.class);


   public static boolean isPublished(Node node) {
      return cService.isPublished(node);
   }


   public static void publish(Node node) {
      cService.publish(node);
   }


   public static void publish(Node node, NodeList nodes) {
      cService.publish(node, nodes);
   }


   public static boolean isPublishable(Node node) {
      return cService.isPublishable(node);
   }


   public static void remove(Node node) {
      cService.remove(node);
   }


   public static void unpublish(Node node) {
      cService.unpublish(node);
   }


   public static int getRemoteNumber(Node node) {
      return cService.getRemoteNumber(node);
   }

   public static Node getRemoteNode(Node node) {
       return cService.getRemoteNode(node);
   }

   public static String getRemoteContentUrl(Node node) {
       return cService.getRemoteContentUrl(node);
   }

   public static String getRemoteUrl(Node node) {
      return cService.getRemoteUrl(node);
   }
   
   /**
    * Calculates the remote URL of a given resource, for example an image.
    * For this to work, the system.livepath property has to be set in either of
    * these two ways: "http://$servername/application-live/" OR "http://www.china.nl/application-live/";
    * @param serverName - this will be replaced by the $servername in the system.livepath
    * @param path - this will be added to the returned URL, for example: http://www.china.nl/application-live/mmbase/image/12345
    * @return - Full URL as String to the remote resource 
    */
   public static String getRemoteResourceUrl(String serverName, String path) {
      return cService.getRemoteResourceUrl(serverName, path);
   }

   public static Cloud getRemoteCloud(Cloud cloud) {
      return cService.getRemoteCloud(cloud);
   }

   public static String getRemoteNavigationUrl(String serverName, String appPath) {
      return cService.getRemoteNavigationUrl(serverName, appPath);
   }
   
   public static boolean inPublishQueue(Node node) {
      return cService.inPublishQueue(node);
   }
}
