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
		
			<%-- Content element selector --%>
			<c:import url="sections/selectelement.jsp" />

			<%-- Use lifecycle option--%>
			<c:import url="sections/lifecycle.jsp" />

	<tr>
		<td colspan="3">
		<h4><fmt:message key="edit_defaults.dimensions" /></h4>
		</td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.width" />:</td>
		<td><input type="text" name="movieWidth" value="${movieWidth}" /></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.height" />:</td>
		<td><input type="text" name="movieHeight" value="${movieHeight}" /></td>
	</tr>

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
</table>
</form>
</div>