/*
 * Copyright (c) 2003, Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * This file creation date: Feb 19, 2003 / 8:56:14 PM
 * The JForum Project
 * http://www.jforum.net 
 */
package net.jforum.dao;

import java.util.List;

import net.jforum.entities.Category;

/**
 * Model interface for {@link net.jforum.Category}.
 * This interface defines methods which are expected to be
 * implementd by a specific data access driver. The intention is
 * to provide all functionality needed to update, insert, delete and
 * select some specific data.
 * 
 * @author Rafael Steil
 * @version $Id: CategoryDAO.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public interface CategoryDAO 
{	
	/**
	 * Gets a specific <code>Category</code>.
	 * 
	 * @param categoryId The Category ID to search
	 * @return <code>Category</code>object containing all the information
	 * @throws Exception
	 * @see #selectAll
	 */
	public Category selectById(int categoryId) throws Exception;
	
	/**
	 * Selects all categories data from the database.
	 * 
	 * @return ArrayList with the categories found 
	 * @throws Exception
	 * @see #selectById
	 */
	public List selectAll() throws Exception;
	
	/**
	 * Checks if is possible to delete a specific category.
	 * 
	 * @param categoryId The category ID to verify
	 * @return <code>true</code> if is possible to delete, <code>false</code> if not
	 * @see #delete(int)
	 * @throws Exception
	 */
	public boolean canDelete(int categoryId) throws Exception;
	
	/**
	 * Delete a category.
	 * 
	 * @param categoryId The category ID to delete
	 * @throws Exception
	 * @see #canDelete(int)
	 */
	public void delete(int categoryId) throws Exception;
	
	/**
	 * Updates a category.
	 * 
	 * @param category Reference to a <code>Category</code> object to update
	 * @throws Exception
	 * @see #update(int)
	 */
	public void update(Category category) throws Exception;
	
	/**
	 * Adds a new category.
	 * 
	 * @param category Reference to a valid and configured <code>Category</code> object
	 * @throws Exception
	 */
	public int addNew(Category category) throws Exception;

	/**
	 * Changes the display order of some category.
	 *  
	 * @param category The <code>Category</code> instance to change its order
	 * @return An <code>int</code> containing the id of the category which 
	 * held the display order now represented by the category passed as
	 * argument to the method.
	 * 
	 * @throws Exception
	 * @see #setOrderDown(Category)
	 */
	public void setOrderUp(Category category, Category otherCategory) throws Exception;
	
	/**
	 * Changes the display order of some category.
	 *  
	 * @param category The <code>Category</code> instance to change its order
	 * @return An <code>int</code> containing the id of the category which 
	 * held the display order now represented by the category passed as
	 * argument to the method.
	 * 
	 * @throws Exception
	 * @see #setOrderUp(Category)
	 */
	public void setOrderDown(Category category, Category otherCategory) throws Exception;
}
