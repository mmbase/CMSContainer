<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://finalist.com/cmsc/community" prefix="community" %>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="select.group.title" />
<body>
  <div class="tabs">
     <div class="tab_active">
        <div class="body">
           <div>
              <a href="#"><fmt:message key="select.group.title" /></a>
           </div>
        </div>
     </div>
  </div>

 <div class="editor" style="height:450px;">
  <div class="body" style="padding:10px; width: auto;">
<community:useSSO var="sso" />
<c:choose>
	<c:when test="${!empty param.group}">
		<mm:cloud>
			<mm:listnodes type="pagegroup" constraints="name = '${param.group}'" max="1">
				<c:set var="number"><mm:field name="number"/></c:set>
			</mm:listnodes>
			<c:if test="${empty number}">
				<mm:createnode type="pagegroup" id="groupnode">
					<mm:setfield name="name">${param.group}</mm:setfield>
				</mm:createnode>
				<mm:node referid="groupnode">
					<c:set var="number"><mm:field name="number"/></c:set>
				</mm:node>           
            <c:if test="${sso}" >
               <community:addAuthority value="${param.group}" />
            </c:if>
			</c:if>
		</mm:cloud>
		<script>
			top.opener.selectContent('${number}', '', ''); 
			top.close();
		</script>
	</c:when>
	<c:otherwise>
		<fmt:message key="select.group.intro" />:<br/>
      <c:if test="${sso}" >
         <community:listLDAPGroups var="groupList" />
      </c:if>
      <c:if test="${! sso}" >
         <community:listGroups var="groupList" />
      </c:if>
		<c:forEach var="group" items="${groupList}">
		* <a href="?group=${group}">${group}</a><br/>
		</c:forEach>
	</c:otherwise>
</c:choose>

</div>
</div>
</body>
</html:html>
</mm:content>