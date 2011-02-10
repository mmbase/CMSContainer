<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc/community" prefix="community"
%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="community.groups.title" />
<body>
  <div class="tabs">
     <a href="#">
        <div class="tab_active">
           <div class="body">
              <div class="title">
                 <fmt:message key="community.groups.groups" />
              </div>
           </div>
        </div>
     </a>
  </div>

<div class="editor">
   <community:useSSO var="sso" />
   <c:if test="${sso}" >
      <div class="body">
         <div class="syn">
            <a href="../community/SyncronizeGroupsFromIDstoreAction.do" ><fmt:message key="community.groups.synchronization" /></a>
         </div>
      </div>
   </c:if>
   <c:if test="${results != null}">
   <div class="ruler_green" >
      <div><fmt:message key="community.groups.result"/></div>
   </div>
   <div class="body">
      <div class="syn">
         <c:forEach var="i" items="${results}">
            <c:out value="${i}"/><br />
         </c:forEach>
         <c:if test="${results != null && (fn:length(results) == 0)}" >
            <fmt:message key="community.groups.noresult"/>
         </c:if>
      </div>
   </div>
   </c:if>

   <div class="ruler_green" >
      <div><fmt:message key="community.groups.list"/></div>
   </div>
   
   <c:if test="${sso}" >
      <community:listLDAPGroups var="groupList" />
   </c:if>
   <c:if test="${! sso}" >
      <community:listGroups var="groupList" />
   </c:if>   
 
   <div class="body">
      <div class="syn">     
         <ul>
		   <c:forEach var="group" items="${groupList}">
		   		<li>${group}</li>
		   </c:forEach>           
         </ul>
      </div>
   </div>

</div>
</body>
</html:html>
</mm:content>
