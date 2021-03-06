<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="search.title">
   <link href="../css/simpleeditor.css" rel="stylesheet" type="text/css"/>
   <script src="../repository/search.js" type="text/javascript"></script>
   <script src="../repository/content.js" type="text/javascript"></script>
   <script type="text/javascript">
      <c:if test="${not empty param.message}">
         addLoadEvent(alert('${param.message}'));
      </c:if>
      <c:if test="${not empty requestScope.workflowmessage}">
         addLoadEvent(alert('${requestScope.workflowmessage}'));
      </c:if>
      function selectChannel(channel, returnpath){
         var newDirection=document.forms[0].direction.value;
         var type=document.forms[0].order.value;
         var offset = document.forms[0].offset.value;
         var pagerDOToffset = '';
         var pagepath = document.location.toString().substr(0, document.location.toString().indexOf("editors"));
         pagepath += "editors/repository/MoveContentFromSearch.do?returnpath=" + returnpath + "&newparentchannel=" + channel + "&objectnumber=" + moveContentNumber+"&orderby="+type+"&direction="+newDirection+'&offset='+offset+'&pager.offset='+pagerDOToffset;
         document.location = pagepath;
      }
   </script>
</cmscedit:head>
<body>
<mm:import externid="returnurl"/>
<mm:import externid="results" from="request" required="true"/>
<mm:import externid="resultCount" from="request" vartype="Integer">0</mm:import>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
<mm:cloud jspvar="cloud" loginpage="../../editors/simple/login.jsp">

<c:set var="listUrl" value="${statustype == 'ready'?'readylist.jsp':'draftlist.jsp'}"/>
<div class="tabs">
    <!-- active TAB -->
	<a href="../simple/SimpleContentDraftAction.do" name="activetab">
    <div class="${(statustype == 'draft' || statustype == null)?'tab_active':'tab'}">
        <div class="body">
            <div class="title">
                <fmt:message key="simple.editor.draft" />
            </div>
        </div>
    </div>
   </a>
	<c:if test="${single =='false' || statustype == 'ready'}">
	 <a href="../simple/SimpleContentReadyAction.do">
		<div class="${statustype == 'ready'?'tab_active':'tab'}">
		  <div class="body">
			 <div class="title">
			   <fmt:message key="simple.editor.ready" />
			 </div>
		  </div>
	   </div>
   </a>
   </c:if>
</div>
<jsp:include page="${listUrl}"/> 
</mm:cloud>
</body>
</html:html>
</mm:content>