<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<%-- Relate to portlet options --%>
			<c:import url="sections/relatetoportlet.jsp" />

			<%-- Click to page options --%>
			<c:import url="sections/clicktopage.jsp" />
		
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>