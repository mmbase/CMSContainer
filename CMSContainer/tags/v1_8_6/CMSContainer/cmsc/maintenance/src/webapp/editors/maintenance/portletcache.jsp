<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.cmsc.util.ThreadUtil" %>
<%@include file="globals.jsp" %>

<%@page import="com.finalist.pluto.portalImpl.aggregation.portletcache.PortletCache"%>
<%@page import="java.util.HashMap"%>
<%@page import="net.sf.ehcache.Cache"%>
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
              <a href="#"><fmt:message key="portletcache.title" /></a>
           </div>
        </div>
     </div>
  </div>
  <div class="editor">
    <div class="body">


	</div>
<%

	Cache cache = PortletCache.getCache();
    %><div class="ruler_green"><div><fmt:message key="portletcache.title" /></div></div>
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
    	<b>Performance: <%= new DecimalFormat("0.##").format((hits*100f)/(hits+misses))%>%</b><br/>
    	</p>
    </div>
  </div>
</body>
</html:html>
</mm:content>