<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
<cmscedit:head title="search.title">
      <script src="../repository/content.js" type="text/javascript"></script>
      <script type="text/javascript">
         function selectItem(channel, path) {
            opener.selectChannel(channel, path, '${param.fieldName}', '${param.fieldPathName}');
            close();
         }
      </script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/simple/login.jsp">
<c:set var="swap" value="0"/>
<table>
<c:forEach var="channel" items="${channelList}">
   <mm:node number="${channel.number}" >
   <tr <c:if test="${swap % 2 ==0}">class="swap"</c:if> onmousedown="javascript:selectItem(<mm:field name="number"/>,'')">
      <c:set var="swap">${swap+1}</c:set>
      <td style="white-space: nowrap;">
         <mm:field name="name" />
      </td>
   </tr>
   </mm:node>
</c:forEach>
</table>
</mm:cloud>
</body>
</html:html>
</mm:content>