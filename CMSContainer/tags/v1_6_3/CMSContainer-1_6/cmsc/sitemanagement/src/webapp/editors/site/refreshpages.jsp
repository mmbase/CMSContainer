<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" %>
<%@include file="globals.jsp"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<cmscedit:head title="pagerefresh.title">
		<script type="text/javascript">
			function refreshPages() {
				if(existFrameWithName('pages') ) {
               refreshFrame('pages');
            }
				if (window.opener) {
					window.close();
				}
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
	<body onload="refreshPages()"></body>
</html:html>