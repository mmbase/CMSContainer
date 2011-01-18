<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" %>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.navigation.*" %>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
	<mm:import externid="showsubsite" jspvar="newsubsite" />
	<mm:node referid="showsubsite" jspvar="node">
		<mm:import id="pagepath">../../<%= NavigationUtil.getPathToRootString(node, !ServerUtil.useServerName()) %></mm:import>
	</mm:node>
</mm:cloud>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<cmscedit:head title="subsiterefresh.title">
		<script type="text/javascript">
			function refreshPages() {
				if( existFrameWithName('pages') ) {
				   refreshFrame('pages');
				}
				if (window.opener) {
					window.close();
				}
				document.location.href = '<mm:write referid="pagepath"/>';
			}

			function existFrameWithName(name, win, parentcall) {
				if (!win) {
				   if (!existFrameWithName(name, window)) {
					  return false;
				   }
				   else {
					  return true;
				   }
				}
				if (win.name == name) {
				   return true;
				}
				else {
				   for (var i = 0; i < win.frames.length; i++) {
					  if(existFrameWithName(name, win.frames[i], true)) {
						 return true;
					  }
				   }
				   if (win.parent && win != parent && !parentcall) {
					  return existFrameWithName(name, win.parent);
				   }
				   if (win.opener) {
					  return existFrameWithName(name, win.opener);
				   }
				   return false;
			}
         }
		</script>
	</cmscedit:head>
	<body onload="refreshPages()">
		<fmt:message key="subsiterefresh.message" />...
	</body>
</html:html>