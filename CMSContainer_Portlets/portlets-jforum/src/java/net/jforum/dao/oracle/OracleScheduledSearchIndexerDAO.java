/*
 * Copyright (c) Rafael Steil
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
 * Created on Mar 12, 2005 4:42:31 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.oracle;

import java.sql.Connection;
import java.sql.ResultSet;

import java.util.List;


/**
 * @author Rafael Steil
 * @version $Id: OracleScheduledSearchIndexerDAO.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class OracleScheduledSearchIndexerDAO extends net.jforum.dao.generic.GenericScheduledSearchIndexerDAO
{
	/**
	 * @see net.jforum.dao.generic.GenericScheduledSearchIndexerDAO#getPostTextFromResultSet(java.sql.ResultSet)
	 */
	protected String getPostTextFromResultSet(ResultSet rs) throws Exception
	{
		return OracleUtils.readBlobUTF16BinaryStream(rs, "post_text");
	}
	
	/**
	 * @see net.jforum.dao.generic.GenericScheduledSearchIndexerDAO#getPosts(int, int, int, int, java.sql.Connection)
	 */
	public List getPosts(int start, int count, int minPostId, int maxPostId, Connection conn) throws Exception
	{
		return super.getPosts(start, start + count, minPostId, maxPostId, conn);
	}
}
