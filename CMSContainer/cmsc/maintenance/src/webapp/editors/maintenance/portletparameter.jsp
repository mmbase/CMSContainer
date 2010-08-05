<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.cmsc.util.ThreadUtil" %>
<%@include file="globals.jsp" %>

<%@page import="org.mmbase.remotepublishing.PublishManager"%>
<%@page import="org.mmbase.remotepublishing.CloudInfo"%>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="threads.title" />
<body>
<b>Portlet parameter instead of node parameters:</b><br/>
    <% if (ServerUtil.isStaging()) { %>	
		<mm:list path="portletparameter,portlet,page" constraints="portletparameter.key='relatedPage'">
			<c:set var="pageNumber"><mm:field name="page.number"/></c:set>
			<c:set var="portletNumber"><mm:field name="portlet.number"/></c:set>
			<c:set var="portletParameterNumber"><mm:field name="portletparameter.number"/></c:set>
	
			<mm:node number="${pageNumber}">
				<mm:field name="path"/>
			</mm:node>
	
			<mm:remove referid="portlet"/>
			<mm:node number="${portletNumber}" id="portlet">
				<mm:field name="title"/>
			</mm:node>
			
			<c:set var="size" value="0"/>
			<mm:list path="portlet,nodeparameter" constraints="portlet.number = ${portletNumber}">
				<mm:first><c:set var="size"><mm:size/></c:set></mm:first>
			</mm:list>
			<c:if test="${size == 0}">
				<c:set var="key"><mm:field name="portletparameter.key"/></c:set>
				<c:set var="value"><mm:field name="portletparameter.value"/></c:set>
				<mm:remove referid="nodeparameter"/>
				<mm:createnode type="nodeparameter" id="nodeparameter">
					<mm:setfield name="key">${key}</mm:setfield>
					<mm:setfield name="value">${value}</mm:setfield>
				</mm:createnode>
				
				<mm:createrelation role="parameterrel" source="portlet" destination="nodeparameter"/>
				
				<%try{ %>
				<mm:deletenode number="${portletParameterNumber}" deleterelations="true"/>
				<% }catch (Exception e) {
				} %>

				<mm:node referid="nodeparameter" jspvar="node">
					<% 
					CloudInfo localCloudInfo = CloudInfo.getCloudInfo(cloud);
					int remoteCloudNumber = org.mmbase.remotepublishing.CloudManager.getLocalCloudNumber("live.server");
			      	CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
			      	PublishManager.createNodeAndRelations(localCloudInfo, node, remoteCloudInfo);
					
					%>
				</mm:node>

				<mm:node referid="portlet" jspvar="node">
					<% 
					CloudInfo localCloudInfo = CloudInfo.getCloudInfo(cloud);
			      	PublishManager.updateNodeAndRelations(localCloudInfo, node);
					%>
				</mm:node>

				// created node parameter and removed portlet parameter


			</c:if>
			<c:if test="${size > 0}">
				<b>Already has node parameter, fix by hand!</b>
			</c:if>
			<br/>
		</mm:list>
	
		<br/><br/><b>Related portlets with no node parameters:</b><br/>
		<mm:list path="portletdefinition,portlet,page" constraints="portletdefinition.definition='relatedcontentportlet'">
			<c:set var="pageNumber"><mm:field name="page.number"/></c:set>
			<c:set var="portletNumber"><mm:field name="portlet.number"/></c:set>
			
			<c:set var="size" value="0"/>
			<mm:list path="portlet,nodeparameter" constraints="portlet.number = ${portletNumber}">
				<mm:first><c:set var="size"><mm:size/></c:set></mm:first>
			</mm:list>
			
			<c:if test="${size == 0}">
				<mm:node number="${pageNumber}">
					<mm:field name="path"/>
				</mm:node>
		
				<mm:remove referid="portlet"/>
				<mm:node number="${portletNumber}" id="portlet">
					<mm:field name="title"/>
				</mm:node>
	
				<mm:remove referid="nodeparameter"/>
				<mm:createnode type="nodeparameter" id="nodeparameter">
					<mm:setfield name="key"> relatedPage</mm:setfield>
					<mm:setfield name="value">${pageNumber}</mm:setfield>
				</mm:createnode>
				
				<mm:createrelation role="parameterrel" source="portlet" destination="nodeparameter"/>
				
				<mm:node referid="nodeparameter" jspvar="node">
					<% 
					CloudInfo localCloudInfo = CloudInfo.getCloudInfo(cloud);
					int remoteCloudNumber = org.mmbase.remotepublishing.CloudManager.getLocalCloudNumber("live.server");
			      	CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
			      	PublishManager.createNodeAndRelations(localCloudInfo, node, remoteCloudInfo);
					
					%>
				</mm:node>
				// created and published node parameter <br/>
			</c:if>
		</mm:list>
    <% } %>	
</body>
</html:html>
</mm:cloud>
</mm:content>