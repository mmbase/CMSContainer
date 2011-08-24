<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ page import="static com.finalist.cmsc.mmbase.PropertiesUtil.getProperty"%>
<%@include file="globals.jsp" %>
<table>
   <thead>
      <tr>
        <th></th>
        <th><fmt:message key="task.created" /></th>
        <th><fmt:message key="task.deadline" /></th>
        <th><fmt:message key="task.title" /></th>
        <th><fmt:message key="task.status" /></th>
        <th><fmt:message key="task.contenttitle" /></th>
        <th><fmt:message key="task.nodetype" /></th>
        <th><fmt:message key="task.description" /></th>
      </tr>
   </thead>
   <tbody class="hover">
        <c:set var="isSwapClass" value="true"/>
        <mm:listnodes nodes="${param.results}" type="task" >
         <c:set var="taskId"><mm:field name="number"/></c:set>
         <tr <c:if test="${isSwapClass}">class="swap"</c:if>>
              <td style="white-space: nowrap;">
                 <mm:hasrank minvalue="basic user">
                 <a href="javascript:window.top.openTasksWithTask('<mm:field name="number" />');"><img src="gfx/icons/edit2.png" align="top" alt="<fmt:message key="task.edit"/>" title="<fmt:message key="task.edit"/>"/></a>
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
                        <a href="javascript:window.top.openRepositoryWithContent('<mm:write referid="elementnumber"/>');"><img src="gfx/icons/edit.png" align="top" alt="<fmt:message key="task.editelement"/>" title="<fmt:message key="task.editelement"/>"/></a> ${elementtitel}
                 
                     </mm:hasrank>
                  </c:otherwise>
               </c:choose>
            </td>
            <td>${elementtype}</td>
            <td><mm:field name="description" /></td>
         </tr>
         <c:choose>
            <c:when test="${isSwapClass eq 'false'}"><c:set var="isSwapClass" value="true"/></c:when>
            <c:when test="${isSwapClass eq 'true'}"><c:set var="isSwapClass" value="false"/></c:when>
         </c:choose>
        </mm:listnodes>
   </tbody>
</table>
