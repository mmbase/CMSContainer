<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><%@page import="com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"
%><%@page import="org.mmbase.bridge.Cloud" 
%><%@page import="org.mmbase.bridge.Node" 
%><%@page import="org.mmbase.bridge.NodeList" 
%><%@page import="org.mmbase.bridge.util.SearchUtil" 
%><%@page import="com.finalist.cmsc.subsite.util.SubSiteUtil" 
%><%@page import="com.finalist.cmsc.services.publish.Publish"
%><%@page import="org.mmbase.bridge.BridgeException"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="site.personal.personalpages">
	<script src="<cmsc:staticurl page='/editors/repository/content.js'/>" type="text/javascript"></script>
	<script src="<cmsc:staticurl page='/editors/repository/search.js'/>" type="text/javascript"></script>
</cmscedit:head>
<body>
<script type="text/javascript">
    <c:if test="${not empty param.message}">
    addLoadEvent(alert('${param.message}'));
    </c:if>
    <c:if test="${not empty param.refreshchannel}">
    addLoadEvent(refreshChannels);
    </c:if>
    addLoadEvent(alphaImages);
</script>

<mm:import id="searchinit"><c:url value='/editors/repository/SearchInitAction.do'/></mm:import>
<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
<mm:import externid="returnurl"/>

<mm:import externid="subsite" jspvar="subsiteold" from="parameters" />
<mm:import externid="from" from="parameters" />

<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

<div class="content">
   <div class="tabs">
     <a href="#" onClick="selectTab('basic');">
       <div class="tab_active">
         <div class="body">
            <div class="title">
               <fmt:message key="site.personal.personalpages" />
            </div>
         </div>
       </div>
	  </a>
   </div>
</div>

<div class="editor">
<div class="body">

<mm:listnodes type="subsite" orderby="title">
<mm:first>
   <c:set var="subsiteExists" value="true"/>
   <p><%@include file="personalpages_newbuttons.jsp" %></p>
</mm:first>
</mm:listnodes>

<html:form action="/editors/subsite/SubSiteAction" method="post">
	<html:hidden property="action" value="${action}"/>
	<html:hidden property="search" value="true"/>
	<html:hidden property="offset"/>
	<html:hidden property="pager.offset" value="${pagerDOToffset}"/>
	<html:hidden property="order"/>
	<html:hidden property="direction"/>
	<input type="hidden" name="from" value="${from}" />
	<mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
	
	<table>
     <c:if test="${subsiteExists eq true}">
        <tr>
           <td style="width:105px"><fmt:message key="subsite.name" />:</td>
           <td>
              <cmsc:select var="subsite" default="${subsiteold}" onchange="document.forms[0].submit();">
              <mm:listnodes type="subsite" orderby="title">
                 <mm:field name="number" id="subsitenumber" write="false" vartype="String" />
                 <cmsc:option value="${subsitenumber}" name="${_node.title}" />
              </mm:listnodes>
              </cmsc:select>
            </td>
        </tr>
     </c:if>
     <c:if test="${subsiteExists ne true}">
       <tr>
          <td colspan="2"  style="font-size:12px;margin-left:11px"><fmt:message key="subsite.notfound" /></td>
       </tr>
	  </c:if>
    <tr>
       <td style="width:105px"><fmt:message key="subsitedelete.subtitle" /></td>
       <td colspan="3"><html:text property="title" style="width:200px"/></td>
       
       <td style="width:20px">
       </td>
   </tr>
   <tr>
      <td></td>
   <td>
     <input type="submit" class="button" name="submitButton" onClick="setOffset(0, 0);" value="<fmt:message key="site.personal.search" />"/>
     </td>
   </tr>
	</table>
</html:form>
</div>
</div>

<div class="editor">
<br />

<div class="ruler_green"><div><fmt:message key="site.personal.personalpages"/></div></div>
<div class="body">


<c:set var="listSize" value="${resultCount}"/>
<c:set var="resultsPerPage" value="${SearchForm.keywords}"/>
<c:set var="offset" value="${SearchForm.offset}"/>
<c:set var="extraparams" value="&subsite=${subsite}&title=${SearchForm.title}&order=${SearchForm.order}"/>

<mm:isempty referid="results" inverse="true">
   <edit:pages search="false" totalElements="${listSize}" elementsPerPage="${resultsPerPage}" offset="${offset}" extraparams="${extraparams}"/>
</mm:isempty>

<table>
<thead>
    <tr>
        <th></th>
        <th><a href="#" class="headerlink" onClick="orderBy('title');" ><fmt:message key="pp.title" /></a></th>
        <th><a href="#" class="headerlink" onclick="orderBy('creationdate');" ><fmt:message key="pp.creationdate" /></a></th>
        <th><a href="#" class="headerlink" onclick="orderBy('publishdate');" ><fmt:message key="pp.publishdate" /></a></th>
    </tr>
</thead>
<tbody class="hover">

<mm:list referid="results" jspvar="node" max="${resultsPerPage}">
   <mm:field name="personalpage.number" id="number">
	   <mm:node number="${number}" jspvar="ppNode">
		   <tr <mm:even inverse="true">class="swap"</mm:even>>
		   <td style="white-space: nowrap;">
		   
		   <mm:field name="number"  write="false" id="nodenumber">
		<c:if test="${not empty subsiteold}" >
			<c:set var="editPath" value="../subsite/SubSiteEdit.do?number=${nodenumber}&subsite=${subsiteold}&from=${from}" />
			<c:set var="deletePath" value="../subsite/SubSiteDelete.do?number=${nodenumber}&subsite=${subsiteold}&from=${from}" />
		</c:if>
		<c:if test="${empty subsiteold}" >
			<c:set var="editPath" value="../subsite/SubSiteEdit.do?number=${nodenumber}&subsite=${subsite}&from=${from}" />
			<c:set var="deletePath" value="../subsite/SubSiteDelete.do?number=${nodenumber}&subsite=${subsite}&from=${from}" />
		</c:if>
         <a href="${editPath}"
		       title="<fmt:message key="pp.content.edit" />"><img src="../gfx/icons/edit.png" width="16" height="16"
		                                                       title="<fmt:message key="pp.content.edit" />"
		                                                       alt="<fmt:message key="pp.content.edit" />"/></a>
		  
        <c:set var="remoteUrl"><%=Publish.getRemoteUrl(ppNode)%></c:set>
         <c:if test="${not empty remoteUrl}">
            <a href="${remoteUrl}" 
               title="<fmt:message key="pp.content.preview" />" target="_blank"><img src="../gfx/icons/preview.png" width="16" height="16"
                                                             title="<fmt:message key="pp.content.preview" />"
                                                             alt="<fmt:message key="pp.content.preview" />"/></a>
         </c:if>
          <a href="${deletePath}"
		       title="<fmt:message key="pp.content.delete" />"><img src="../gfx/icons/delete.png" width="16" height="16"
		                                                       title="<fmt:message key="pp.content.delete" />"
		                                                       alt="<fmt:message key="pp.content.delete" />"/></a>
		   <% request.removeAttribute("appPath"); %>              
		   </mm:field>
		   </td>
		   <td>
		      <b><mm:field name="title" /></b>
		   </td>
		   <%--
		   <td>
		   <a href="../subsite/PersonalPageElements.do?personalpage=<mm:field name="number" />">Edit Articles</a>
		   </td>
         --%>
		   
		   <td>
            <mm:field name="creationdate"><cmsc:dateformat displaytime="true"/></mm:field>
         </td>
         <td>
		      <mm:field name="publishdate"><cmsc:dateformat displaytime="true"/></mm:field>
		   </td>
		   
		   </tr>
	   </mm:node>
   </mm:field>
</mm:list>
<%-- Now print if no results --%>
<mm:isempty referid="results">
   <tr><td style="font-size:12px;padding-left:11px"><fmt:message key="site.personal.nonefound" /></td></tr>
</mm:isempty>
</tbody>
</table>

<mm:isempty referid="results" inverse="true">
<edit:pages search="false" totalElements="${listSize}" elementsPerPage="${resultsPerPage}" offset="${offset}" extraparams="${extraparams}"/>
</mm:isempty>
   
<br />

</div>
</div>

</mm:cloud>

</body>
</html:html>
</mm:content>