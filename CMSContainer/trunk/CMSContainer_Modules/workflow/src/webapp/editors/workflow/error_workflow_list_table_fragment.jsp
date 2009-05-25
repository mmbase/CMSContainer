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
<table style="background-color:#CC0000;">
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
   <tbody>
      <mm:listnodes referid="errors">
         <tr>
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