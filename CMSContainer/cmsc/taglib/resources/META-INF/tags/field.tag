<%@ tag body-content="empty" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ attribute name="elementid" required="true" rtexprvalue="true"
%><%@ attribute name="name" required="true" rtexprvalue="true"
%><%@ attribute name="edit" required="false" rtexprvalue="true"
%><%@ attribute name="container" required="false" rtexprvalue="true"
%><%@ attribute name="options" required="false" rtexprvalue="true"
%><c:set var="edit" value="${empty edit ? false :edit }"/>
<c:set var="container" value="${empty container ? 'p' :container }"/>
<c:set var="istitle" value="${name eq 'title' || name eq 'subtitle' ? true :false }"/>
<c:if test="${edit && !istitle}">
   <div id="content_${elementId}_${name}" class="${name}">
</c:if>
<mm:field name="${name}" escape="none">
  <mm:isnotempty>
    <${container}><mm:write /></${container}>
  </mm:isnotempty>
</mm:field>
<c:if test="${edit && !istitle}">
   </div>
</c:if>
<c:if test="${edit}">
<script type="text/javascript">
   new InPlaceEditor.Local('content_${elementId}_${name}', {${options}});
</script>
</c:if>