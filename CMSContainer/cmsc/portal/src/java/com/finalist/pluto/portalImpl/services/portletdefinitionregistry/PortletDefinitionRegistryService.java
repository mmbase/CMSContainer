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

package com.finalist.pluto.portalImpl.services.portletdefinitionregistry;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinitionList;
import org.apache.pluto.om.portlet.PortletDefinition;

import com.finalist.cmsc.services.Service;

/**
 * <P>
 * The <CODE>PortletRegistryService</CODE> interface represents all portlets
 * and portlet applications available in the portal. It is accessed by the
 * datastore layer to get information about the portlets and portlet
 * applications.
 * </P>
 * <P>
 * The interfaces defined in this package represent an abstract object model
 * (OM) that is applicable for different implementations. The abstract OM
 * defines only how the data is stored and accessed in the memory. Each
 * implementation can store the data in different ways.
 * </P>
 * <P>
 * This abstraction layer helps to generalize the portlet container from special
 * implementations like data storage and moreover it is not bound to a special
 * Application Server.
 * </P>
 */
public abstract class PortletDefinitionRegistryService extends Service {

   /**
    * Returns a set containg all portlet application definitions
    * 
    * @return the portlet application definition set
    */
   abstract public PortletApplicationDefinitionList getPortletApplicationDefinitionList();


   /**
    * Returns the portlet definition to the given object id
    * 
    * @param id
    *           ObjectID of the portlet definition
    * @return the portlet definition
    */
   abstract public PortletDefinition getPortletDefinition(ObjectID id);
}
