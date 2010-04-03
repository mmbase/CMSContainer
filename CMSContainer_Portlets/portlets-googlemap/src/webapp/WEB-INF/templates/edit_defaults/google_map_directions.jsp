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
				<td colspan="2"><fmt:message key="edit_defaults.addressFrom" />:</td>
				<td><cmsc:text var="addressFrom" name="param_addressFrom" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.infoFrom" />:</td>
				<td><cmsc:text var="infoFrom" name="param_infoFrom" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.addressTo" />:</td>
				<td><cmsc:text var="addressTo" name="param_addressTo" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.infoTo" />:</td>
				<td><cmsc:text var="infoTo" name="param_infoTo" /></td>
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
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.heightDirections" />:</td>
				<td><cmsc:text var="heightDirections" name="param_heightDirections" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.widthDirections" />:</td>
				<td><cmsc:text var="widthDirections" name="param_widthDirections" /></td>
			</tr>
			
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
		</table>
	</form>
</div>