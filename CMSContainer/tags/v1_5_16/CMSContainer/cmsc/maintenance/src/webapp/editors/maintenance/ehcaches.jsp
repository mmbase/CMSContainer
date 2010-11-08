<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.cmsc.util.ThreadUtil" %>
<%@include file="globals.jsp" %>

<%@page import="com.finalist.pluto.portalImpl.aggregation.portletcache.PortletCache"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.ehcache.Cache"%>
<%@page import="net.sf.ehcache.CacheManager"%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="net.sf.ehcache.Statistics"%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="portletcache.title" />
<body>
  <div class="tabs">
     <div class="tab_active">
        <div class="body">
           <div>
              <a href="#">EHCACHES</a>
           </div>
        </div>
     </div>
  </div>
  <div class="editor">
    <div class="body">


        </div>
<%
	CacheManager manager = CacheManager.create();
	String[] names = manager.getCacheNames();
	for(String name : names) {
		Cache cache = manager.getCache(name);
		%><div class="ruler_green"><div><%= name %></div></div>
		<div class="body">
			<p>
			<%
			Statistics stats = cache.getStatistics();
			long hits = stats.getCacheHits(); // 1.1: cache.getHitCount();
			long misses = stats.getCacheMisses(); // 1.1: cache.getMissCountExpired()+cache.getMissCountNotFound();
			%>
			Size: <%=cache.getSize() %><br/>
			Hits: <%=hits %><br/>
			Misses: <%=misses %><br/>
         Memory: <%=cache.calculateInMemorySize() / 1024 %>KB - (<%=(cache.calculateInMemorySize()/1024) / (cache.getSize()+1) %>KB/entry)<br/>
			<b>Performance: <%= new DecimalFormat("0.##").format((hits*100f)/(hits+misses))%>%</b><br/>
			</p>
			
			<%--
			if (name.eqauls"com.finalist.cmsc.beans.om.Portlet")) {
				List keys = cache.getKeysNoDuplicateCheck();
				for(Object cacheKey : keys) {
					%><%=cacheKey%>, <%
				}
			}
			--%>
         
		</div>
		<% } %>
  </div>
</body>
</html:html>
</mm:content>