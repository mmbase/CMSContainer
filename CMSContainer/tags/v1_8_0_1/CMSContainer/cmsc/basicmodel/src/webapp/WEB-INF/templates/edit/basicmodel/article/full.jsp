<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<c:if test="${viewtype eq 'list'}">
	<cmsc:renderURL var="renderUrl" />
	<a href="${renderUrl}"><fmt:message key="view.back" /></a>
</c:if>

<mm:cloud>
<form name="contentportlet" id="contentportlet" method="post"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">

		<cmsc:field elementid="${elementId}" name="title" edit="true" container="h1"/>
		<cmsc:field elementid="${elementId}" name="subtitle" edit="true" container="h2"/>
		<cmsc:field elementid="${elementId}" name="intro" edit="true" options="minHeight:100, htmlarea:true, formId:'contentportlet'"/>

		<cmsc-bm:linkedimages position="top-left" style="float: left;" />
		<cmsc-bm:linkedimages position="top-right" style="float: right;" />
		<cmsc:field elementid="${elementId}" name="body" edit="true" options="minHeight:300, htmlarea:true, formId:'contentportlet'"/>
		<cmsc-bm:linkedimages position="bottom-left" style="float: left;" />
		<cmsc-bm:linkedimages position="bottom-right" style="float: right;" />

	</mm:node>
<p>
<input type="submit" value="<fmt:message key="edit.save" />" />
</p>
</form>
</mm:cloud>