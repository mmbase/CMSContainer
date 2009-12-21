<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div class="kolom">
<cmsc:portletmode name="edit">
   <form name="contentportlet" method="post" action="<portlet:actionURL><portlet:param name="action" value="edit"/></portlet:actionURL>">
</cmsc:portletmode>

<mm:cloud method="asis">
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">
	
		<cmsc:portletmode name="edit">
			<mm:relatednodes type="contentchannel" role="creationrel" searchdir="destination">
				<mm:field name="number" write="false" jspvar="channelnumber"/>
				<cmsc:isallowededit channelNumber="${channelnumber}">
					<c:set var="edit" value="true"/>
				</cmsc:isallowededit>
			</mm:relatednodes>
		</cmsc:portletmode>
		
		<cmsc:field elementid="${elementId}" name="title" edit="${edit}" container="h2"/>	
		<cmsc:field elementid="${elementId}" name="body" edit="${edit}" options="minHeight:300, htmlarea:true, formId:'contentportlet'"/>
	</mm:node>
</mm:cloud>

<cmsc:portletmode name="edit">
	</form>
</cmsc:portletmode>

<cmsc:portletmode name="edit">
	</form>
</cmsc:portletmode>
</div>