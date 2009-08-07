<%@ page language="java" contentType="text/html;charset=utf-8"
%><%@ include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%>

<c:set var="returnurl" value="../editors/simple/SimpleContentDraftAction.do"/>
<c:set var="typesNumber"  value="${fn:length(typesList)}" />
<c:set var="channelsNumber"  value="${fn:length(channelsList)}" />
   <div class="editor">
	<div style="margin-left:10px;color:green"><h1><fmt:message key="simple.editor.title" /></h1></div>

	 <div style="margin-left:10px;font-size:12px"><fmt:message key="simple.editor.introduction.text" /></div>
	<br/>
      <div class="body">
         <html:form action="/editors/simple/SimpleContentDraftAction" method="post">
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
			<html:hidden property="title"/>
			<html:hidden property="pager.offset" value="${pagerDOToffset}"/>
         </html:form>
         <form name="initForm" action="../WizardInitAction.do" method="post">
            <input type="hidden" name="action" value="create"/>
            <input type="hidden" name="returnurl" value="${returnurl}"/>
            <input type="hidden" name="order" value="${orderby}" />
            <input type="hidden" name="direction" value="${direction}"/>
            <input type="hidden" name="offset" value="${param.offset}"/>
             <input type="hidden" name="pager.offset" value="${pagerDOToffset}"/>
<div style="padding:0px 0px 5px 5px">
            <c:if test="${channelsNumber eq 0}">
              <fmt:message key="simple.editor.nochannel.error"/><br/>
            </c:if>
            <c:if test="${channelsNumber gt 1}">
               <b style="margin-left:5px;margin-bottom:10px"><fmt:message key="simple.editor.place.in"/></b>
               <select name="creation" style="margin-bottom:10px">
                   <c:forEach var="channel" items="${channelsList}">
                       <option value="${channel.value}">${channel.label}</option>
                   </c:forEach>
               </select><br>
            </c:if>
            <c:if test="${channelsNumber eq 1}" >
               <b style="margin-left:5px;margin-bottom:10px"><fmt:message key="simple.editor.place.in"/></b>
                  <c:forEach var="channel" items="${channelsList}">
                       <input type="hidden" name="creation" value="${channel.value}"/>${channel.label}
                  </c:forEach><br/>
            </c:if>
</div>
<div style="padding:0px 0px 0px 5px">
            <c:if test="${typesNumber eq 0}">
              <fmt:message key="simple.editor.notype.error"/><br/>
            </c:if>
            <c:if test="${typesNumber gt 1}">
               <b style="margin-left:5px;margin-bottom:15px"><fmt:message key="simple.editor.create.new"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </b>
               <select name="contenttype">
                   <c:forEach var="type" items="${typesList}">
                       <option value="${type.value}">${type.label}</option>
                   </c:forEach>
               </select><br/>
               <input style="margin-left:55px" type="submit" class="button" name="submitButton" value="create"/>
            </c:if>
            <c:if test="${typesNumber eq 1}" >
               <b style="margin-left:5px;margin-bottom:15px"><fmt:message key="simple.editor.create.new"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </b>
                  <c:forEach var="type" items="${typesList}">
                       <input type="hidden" name="contenttype" value="${type.value}"/>${type.label}
                  </c:forEach><br/>
               <input style="margin-left:55px" type="submit" class="button" name="submitButton" value="create"/>
            </c:if>
</div>
        </form>
      </div>
   <div class="ruler_green"><div><fmt:message key="simple.editor.draft" /></div></div>

<html:form action="/editors/simple/SimpleContentDraftAction.do" method="post">
<html:text style="margin-left:10px;margin-top:10px" property="title" /><input type="submit" class="button" value="<fmt:message key="simple.editor.search" />">
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
                  <c:if test="${typesNumber gt 1}">
                  <th><a href="javascript:orderBy('otype')" class="headerlink">
                      <fmt:message key="content.typecolumn"/></a>
                  </th>
                  </c:if>
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
                          <mm:param name="returnurl" value="${returnurl}" />
                      </mm:url>">
                         <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="searchform.icon.edit.title" />" title="<fmt:message key="searchform.icon.edit.title" />" /></a>
            

                  <mm:field name="number"  write="false" id="nodenumber">
                     <a href="<cmsc:contenturl number="${nodenumber}"/>" target="_blank"><img src="../gfx/icons/preview.png" alt="<fmt:message key="searchform.icon.preview.title" />" title="<fmt:message key="searchform.icon.preview.title" />" /></a>
                  </mm:field>
			   	<c:if test="${hasWorkflow}">
                  <a href="<mm:url page="../simple/SimpleContentWorkflowAction.do">
                          <mm:param name="content"><mm:field name="number"/></mm:param>
                      </mm:url>" ><img src="../gfx/icons/status_finished.png" alt="<fmt:message key="simple.editor.send" />" title="<fmt:message key="simple.editor.send" />" /></a>
			    </c:if>
            <c:if test="${channelsNumber gt 1}">
				    <a onclick="moveContent(<mm:field name="number"/>, ${channelNumber} )" target="selectchannel" href="<mm:url page="../simple/SimpleEditorChannelAction.do">
                          <mm:param name="returnpath">simpleeditordraft</mm:param>
                      </mm:url>" >
                  <img src="../gfx/icons/page_move.png" title="<fmt:message key="searchform.icon.move.title" />"/></a>
            </c:if>
               </td>
            <c:if test="${typesNumber gt 1}">
               <td onMouseDown="objClick(this);">
                  <mm:nodeinfo type="guitype"/>
               </td>
            </c:if>
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
						<c:if test="${hasWorkflow}">
							 <c:set var="status" value="waiting"/>
							 <mm:relatednodes type="workflowitem" constraints="type='content'">
								<c:set var="status"><mm:field name="status"/></c:set>
							 </mm:relatednodes>
							 <c:if test="${status == 'waiting'}">
								<mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
								   <c:set var="status" value="onlive"/>
								</mm:listnodes>
							 </c:if>
						 <c:if test="${status != 'onlive'}">
							   <c:set var="status" value="waiting"/>
						 </c:if>
						 <img src="../gfx/icons/status_${status}.png" alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" title="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" />
						</c:if>
				  </td>               
				 </tr>
			  </mm:node>
			  </mm:field>
			  <mm:last>
              </tbody>
            </table>
         <edit:pages  search="true" totalElements="${resultCount}" offset="${param.offset}"/>
      </mm:last>
    </mm:list>
	</div>	
</html:form>
