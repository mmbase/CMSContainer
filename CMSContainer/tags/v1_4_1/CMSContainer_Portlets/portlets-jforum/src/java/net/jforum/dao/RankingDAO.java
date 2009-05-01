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
 * This file creating date: Feb 19, 2003 / 8:57:06 PM
 * The JForum Project
 * http://www.jforum.net 
 */
package net.jforum.dao;

import java.util.List;

import net.jforum.entities.Ranking;

/**
 * Model interface for {@link net.jforum.Ranking}.
 * This interface defines methods which are expected to be
 * implementd by a specific data access driver. The intention is
 * to provide all functionality needed to update, insert, delete and
 * select some specific data.
 * 
 * @author Rafael Steil
 * @version $Id: RankingDAO.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public interface RankingDAO 
{
	/**
	 * Gets a specific <code>Ranking</code>.
	 * 
	 * @param rankingId The ranking ID to search
	 * @return <code>Ranking</code>object containing all the information
	 * @throws Exception
	 * @see #selectAll
	 */
	public Ranking selectById(int rankingId) throws Exception;
	
	/**
	 * Selects all ranking data from the database.
	 * 
	 * @return ArrayList with the rankings. Each entry is a <code>Ranking</code> object 
	 * @throws Exception
	 * @see #selectById
	 */
	public List selectAll() throws Exception;
	

	/**
	 * Delete a ranking.
	 * 
	 * @param rankingId The ranking ID to delete
	 * @throws Exception
	 * @see #canDelete(int)
	 */
	public void delete(int rankingId) throws Exception;
	

	/**
	 * Updates a ranking.
	 * 
	 * @param ranking Reference to a <code>Ranking</code> object to update
	 * @throws Exception
	 * @see #update(int)
	 */
	public void update(Ranking ranking) throws Exception;
	
	/**
	 * Adds a new ranking.
	 * 
	 * @param ranking Reference to a valid and configured <code>Ranking</code> object
	 * @throws Exception
	 */
	public void addNew(Ranking ranking) throws Exception;
}
