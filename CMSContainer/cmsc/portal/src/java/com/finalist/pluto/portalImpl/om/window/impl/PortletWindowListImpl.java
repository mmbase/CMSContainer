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

import java.util.HashMap;
import java.util.Iterator;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.window.PortletWindowListCtrl;

public class PortletWindowListImpl implements PortletWindowList, PortletWindowListCtrl {

   HashMap<String, PortletWindow> windows;


   public PortletWindowListImpl() {
      windows = new HashMap<String, PortletWindow>();
   }


   // PortletWindowList implementation.

   /**
    * Returns the elements of this set
    * 
    * @return An iterator containg all elements
    */
   public Iterator<PortletWindow> iterator() {

      return windows.values().iterator();
   }


   /**
    * Returns the portlet window object of the given id
    * 
    * @param id
    *           the window id.
    * @return the portlet window object or null if the list does not contain a
    *         portlet window with the given id
    */
   public PortletWindow get(ObjectID id) {
      return windows.get(id.toString());
   }


   // PortletWindowListCtrl implementation.

   /**
    * Add a portlet window to the list
    * 
    * @param window
    *           the porlet window to add
    */
   public void add(PortletWindow window) {
      if (window != null) {
         windows.put(window.getId().toString(), window);
      }
   }


   /**
    * Remove the portlet window with the given Id from the list
    * 
    * @param id
    *           the Id of the portlet window which should be removed
    */
   public void remove(ObjectID id) {
      if (id != null) {
         windows.remove(id.toString());
      }
   }
}
