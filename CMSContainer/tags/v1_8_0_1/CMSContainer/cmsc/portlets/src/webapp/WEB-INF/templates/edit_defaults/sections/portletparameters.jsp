<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<mm:cloud method="asis">
	<mm:node number="${portletId}" notfound="skip">
		<mm:relatednodes type="portletparameteroption" role="parameterrel">
			<mm:field name="key" jspvar="key" write="false" />
			<mm:field name="type" jspvar="type" write="false" />
			<mm:first>
				<tr>
					<td colspan="3">
						<h4><fmt:message key="edit_defaults.parameters" /></h4>
					</td>
				</tr>
			</mm:first>
			<tr>
				<td>${key}</td>
				<td>&nbsp;</td>
				<td>
					<c:if test="${type eq '1'}">
						<input type="text" name="nodeparam_${key}" value="" />
					</c:if>
					<c:if test="${type eq '2'}">
						<select name="nodeparam_${key}">

						</select>
					</c:if>
				</td>
			</tr>
		</mm:relatednodes>
	</mm:node>
</mm:cloud>