<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.cmsc.util.ThreadUtil" %>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="threads.title" />
<body>

<mm:import externid="show" jspvar="show">active</mm:import>
  <div class="tabs">
     <div class="tab">
        <div class="body">
           <div>
              <a href="threads.jsp?show=active"><fmt:message key="threads.active" /></a>
           </div>
        </div>
     </div>
     <div class="tab">
        <div class="body">
           <div>
              <a href="threads.jsp?show=all"><fmt:message key="threads.all" /></a>
           </div>
        </div>
     </div>
     <div class="tab">
        <div class="body">
           <div>
              <a href="threads.jsp?show=waiting"><fmt:message key="threads.waiting" /></a>
           </div>
        </div>
     </div>
     <div class="tab">
        <div class="body">
           <div>
              <a href="threads.jsp?show=counts"><fmt:message key="threads.counts" /></a>
           </div>
        </div>
     </div>
  </div>
  <div class="editor">
<%
if ("counts".equals(show)) {
   Map<String,Map<Thread, StackTraceElement[]>> map = ThreadUtil.getThreadsByApplication();
   %>
<div class="ruler_green"><div><fmt:message key="threads.summary" /></div></div>
<div id="summary" class="body">
   <ul>
   <%
   for (Map.Entry<String, Map<Thread,StackTraceElement[]>> entry : map.entrySet()) {
	   String applicationName = entry.getKey();
	   Map<Thread,StackTraceElement[]> threads = entry.getValue();
	   Map<Thread,StackTraceElement[]> active = new HashMap<Thread,StackTraceElement[]>(threads);
	   ThreadUtil.filterActiveThread(active);
	   Map<Thread,StackTraceElement[]> waiting = new HashMap<Thread,StackTraceElement[]>(threads);
	   ThreadUtil.filterWaitingThread(waiting);
	%>
		<li><a href="#<%=applicationName%>"><%=applicationName%> - <%= threads.size() %> ( <%= active.size() %> / <%= waiting.size() %> )</a></li>
	<% } %>
	</ul></div>
<% 
} else {
   Map<String,Map<Thread, StackTraceElement[]>> map;
   if ("all".equals(show)) {
      map = ThreadUtil.getThreadsByApplication();
   } else {
      if ("waiting".equals(show)) {
         map = ThreadUtil.getWaitingThreadsByApplication();
      } else {
      	  map = ThreadUtil.getActiveThreadsByApplication();
      }
   }
   %>
<div class="ruler_green"><div><fmt:message key="threads.summary" /></div></div>
<div id="summary" class="body">
   <ul>
   <%
   for (Map.Entry<String, Map<Thread,StackTraceElement[]>> entry : map.entrySet()) {
	   String applicationName = entry.getKey();
	   Map<Thread,StackTraceElement[]> threads = entry.getValue();
	%>
		<li><a href="#<%=applicationName%>"><%=applicationName%> - <%= threads.size() %></a></li>
	<% } %>
	</ul></div>
<%
	for (Map.Entry<String, Map<Thread,StackTraceElement[]>> entry : map.entrySet()) {
	    String applicationName = entry.getKey();
	    Map<Thread,StackTraceElement[]> threads = entry.getValue();
	    %><div class="ruler_green"><div><%=applicationName%> - <%= threads.size() %> </div></div>
	    <div id="<%=applicationName%>" class="body">
	    <a name="<%=applicationName%>>"></a>
	    <pre><%
	    for (Map.Entry<Thread, StackTraceElement[]> thread : threads.entrySet()) {
	    	StackTraceElement[] stack = thread.getValue();
	    	if (stack != null && stack.length > 0 && !stack[0].getMethodName().equals("dumpThreads")) {
	%>
<%= ThreadUtil.printStackTrace(thread.getKey(), stack) %>
	<%
			}
	    }
	    %></pre></div><%
	} 
} %>
  </div>
</body>
</html:html>
</mm:content>