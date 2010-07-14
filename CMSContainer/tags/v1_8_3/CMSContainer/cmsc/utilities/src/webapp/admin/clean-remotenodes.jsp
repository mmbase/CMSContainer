<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
      <title>Clean Remote Nodes</title>
   </head>
   <body>
   
<mm:cloud jspvar="cloud" rank="administrator" method="http">
   <h1>Clean Remote Nodes</h1>
   <b>Warning: This script removes all the references in the remotenodes 
   table of the published items!!<br/> 
   This means that all contentelements are noted
   as NOT published. Please only continue if you are very sure what you are doing.</b>
   <br/>
   
<form method="post" action="#">
   <input type="submit" name="action" value="remove"/>
</form>
<br/>
<mm:import externid="action"></mm:import>
   
   <c:if test="${action eq 'remove'}">
       Cleaning remote nodes:<br/>
   </c:if>
      <mm:listnodes type="remotenodes">
         <mm:first>Number of remote nodes found: <mm:size/><hr/></mm:first>
         <c:if test="${action eq 'remove'}">
           <mm:deletenode />
         </c:if>
      </mm:listnodes>
   
</mm:cloud>
Done.<br/>
   </body>
</html:html>
