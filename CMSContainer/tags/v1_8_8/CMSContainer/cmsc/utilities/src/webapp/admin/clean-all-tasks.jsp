<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
      <title>Clean Task Nodes</title>
   </head>
   <body>
   
<mm:cloud jspvar="cloud" rank="administrator" method="http">
   <h1>Clean Task Nodes</h1>
   <b>Warning: This script removes all Task nodes!<br/> 
   <br/>
   
<form method="post" action="#">
   <input type="submit" name="action" value="remove"/>
</form>
<br/>
<mm:import externid="action"></mm:import>
   
   <c:if test="${action eq 'remove'}">
       Cleaning task nodes:<br/>
   </c:if>
      <mm:listnodes type="task">
         <mm:first>Number of task nodes found: <mm:size/><hr/></mm:first>
         <c:if test="${action eq 'remove'}">
           <mm:deletenode deleterelations="true"/>
         </c:if>
      </mm:listnodes>
   
</mm:cloud>
Done.<br/>
   </body>
</html:html>
