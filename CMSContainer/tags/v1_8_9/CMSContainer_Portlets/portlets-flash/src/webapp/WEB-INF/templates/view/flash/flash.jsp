<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@ taglib uri="http://finalist.com/cmsc/flash" prefix="flash" %>

<mm:content escaper="text/xml">
	<mm:cloud>
		<mm:node number="${elementId}" notfound="skip">
			<%-- save the attachment url (to the swf) --%>
			<mm:relatednodes type="attachments" role="posrel" searchdir="destination" max="1">
				<mm:attachment jspvar="swfUrl" write="false" />
			</mm:relatednodes>

			<%-- 
				output the flash content. The body of this tag is the 'alternative content'
				if people don't have flash installed. 
			--%>
			<flash:flash swfUrl="${swfUrl}" width="${movieWidth}" height="${movieHeight}">
				<flash:param name="wmode" value="transparent" />

				<mm:relatednodes type="images" role="imagerel" searchdir="destination">
					<%-- alternative content (image) --%>
					<mm:image template="s(${movieWidth}x${movieHeight}>)">
						<img 
							src="${_}" 
							title="${_node.title}" 
							alt="${_node.title}" 
							width="${dimension.width}" 
							height="${dimension.height}" 
						/>
					</mm:image>
				</mm:relatednodes>
			</flash:flash>
		</mm:node>
	</mm:cloud>
</mm:content>