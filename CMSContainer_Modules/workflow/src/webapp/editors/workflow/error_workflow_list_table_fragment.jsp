<%@ include file="globals.jsp" %>
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
   <tbody>
      <mm:listnodes referid="errors">
         <tr>
            <td>
               <mm:field name="number" id="errorWorkflowNumber" write="false"/>
               <input type="checkbox" name="check_${errorWorkflowNumber}" value="on"/>
            </td>
            <td align="left" style="white-space: nowrap;">
               <mm:url page="../WizardInitAction.do" id="errorUrl" write="false">
                  <mm:param name="objectnumber"><mm:field name="number"/></mm:param>
                  <mm:param name="returnurl" value="workflow/PageWorkflowAction.do?status=${param.status}"/>
               </mm:url>
               <a href="${errorUrl}">
                  <img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="workflow.editelement"/>" title="<fmt:message key="workflow.editelement"/>"/>
               </a>
            </td>
            <td><mm:nodeinfo type="guitype" /></td>
            <td><mm:hasfield name="title">
               <mm:field name="title" />
            </mm:hasfield> <mm:hasfield name="name">
               <mm:field name="name" />
            </mm:hasfield></td>
            <td><mm:hasfield name="lastmodifier">
               <mm:field name="lastmodifier" />
            </mm:hasfield></td>
            <td><mm:hasfield name="lastmodifieddate">
               <mm:field name="lastmodifieddate">
                  <cmsc:dateformat displaytime="true" />
               </mm:field>
            </mm:hasfield></td>
         </tr>
      </mm:listnodes>
   </tbody>
</table>