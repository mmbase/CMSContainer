<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil,com.finalist.cmsc.repository.RepositoryUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="urls.title">
<script src="../repository/search.js" type="text/javascript"></script>
<script src="../resources/assetsearch.js" type="text/javascript"></script>
<script type="text/javascript">
	function showInfo(objectnumber) {
		openPopupWindow('urlinfo', '900', '500',
				'../resources/urlinfo.jsp?objectnumber=' + objectnumber);
	}
	</script>
	   <link rel="stylesheet" type="text/css" href="../css/assetsearch.css" />
	   </cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either often or search --%>
<mm:import externid="assetShow">list</mm:import><%-- either list or thumbnail --%>

<div class="tabs"><!-- actieve TAB -->
   <div class="tab_active">
      <div class="body">
         <div><a><fmt:message key="urls.title" /></a></div>
      </div>
   </div>
</div>

   <div class="editor" style="height:500px">
      <mm:import id="formAction">/editors/resources/UrlAction</mm:import>
      <mm:import id="channelMsg"><fmt:message key="urls.results" /></mm:import>
      <div class="body">
         <html:form action="${formAction}" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="assetShow" value="${assetShow}"/>
            <html:hidden property="strict" value="${strict}"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <mm:import id="contenttypes" jspvar="contenttypes">urls</mm:import>
               <%@include file="urlform.jsp" %>
         </html:form>
      </div>

   <div class="ruler_green">
         <div><c:out value="${channelMsg}" /></div>
   </div>
   <div class="show_mode_selector">
   <select name="urlMode" id="urlMode" onchange="javascript:setShowMode()">
      <c:if test="${assetShow eq 'list'}">
         <option id="a_list" selected="selected"><fmt:message key="asset.url.list"/></option>
         <option id="a_thumbnail"><fmt:message key="asset.url.thumbnail"/></option>
      </c:if>
      <c:if test="${assetShow eq 'thumbnail'}">
         <option id="a_list"><fmt:message key="asset.url.list"/></option>
         <option id="a_thumbnail" selected="selected"><fmt:message key="asset.url.thumbnail"/></option>
      </c:if>
   </select>
   </div>

   <mm:import externid="results" jspvar="nodeList" vartype="List" />
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
         <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
         <c:if test="${resultCount == 0 && param.title != null}">
            <div class="no_results">
               <fmt:message key="urlsearch.noresult" />
            </div>
         </c:if>
            <div class="body" style="max-height:400px;overflow-y:auto; overflow-x:hidden">
         <c:if test="${resultCount > 0}">
            <%@include file="../repository/searchpages.jsp" %>

            <c:if test="${assetShow eq 'thumbnail'}">
            <div id="assetList" class="hover" style="width:100%">
                  <mm:listnodes referid="results">
                     <c:if test="${ empty strict}">
                       <c:set var="url">javascript:selectElement('<mm:field name="number" />', '<mm:field name="title" escape="js-single-quotes"/>','<mm:field name="url" />');</c:set>
                     </c:if>
                     <div class="grid" href="${url}" onMouseOut="javascript:hideIcons(<mm:field name='number'/>)" onMouseOver="showIcons(<mm:field name='number'/>)">
                        <div id="thumbnail-icons-<mm:field name='number'/>" class="thumbnail-icons">
                            <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="urlsearch.icon.info" />" title="<fmt:message key="urlsearch.icon.info" />"/></a>
                        </div>
                        <div class="thumbnail" onclick="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
                           <c:set var="thumbnail_alt"><mm:field name="url" /></c:set>
                           <img src="../gfx/url.gif" title="${thumbnail_alt}" alt="${thumbnail_alt}"/>
                        </div>
                        <div class="assetInfo">
                              <mm:field name="title" jspvar="title" write="false"/>
                              ${fn:substring(title, 0, 40)}<c:if test="${fn:length(title) > 40}">...</c:if>
                              <br/>
                              <mm:field name="valid" write="false" jspvar="isValidUrl"/>
                              <c:choose>
                                   <c:when test="${empty isValidUrl}">
                                       <fmt:message key="urlsearch.validurl.unknown" />
                                   </c:when>
                                   <c:when test="${isValidUrl eq false}">
                                       <fmt:message key="urlsearch.validurl.invalid" />
                                   </c:when>
                                   <c:when test="${isValidUrl eq true}">
                                       <fmt:message key="urlsearch.validurl.valid" />
                                   </c:when>
                                   <c:otherwise>
                                       <fmt:message key="urlsearch.validurl.unknown" />
                                   </c:otherwise>
                               </c:choose>
                        </div>
                     </div>
                  </mm:listnodes>
            </div>
            </c:if>

         <c:if test="${assetShow eq 'list'}">
             <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
                <mm:field id="trashnumber" name="number" write="false"/>
            </mm:node>
            <table>
                  <tr class="listheader">
                     <th width="55"></th>
                     <th nowrap="true"><a href="javascript:orderBy('title')" class="headerlink"><fmt:message
                        key="urlsearch.titlecolumn" /></a></th>
                     <th nowrap="true"><a href="javascript:orderBy('url')"
                        class="headerlink"><fmt:message key="urlsearch.urlcolumn" /></a></th>
                     <th nowrap="true"><a href="javascript:orderBy('valid')"
                        class="headerlink"><fmt:message
                        key="urlsearch.validcolumn" /></a></th>
                     <th><fmt:message
							key="search.creationchannelcolumn" />
                     </th>
                  </tr>
               <tbody id="assetList" class="hover">
               <c:set var="useSwapStyle">true</c:set>
               <mm:listnodes referid="results">
                  <mm:relatednodes role="creationrel" type="contentchannel">
                     <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
                     <mm:field name="number" jspvar="channelNumber" write="false"/>
                     <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
                     <mm:compare referid="trashnumber" referid2="creationnumber" inverse="true">
                         <mm:field name="name" jspvar="channelName" write="false"/>
                         <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
                         <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
                         <mm:field name="path" id="fullChannelPath" write="false" />
                     </mm:compare>
                  </mm:relatednodes>
               <c:if test="${ empty strict}">
                  <c:set var="url">javascript:selectElement('<mm:field name="number" />', '<mm:field name="title" escape="js-single-quotes"/>','<mm:field name="url" />');</c:set>
               </c:if>
                  <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="${url}">
                     <td style="white-space:nowrap;">
                         <a href="javascript:showInfo(<mm:field name="number" />)">
                               <img src="../gfx/icons/info.png" title="<fmt:message key="urlsearch.icon.info" />" /></a>
                     </td>
                     <mm:field name="title" jspvar="title" write="false"/>
                     <td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">${fn:substring(title, 0, 40)}<c:if test="${fn:length(title) > 40}">...</c:if></td>
                     <mm:field name="url" jspvar="url" write="false"/>
                     <td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">${fn:substring(url, 0, 40)}<c:if test="${fn:length(url) > 40}">...</c:if></td>
                     <mm:field name="valid" write="false" jspvar="isValidUrl"/>
                     <td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
                         <c:choose>
                             <c:when test="${empty isValidUrl}">
                                 <fmt:message key="urlsearch.validurl.unknown" />
                             </c:when>
                             <c:when test="${isValidUrl eq false}">
                                 <fmt:message key="urlsearch.validurl.invalid" />
                             </c:when>
                             <c:when test="${isValidUrl eq true}">
                                 <fmt:message key="urlsearch.validurl.valid" />
                             </c:when>
                             <c:otherwise>
                                 <fmt:message key="urlsearch.validurl.unknown" />
                             </c:otherwise>
                         </c:choose>
                     </td>
                     <td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
                        <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}" />
                        <span title="${fullChannelPath}">${channelName}</span>
                     </td>
                  </tr>
                  <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
               </mm:listnodes>
            </tbody>
         </table>
   </c:if>
</c:if>
<c:if test="${resultCount > 0}">
<%@include file="../repository/searchpages.jsp" %>
</c:if>
</div>
</div>
<c:set var="content_type"><fmt:message key="asset.search.url" /></c:set>
<%@include file="footbar.jsp" %>
</mm:cloud>
</body>
</html:html>
</mm:content>
