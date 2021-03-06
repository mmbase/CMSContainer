/*
 * Copyright 2003,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finalist.pluto.portalImpl.om.window.impl;

import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowCtrl;

import com.finalist.pluto.portalImpl.util.ObjectID;

public class PortletWindowImpl implements PortletWindow, PortletWindowCtrl {

   private ObjectID objectId;

   private PortletEntity portletEntity;


   public PortletWindowImpl(String id) {
      this.setId(id);
   }


   // PortletWindow implementation.

   /**
    * Returns the identifier of this portlet instance window as object id
    *
    * @return the object identifier
    */
   public ObjectID getId() {
      return objectId;
   }


   /**
    * Returns the portlet entity
    *
    * @return the portlet entity
    */
   public PortletEntity getPortletEntity() {
      return portletEntity;
   }


   // PortletWindowCtrl implementation.
   /**
    * binds an identifier to this portlet window
    *
    * @param id
    *           the new identifier
    */
   public final void setId(String id) {
      id = id.toLowerCase();
      id = id.replaceAll("[,\\s]+", "");
      id = id.replaceAll("[^a-zA-Z_0-9-]", "");
      objectId = ObjectID.createFromString(id);
   }


   /**
    * binds a portlet instance to this portlet window
    *
    * @param portletEntity
    *           a portlet entity object
    */
   public void setPortletEntity(PortletEntity portletEntity) {
      this.portletEntity = portletEntity;
   }

}