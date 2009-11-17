<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><%@ page import="com.finalist.cmsc.maintenance.beans.*"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Check integrity of MMBase database</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link href="../style.css" type="text/css" rel="stylesheet"/>
</head>
   <body>
      <h2>Check integrity of MMBase database.</h2>
      <mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
      <mm:log jspvar="log">
      <form method="post">
         <input type="hidden" name="action" value="check"/>

         <input type="submit" name="action" value="Check"/>
      </form>

   <mm:import externid="action"/>
   <mm:present referid="action">
         <%
			java.util.List nodes = new CheckIntegrityBean(cloud).execute();
			 pageContext.setAttribute("list", nodes);

	     %>
	    <c:if test="${fn:length(list) > 0}">
			<p>Corrupt nodes :</p>
			<ul>
			<c:forEach var='item' items='${list}'>
            <li><c:out value='${item}'/></li>
			
         </c:forEach>
</ul>
		</c:if>
		<c:if test="${fn:length(list) == 0}">
			No corrupt nodes. 
		</c:if>
   </mm:present>

</mm:log>
</mm:cloud>
</body>
