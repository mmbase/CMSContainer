<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="portlet-config-canvas">

<script type="text/javascript">
	function selectPage(page, path, positions) {
		document.forms['<portlet:namespace />form'].page.value = page;
		document.forms['<portlet:namespace />form'].pagepath.value = path;
		
		var selectWindow = document.forms['<portlet:namespace />form'].window;
		for (var i = selectWindow.options.length -1 ; i >=0 ; i--) {
			selectWindow.options[i] = null;
		}
		for (var i = 0 ; i < positions.length ; i++) {
			var position = positions[i];
			selectWindow.options[selectWindow.options.length] = new Option(position, position);
		}
	}
	function erase(field) {
		document.forms['<portlet:namespace />form'][field].value = '';
	}
	function eraseList(field) {
		document.forms['<portlet:namespace />form'][field].selectedIndex = -1;
	}
</script>

<h3><fmt:message key="edit_defaults.title" /></h3>

<form method="post" name="<portlet:namespace />form" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" target="_parent">

<table class="editcontent">

	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.formResource" />:</td>
		<td>
			<cmsc:select var="formResource">
				<c:forEach var="r" items="${formResources}">
					<cmsc:option value="${r}" name="${r}" />
				</c:forEach>
			</cmsc:select>
		</td>
	</tr>

	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.activeStep" />:</td>
		<td><cmsc:text var="activeStep" /></td>
	</tr>

	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.view" />:</td>
		<td>
			<cmsc:select var="view">
				<c:forEach var="v" items="${views}">
					<cmsc:option value="${v.id}" name="${v.title}" />
				</c:forEach>
			</cmsc:select>
		</td>
	</tr>

	<tr>
		<td colspan="3">
			<h4><fmt:message key="edit_defaults.clickpage" /></h4>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.page" />:</td>
		<td align="right">
			<a href="<c:url value='/editors/site/select/SelectorPage.do?channel=${page}' />"
				target="selectpage" onclick="openPopupWindow('selectpage', 340, 400)"> 
					<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.pageselect" />"/></a>
			<a href="javascript:erase('page');erase('pagepath');eraseList('window')">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/></a>
		</td>
		<td>
		<mm:cloud>
			<mm:node number="${page}" notfound="skip">
				<mm:field name="path" id="pagepath" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="page" value="${page}" />
		<input type="text" name="pagepath" value="${pagepath}" disabled="true" />
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.window" />:</td>
		<td>
			<cmsc:select var="window">
				<c:forEach var="position" items="${pagepositions}">
					<cmsc:option value="${position}" />
				</c:forEach>
			</cmsc:select>
		</td>
	</tr>
	
	<tr>
		<td colspan="3">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
		</td>
	</tr>
</table>
</form>
</div>