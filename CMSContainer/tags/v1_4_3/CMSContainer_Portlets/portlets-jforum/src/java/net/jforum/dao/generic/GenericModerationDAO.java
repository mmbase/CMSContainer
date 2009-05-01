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
 * Created on Jan 30, 2005 11:38:30 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.ModerationDAO;
import net.jforum.entities.ModerationPendingInfo;
import net.jforum.entities.Post;
import net.jforum.entities.TopicModerationInfo;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericModerationDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericModerationDAO implements ModerationDAO
{
	/**
	 * @see net.jforum.dao.ModerationDAO#aprovePost(int)
	 */
	public void aprovePost(int postId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("ModerationModel.aprovePost"));
		p.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		p.setInt(2, postId);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see net.jforum.dao.ModerationDAO#topicsByForum(int)
	 */
	public Map topicsByForum(int forumId) throws Exception
	{
		Map m = new HashMap();
		
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("ModerationModel.topicsByForum"));
		p.setInt(1, forumId);
		
		int lastId = 0;
		TopicModerationInfo info = null;
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("topic_id");
			if (id != lastId) {
				lastId = id;
				
				if (info != null) {
					m.put(new Integer(info.getTopicId()), info);
				}
				
				info = new TopicModerationInfo();
				info.setTopicId(id);
				info.setTopicReplies(rs.getInt("topic_replies"));
				info.setTopicTitle(rs.getString("topic_title"));
			}
			
			info.addPost(this.getPost(rs));
		}
		
		if (info != null) {
			m.put(new Integer(info.getTopicId()), info);
		}
		
		rs.close();
		p.close();
		
		return m;
	}
	
	protected Post getPost(ResultSet rs) throws Exception
	{
		Post p = new Post();
		p.setPostUsername(rs.getString("username"));
		p.setId(rs.getInt("post_id"));
		p.setUserId(rs.getInt("user_id"));
		p.setBbCodeEnabled(rs.getInt("enable_bbcode") == 1);
		p.setHtmlEnabled(rs.getInt("enable_html") == 1);
		p.setSmiliesEnabled(rs.getInt("enable_smilies") == 1);
		p.setSubject(rs.getString("post_subject"));
		p.setText(this.getPostTextFromResultSet(rs));
		
		return p;
	}
	
	protected String getPostTextFromResultSet(ResultSet rs) throws Exception
	{
		return rs.getString("post_text");
	}
	
	/**
	 * @see net.jforum.dao.ModerationDAO#categoryPendingModeration()
	 */
	public List categoryPendingModeration() throws Exception
	{
		List l = new ArrayList();
		int lastId = 0;
		ModerationPendingInfo info = null;
		Statement s = JForumExecutionContext.getConnection().createStatement();
		
		ResultSet rs = s.executeQuery(SystemGlobals.getSql("ModerationModel.categoryPendingModeration"));
		while (rs.next()) {
			int id = rs.getInt("categories_id");
			if (id != lastId) {
				lastId = id;
				
				if (info != null) {
					l.add(info);
				}
				
				info = new ModerationPendingInfo();
				info.setCategoryName(rs.getString("title"));
				info.setCategoryId(id);
			}
			
			info.addInfo(rs.getString("forum_name"), 
					rs.getInt("forum_id"), 
					rs.getInt("total"));
		}
		
		if (info != null) {
			l.add(info);
		}
		
		rs.close();
		s.close();
		
		return l;
	}
	
}
