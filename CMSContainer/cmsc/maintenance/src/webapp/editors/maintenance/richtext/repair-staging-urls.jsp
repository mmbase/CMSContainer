<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@ page import="com.finalist.cmsc.maintenance.richtext.*"%>
<html>
<head>
    <link href="../../style.css" type="text/css" rel="stylesheet"/>
    <title>repair staging urls</title>
</head>
    <body>
       <h2>repair staging urls</h2>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<form method="post">
	<input type="submit" name="action" value="view"/>
	<input type="submit" name="action" value="repair"/>
</form>

<mm:import externid="action"/>
<mm:present referid="action">
	<mm:write referid="action" jspvar="action" vartype="String">
	<% new RepairStagingUrls(cloud, pageContext).execute("repair".equals(action)); %>
	</mm:write>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>