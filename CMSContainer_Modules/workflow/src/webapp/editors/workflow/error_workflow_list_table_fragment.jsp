<%@ include file="globals.jsp" %>
<table border="0" width="100%">
   <tr>
      <td style="width:50%;">
            <fmt:message key="workflow.block.totalresults">
               <fmt:param>${fn:length(errors)}</fmt:param>
            </fmt:message>
      </td>
   </tr>
</table>
<table>
   <thead>
      <tr>
         <th />
         <th />
         <th><fmt:message key="workflow.content.type" /></th>
         <th><fmt:message key="workflow.title" /></th>
         <th><fmt:message key="workflow.lastmodifier" /></th>
         <th><fmt:message key="workflow.lastmodifieddate" /></th>
      </tr>
   </thead>
   <tbody class="hover">
      <mm:listnodes referid="errors">
         <tr <mm:even inverse="true">class="swap"</mm:even>>
            <td>
               <mm:field name="number" id="errorElemNumber" write="false"/>
               <input type="checkbox" name="check_${errorElemNumber}" value="on"/>
            </td>
            <td align="left" style="white-space: nowrap;">
               <mm:url page="../WizardInitAction.do" id="errorUrl" write="false">
                  <mm:param name="objectnumber"><mm:field name="number"/></mm:param>
                  <mm:param name="returnurl" value="workflow/PageWorkflowAction.do?status=${param.status}"/>
               </mm:url>
               <a href="${errorUrl}">
                  <img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="workflow.editelement"/>" title="<fmt:message key="workflow.editelement"/>"/>
               </a>
               <mm:listnodes type="contentelement" max="1" constraints="number = ${errorElemNumber}" id="errorContent" />
               <c:if test="${not empty errorContent}">
                  <a href="<cmsc:contenturl number="${errorElemNumber}"/>" target="_blank">
                     <img src="../gfx/icons/preview.png" alt="<fmt:message key="workflow.preview.title"/>"
                          title="<fmt:message key="workflow.preview.title"/>"/></a>
                  <a href="javascript:info('${errorElemNumber}')">
                     <img src="../gfx/icons/info.png" title="<fmt:message key="workflow.info" />"
                          alt="<fmt:message key="workflow.info"/>"/></a>
                  <mm:haspage page="/editors/versioning">
                     <c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
                        <c:param name="nodenumber">${errorElemNumber}</c:param>
                     </c:url>
                     <a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')">
                        <img src="../gfx/icons/versioning.png"
                             title="<fmt:message key="workflow.icon.versioning.title" />"
                             alt="<fmt:message key="workflow.icon.versioning.title"/>"/></a>
                  </mm:haspage>
                  <c:if test="${status != 'published'}">
                    <mm:url page="WorkflowItemDelete.do" id="deleteAction" write="false">
                      <mm:param name="number" value="${errorElemNumber}"/>
                      <mm:param name="returnurl" value="/editors/workflow/PageWorkflowAction.do?status=${param.status}"/>
                    </mm:url>
                    <a href="${deleteAction}" ">
                      <img src="../gfx/icons/delete.png" title="<fmt:message key="workflow.item.delete" />" alt="<fmt:message key="workflow.item.delete"/>"/>
                    </a>
                 </c:if>
               </c:if>
            </td>
            <td style="white-space: nowrap;"><mm:nodeinfo type="guitype" /></td>
            <td style="white-space: nowrap;"><mm:hasfield name="title">
               <mm:field name="title" />
            </mm:hasfield> <mm:hasfield name="name">
               <mm:field name="name" />
            </mm:hasfield></td>
            <td style="white-space: nowrap;"><mm:hasfield name="lastmodifier">
               <mm:field name="lastmodifier" />
            </mm:hasfield></td>
            <td style="white-space: nowrap;"><mm:hasfield name="lastmodifieddate">
               <mm:field name="lastmodifieddate">
                  <cmsc:dateformat displaytime="true" />
               </mm:field>
            </mm:hasfield></td>
         </tr>
      </mm:listnodes>
   </tbody>
</table>
<table border="0" width="100%">
   <tr>
      <td style="width:50%;">
            <fmt:message key="workflow.block.totalresults">
               <fmt:param>${fn:length(errors)}</fmt:param>
            </fmt:message>
      </td>
   </tr>
</table>