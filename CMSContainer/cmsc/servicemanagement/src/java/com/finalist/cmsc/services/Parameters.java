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
package com.finalist.cmsc.services;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class Parameters extends NameValuePairs {

   public Parameters(Map<String, String> params) {
      Iterator<String> iterator = params.keySet().iterator();

      while (iterator.hasNext()) {
         String name = iterator.next();

         super.add(name, params.get(name));
      }
   }


   public Parameters(ServletConfig aConfig) {
      for (Enumeration<String> e = aConfig.getInitParameterNames(); e.hasMoreElements();) {
         String name = e.nextElement();

         super.add(name, aConfig.getInitParameter(name));
      }
   }


   public Parameters(ServletContext aContext) {
      for (Enumeration<String> e = aContext.getInitParameterNames(); e.hasMoreElements();) {
         String name = e.nextElement();

         super.add(name, aContext.getInitParameter(name));
      }
   }


   public void setString(String aKey, String aValue) {
      if (aKey == null)
         throw (new IllegalArgumentException("Parameters: Argument \"aKey\" cannot be null."));
      if (aValue == null)
         throw (new IllegalArgumentException("Parameters: Argument \"aValue\" cannot be null."));

      super.add(aKey, aValue);
   }


   /**
    * * Removes all values with the given name. * *
    * 
    * @param aName *
    *           the name of a pair
    */

   public void remove(String aName) {
      super.removeEntry(aName);
   }


   /**
    * * Removes all values with names that start with the given prefix. * *
    * 
    * @param aPrefix *
    *           the name prefix
    */

   public void removeWithPrefix(String aPrefix) {
      List<String> deletables = new ArrayList<String>();

      for (Iterator<String> iter = names(); iter.hasNext();) {
         String name = iter.next();

         if (name.startsWith(aPrefix)) {
            deletables.add(name);
         }
      }

      for (String string : deletables) {
         super.removeEntry(string);
      }
   }
}
