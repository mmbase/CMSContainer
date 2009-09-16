<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0"><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="sitedelete.title">
	<link rel="stylesheet" type="text/css" href="../css/main_extension.css" />
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:import externid="pageMap" from="request" required="true"/>
<mm:cloud jspvar="cloud" rank="administrator" loginpage="../login.jsp">
<body>
<div class="tabs">
	<a href="#">
		<div class="tab_active">
			<div class="body">
				<div class="title">
					<fmt:message key="pagedelete.unlink.title"/>
				</div>
			</div>
		</div>
  </a>
</div>
<div class="editor">
   <div class="body">
<p>
    <fmt:message key="pagedelete.unlink.info"/>
</p>
<c:if test="${fn:length(pageMap) > 0}">   
   <ul class="shortcuts">
      <c:forEach items="${pageMap}" var="current" >
         <li>
            <a href="${pageContext.request.contextPath}/${current.key}">
            <mm:node number="${current.value}">
              <mm:field name="title"/>
            </mm:node>
            </a>
         </li>
      </c:forEach>
   </ul>
</c:if>
   </div>
   <div class="side_block_end"></div>
</div>   
</body>
</mm:cloud>
</html:html>
</mm:content>