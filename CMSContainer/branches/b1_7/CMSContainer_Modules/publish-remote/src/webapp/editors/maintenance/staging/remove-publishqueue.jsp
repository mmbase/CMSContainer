<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<mm:cloud method="http" rank="administrator">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>

<mm:import externid="nodenumber" />
<mm:import externid="action" />
<mm:import externid="status">init</mm:import>


<form method="post">
	Node number:<input type="text" name="nodenumber" value="${nodenumber}"/>
   status:
	<select name="status">
		<option value="fail" <c:if test="${status eq 'fail'}">selected="selected"</c:if>>fail</option>
		<option value="init" <c:if test="${status eq 'init'}">selected="selected"</c:if>>init</option>
		<option value="done" <c:if test="${status eq 'done'}">selected="selected"</c:if>>done</option>
	</select>
	<input type="submit" value="show"/>
</form>

<form method="post">
   <c:if test="${not empty nodenumber}">
	  <input type="hidden" name="nodenumber" value="${nodenumber}"/>
   </c:if>
	<input type="hidden" name="status" value="${status}"/>
	<input type="hidden" name="action" value="delete"/>
	<input type="submit" value="delete all"/> (only at your own risk!)
</form>


<mm:listnodescontainer type="publishqueue">
   <c:if test="${not empty nodenumber}">
      <mm:constraint field="sourcenumber" operator="EQUAL" referid="nodenumber" />
   </c:if>
   <mm:constraint field="status" operator="EQUAL" value="${status}" casesensitive="true" />
   <c:if test="${not empty nodenumber}">
      nodenumber:	<mm:write referid="nodenumber" />.<br />
   </c:if>
   status:	<mm:write referid="status" />.<br />
   size: <mm:size /> items.<br />
   <br />
      <mm:listnodes max="2000">
         <mm:field name="number" />
         <mm:field name="action" />
         <mm:field name="destinationcloud" />
         
         <mm:present referid="action">
            <mm:compare referid="action" value="delete">
               <mm:deletenode /> deleted
            </mm:compare>
         </mm:present>
         <br />
      </mm:listnodes>
      <mm:present referid="action">
         <br/><b>Note: listnodes ran with limit at 2000 items!</b>
      </mm:present>
</mm:listnodescontainer>

</body>
</html>

</mm:cloud>