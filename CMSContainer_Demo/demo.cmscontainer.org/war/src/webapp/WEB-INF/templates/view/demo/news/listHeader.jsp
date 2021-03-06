<%@ include file="/WEB-INF/templates/portletglobals.jsp" %>
<!-- start news listHeader ${showExpanded} ${portletTitle} -->
<mm:cloud method="asis">
   <mm:node number="${contentchannel}">              
      <div class="heading">
         <c:choose>
            <c:when test="${not empty portletTitle}">
               <h2>${portletTitle}</h2>
            </c:when>
            <c:otherwise>
               <h2><mm:field name="name"/></h2>
            </c:otherwise>
         </c:choose>
      </div>
   </mm:node>
</mm:cloud>
<div class="content">
   <!-- nieuws -->
   <div class="itemlist">
   <c:if test="${empty showExpanded}">
      <div class="list">
         <ul>
   </c:if>
