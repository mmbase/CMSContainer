<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="admindashboard.title">
	<link href="../../css/compact.css" type="text/css" rel="stylesheet" />
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
	<mm:hasrank minvalue="administrator">
		<h1><fmt:message key="admindashboard.system.javaos.header" /></h1>
		
		<%=System.getProperty("java.vendor")%>
		<br/>
		<%=System.getProperty("java.vm.name")%>
		<%=System.getProperty("java.version")%>
		<br/>
		<%=System.getProperty("os.name")%>
		<%=System.getProperty("os.version")%>
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>