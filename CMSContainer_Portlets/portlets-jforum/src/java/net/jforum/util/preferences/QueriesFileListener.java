/*
 * Copyright (c) 2003, Rafael Steil
 * 
 * All rights reserved.
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
 * Created on 05/06/2004 14:49:22
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.preferences;

import java.io.IOException;

import net.jforum.util.FileChangeListener;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: QueriesFileListener.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class QueriesFileListener implements FileChangeListener
{
	private static final Logger logger = Logger.getLogger(QueriesFileListener.class);
	
	/** 
	 * @see net.jforum.util.FileChangeListener#fileChanged(java.lang.String)
	 */
	public void fileChanged(String filename)
	{
		try {
			logger.info("Reloading "+ filename);
			SystemGlobals.loadQueries(filename);
			
			String driverQueries = SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER);
			
			// Force reload of driver specific queries
			if (!filename.equals(driverQueries)) {
				SystemGlobals.loadQueries(driverQueries);
			}
		}
		catch (IOException e) {
			logger.info(e);
		}
	}

}
