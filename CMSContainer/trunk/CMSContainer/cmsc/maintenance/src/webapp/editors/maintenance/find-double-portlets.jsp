<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@ page import="com.finalist.cmsc.maintenance.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
    <link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
    <title>Find double portlets at pages</title>
</head>

<body>
    <h2>Searcher</h2>
   <mm:cloud jspvar="cloud" loginpage="../login.jsp" rank="administrator">
   <mm:log jspvar="log">
   
      <form method="post">
      	<input type="submit" name="action" value="Refresh"/>
      </form>
      <%= new SqlExecutor().execute(new DoublePortlets()) %>

   </mm:log>
</mm:cloud>

</body>
</html:html>