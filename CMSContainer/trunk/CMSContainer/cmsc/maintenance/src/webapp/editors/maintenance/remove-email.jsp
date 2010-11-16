<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<html>
<head>
    <link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
    <title>email-remove-all</title>
</head>
    <body>
       <h2>email-remove-all</h2>
<mm:cloud jspvar="cloud" loginpage="../login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="username"/>
This script removes all stored email nodes from the database. Use at own risk! <br/>
<form method="post">
   <input type="submit" value="remove all email items"/>
   <input type="hidden" name="username"/>
</form>

<mm:present referid="username">
   Removing email items<br/>
   <mm:listnodes type="email">
      Email nodes: <mm:field name="number" /> deleted <br />
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>