<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="search.title">
      <script src="../../../mmbase/edit/wizard/javascript/validator.js" type="text/javascript"></script>
</cmscedit:head>
<body>
<mm:import externid="returnurl"/>
<c:set var="pagerDOToffset"><%=request.getParameter("draftPager.offset")%></c:set>
<c:set var="pagerDOToffset2"><%=request.getParameter("finishedPager.offset")%></c:set>
<c:set var="single" value="false"/>
<c:set var="returnurl" value="${fn:replace(returnurl,'&amp;','&')}"/>
<mm:cloud jspvar="cloud" loginpage="../../editors/simple/login.jsp">
   <div class="editor">
   <br />
      <div class="body">
         <html:form action="/editors/simple/SimpleContentAction" method="post">
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
			<html:hidden property="title"/>
			<html:hidden property="type"/>
			<html:hidden property="offsetFinished"/>
            <html:hidden property="orderFinished"/>
            <html:hidden property="directionFinished"/>
			<html:hidden property="draftPager.offset" value="${pagerDOToffset}"/>
			<html:hidden property="finishedPager.offset" value="${pagerDOToffset2}"/>
			<b style="margin-left:5px">Placed in</b> <select name="channel">
                    <option>Repository</option>
                    <option>newchannel</option>        
                </select><br>
               <b style="margin-left:5px;margin-bottom:10px"> New  </b>  <select style="margin-left:26px;margin-top:15px" name="contenttype">
                    <option>Article</option>
                    <option>FAQ</option>        
                </select><br>
               <input style="margin-left:55px;margin-top:10px" type="submit" class="button" name="submitButton" value="create"/>
         </html:form>
      </div>

   <div class="ruler_green"><div><fmt:message key="simple.editor.draft" /></div></div>

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
   <c:if test="${single == 'false'}">
   <div class="body">
   <%-- Now print the results --%>
   <c:set var="swap" value="0"/>
   <mm:list referid="results">
      <mm:first>
         <edit:pages pageId="draftPager" jsMethod="setDraftPagerOffset" search="true" totalElements="${resultCount}" offset="${param.offset}"/>
         <form action="SimpleContentAction.do" name="SimpleContentForm">
				<input type="text" name="title"><input type="button" class="button" value="<fmt:message key="simple.editor.search" />">
          <table>
            <thead>
               <tr>
                  <th>

                  </th>
                  <th><a href="javascript:sortBy('Content')" class="headerlink">
        <fmt:message key="content.typecolumn"/></a>

                  </th>
                  <th><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="locate.titlecolumn" /></a></th>
                  
                  <th><a href="javascript:orderBy('creationdate')" class="headerlink" >date</th>
				  <th>placed in</th>
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

			 <c:if test="${hasWorkflow}">
				 <c:set var="status" value="waiting"/>
				 <mm:relatednodes type="workflowitem">
					<c:set var="status"><mm:field name="status"/></c:set>
				 </mm:relatednodes>
				 <c:if test="${status == 'waiting'}">
					<mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
					   <c:set var="status" value="onlive"/>
					</mm:listnodes>
				 </c:if>
			 </c:if>
 
			<c:if test="${empty status || (status != 'onlive' && status =='draft')}">
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
			   	<c:if test="${hasWorkflow}">
                  <a href="#" onclick="showItem(<mm:field name="number"/>);"><img src="../gfx/icons/status_finished.png" alt="<fmt:message key="searchform.icon.info.title" />" title="<fmt:message key="searchform.icon.info.title" />" /></a>
			    </c:if>
				    <a href="#" target="selectchannel" >
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
					  	 <c:if test="${hasWorkflow}">
						 <c:if test="${status != 'onlive'}">
							   <c:set var="status" value="waiting"/>
						 </c:if>
						 <img src="../gfx/icons/status_${status}.png" alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" title="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" />
						</c:if>
				  </td>               
				 </tr>
			   </c:if>
			  </mm:node>
			  </mm:field>
			  <mm:last>
              </tbody>
            </table>
          </form>
         <edit:pages pageId="draftPager" jsMethod="setDraftPagerOffset"  search="true" totalElements="${resultCount}" offset="${param.offset}"/>
      </mm:last>
    </mm:list>
	</div>
    <c:if test="${hasWorkflow}">
	 <div class="ruler_green"><div><fmt:message key="simple.editor.ready" /></div></div>
	 <div class="body">
	 <c:set var="swap" value="0"/>
     <mm:list referid="finishedResults">
      <mm:first>
         <edits:pages  pageId="finishedPager"   search="true"  totalElements="${finishedResultCount}" jsMethod="setFinishedPagerOffset"   offset="${param.offsetFinished}"/>
         <form action="SimpleContentAction.do" name="SimpleContentForm">
				<input type="text" name="title"><input type="button" class="button" value="<fmt:message key="simple.editor.search" />">
          <table>
            <thead>
               <tr>
                  <th>

                  </th>
				                    <th><a href="javascript:sortBy('Content')" class="headerlink">
        <fmt:message key="content.typecolumn"/></a>

                  </th>
                  <th><a href="javascript:seOrderBy('title')" class="headerlink" >title</a></th>
                  
                  <th><a href="javascript:seOrderBy('creationdate')" class="headerlink" >date
				  </a>
				  </th>
				  <th>placed in</th>
                  <th></th>
               </tr>
            </thead>
            <tbody class="hover">
      </mm:first>


      <mm:field name="number" id="contentNumber">
         <mm:node number="${contentNumber}">

            <mm:relatednodes role="creationrel" type="contentchannel">
			   <mm:field name="number" jspvar="contentChannelNumber" write="false"/>
			   <cmsc:rights nodeNumber="${contentChannelNumber}" var="rights"/>
			   <mm:field name="name" jspvar="contentChannelName" write="false"/>

			   <c:set var="contentChannelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
			   <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
			   <mm:field name="path" id="fullContentChannelPath" write="false" />
            </mm:relatednodes>
			 <c:set var="itemStatus" value="waiting"/>
			 <mm:relatednodes type="workflowitem">
				<c:set var="itemStatus"><mm:field name="status"/></c:set>
			 </mm:relatednodes>
			 <c:if test="${itemStatus == 'waiting'}">
				<mm:listnodes type="remotenodes" constraints="sourcenumber=${contentNumber}">
				   <c:set var="itemStatus" value="onlive"/>
				</mm:listnodes>
			 </c:if>
			
			<c:if test="${itemStatus != 'draft'}">
            <tr <c:if test="${swap % 2 ==0}">class="swap"</c:if>>
			 <c:set var="swap">${swap+1}</c:set>
               <td style="white-space: nowrap;">           
                
                 <%-- also show the edit icon when we return from an edit wizard! --%>
                      <a href="<mm:url page="../WizardInitAction.do">
                          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                          <mm:param name="returnurl" value="/editors/repository/ContentSearchAction.do${geturl}" />
                      </mm:url>">
                         <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="searchform.icon.edit.title" />" title="<fmt:message key="searchform.icon.edit.title" />" /></a>          

                  <mm:field name="number"  write="false" id="contentnumber">
                     <a href="<cmsc:contenturl number="${contentnumber}"/>" target="_blank"><img src="../gfx/icons/preview.png" alt="<fmt:message key="searchform.icon.preview.title" />" title="<fmt:message key="searchform.icon.preview.title" />" /></a>

					 				    <a href="#" target="selectchannel" >
        <img src="../gfx/icons/page_move.png" title="<fmt:message key="searchform.icon.move.title" />"/></a>
                  </mm:field>
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
              <img src="<cmsc:staticurl page="${contentChannelIcon}"/>" align="top" alt="${channelIconMessage}" />
                     <span title="${fullContentChannelPath}">${contentChannelName}</span>
               </td>
                  <td width="10" style="white-space: nowrap;">
						 <c:if test="${itemStatus != 'onlive'}">
							   <c:set var="itemStatus" value="waiting"/>
						 </c:if>
						 <img src="../gfx/icons/status_${itemStatus}.png" alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${itemStatus}" />" title="<fmt:message key="content.status" />: <fmt:message key="content.status.${itemStatus}" />" />
				  </td>               
				 </tr>
				</c:if>
			  </mm:node>
			  </mm:field>
			  <mm:last>
              </tbody>
            </table>
          </form>
         <edits:pages pageId="finishedPager"  search="true" totalElements="${finishedResultCount}" jsMethod="setFinishedPagerOffset" offset="${param.offsetFinished}"/>
      </mm:last>
    </mm:list>
	   </div>
	</c:if>
</c:if>

<c:if test="${single == 'true'}">
 <div class="body">
   <%-- Now print the results --%>
   <c:set var="swap" value="0"/>
   <mm:list referid="results">
      <mm:first>
         <edit:pages pageId="draftPager" jsMethod="setDraftPagerOffset" search="true" totalElements="${resultCount}" offset="${param.offset}"/>
         <form action="SimpleContentAction.do" name="SimpleContentForm">
				<input type="text" name="title"><input type="button" class="button" value="<fmt:message key="simple.editor.search" />">
          <table width="100%">
            <thead>
               <tr>
                  <th>

                  </th>
                  <th><a href="javascript:sortBy('Content')" class="headerlink">
        <fmt:message key="content.typecolumn"/></a>

                  </th>
                  <th><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="locate.titlecolumn" /></a></th>
                  
                  <th><a href="javascript:orderBy('creationdate')" class="headerlink" >date</th>
				  <th>placed in</th>
               
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

			 <c:if test="${hasWorkflow}">
				 <c:set var="status" value="waiting"/>
				 <mm:relatednodes type="workflowitem">
					<c:set var="status"><mm:field name="status"/></c:set>
				 </mm:relatednodes>
				 <c:if test="${status == 'waiting'}">
					<mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
					   <c:set var="status" value="onlive"/>
					</mm:listnodes>
				 </c:if>
			 </c:if>
 
			<c:if test="${empty status || (status != 'onlive' && status =='draft')}">
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
				    <a href="#" target="selectchannel" >
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
               
				 </tr>
			   </c:if>
			  </mm:node>
			  </mm:field>
			  <mm:last>
              </tbody>
            </table>
          </form>
         <edit:pages pageId="draftPager" jsMethod="setDraftPagerOffset"  search="true" totalElements="${resultCount}" offset="${param.offset}"/>
      </mm:last>
    </mm:list>
	</div>
</c:if>
   </div>
</mm:cloud>
   </body>
</html:html>
</mm:content>