<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="org.mmbase.bridge.*,org.mmbase.util.logging.*" %>
<%@include file="globals.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<body>

<%-- 
CMSC 1.6 introduces a new field in workflowitem, nodetype. 
for example if we publish an article, nodetype will have the value 'article'.
The workflow view uses this field to determine in which subtree to place the content element. 
Old workflowitems that exist prior to the upgrade will now have this field but it will be empty.
Run this script to fill in the correct value in the nodetype field 
--%>

<mm:cloud>
   <h3>Fix field nodetype of workflowitems</h3>
   <p>This script list all the workflowitems with type 'content' by field number followed by field nodetype.
   If it finds a workflowitem where the nodetype is not set, it will determine the correct type and set the nodetype field</p>
      
   <c:set var="numChanged" value="0" />
   <mm:listnodes type="workflowitem">
      <mm:field id="type" name="type" write="false"/>
      <c:if test="${type eq 'content'}">
         <mm:field id="number" name="number" write="false"/>
         <mm:field id="nodetype" name="nodetype" write="false"/>
         number: <strong>${number}</strong> - nodetype: [<strong>${nodetype}</strong>] <br/>
         <c:if test="${empty nodetype}">
            number: <strong>${number}</strong> - 
            <mm:relatednodes type="contentelement" searchdir="destination">
               <mm:field name="otype" id="otype" write="false"/>
               <mm:listnodes type="typedef" constraints="number=${otype}" max="1">
                  nodetype is set to <strong><mm:field id="newnodetype" name="name"/></strong>
               </mm:listnodes>
               <br/> 
            </mm:relatednodes>
            <c:set var="numChanged">${numChanged + 1}</c:set>
            <br/>
            <mm:setfield name="nodetype">${newnodetype}</mm:setfield>
         </c:if>
      </c:if>    
   </mm:listnodes>
   <p>While running this script <strong>${numChanged}</strong> workflowitems were changed</p>
   
</mm:cloud>

The end!

</body>
</html:html>