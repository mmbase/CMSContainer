<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="versioning.title.content" >
   <script type="text/javascript">
      var checkboxes = new Array();
      var versions = new Array();
      
      function diff() {
         if(versions.length == 2){
            openPopupWindow("diff", 500, 500, "../versioning/DiffAction.do?objectnumber=" + versions[0]+"&archivenumber="+versions[1]);
         }
      }
      
      function changeVersion(element, versionId){
         if(element.checked){
            addVersion(element, versionId);
         } else {
            removeVersion(element, versionId);
         }
         if(versions.length == 2){
            document.getElementById("textdiv").style.display="none";
            document.getElementById("anchordiv").style.display="block";
         } else {
            document.getElementById("anchordiv").style.display="none";
            document.getElementById("textdiv").style.display="block";
         }
      }
      
      function addVersion(element, versionId){
         if(versions.length < 2){
            versions[versions.length] = versionId;
            checkboxes[checkboxes.length] = element;
         } else {
            checkboxes[0].checked = false;
            versions[0] = versions[1];
            versions[1] = versionId;
            checkboxes[0] = checkboxes[1];
            checkboxes[1] = element;
         }
      }
      
      function removeVersion(element, versionId){
         var startMove = false;
         for(var i = 0, n = 0; i <versions.length; i++){
            if(versions[i] == versionId) {
               startMove = true;
            } else {
               if(startMove){
                  versions[n] = versions[i];
                  checkboxes[n] = checkboxes[i];
               }
               n++;
            }
         }
         versions.length -= 1;
         checkboxes.length -= 1;
      }
      
   </script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<mm:import externid="archiveNodes" jspvar="nodeList" vartype="List" />

   <div class="content">
      <div class="tabs">
	    <a href="#">
          <div class="tab_active">
            <div class="body">
               <div class="title">
                  <fmt:message key="versioning.title.content" />
               </div>
            </div>
          </div>
		 </a>
      </div>
   </div>
   <div class="editor">
      <div class="body">
         <mm:node number="${param.nodenumber}" notfound="skipbody">
            <p>
               <fmt:message key="versioning.versions.of" />
               <b><mm:nodeinfo type="guitype" /></b>:
               <mm:field name="title"/>
            </p>
         </mm:node>
         <c:if test="${not empty error}">
            <p class="error"><img src="../gfx/icons/error.png" alt="!">${error}</p>
         </c:if>
         <c:if test="${not empty message}">
            <p>${message}</p>
         </c:if>
         <c:if test="${not isAllowed}">
            <p><fmt:message key="versioning.not.allowed"/></p>
         </c:if>
         <c:if test="${empty archiveNodes}">
            <p><fmt:message key="versioning.no.versions"/></p>
         </c:if>
         <c:if test="${action == 'workflow' }">
             <fmt:message key="versioning.workflow.restore" />
         </c:if>
         </div>
         <div class="ruler_green">
            <div><fmt:message key="versioning.title.content" /></div>
         </div>
		<div class="body">

         <mm:list referid="archiveNodes">
            <mm:first>
               <table>
                  <thead>
                     <tr>
                        <th>&nbsp;</th>
                        <th nowrap="true"><fmt:message key="versioning.date"/></th>
                        <th><fmt:message key="versioning.author"/></th>
                        <th><fmt:message key="versioning.publish"/></th>
                        <th>&nbsp;</th>
                        <th>&nbsp;</th>
                     </tr>
                  </thead>
                  <tbody class="hover">
            </mm:first>
            <mm:even inverse="true"><c:set var="class">class="swap"</c:set></mm:even>
            <mm:even><c:set var="class"></c:set></mm:even>
            <tr ${class}>
               <td><input type="checkbox" onclick='changeVersion(this, <mm:field name="number"/>)' /></td>
               <td>
                  <mm:field name="date"><cmsc:dateformat displaytime="true" /></mm:field>
               </td>
               <td>
                  <mm:field name="author_number" id="author_number" write="false"/>
                  <mm:hasnode number="${author_number}" inverse="true">
                     <fmt:message key="versioning.no.author"/>
                  </mm:hasnode>
                  <mm:node number="${author_number}" notfound="skipbody">
                     <mm:field name="firstname"/> <mm:field name="prefix"/>  <mm:field name="surname"/> (<mm:field name="username"/>)
                  </mm:node>
               </td>
               <td><mm:field name="onlive"/>
               </td>
               <mm:field name="publish" jspvar="isPublished" write="false"/>
               <td>
                  <c:url value="/editors/versioning/RestoreAction.do" var="restoreUrl">
                     <c:param name="archivenumber"><mm:field name="number"/></c:param>
                     <c:param name="nodenumber"><mm:field name="original_node"/></c:param>
                  </c:url>
                  <c:if test="${isAllowed}">
                     <c:if test="${empty action}">
                        <a href="${restoreUrl}" class="button">
                           <fmt:message key="versioning.restore"/>
                        </a>
                     </c:if>
                     <c:if test="${action == 'workflow' && isPublished}">
                        <c:url value="/editors/workflow/WorkflowItemDelete.do" var="returnurl">
                           <c:param name="number">${number}</c:param>
                           <c:param name="action">delete</c:param>
                           <c:param name="archivenumber"><mm:field name="number"/></c:param>
                           <c:param name="returnurl">${returnUrl}</c:param>
                        </c:url>
                        <a href="${returnurl}" class="button">
                           <fmt:message key="versioning.restore"/>
                        </a>
                     </c:if> 
                     <c:if test="${action == 'workflow' && !isPublished}">
                        <fmt:message key="versioning.restore"/>
                     </c:if> 
                  </c:if>
               </td>
               <td>
               <c:if test="${isPublished}">
                   <c:set var="status" value="published"/>
               </c:if>
               <c:if test="${!isPublished}">
                   <c:set var="status" value="draft"/>
               </c:if>
               <img src="../gfx/icons/status_${status}.png"
             alt="<fmt:message key="versioning.status.${status}" />"
             title="<fmt:message key="versioning.status.${status}" />"/>
               </td>
            </tr>
            <mm:last>
            <tr>
               <td colspan="6" >
                  <div id="anchordiv" style="display:none" >
                     <a id="versioncompare" disabled="disabled" class="button" onclick="diff()" >
                        <fmt:message key="versioning.compare"/>
                     </a>
                  </div>
                  <div id="textdiv" style="font-weight:bold; color:gray;" >
                     <fmt:message key="versioning.compare"/>
                  </div>
               </td>
            </tr>
                  </tbody>
               </table>
            </mm:last>
         </mm:list>
      </div>
   </div>
</mm:cloud>
</body>
</html:html>
</mm:content>