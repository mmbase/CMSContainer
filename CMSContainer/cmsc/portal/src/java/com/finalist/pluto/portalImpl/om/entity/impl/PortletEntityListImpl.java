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
/* 

 */

package com.finalist.pluto.portalImpl.om.entity.impl;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityList;
import org.apache.pluto.om.entity.PortletEntityListCtrl;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class PortletEntityListImpl extends AbstractSupportSet implements PortletEntityList, PortletEntityListCtrl,
      Serializable, Support {

   // PortletEntityList implementation.

   public PortletEntity get(ObjectID objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletEntity portletEntity = (PortletEntity) iterator.next();
         if (portletEntity.getId().equals(objectId)) {
            return portletEntity;
         }
      }
      return null;
   }


   // PortletEntityListCtrl implementation.

   public PortletEntity add(PortletApplicationEntity appEntity, String definitionId) {
      PortletEntityImpl entity = new PortletEntityImpl();

      int id = -1;
      for (Iterator iter = iterator(); iter.hasNext();) {
         PortletEntityImpl ent = (PortletEntityImpl) iter.next();
         // TODO no more castor woutz
         // try {
         // id = Math.max(id, Integer.parseInt(ent.getCastorId()));
         // } catch (NumberFormatException e) {
         // // don't care
         // }
      }
      entity.setId(Integer.toString(++id));
      entity.setDefinitionId(definitionId);
      entity.setPortletApplicationEntity(appEntity);

      add(entity);

      return entity;
   }


   // additional methods.

   public PortletEntity get(String objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletEntity portletEntity = (PortletEntity) iterator.next();
         if (portletEntity.getId().equals(objectId)) {
            return portletEntity;
         }
      }
      return null;
   }
}
