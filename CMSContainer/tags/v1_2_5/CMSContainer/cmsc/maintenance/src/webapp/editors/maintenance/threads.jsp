<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.cmsc.util.ThreadUtil" %>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
<title><fmt:message key="threads.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
<link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
<link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
<style type="text/css" xml:space="preserve">
   body { behavior: url(<cmsc:staticurl page='/editors/css/hover.htc)'/>;}
</style>
<script src="<cmsc:staticurl page='/editors/utils/rowhover.js'/>" type="text/javascript"></script>
<script src="<cmsc:staticurl page='/js/window.js'/>" type="text/javascript"></script>
<script src="<cmsc:staticurl page='/js/transparent_png.js'/>" type="text/javascript"></script>
</head>
<body>
  <div class="tabs">
     <div class="tab_active">
        <div class="body">
           <div>
              <a href="#"><fmt:message key="threads.active" /></a>
           </div>
        </div>
     </div>
<%-- 
     <div class="tab">
        <div class="body">
           <div>
              <a href="#"><fmt:message key="threads.all" /></a>
           </div>
        </div>
     </div>
 --%>
  </div>
  <div class="editor">
    <div class="body">

	</div>
<%
Map<String,Map<Thread, StackTraceElement[]>> map = ThreadUtil.getActiveThreadsByApplication();
for (Map.Entry<String, Map<Thread,StackTraceElement[]>> entry : map.entrySet()) {
    String applicationName = entry.getKey();
    %><div class="ruler_green"><div><%=applicationName%></div></div>
    <div id="<%=applicationName%>" class="body"><pre><%
    Map<Thread,StackTraceElement[]> threads = entry.getValue();
    for (Map.Entry<Thread, StackTraceElement[]> thread : threads.entrySet()) {
    	StackTraceElement[] stack = thread.getValue();
    	if (stack != null && stack.length > 0 && !stack[0].getMethodName().equals("dumpThreads")) {
%>
<%= ThreadUtil.printStackTrace(thread.getKey(), stack) %>
<%
		}
    }
    %></pre></div><%
} %>
  </div>
</body>
</html:html>
</mm:content>