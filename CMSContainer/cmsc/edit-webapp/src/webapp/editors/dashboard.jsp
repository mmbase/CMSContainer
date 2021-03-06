<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0"
><%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">--%>
<html:html xhtml="true">
<cmscedit:head title="dashboard.title">
   <script type="text/javascript">
        function info(objectNumber) {
      openPopupWindow("info", 500, 500, "repository/showitem.jsp?objectnumber=" + objectNumber);
   }

	  function clearDefaultSearchText(defaultText) {
      	var searchField = document.forms["searchForm"]["title"];
      	if(searchField.value == defaultText) {
	      	searchField.value = "";
      	}
      }
   </script>
</cmscedit:head>
<body>
		<div id="left">
			<!-- Zoek block -->
			<cmscedit:sideblock title="dashboard.search.header">
				<form action="repository/index.jsp" name="searchForm" method="post">
  							<div class="search_form"><input type="text" name="title" value="<fmt:message key="dashboard.search.term" />" onfocus="clearDefaultSearchText('<fmt:message key="dashboard.search.term" />');"/></div>

					<div class="search_form_options">
 								<a href="javascript:document.forms['searchForm'].submit()" class="button"><fmt:message key="dashboard.search.search" /></a>
					</div>
				</form>
			</cmscedit:sideblock>

            <c:set var="message"><cmsc:property key="dashboard.welcome.message"/></c:set>
            <c:if test="${fn:length(message) gt 0}">
	            <cmscedit:sideblock title="dashboard.welcome.header" titleClass="side_block_gray">
					<br />
					${message}
				</cmscedit:sideblock>
            </c:if>
      </div>
<div id="content">
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<mm:cloudinfo type="user" id="cloudusername" write="false" />

    <mm:haspage page="/editors/repository/">
	    <c:set var="dashboardRepositorySize" value="10"/>
	    <c:set var="dashboardRepositoryTitle"><fmt:message key="dashboard.repository.header"><fmt:param>${dashboardRepositorySize}</fmt:param></fmt:message></c:set>
	    <cmscedit:contentblock title="${dashboardRepositoryTitle}" titleMode="plain"
	        titleClass="content_block_pink" bodyClass="body_table">
	
	      <mm:listnodescontainer type="contentelement">
	         <mm:constraint field="lastmodifier" operator="EQUAL" referid="cloudusername" />
	         <mm:maxnumber value="${dashboardRepositorySize}" />
	         <mm:sortorder field="lastmodifieddate" direction="down" />
	      <table>
	         <thead>
	            <tr>
	               <th style="width: 80px;"></th>
	               <th style="width: 68px;"><fmt:message key="dashboard.repository.element" /></th>
	               <th><fmt:message key="dashboard.repository.title" /></th>
	               <th style="width: 150px;"><fmt:message key="dashboard.repository.date" /></th>
	               <th style="width: 80px;"><fmt:message key="dashboard.repository.number" /></th>
	            </tr>
	         </thead>
	         <tbody class="hover">
	            <mm:listnodes>
	              <mm:field name="number" write="false" id="number"/>
	               <tr <mm:even inverse="true">class="swap"</mm:even> href="javascript:window.top.openRepositoryWithContent('<mm:write referid="number"/>');">
				   <td>
	            	<a href="javascript:window.top.openRepositoryWithContent('<mm:write referid="number"/>');"><img src="gfx/icons/edit.png" alt="<fmt:message key="dashboard.content.edit" />" title="<fmt:message key="dashboard.content.edit" />" /></a>
	            	<a href="<cmsc:contenturl number="${number}"/>" target="_blank"><img src="gfx/icons/preview.png" alt="<fmt:message key="dashboard.content.preview.title" />" title="<fmt:message key="dashboard.content.preview.title" />" /></a>
						<a href="javascript:info('${number}')"><img src="gfx/icons/info.png" title="<fmt:message key="dashboard.content.info" />" alt="<fmt:message key="dashboard.content.info" />"/></a>
						<mm:haspage page="/editors/versioning">
						<c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
						   <c:param name="nodenumber">${number}</c:param>
						</c:url>
						<a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')"><img src="gfx/icons/versioning.png" title="<fmt:message key="dashboard.content.icon.versioning.title" />" alt="<fmt:message key="dashboard.content.icon.versioning.title" />"/></a>
						</mm:haspage>
					</td>
	                  <td onMouseDown="objClick(this);"><mm:nodeinfo type="guitype"/></td>
	                  <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
	                  <td onMouseDown="objClick(this);"><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
	                  <td onMouseDown="objClick(this);"><mm:field name="number"/></td>
	               </tr>
	            </mm:listnodes>
	         </tbody>
	      </table>
	      </mm:listnodescontainer>
		</cmscedit:contentblock>
   </mm:haspage>

   <mm:haspage page="/editors/taskmanagement/">
      <c:set var="dashboardTaskSize" value="5" />
      <c:set var="tmpRole" value="assignedrel" />
      <c:set var="dashboardTaskTitle">
         <fmt:message key="task.header">
            <fmt:param>${dashboardTaskSize}</fmt:param>
         </fmt:message>
      </c:set>
      <mm:listnodescontainer type="user">
         <mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
         <mm:maxnumber value="10" />
         <mm:listnodes>
            <mm:relatednodescontainer type="task" role="${tmpRole}" searchdirs="source">
               <mm:constraint field="status" value="task.status.done" operator="EQUAL" inverse="true" />
               <mm:relatednodes comparator="com.finalist.cmsc.tasks.TaskUrgencyComparator" max="${dashboardTaskSize}" id="resultList" jspvar="resultList"/>
            </mm:relatednodescontainer>
         </mm:listnodes>
      </mm:listnodescontainer>
      <c:if test="${not empty resultList}">
         <cmscedit:contentblock title="${dashboardTaskTitle}" titleMode="plain" titleClass="content_block_pink" bodyClass="body_table">
            <jsp:include page="/editors/taskmanagement/tasklist_table.jsp" flush="true">
				<jsp:param name="results" value="${resultList}"/>
			</jsp:include>
         </cmscedit:contentblock>
      </c:if>
   </mm:haspage>

   </mm:cloud>
</div>
</body>
</html:html>
</mm:content>