<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><%@ page import="com.finalist.cmsc.maintenance.sql.*"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Show process list of MMBase database.</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link href="../style.css" type="text/css" rel="stylesheet"/>
</head>
   <body>
      <h2>Show process list of MMBase database.</h2>
      <mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
      <mm:log jspvar="log">
      <form method="post">
         <input type="hidden" name="action" value="check"/>
         <input type="submit" name="action" value="Check"/>
      </form>

   <mm:import externid="action"/>
   <mm:present referid="action">
         <%			
		    Map<Integer,List> results = new HashMap<Integer,List>();

		    new SqlExecutor().execute(new ShowDatabaseProcessList(results));
			pageContext.setAttribute("results", results);
	     %>
	    <c:if test="${fn:length(results) > 0}">
			<p>Processes List :</p>
			<table border="1px">
			<tr><th>Id</th><th>User</th><th>Host</th><th>db</th><th>Command</th><th>Time</th><th>State</th><th>Info</th></tr>
<c:forEach var="entry" items="${results}">
<tr >
<c:forEach var="value" items='${entry.value}'>
<td>
${value}
</td>
</c:forEach>
</tr>
</c:forEach>
</table>
		</c:if>
		<c:if test="${fn:length(results) == 0}">
			No process. 
		</c:if>
   </mm:present>

</mm:log>
</mm:cloud>
</body>
