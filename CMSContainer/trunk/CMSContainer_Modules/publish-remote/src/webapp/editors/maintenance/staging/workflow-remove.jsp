<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>workflow-remove</title>
</head>
    <body>
       <h2>workflow-remove</h2>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="username"/>
<mm:import externid="status"/>
<mm:import externid="action"/>

<form method="post">
   <select name="username">
      <mm:listnodes type="user" orderby="username">
         <option value="${_node.username}" <c:if test="${username eq _node.username}">selected="selected"</c:if>>${_node.username}</option>
      </mm:listnodes>
   </select>
   <select name="status">
      <option value="draft">draft</option>
      <option value="finished" <c:if test="${status eq 'finished'}">selected="selected"</c:if>>finished</option>
      <option value="approved" <c:if test="${status eq 'approved'}">selected="selected"</c:if>>approved</option>
      <option value="published" <c:if test="${status eq 'published'}">selected="selected"</c:if>>published</option>
      <option value="all" <c:if test="${status eq 'all'}">selected="selected"</c:if>>All workflows</option>
   </select>
   <input type="submit" name="action" value="remove"/>
   <input type="submit" name="action" value="view"/>
</form>

<mm:present referid="username">
username: <mm:write referid="username" /><br />
status: <mm:write referid="status" /><br />
<br />

<mm:listnodescontainer type="user">
   <mm:constraint field="username" operator="EQUAL" referid="username" />
   <mm:listnodes>
      <mm:relatednodescontainer type="workflowitem" role="creatorrel">
       <c:if test="${status ne 'all' }">
         <mm:constraint field="status" operator="EQUAL" value="${status}" />
       </c:if>
         <mm:relatednodes>
         <mm:present referid="action">
         <c:if test="${action eq 'remove'}">
            workflow <mm:field name="number" />/<mm:field name="status" /> deleted <br />
            <mm:deletenode deleterelations="true" />
         </c:if>
         <c:if test="${action eq 'view'}">
            Found workflow: <mm:field name="number" />/<mm:field name="status" /><br />
         </c:if>
         </mm:present>
      </mm:relatednodes>
      </mm:relatednodescontainer>
    </mm:listnodes>
</mm:listnodescontainer>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>