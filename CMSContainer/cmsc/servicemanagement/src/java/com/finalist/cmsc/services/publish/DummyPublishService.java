/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.*;

public class DummyPublishService extends PublishService {

   @Override
   public boolean isPublishable(Node node) {
      return false;
   }


   @Override
   public boolean isPublished(Node node) {
      return true;
   }


   @Override
   public void publish(Node node) {
      // nothing
   }


   @Override
   public void publishRelations(Node node, NodeList nodes) {
      // nothing
   }


   @Override
   public void remove(Node node) {
      // nothing
   }


   @Override
   public void unpublish(Node node) {
      // nothing
   }


   @Override
   public int getRemoteNumber(Node node) {
      return node.getNumber();
   }


   @Override
   public Node getRemoteNode(Node node) {
       return null;
   }


   @Override
   public Cloud getRemoteCloud(Cloud cloud) {
      return null;
   }
   
   public String getRemoteUrl(Node node) {
      //TODO Implement this function outside this servicemanagement for single war situations.
      return null;
   }

   @Override
   public boolean inPublishQueue(Node node) {
      return false;
   }


   @Override
   public void updateUser(Node userNode, String password) {
   }
}
