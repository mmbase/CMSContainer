<%@include file="globals.jsp" %>
<mm:import id="resultList" from="reqyest" vartype="List" />
<c:if test="${empty elementsPerPage}">
   <c:set var="elementsPerPage"><%=com.finalist.cmsc.mmbase.PropertiesUtil.getProperty("repository.search.results.per.page")%></c:set>
</c:if>
<table>
   <thead>
      <tr>
          <th><mm:hasrank minvalue="basic user">
				<c:if test="${empty dashboardTaskSize and fn:length(resultList)>1}">
					<input type="checkbox" name="selectall" class="checkbox" onclick="selectAll(this.checked, 'taskForm', 'chk_');" value="on" />
				</c:if>
          </mm:hasrank></th>
<c:if test="${empty dashboardTaskSize }">
        <c:set var="reverse" value="${direction =='up'?'down':'up'}"/>
        <th><a href="tasklist.jsp?sortBy=creationdate&direction=${reverse}&offset=${offset}&pager.offset=${pagerDOToffset}"><fmt:message key="task.created" /></a></th>
        <th><a href="tasklist.jsp?sortBy=deadline&direction=${reverse}&offset=${offset}&pager.offset=${pagerDOToffset}"><fmt:message key="task.deadline" /></a></th>
        <th><a href="tasklist.jsp?sortBy=title&direction=${reverse}&offset=${offset}&pager.offset=${pagerDOToffset}"><fmt:message key="task.title" /></a></th>
        <th><a href="tasklist.jsp?sortBy=status&direction=${reverse}&offset=${offset}&pager.offset=${pagerDOToffset}"><fmt:message key="task.status" /></th>
        <th><fmt:message key="task.contenttitle" /></th>
        <th><fmt:message key="task.nodetype" /></th>
        <th><a href="tasklist.jsp?sortBy=description&direction=${direction}&offset=${offset}&pager.offset=${pagerDOToffset}"><fmt:message key="task.description" /></a></th>
</c:if>
<c:if test="${!empty dashboardTaskSize }">
        <th><fmt:message key="task.created" /></th>
        <th><fmt:message key="task.deadline" /></th>
        <th><fmt:message key="task.title" /></th>
        <th><fmt:message key="task.status" /></th>
        <th><fmt:message key="task.contenttitle" /></th>
        <th><fmt:message key="task.nodetype" /></th>
        <th><fmt:message key="task.description" /></th>
</c:if>

      </tr>
   </thead>
   <tbody class="hover">
        <c:set var="taskList" value="" />
        <c:set var="isSwapClass" value="true"/>
       
        <mm:listnodes referid="resultList" max="${elementsPerPage}" offset="${offset*elementsPerPage}">
         <c:set var="taskId"><mm:field name="number"/></c:set>
         <tr <c:if test="${isSwapClass}">class="swap"</c:if>>
              <td style="white-space: nowrap;">
                 <mm:hasrank minvalue="basic user">
                 <c:if test="${empty dashboardTaskSize and fn:length(resultList)>1}">
                    <input type="checkbox"  name="chk_<mm:field name="number" />" class="checkbox" value="<mm:field name="number" />" onClick="document.forms['taskForm'].elements.selectall.checked=false;"/>
                 </c:if>
                 <c:if test="${empty dashboardTaskSize}">
                 <a href="javascript:callEditWizard('<mm:field name="number" />');"><img src="../gfx/icons/edit2.png" align="top" alt="<fmt:message key="task.edit"/>" title="<fmt:message key="task.edit"/>"/></a>
                 </c:if>
                 <c:if test="${not empty dashboardTaskSize}">
                 <a href="javascript:window.top.openTasksWithTask('<mm:field name="number" />');"><img src="gfx/icons/edit2.png" align="top" alt="<fmt:message key="task.edit"/>" title="<fmt:message key="task.edit"/>"/></a>
                 </c:if>
                 <c:if test="${empty dashboardTaskSize}">
                 <cmsc-tasks:isdeletable number="${taskId}">
                 <a href="javascript:deleteTask('<mm:field name="number" />');"><img src="../gfx/icons/delete.png" width="16" height="16" title="<fmt:message key="task.delete" />" alt="<fmt:message key="task.delete" />" /></a>
                 </cmsc-tasks:isdeletable>
                 <cmsc-tasks:isdeletable number="${taskId}" inverse="true">
                 <img src="../gfx/icons/delete_gray.gif" width="16" height="16" title="<fmt:message key="task.delete" />" alt="<fmt:message key="task.delete" />" />
                 </cmsc-tasks:isdeletable>
                 </c:if>
                 </mm:hasrank>
              </td>
            <td><mm:field name="creationdate" id="created"><mm:time time="${created}" format="d/M/yyyy HH:mm" /></mm:field></td>
            <td><mm:field name="deadline" id="deadl"><mm:time time="${deadl}" format="d/M/yyyy HH:mm"/></mm:field></td>
            <td><mm:field name="title"/></td>
            <c:set var="elementtitel"><mm:field name="title"/></c:set>
            <c:set var="elementnumber"/>
            <c:set var="elementtype"/>
            <mm:relatednodescontainer type="contentelement" role="taskrel" searchdirs="destination">
               <mm:maxnumber value="1" />
               <mm:relatednodes>
                  <c:set var="elementtitel"><mm:field name="title"/></c:set>
                  <c:set var="elementnumber"><mm:field name="number"/></c:set>
                  <c:set var="elementtype"><mm:field name="number"><mm:isnotempty><mm:nodeinfo type="guitype"/></mm:isnotempty></mm:field></c:set>
               </mm:relatednodes>
            </mm:relatednodescontainer>
            <c:set var="status"><mm:field name="status" /></c:set>
            <td><fmt:message key="${status}" /></td>
            <td>
               <c:choose>
                  <c:when test="${empty elementnumber}">
                     <fmt:message key="task.noelement"/>
                  </c:when>
                  <c:otherwise>
                     <mm:hasrank minvalue="basic user">
                        <c:if test="${empty dashboardTaskSize}">
                        <a href="javascript:callEditWizard('<mm:write referid="elementnumber"/>');"><img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="task.editelement"/>" title="<fmt:message key="task.editelement"/>"/></a> ${elementtitel}
                        </c:if>
                        <c:if test="${not empty dashboardTaskSize}">
                        <a href="javascript:window.top.openRepositoryWithContent('<mm:write referid="elementnumber"/>');"><img src="gfx/icons/edit.png" align="top" alt="<fmt:message key="task.editelement"/>" title="<fmt:message key="task.editelement"/>"/></a> ${elementtitel}
                        </c:if>
                     </mm:hasrank>
                  </c:otherwise>
               </c:choose>
            </td>
            <td>${elementtype}</td>
            <td><mm:field name="description" /></td>
         </tr>
         <c:set var="taskList">${taskList},${taskId}</c:set>
         <c:choose>
            <c:when test="${isSwapClass eq 'false'}"><c:set var="isSwapClass" value="true"/></c:when>
            <c:when test="${isSwapClass eq 'true'}"><c:set var="isSwapClass" value="false"/></c:when>
         </c:choose>
        </mm:listnodes>
   </tbody>
</table>
