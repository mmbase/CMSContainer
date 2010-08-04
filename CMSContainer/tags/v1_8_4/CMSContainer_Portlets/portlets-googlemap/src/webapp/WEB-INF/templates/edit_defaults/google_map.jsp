<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.address" />:</td>
				<td><cmsc:text var="address" name="param_address" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.info" />:</td>
				<td><cmsc:text var="info" name="param_info" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.key" />:</td>
				<td><cmsc:text var="key" name="param_key" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.height" />:</td>
				<td><cmsc:text var="height" name="param_height" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.width" />:</td>
				<td><cmsc:text var="width" name="param_width" /></td>
			</tr>
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
		</table>
	</form>
</div>