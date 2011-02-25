<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

<table class="editcontent">
			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />

			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />
	<tr>
		<td colspan="3">
		<h4><fmt:message key="edit_defaults.dimensions" /></h4>
		</td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.movieUrl" />:</td>
		<td><input type="text" name="param_movieUrl" value="${movieUrl}" /></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.width" />:</td>
		<td><input type="text" name="param_movieWidth" value="${movieWidth}" /></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.height" />:</td>
		<td><input type="text" name="param_movieHeight" value="${movieHeight}" /></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.allowfullscreen" />:</td>
		<td><input type="text" name="param_allowfullscreen" value="${allowfullscreen}" /></td>
	</tr>

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
</table>
</form>
</div>