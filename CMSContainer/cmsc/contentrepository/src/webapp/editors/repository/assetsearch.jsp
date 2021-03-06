<%@page language="java" contentType="text/html;charset=utf-8" 
%><%@include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><%@page import="com.finalist.cmsc.repository.AssetElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"
%><%@ page import="com.finalist.cmsc.security.UserRole"
%><%@ page import="com.finalist.cmsc.security.SecurityUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<!-- we check to see if we have workflow, this is done by looking if the editors for the workflow are on the HD -->
<c:set var="hasWorkflow" value="false"/>
<mm:haspage page="/editors/workflow">
   <c:set var="hasWorkflow" value="true"/>
</mm:haspage>

<html:html xhtml="true">
<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<mm:import externid="returnurl"/>
<mm:import externid="parentchannel" jspvar="parentchannel"/>
<mm:import externid="assettypes" jspvar="assettypes"><%= AssetElementUtil.ASSETELEMENT %></mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>

<cmscedit:head title="search.title">
      <link rel="stylesheet" type="text/css" href="../css/assetsearch.css" />
      <link rel="stylesheet" href="<cmsc:staticurl page='../css/thumbnail.css'/>" type="text/css"/>
      <script src="../../mmbase/edit/wizard/javascript/validator.js" type="text/javascript"></script>
      <script src="../repository/asset.js" type="text/javascript"></script>
      <script src="search.js" type="text/javascript"></script>
            <script type="text/javascript">
               function showEditItems(id){
                  document.getElementById('asset-info-'+id).style.display = 'block';
                  document.getElementById('asset-info-'+id).style.zIndex = 2001;
               }
               function hideEditItems(id){
                  document.getElementById('asset-info-'+id).style.display = 'none';
                  document.getElementById('asset-info-'+id).style.zIndex = 2000;
               }
               function changeMode(offset){
                   if(offset==null){offset=0;}
                   var assetsMode = document.getElementsByTagName("option");3
                   for(i = 0; i < assetsMode.length; i++){
                      if(assetsMode[i].selected & assetsMode[i].id=="a_list"){
                          document.forms[0].searchShow.value = 'list';
                          document.forms[0].submit();
                          //document.location.href = 'AssetSearchAction.do?type=asset&direction=down&searchShow=list&offset='+offset;
                      }else if(assetsMode[i].selected & assetsMode[i].id=="a_thumbnail"){
                          document.forms[0].searchShow.value = 'thumbnail';
                          document.forms[0].submit();
                         //document.location.href = 'AssetSearchAction.do?type=asset&direction=down&searchShow=thumbnail&offset='+offset;
                      }
                   }
                }
					function selectChannel(channel, path) {
					    var newDirection=document.forms[0].direction.value;
					    var type=document.forms[0].order.value;
					    var offset = document.forms[0].offset.value;
					    var pagerDOToffset = document.forms[0]['pager.offset'].value;
					    document.location = "../MoveAssetFromSearch.do?newparentchannel=" + channel + "&objectnumber=" + moveContentNumber+"&orderby="+type+"&direction="+newDirection+'&offset='+offset+'&pager.offset='+pagerDOToffset;
					}
					
				    <c:if test="${not empty param.message}">
				    addLoadEvent(alert('${param.message}'));
				    </c:if>
      </script>
    <c:if test="${not empty requestScope.refreshChannels}">
        <script>
        refreshFrame('channels');
        </script>
    </c:if>
</cmscedit:head>
<body>
<mm:import id="assetsearchinit"><c:url value='/editors/repository/AssetSearchInitAction.do'/></mm:import>

<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<c:if test="${empty strict}">
   <div class="tabs">
    <!-- active TAB -->
	<a href="SearchInitAction.do?index=yes">
		  <div class="tab">
			 <div class="body">
				<div class="title">
				   <fmt:message key="content.search.title" />
				</div>
			 </div>
		  </div>
	  </a>
	  <a href="AssetSearchInitAction.do">
		   <div class="tab_active">
			   <div class="body">
				   <div class="title">
					   <fmt:message key="asset.search.title"/>
				   </div>
			   </div>
		   </div>
	   </a>
   </div>
</c:if>
   <div class="editor">
   <br />
      <div class="body">
         <html:form action="/editors/repository/AssetSearchAction" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="mode"/>
            <html:hidden property="search" value="true"/>
            <html:hidden property="offset"/>
            <html:hidden property="pager.offset" value="${pagerDOToffset}"/>
            <html:hidden property="order"/>
            <html:hidden property="searchShow" value="${searchShow}"/>
            <html:hidden property="direction"/>
            <input type="hidden" name="deleteAssetRequest"/>
            <c:if test="${not empty strict}">
			<input type="hidden" name="assettypes" value="${strict}"/>
			<input type="hidden" name="strict" value="${strict}"/>
			</c:if>
            <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
                <mm:compare referid="mode" value="advanced" >
                   <a href="#" onclick="selectTab('basic');"><input type="button" class="button" value="<fmt:message key="search.simple.search" />"/></a>
                </mm:compare>
                <mm:compare referid="mode" value="basic" >
                        <a href="#" onclick="selectTab('advanced');"><input type="button" class="button" value="<fmt:message key="search.advanced.search" />"/></a>
                </mm:compare>
            <div id="formcontent" >
               <div id="leftform">
                  <table>
                     <tr>
                        <td style="width:80px" width="80px" nowrap><fmt:message key="searchform.title" /></td>
                        <td style="width:217px" width="217px" nowrap><html:text property="title" style="width:145px"  value="${title}" /></td>
                        <td style="width:90px" width="90px" nowrap><fmt:message key="searchform.assettype" /></td>
                        <td>
                           <c:if test="${not empty strict}">
                              ${strict}
                           </c:if>
                           <c:if test="${empty strict}">
                              <html:select property="assettypes" style="width:145px" onchange="selectAssettype('${searchinit}');" >
                                 <html:option value="assetelement">&lt;<fmt:message key="searchform.assettypes.all" />&gt;</html:option>
                                 <html:optionsCollection name="typesList" value="value" label="label"/>
                              </html:select>
                           </c:if>
                        </td>
                     </tr>
                   <mm:compare referid="mode" value= "advanced">
                        <tr>
                           <td></td>
                           <td><b><fmt:message key="searchform.dates" /></b></td>
                           <td></td>
                           <td><b><fmt:message key="searchform.users" /></b></td>
                        </tr>
                        <tr valign="top">
                           <td><fmt:message key="searchform.creationdate" /></td>
                           <td>
                              <html:select style="width:145px" property="creationdate" size="1">
                                 <html:option value="0"> - </html:option>
                                 <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                                 <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                                 <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                                 <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                                 <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                              </html:select>
                           </td>
                           <td><fmt:message key="searchform.personal" /></td>
                           <td>
                              <html:select style="width:145px" property="personal" size="1">
                                 <html:option value=""> - </html:option>
                                 <html:option value="lastmodifier"><fmt:message key="searchform.personal.lastmodifier" /></html:option>
                                 <html:option value="author"><fmt:message key="searchform.personal.author" /></html:option>
                              </html:select>
                           </td>
                        </tr>
                        <tr>
                           <td><fmt:message key="searchform.lastmodifieddate" /></td>
                           <td>
                              <html:select style="width:145px" property="lastmodifieddate" size="1">
                                 <html:option value="0"> - </html:option>
                                 <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                                 <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                                 <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                                 <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                                 <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                              </html:select>
                           </td>
                           <td>
                              <mm:hasrank minvalue="siteadmin">
                                 <fmt:message key="searchform.useraccount" />
                              </mm:hasrank>
                           </td>
                           <td>
                              <mm:hasrank minvalue="siteadmin">
                                 <html:select style="width:145px" property="useraccount" size="1">
                                    <html:option value=""> - </html:option>
                                     <mm:listnodes type='user' orderby='username'>
                                         <mm:field name="username" id="useraccount" write="false"/>
                                        <html:option value="${useraccount}"> <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /> </html:option>
                                     </mm:listnodes>
                                 </html:select>
                              </mm:hasrank>
                           </td>
                        </tr>
                        <tr>
                           <td><fmt:message key="searchform.publishdate" /></td>
                           <td>
                              <html:select style="width:145px" property="publishdate" size="1">
                                 <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                                 <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                                 <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                                 <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                                 <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                                 <html:option value="0"> - </html:option>
                                 <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                                 <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                                 <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                                 <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                                 <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                              </html:select>
                           </td>
                           <td></td>
                           <td><b><fmt:message key="searchform.other" /></b></td>
                        </tr>
                        <tr>
                           <td><fmt:message key="searchform.expiredate" /></td>
                           <td>
                              <html:select style="width:145px" property="expiredate" size="1">
                                 <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                                 <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                                 <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                                 <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                                 <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                                 <html:option value="0"> - </html:option>
                                 <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                                 <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                                 <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                                 <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                                 <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                              </html:select>
                           </td>
                           <td><fmt:message key="searchform.number" /></td>
                           <td><html:text style="width:145px" property="objectid"/></td>
                        </tr>
                       <tr height="31px">
                     <c:choose>
                     <c:when test="${hasWorkflow}">
                          <td><fmt:message key="searchform.state" /></td>
                          <td>
                             <html:select style="width:145px" property="workflowstate" size="1">
                                <html:option value=""><fmt:message key="content.status.all" /></html:option>
                                <html:option value="draft"><fmt:message key="asset.status.draft" /></html:option>
                                <html:option value="finished"><fmt:message key="asset.status.finished" /></html:option>
                                <html:option value="approved"><fmt:message key="asset.status.approved" /></html:option>
                                <html:option value="published"><fmt:message key="asset.status.published" /></html:option>
<%--                                <html:option value="onlive"><fmt:message key="content.status.onlive" /></html:option> --%>
                             </html:select>
                          </td>
                     </c:when>
                     <c:otherwise>
                       <td colspan="2">&nbsp;</td>
                     </c:otherwise>
                     </c:choose>
                           <td><fmt:message key="searchform.description" /></td>
                           <td><html:text style="width:145px" property="description"/></td>
                       </tr>

                        <tr>
                           <td></td>
                           <td></td>
                           <td nowrap>
                              <mm:compare referid="action" value="link">
                                 <mm:write write="false" id="showTreeOption" value="true" />
                              </mm:compare>

                              <mm:compare referid="action" value="selectforwizard">
                                 <mm:write write="false" id="showTreeOption" value="true" />
                              </mm:compare>
                              <mm:present referid="showTreeOption">
                                 <fmt:message key="searchform.select.channel" />
                                 <a href="<c:url value='/editors/repository/select/SelectorChannel.do' />"
                                    target="selectChannel" onclick="openPopupWindow('selectChannel', 340, 400)">
                                 <img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="searchform.select.channel" />"/></a>
                                 <a href="#" onClick="selectChannel('', '');" ><img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="searchform.clear.channel.button" />" /></a>
                              </mm:present>
                           </td>
                           <td>
                              <mm:present referid="showTreeOption">
                              <html:hidden property="parentchannel" />
                                 <html:hidden property="parentchannelpath"/>
                                 <input type="text" name="parentchannelpathdisplay" disabled value="${SearchForm.parentchannelpath}"/><br />
                              </mm:present>
                           </td>
                        </tr>
                     </mm:compare>
                  </table>
               </div>
               <div id="assetrightform">
                  <mm:compare referid="mode" value= "advanced">
                     <c:set var="fields"/>
                     <c:set var="fieldtypes"/>
                     <table>
                        <tr>
                           <td colspan="2" nowrap>
                              <mm:compare referid="assettypes" value="assetelement" inverse="true">
                                 <div style="padding-right:5px;">
                                    <fmt:message key="searchform.searchfor">
                                       <fmt:param><mm:nodeinfo nodetype="${assettypes}" type="guitype"/></fmt:param>
                                    </fmt:message>
                                 </div>
                              </mm:compare>
                           </td>
                        </tr>
                        <tr>
                           <td>
                              <mm:compare referid="assettypes" value="assetelement" inverse="true">
                                 <table>
                                    <mm:fieldlist nodetype="${assettypes}">
                                       <%-- check if the field is from assetelement --%>
                                       <c:set var="showField" value="true"/>
                                       <mm:fieldinfo type="name" id="fname">
                                           <mm:fieldlist nodetype="assetelement">
                                               <mm:fieldinfo type="name" id="cefname">
                                                  <mm:compare referid="fname" referid2="cefname">
                                                     <c:set var="showField" value="false"/>
                                                  </mm:compare>
                                               </mm:fieldinfo>
                                           </mm:fieldlist>
                                       </mm:fieldinfo>
                                       <c:if test="${showField}">
                                      	<mm:fieldinfo type="type" jspvar="field_type" write="false">
											<c:if test="${field_type eq 1 || field_type eq 2}" >
                                          <tr>
                                             <td height="31px" nowrap>
                                                <mm:fieldinfo type="guiname" jspvar="guiname"/>:
                                                <mm:fieldinfo type="name" jspvar="fieldname" write="false">
                                                <c:choose>
                                                   <c:when test="${empty fields}">
                                                      <c:set var="fields">${assettypes}.${fieldname}</c:set>
                                                      <c:set var="fieldtypes"><mm:fieldinfo type="typedescription"/></c:set>
                                                   </c:when>
                                                   <c:otherwise>
                                                      <c:set var="fields">${fields},${assettypes}.${fieldname}</c:set>
                                                      <c:set var="fieldtypes">${fieldtypes},<mm:fieldinfo type="typedescription"/></c:set>
                                                   </c:otherwise>
                                                </c:choose>
                                                </mm:fieldinfo>
                                             </td>
                                          </tr>
											</c:if>
										</mm:fieldinfo>
                                       </c:if>
                                    </mm:fieldlist>
                                 </table>
                              </mm:compare>
                           </td>
                           <td>
                              <mm:compare referid="assettypes" value="assetelement" inverse="true">
                                 <table>
                                    <c:forTokens items="${fields}" var="field" delims="," varStatus="status">
                                       <c:forTokens items="${fieldtypes}" var="fieldtype" delims="," begin="${status.index}" end="${status.index}">
                                       <tr>
                                          <td height="31px" nowrap>
                                             <input type="text" name="${field}" style="width:145px" dttype="${fieldtype}" value="${param.field}" />
                                          </td>
                                       </tr>
                                       </c:forTokens>
                                    </c:forTokens>
                                 </table>
                              </mm:compare>
                           </td>
                        </tr>
                     </table>                          
                  </mm:compare>
               </div>
            </div>
            <div id="bottomform">
               <input type="submit" class="button" name="submitButton" onclick="return validateInputs();" value="<fmt:message key="searchform.submit" />" />
            </div>
         </html:form>
      </div>


   <div class="ruler_green"><div><fmt:message key="searchform.results" /></div></div>
   <div class="body">

   <div style="padding:10px 0px 0px 11px">
   <select name="assesMode" onchange="javascript:changeMode(${param.offset})">
      <c:if test="${empty searchShow || searchShow eq 'list'}">
         <option id="a_list" selected="selected"><fmt:message key="asset.image.list"/></option>
         <option id = "a_thumbnail" ><fmt:message key="asset.image.thumbnail"/></option>
      </c:if>
      <c:if test="${searchShow eq 'thumbnail'}">
         <option id="a_list"><fmt:message key="asset.image.list"/></option>
         <option id = "a_thumbnail" selected="selected" ><fmt:message key="asset.image.thumbnail"/></option>
      </c:if>
   </select>
   </div>

   <%-- Now print if no results --%>
   <mm:isempty referid="results">
   <div style="padding:10px 0px 0px 11px">
      <fmt:message key="searchform.searchpages.nonefound" />
   </div>
   </mm:isempty>

   <%-- Now print the results --%>
   <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
      <mm:field id="trashnumber" name="number" write="false"/>
   </mm:node>

<c:if test="${searchShow eq 'list'}">
   <mm:list referid="results">
      <mm:first>
         <edit:pages search="true" totalElements="${resultCount}" offset="${param.offset}"/>
            <mm:hasrank minvalue="siteadmin">
               <c:if test="${fn:length(results) >1}">
               <div align="left">
				  <input type="submit" class="button" name="massdelete"
                        onclick="javascript:deleteAsset('massdelete','<fmt:message key="asset.delete.massdeleteconfirm"/>')"
                        value="<fmt:message key="asset.delete.massdelete" />"/>
                  <input type="button" class="button" value="<fmt:message key="content.delete.massmove" />" onclick="massMoveFromSearch('<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />')"/>
               </div>
               </c:if>
            </mm:hasrank>
         <form action="" name="linkForm" method="post">
            <table>
            <thead>
               <tr>
                  <th>
                     <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
                     <c:if test="${fn:length(results) >1}">
                       <input type="checkbox" onclick="selectAll(this.checked, 'linkForm', 'chk_');" class="checkbox" value="on" name="selectall" style="margin:0px 0px 0px 4px !important;> margin:0px 0px !important;margin:0px 0px;"/><span style="padding-left:4px;position:absolute;text-transform:none"><fmt:message key="locate.selectall" /></span>
                     </c:if>
                  </th>
                  <th><a href="javascript:orderBy('otype')" class="headerlink" ><fmt:message key="locate.typecolumn" /></a></th>
                  <th><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="locate.titlecolumn" /></a></th>
                  <th><fmt:message key="locate.creationchannelcolumn" /></th>
                  <th><a href="javascript:orderBy('lastmodifier')" class="headerlink" ><fmt:message key="locate.lastmodifiercolumn" /></th>
                  <th><a href="javascript:orderBy('lastmodifieddate')" class="headerlink" ><fmt:message key="locate.lastmodifieddatecolumn" /></th>
                  <th><a href="javascript:orderBy('number')" class="headerlink" ><fmt:message key="locate.numbercolumn" /></th>
               </tr>
            </thead>

            <tbody class="hover">
      </mm:first>

   <mm:field name="number" id="number" write="false">
      <mm:node number="${number}">
         <mm:relatednodes role="creationrel" type="contentchannel">
            <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
            <mm:compare referid="trashnumber" referid2="creationnumber">
                <c:set var="channelName"><fmt:message key="search.trash" /></c:set>
                <c:set var="channelIcon" value="/editors/gfx/icons/trashbin.png"/>
                <c:set var="channelIconMessage"><fmt:message key="search.trash" /></c:set>
                <c:set var="channelUrl" value="../recyclebin/assettrash.jsp"/>
            </mm:compare>
            <mm:field name="number" jspvar="channelNumber" write="false"/>
            <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
            <mm:compare referid="trashnumber" referid2="creationnumber" inverse="true">
                <mm:field name="name" jspvar="channelName" write="false"/>
                <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
                <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
                <c:set var="channelUrl" value="Asset.do?type=asset&parentchannel=${channelNumber}&direction=down"/>
               <mm:field name="path" id="contentChannelPath" write="false" />
            </mm:compare>
         </mm:relatednodes>
         <tr <mm:even inverse="true">class="swap"</mm:even>>
            <td style="white-space: nowrap;">
                  <c:if test="${(rights == 'writer' || rights == 'chiefeditor' || rights == 'editor' || rights == 'webmaster') && fn:length(results) >1}">
                    <input type="checkbox" value="moveToRecyclebin:<mm:field name="number" />" class="checkbox" name="chk_<mm:field name="number" />" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
                  </c:if>
               <%@ include file="searchIconsBar.jspf" %>
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);">
               <mm:nodeinfo type="guitype"/>
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);">
               <c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
               <mm:field id="title" write="false" name="title"/>
               <c:if test="${fn:length(title) > 50}">
                  <c:set var="title">${fn:substring(title,0,49)}...</c:set>
               </c:if>
               ${title}
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);">
                <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}" />
                  <mm:compare referid="action" value="search">
                     <a href="${channelUrl}"  title="${contentChannelPath}">${channelName}</a>
                  </mm:compare>
                  <mm:compare referid="action" value="search" inverse="true">
                     <span title="${contentChannelPath}">${channelName}</span>
                  </mm:compare>
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);"><mm:field name="lastmodifier" /></td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);"><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
            <td  style="white-space: nowrap;" onMouseDown="objClick(this);"><mm:field name="number"/></td>
            <c:if test="${hasWorkflow}">
               <td width="10" onMouseDown="objClick(this);">
                  <c:set var="status" value="waiting"/>
                  <mm:relatednodes type="workflowitem" constraints="type='asset'">
                     <c:set var="status"><mm:field name="status"/></c:set>
                  </mm:relatednodes>
                  <c:if test="${status == 'waiting'}">
                     <mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
                        <c:set var="status" value="onlive"/>
                     </mm:listnodes>
                  </c:if>
                     <img src="../gfx/icons/status_${status}.png"
                        alt="<fmt:message key="asset.status" />: <fmt:message key="asset.status.${status}" />"
                        title="<fmt:message key="asset.status" />: <fmt:message key="asset.status.${status}" />"/>
               </td>
            </c:if>
         </tr>
      </mm:node>
   </mm:field>
   <mm:last>
   </tbody>
   </table>
   </form>
   <mm:hasrank minvalue="siteadmin">
      <c:if test="${fn:length(results) >1}">
      <div align="left">
		 <input type="submit" class="button" name="massdelete"
                        onclick="javascript:deleteAsset('massdelete','<fmt:message key="asset.delete.massdeleteconfirm"/>')"
                        value="<fmt:message key="asset.delete.massdelete" />"/>
         <input type="button" class="button" value="<fmt:message key="content.delete.massmove" />" onclick="massMoveFromSearch('<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />')"/>
      </div>
      </c:if>
   </mm:hasrank>
   <edit:pages search="true" totalElements="${resultCount}" offset="${param.offset}"/>
   </mm:last>
   </mm:list>
</c:if>

<c:if test="${searchShow eq 'thumbnail'}">
   <mm:list referid="results">
      <mm:first>
         <edit:pages search="true" totalElements="${resultCount}" offset="${param.offset}"/>
         <div style="width:100%;float:left;">
      </mm:first>

   <mm:field name="number" id="number" write="false">
      <mm:node number="${number}">

         <mm:relatednodes role="creationrel" type="contentchannel">
            <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
            <mm:field name="number" jspvar="channelNumber" write="false"/>
            <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
         </mm:relatednodes>
         <div class="thumbnail_show" onMouseOut="javascript:hideEditItems(<mm:field name='number'/>)" onMouseOver="showEditItems(<mm:field name='number'/>)">
            <div class="thumbnail_operation">
               <div class="asset-info" id="asset-info-<mm:field name='number'/>" style="display: none; position: relative; border: 1px solid #eaedff" >
               <%@ include file="searchIconsBar.jspf" %>
               </div>
            </div>
            <div class="thumbnail_body" >
               <div class="thumbnail_img" onMouseOver="this.style.background = 'yellow';" onMouseOut="this.style.background = 'white';">
                   <a href="javascript:showInfo('<mm:nodeinfo type="type"/>', '<mm:field name="number" />')">
                     <c:set var="typedef" ><mm:nodeinfo type="type"/></c:set>
                     <c:if test="${typedef eq 'images'}">
						<c:set var="filesize"><mm:field name="filesize"/></c:set>
						<c:if test="${ filesize gt 0}">
                                 	<img src="<mm:image template="s(128x128)"/>" alt=""/>
						</c:if>
						<c:if test="${ filesize eq 0}">
                                 	<img src="../gfx/nullimage.gif" alt=""/>
						</c:if>
                     </c:if> 
                     <c:if test="${typedef eq 'attachments'}">
						<c:set var="filesize"><mm:field name="size"/></c:set>
						<c:if test="${ filesize gt 0}">
	                                <%@include file="../resources/attachmentthumbnail.jsp" %>
						</c:if>
						<c:if test="${ filesize eq 0}">
	                                	<img src="../gfx/nullattachment.gif" alt=""/>
						</c:if>
                     </c:if>
                     <c:if test="${typedef eq 'urls'}">
						<c:set var="urltitle"><mm:field name="title"/></c:set>
						<c:set var="url"><mm:field name="url"/></c:set>
						<c:if test="${!(empty urltitle || empty url)}">
                                 	<img src="../gfx/url.gif" alt=""/>
						</c:if>
						<c:if test="${empty urltitle || empty url }">
                                 	<img src="../gfx/nullurl.gif" alt=""/>
						</c:if>
                     </c:if>
                  </a>
               </div>
               <div class="thumnail_info">
                  <c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
                              <mm:field id="title" write="false" name="title"/>
                              <c:if test="${fn:length(title) > 15}">
                                 <c:set var="title">${fn:substring(title,0,14)}...</c:set>
                              </c:if>${title}
                              <c:if test="${ assettype == 'images'}">
                              <br/><mm:field name="itype" />
                              </c:if>
               </div>
            </div>
            </div>

      </mm:node>
   </mm:field>

   <mm:last>
   <div style="clear:both;"></div>
   </div>
   <edit:pages search="true" totalElements="${resultCount}" offset="${param.offset}"/>
   </mm:last>
   </mm:list>
</c:if>

</div>
   </div>
</mm:cloud>

   </body>
</html:html>
</mm:content>