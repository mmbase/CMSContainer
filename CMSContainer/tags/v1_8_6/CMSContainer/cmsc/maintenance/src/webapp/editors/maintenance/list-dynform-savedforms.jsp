<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
      <title>List Dynamic Form relation: saved forms</title>
   </head>
   <body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">

   <mm:import externid="action">view</mm:import>
   <b>List Dynamic Form relations: saved forms</b><br/>
   <form method="post" action="#">
      <input type="submit" name="action" value="Refresh"/>
   </form>
   <br/>
   
   <mm:listnodes type="responseform">
      <mm:countrelations type="savedform" role="posrel" searchdir="destination"/> - saved forms -
      node:<mm:field name="number"/>, with title: <b>'<mm:field name="title"/>'</b><br/> 
   </mm:listnodes>
</mm:cloud>
<br/>
   </body>
</html:html>