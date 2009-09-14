<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@taglib uri="http://finalist.com/cmsc/flash" prefix="flash" %>

<mm:cloud>
	<%-- this is the 'movie' node --%>
	<mm:node notfound="skip">
		<%-- 'attachment' movie --%>
		<cmsc:protected>
			<br /><br />
		</cmsc:protected>
		<mm:relatednodes path="posrel,attachments" searchdirs="destination">
			<%-- if we find an attachment, we assume this is a playable file --%>
			<mm:attachment jspvar="videoFile" write="false" />
			<%@include file="flowplayer.jsp" %>
		</mm:relatednodes>

		<%-- 'url' movie --%>
		<mm:relatednodes path="posrel,urls" searchdirs="destination">
			<c:set var="videoFile" value="${ic:parseFlashUrl(_node.url)}" />
		
			<flash:flash id="yt_${_node.number}" swfUrl="${videoFile}" width="530" height="425" styleClass="movie-youtube">
				<flash:param name="allowscriptaccess" value="always" />
				<flash:param name="allowfullscreen" value="true" />
			</flash:flash>
		</mm:relatednodes>
	</mm:node>
</mm:cloud>
