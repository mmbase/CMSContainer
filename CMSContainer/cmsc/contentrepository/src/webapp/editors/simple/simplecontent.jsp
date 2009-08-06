<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="search.title">
   <script src="../repository/search.js" type="text/javascript"></script>
      <script src="../repository/content.js" type="text/javascript"></script>
      <script type="text/javascript">
         function selectChannel(channel, path){
             var newDirection=document.forms[0].direction.value;
             var type=document.forms[0].order.value;
             var offset = document.forms[0].offset.value;
             var pagerDOToffset = '';
             var path = document.location.toString().substr(0, document.location.toString().indexOf("editors"));
             path += "editors/repository/MoveContentFromSearch.do?from=simpleeditor&newparentchannel=" + channel + "&objectnumber=" + moveContentNumber+"&orderby="+type+"&direction="+newDirection+'&offset='+offset+'&pager.offset='+pagerDOToffset;
             document.location = path;
         }
      </script>
</cmscedit:head>
<body>
<mm:import externid="returnurl"/>
<mm:import externid="results" from="request" required="true"/>
<mm:import externid="resultCount" from="request" vartype="Integer">0</mm:import>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
<c:set var="returnurl" value="../editors/simple/SimpleContentInitAction.do"/>
<c:set var="typesNumber"  value="${fn:length(typesList)}" />
<c:set var="channelsNumber"  value="${fn:length(channelsList)}" />

<mm:cloud jspvar="cloud" loginpage="../../editors/simple/login.jsp">

<c:set var="listUrl" value="${statustype == 'ready'?'readylist.jsp':'draftlist.jsp'}"/>
<div class="tabs">
    <!-- active TAB -->
    <div class="${(statustype == 'draft' || statustype == null)?'tab_active':'tab'}">
        <div class="body">
            <div>
                <a href="SimpleContentDraftAction.do" name="activetab"><fmt:message key="simple.editor.draft" /></a>
            </div>
        </div>
    </div>
	<c:if test="${single =='false' || statustype == 'ready'}">
    <div class="${statustype == 'ready'?'tab_active':'tab'}">
      <div class="body">
         <div>
            <a href="SimpleContentReadyAction.do"><fmt:message key="simple.editor.ready" /></a>
         </div>
      </div>
   </div>
   </c:if>
</div>
<jsp:include page="${listUrl}"/> 
</mm:cloud>
</body>
</html:html>
</mm:content>