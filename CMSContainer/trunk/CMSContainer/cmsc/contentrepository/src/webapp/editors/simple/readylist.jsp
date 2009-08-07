<%@ page language="java" contentType="text/html;charset=utf-8"
%><%@ include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%>
   <div class="editor">
	<div style="margin-left:10px;color:green"><h1><fmt:message key="simple.editor.title" /></h1></div>

	 <div style="margin-left:10px;font-size:12px"><fmt:message key="simple.editor.introduction.text" /></div>
	<br/>
      <div class="body">
         <html:form action="/editors/simple/SimpleContentReadyAction" method="post">
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
			<html:hidden property="title"/>
			<html:hidden property="pager.offset" value="${pagerDOToffset}"/>
         </html:form>
      </div>
   <div class="ruler_green"><div><fmt:message key="simple.editor.ready" /></div></div>

<form action="SimpleContentReadyAction.do" name="SimpleContentForm">
<input style="margin-left:10px;margin-top:10px" type="text" name="title"><input type="submit" class="button" value="<fmt:message key="simple.editor.search" />">
<!-- we check to see if we have workflow, this is done by looking if the editors for the workflow are on the HD -->
<c:set var="hasWorkflow" value="false"/>
<mm:haspage page="/editors/workflow">
   <c:set var="hasWorkflow" value="true"/>
</mm:haspage>
   <%-- Now print if no results --%>
   <mm:isempty referid="results">
   <div style="padding:10px 0px 0px 11px">
      <fmt:message key="searchform.searchpages.nonefound" />
   </div>
   </mm:isempty>
   <div class="body">
   <%-- Now print the results --%>
   <c:set var="swap" value="0"/>
   <mm:list referid="results">
      <mm:first>
         <edit:pages search="true" totalElements="${resultCount}" offset="${param.offset}"/>
          <table>
            <thead>
               <tr>
                  <th>

                  </th>
                  <th><a href="javascript:orderBy('otype')" class="headerlink">
                      <fmt:message key="content.typecolumn"/></a>
                  </th>
                  <th><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="locate.titlecolumn" /></a>
				  </th>                  
                  <th><a href="javascript:orderBy('creationdate')" class="headerlink" ><fmt:message key="simple.editor.list.date.field" /></th>
				  <th><fmt:message key="simple.editor.list.channel.field" /></th>
                  <th></th>
               </tr>
            </thead>
            <tbody class="hover">
      </mm:first>
      <mm:field name="number" id="number">
         <mm:node number="${number}">

            <mm:relatednodes role="creationrel" type="contentchannel">
			   <mm:field name="number" jspvar="channelNumber" write="false"/>
			   <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
			   <mm:field name="name" jspvar="channelName" write="false"/>

			   <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
			   <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
			   <mm:field name="path" id="contentChannelPath" write="false" />
            </mm:relatednodes>
		
	         <tr <c:if test="${swap % 2 ==0}">class="swap"</c:if>>
			   <c:set var="swap">${swap+1}</c:set>
               <td style="white-space: nowrap;"> 
                  <%-- also show the edit icon when we return from an edit wizard! --%>        
                      <a href="<mm:url page="../WizardInitAction.do">
                          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                          <mm:param name="returnurl" value="/editors/repository/ContentSearchAction.do${geturl}" />
                      </mm:url>">
                         <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="searchform.icon.edit.title" />" title="<fmt:message key="searchform.icon.edit.title" />" /></a>
            

                  <mm:field name="number"  write="false" id="nodenumber">
                     <a href="<cmsc:contenturl number="${nodenumber}"/>" target="_blank"><img src="../gfx/icons/preview.png" alt="<fmt:message key="searchform.icon.preview.title" />" title="<fmt:message key="searchform.icon.preview.title" />" /></a>
                  </mm:field>
				    <a onclick="moveContent(<mm:field name="number"/>, ${channelNumber} )" target="selectchannel" href="<mm:url page="../simple/SimpleEditorChannelAction.do">
                          <mm:param name="content"><mm:field name="number"/></mm:param>
                      </mm:url>" >
                  <img src="../gfx/icons/page_move.png" title="<fmt:message key="searchform.icon.move.title" />"/></a>
               </td>
			   <td onMouseDown="objClick(this);">
                   <mm:nodeinfo type="guitype"/>
               </td>
                 <td>
                  <mm:field jspvar="title" write="false" name="title" />
                  <c:if test="${fn:length(title) > 50}">
                     <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                  </c:if>
                  ${title}
               </td>
			   <td style="white-space: nowrap;"><mm:field name="creationdate"><cmsc:dateformat displaytime="true" /></mm:field></td>

               <td style="white-space: nowrap;">
              <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}" />
                     <span title="${contentChannelPath}">${channelName}</span>
               </td>
                  <td width="10" style="white-space: nowrap;">
				  		<c:set var="itemStatus" value="waiting"/>
						 <mm:relatednodes type="workflowitem" constraints="type='content'">
							<c:set var="itemStatus"><mm:field name="status"/></c:set>
						 </mm:relatednodes>
						 <c:if test="${itemStatus == 'waiting'}">
							<mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
							   <c:set var="itemStatus" value="onlive"/>
							</mm:listnodes>
						 </c:if>	
							 <c:if test="${status != 'onlive'}">
							   <c:set var="status" value="waiting"/>
						 </c:if>
						 <img src="../gfx/icons/status_${status}.png" alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" title="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" />
				  </td>               
				 </tr>
	      	  </mm:node>
			  </mm:field>
			  <mm:last>
              </tbody>
            </table>
         <edit:pages search="true" totalElements="${resultCount}" offset="${param.offset}"/>
      </mm:last>
    </mm:list>
	</div>
</form>
	