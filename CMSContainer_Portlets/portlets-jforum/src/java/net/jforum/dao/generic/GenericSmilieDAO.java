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
 * This file creation date: 13/01/2004 / 12:02:54
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.entities.Smilie;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericSmilieDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericSmilieDAO extends AutoKeys implements net.jforum.dao.SmilieDAO {

	/** 
	 * @see net.jforum.repository.SmilieDAO#addNew(net.jforum.entities.Smilie)
	 */
	public int addNew(Smilie smilie) throws Exception 
	{
		PreparedStatement p = this.getStatementForAutoKeys("SmiliesModel.addNew");
		
		p.setString(1, smilie.getCode());
		p.setString(2, smilie.getUrl());
		p.setString(3, smilie.getDiskName());
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("SmiliesModel.lastGeneratedSmilieId"));
		int id = this.executeAutoKeysQuery(p);

		p.close();
		return id;
	}

	/** 
	 * @see net.jforum.repository.SmilieDAO#delete(int)
	 */
	public void delete(int id) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.delete"));
		p.setInt(1, id);
		p.executeUpdate();
		
		p.close();
	}

	/** 
	 * @see net.jforum.repository.SmilieDAO#update(net.jforum.entities.Smilie)
	 */
	public void update(Smilie smilie) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.update"));
		p.setString(1, smilie.getCode());
		p.setString(2, smilie.getUrl());
		p.setString(3, smilie.getDiskName());
		p.setInt(4, smilie.getId());
		
		p.executeUpdate();
		p.close();
	}
	
	private Smilie getSmilie(ResultSet rs) throws SQLException
	{
		Smilie s = new Smilie();
		
		s.setId(rs.getInt("smilie_id"));
		s.setCode(rs.getString("code"));
		s.setUrl(rs.getString("url"));
		s.setDiskName(rs.getString("disk_name"));
		
		return s;
	}

	/** 
	 * @see net.jforum.repository.SmilieDAO#selectAll()
	 */
	public List selectAll() throws Exception 
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.selectAll"));
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getSmilie(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}

	/** 
	 * @see net.jforum.dao.SmilieDAO#selectById(int)
	 */
	public Smilie selectById(int id) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.selectById"));
		p.setInt(1, id);
		
		Smilie s = new Smilie();
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			s = this.getSmilie(rs);
		}
		
		rs.close();
		p.close();
		
		return s;
	}
}
