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

		<mm:relatednodes type="attachments" role="posrel">
			<mm:first><ul></mm:first>
			<mm:field name="number" id="attachmentNumber" write="false" />
			<li><cmsc:field elementid="${attachmentNumber}" name="title" edit="true" container="span"/><br />
				<cmsc:field elementid="${attachmentNumber}" name="description" edit="true" container="span"/></li>
			<mm:last></ul></mm:last>
		</mm:relatednodes>
		<mm:relatednodes type="urls" role="posrel">
			<mm:first><ul></mm:first>
			<mm:field name="number" id="urlNumber" write="false" />
			<mm:field name="url" id="url" write="false" />
			<li><cmsc:field elementid="${urlNumber}" name="name" edit="true" container="span"/><br />
				<cmsc:field elementid="${urlNumber}" name="url" edit="true" container="span"/></li>
			<mm:last></ul></mm:last>
		</mm:relatednodes>
	</mm:node>
<p>
<input type="submit" value="<fmt:message key="edit.save" />" />
</p>
</form>
</mm:cloud>