<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil,com.finalist.cmsc.repository.RepositoryUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="attachments.title">
   <script src="../repository/search.js" type="text/javascript"></script>
   <script src="../resources/assetsearch.js" type="text/javascript"></script>
   <script type="text/javascript">
   function setDefaultSearchCondition(){
      <c:if test="${not empty title}">
	      document.forms[0].title.value = "${title}";
      </c:if><c:if test="${not empty description}">
	      document.forms[0].description.value = "${description}";
      </c:if>
    }
	function showInfo(objectnumber) {
		openPopupWindow('attachmentinfo', '900', '500',
				'../resources/attachmentinfo.jsp?objectnumber=' + objectnumber);
	}
</script>
   <link rel="stylesheet" type="text/css" href="../css/assetsearch.css" />
	</cmscedit:head>
<body style="overflow-x:hidden" onload="setDefaultSearchCondition()">
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either often or search --%>
<mm:import externid="assetShow">list</mm:import><%-- either list or thumbnail --%>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
   <c:if test="${action eq 'search'}">
      <div class="tabs"><!-- actieve TAB -->
	  <a>
		  <div class="tab_active">
			  <div class="body">
				  <div class="title"><fmt:message key="attachments.title" /></div>
			  </div>
		  </div>
		</a>
		<a href="attachmentupload.jsp?uploadAction=${action}&uploadedNodes=0&strict=${param.strict}">
		<div class="tab">
		 <div class="body">
		   <div class="title">
			   <fmt:message key="attachments.upload.title" />
		   </div>
		 </div>
	    </div>
	   </a>
      </div>
   </c:if>

   <div class="editor">

      <mm:import id="formAction">/editors/resources/AttachmentAction</mm:import>
      <mm:import id="channelMsg"><fmt:message key="attachments.results" /></mm:import>
      <div class="body" >
         <html:form action="${formAction}" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="assetShow" value="${assetShow}"/>
            <html:hidden property="strict" value="${strict}"/>
            <html:hidden property="offset"/>
            <html:hidden property="pager.offset" value="${pagerDOToffset}"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <mm:import id="contenttypes" jspvar="contenttypes">attachments</mm:import>
            <%@include file="attachmentform.jsp" %>
         </html:form>
      </div>
      <div class="ruler_green">
         <div><c:out value="${channelMsg}" /></div>
      </div>
      <div class="show_mode_selector">
		<select name="attachmentMode" id="attachmentMode" onchange="javascript:setShowMode()">
			<c:if test="${assetShow eq 'list'}">
				<option id="a_list" selected="selected"><fmt:message key="asset.attachment.list"/></option>
				<option id="a_thumbnail"><fmt:message key="asset.attachment.thumbnail"/></option>
			</c:if>
			<c:if test="${assetShow eq 'thumbnail'}">
				<option id="a_list"><fmt:message key="asset.attachment.list"/></option>
				<option id="a_thumbnail" selected="selected"><fmt:message key="asset.attachment.thumbnail"/></option>
			</c:if>
		</select>
      </div>

         <mm:import externid="results" jspvar="nodeList" vartype="List"/>
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
         <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
         <c:if test="${resultCount == 0 && param.title != null}">
         <div class="no_results">
            <fmt:message key="attachmentsearch.noresult" />
         </div>
         </c:if>
         <div class="body">
         <c:if test="${resultCount > 0}">
            <edit:pages search="true" totalElements="${resultCount}" offset="${offset}"/>

            <c:if test="${assetShow eq 'thumbnail'}">
            <div id="assetList" class="hover" style="width:100%">
                  <mm:listnodes referid="results">
                     <mm:field name="description" escape="js-single-quotes" jspvar="description">
                        <%
                           description = ((String) description).replaceAll("[\\n\\r\\t]+", " ");
                        %>
                        <c:if test="${ empty strict}">
                        	<c:set var="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','128','128', '<%=description%>');</c:set>
                        </c:if>
                     </mm:field>
                     <div class="grid" href="${url}" onMouseOut="javascript:hideIcons(<mm:field name='number'/>)" onMouseOver="showIcons(<mm:field name='number'/>)">
                        <div id="thumbnail-icons-<mm:field name='number'/>" class="thumbnail-icons">
                            <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="attachmentsearch.icon.info" />" title="<fmt:message key="attachmentsearch.icon.info" />"/></a>
                        </div>
                        <div class="thumbnail" onclick="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
	                         <c:set var="typedef" ><mm:nodeinfo type="type"/></c:set>
	                         <c:if test="${typedef eq 'attachments'}">
	                            	<c:set var="filesize"><mm:field name="size"/></c:set>
									<c:if test="${ filesize gt 0}">
                                    <%@include file="../resources/attachmentthumbnail.jsp" %>
									</c:if>
									<c:if test="${ filesize eq 0}">
                                    	<img src="../gfx/nullattachment.gif" alt=""/>
									</c:if>
	                         </c:if>
						</div>
                        <div class="assetInfo">
                              <mm:field id="title" write="false" name="title"/>
                              <c:if test="${fn:length(title) > 15}">
                                 <c:set var="title">${fn:substring(title,0,14)}...</c:set>
                              </c:if>${title}
                              <br/>
                        </div>
                     </div>
                  </mm:listnodes>
            </div>
            </c:if>

			<c:if test="${assetShow eq 'list'}">
				<table>
					<tr class="listheader">
						<th width="55"></th>
						<th nowrap="true"><a href="javascript:orderBy('title')"
							class="headerlink"><fmt:message key="attachmentsearch.titlecolumn" /></a></th>
						<th nowrap="true"><a href="javascript:orderBy('filename')"
							class="headerlink"><fmt:message
							key="attachmentsearch.filenamecolumn" /></a></th>
						<th nowrap="true"><a href="javascript:orderBy('mimetype')"
							class="headerlink"><fmt:message
							key="attachmentsearch.mimetypecolumn" /></a></th>
						<th><fmt:message
							key="search.creationchannelcolumn" /></th>
					</tr>
					<tbody id="assetList" class="hover">
						<c:set var="useSwapStyle">true</c:set>
                  <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
                     <mm:field id="trashnumber" name="number" write="false"/>
                  </mm:node>
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
							<mm:field name="description" escape="js-single-quotes"
								jspvar="description">
								<%
								   description = ((String) description).replaceAll("[\\n\\r\\t]+", " ");
								%>
		                        <c:if test="${ empty strict}">
		                        	<c:set var="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','120','100', '<%=description%>');</c:set>
		                        </c:if>
							</mm:field>
							<tr <c:if test="${useSwapStyle}">class="swap"</c:if>
								href="${url}">
								<td style="white-space: nowrap;">
                        <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="attachmentsearch.icon.info" />" title="<fmt:message key="attachmentsearch.icon.info" />" /></a>
								</td>
                        <td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
                           <mm:field id="title" write="false" name="title"/>
                           <c:if test="${fn:length(title) > 50}">
                              <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                           </c:if>
                           ${title}
                        </td>
                        <td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
                           <mm:field name="filename"/>
                        </td>
								<td onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')"> <mm:field name="mimetype"/></td>
								<td  onMouseDown="addItem(this.parentNode, '<mm:field name="number"/>', '${strict}')">
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
         <div style="clear:both" ></div>
	      <c:if test="${resultCount > 0}">
	         <edit:pages search="true" totalElements="${resultCount}" offset="${offset}"/>
	      </c:if>
      </div>
</div>
<c:set var="content_type"><fmt:message key="asset.search.attachment" /></c:set>
<%@include file="footbar.jsp" %>
</mm:cloud>
</body>
</html:html>
</mm:content>