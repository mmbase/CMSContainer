<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="newslettereditiondelete.title">
	<style type="text/css">
	input { width: 100px;}
	</style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="newslettereditiondelete.title" titleClass="side_block_green">
	<p>
		<fmt:message key="newslettereditiondelete.subtitle" /> <mm:node referid="number"><b><mm:field name="title"/></b></mm:node>
	</p>
	<p>
		<fmt:message key="newslettereditiondelete.confirm" />
	</p>
	<form action="?">
		<html:hidden property="number" value="${number}" />
			<html:submit property="remove"><fmt:message key="newslettereditiondelete.yes"/></html:submit>&nbsp;
			<html:submit property="cancel"><fmt:message key="newslettereditiondelete.no"/></html:submit>
			<input type="hidden" name="parent" value="${requestScope.parent}"/>
			<input type="hidden" name="forward" value="${requestScope.forward}"/>
	</form>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>