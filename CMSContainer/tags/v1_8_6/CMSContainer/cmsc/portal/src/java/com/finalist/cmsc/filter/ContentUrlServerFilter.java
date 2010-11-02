/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * 
 */
public class ContentUrlServerFilter implements Filter {

   private static final Log log = LogFactory.getLog(ContentUrlServerFilter.class);
    
   private static final String CONTENTURL_PATTERN = "\"(/[-a-zA-Z0-9/]*?/content/[0-9]*?/[-_a-zA-Z0-9]*?)\"";
   private static final int REPLACE_GROUP = 1;
   private static final String SERVER_URL_PARAMETER = "?server=";
   
   private static final Pattern PATTERN = Pattern.compile(CONTENTURL_PATTERN);
   
   private Pattern excludePattern;
   
   /**
    * 
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {

      String path = getPath((HttpServletRequest)request);
      if (path == null || excludePattern == null || !excludePattern.matcher(path).find()) {

	   	PrintWriter out = response.getWriter();

	   	CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse)response);
	   	chain.doFilter(request, wrapper);
	   	if(wrapper.getContentType() != null && wrapper.getContentType().startsWith("text/html")) {
	   	   String output = wrapper.toString(); 
	   	   Matcher m = PATTERN.matcher(output);
	   	   
	   	   int lastIndex = 0;
	   	   StringBuilder newOutput = null;
	   	   while(m.find()) {
	   	   	if(newOutput == null) {
	   	   		newOutput = new StringBuilder();
	   	   	}
	   	   	String contentUrl = m.group(REPLACE_GROUP);
	   	   	int to = m.start(REPLACE_GROUP);
	   	   	newOutput.append(output.substring(lastIndex, to));
	   	   	newOutput.append(contentUrl);
	   	   	newOutput.append(SERVER_URL_PARAMETER);
	   	   	newOutput.append(getServer((HttpServletRequest)request));
	   	   	lastIndex = m.end(REPLACE_GROUP);
	   	   }
	   	   
	   	   if(newOutput != null) {
	   	   	newOutput.append(output.substring(lastIndex, output.length()));
	   	   	output = newOutput.toString();
	   	   }
	   	   response.setContentLength(output.length());
	   	   out.write(output);
	   	} else {
	   	   out.write(wrapper.toString());
	   	}
	   	out.close();
   	}
   	else {
	   	chain.doFilter(request, response);
   	}
   }

	private String getServer(HttpServletRequest request) {
		PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
		PortalURL currentURL = env.getRequestedPortalURL();
		String path = currentURL.getGlobalNavigationAsString();
		return (path.indexOf("/") != -1)?(path.substring(0, path.indexOf("/"))):path;
	}

	public void destroy() {
		// Do nothing
	}

	public void init(FilterConfig config) throws ServletException {
      String excludes = config.getInitParameter("excludes");
      if (excludes != null && excludes.length() > 0) {
          excludePattern = Pattern.compile(excludes);
      }
	}
	
   /**
    * Get path part of request.
    * @param request servlet request
    * @return the path part of request or <tt>null</tt> when it cannot
    * be determined
    */
   private static String getPath (HttpServletRequest request) {
       String path = request.getServletPath();
       return path != null ? path : request.getPathInfo();
   }
}