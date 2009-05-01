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

package com.finalist.pluto.portalImpl.aggregation;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.services.Parameters;
import com.finalist.pluto.portalImpl.core.PortalURL;

public interface Fragment {

	/**
	 * Is called to render the fragment. This may be a page, column or even a
	 * portlet.
	 * 
	 * @param request the servlet request
	 * @param response the servlet response
	 * @exception ServletException
	 * @exception IOException
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	/**
	 * Returns the identifier of this fragment. Mostly this is a name visible in
	 * the Portal URL.
	 * 
	 * @return the identifier of this fragment
	 */
	public String getId();

	/**
	 * Returns the parent fragment. If this fragment is the root fragment the
	 * return value is NULL
	 * 
	 * @return the parent fragment or NULL if this is the root fragment
	 */
	public Fragment getParent();

	/**
	 * Returns the initialization parameters of this fragment
	 * 
	 * @return the init parameters
	 */
	public Parameters getInitParameters();

	/**
	 * Returns a collection of all child fragments
	 * 
	 * @return a collection containing objects implementing the interface
	 *         fragment
	 */
	public Collection<Fragment> getChildFragments();

	/**
	 * Adds an child to the fragment
	 * 
	 * @param child the child fragment
	 */
	public void addChild(Fragment child);

	/**
	 * Creates an URL pointing to this fragment
	 * 
	 * @param url the PortalURL object used
	 */
	public void createURL(PortalURL url);

	public String getKey();
}
