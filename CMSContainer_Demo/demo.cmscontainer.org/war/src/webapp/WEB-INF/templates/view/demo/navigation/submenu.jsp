<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cmsc:location var="cur" sitevar="site" />
<cmsc:path var="listPath" />
<cmsc:list-navigations var="pages" origin="${listPath[0]}" />
       
<div id="menu">
  <div class="center">
    <ul>
        <li <c:if test="${cur eq site}">class="selected"</c:if>>
          <a href="<cmsc:link dest="${site.id}"/>" title="Overzicht">
          	<fmt:message key="view.submenu.home"/>
          </a>
        </li>
      <c:forEach var="page" items="${pages}">
      	<community:useSSO var="sso" />
			<c:if test="${sso}" >
		         <community:pageReadable pageId="${page.id}" >
		         <li <cmsc:onpath origin="${page}">class="selected"</cmsc:onpath>>
		            <a href="<cmsc:link dest="${page.id}"/>" title="<c:out value='${page.description}'/>">
		               <c:out value='${page.title}'/>
		            </a>
		         </li>
		         </community:pageReadable>
			</c:if>
			<c:if test="${! sso}" >
		         <li <cmsc:onpath origin="${page}">class="selected"</cmsc:onpath>>
		            <a href="<cmsc:link dest="${page.id}"/>" title="<c:out value='${page.description}'/>">
		               <c:out value='${page.title}'/>
		            </a>
		         </li>
			</c:if>
      </c:forEach>
    </ul>
  </div>
</div>