<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" %><%@page import="com.finalist.util.http.BulkUploadUtil"
%><%@page import="java.util.List"
%> <mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="images.upload.title">
  <script src="../repository/search.js" type="text/javascript"></script>
  <script type="text/javascript">
    function upload() {
        var f=document.forms[0];
        f.submit();
        setTimeout('sayWait();',0);

    }

    function sayWait() {
        document.getElementById("busy").style.visibility="visible";
        document.getElementById("notbusy").style.visibility="hidden";
    }
    
         
   function showInfo(objectnumber) {
      openPopupWindow('imageinfo', '900', '500', 'imageinfo.jsp?objectnumber='+objectnumber);
    }
          
    var blockSelect = false;
  </script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="tabs">
         <div class="tab">
            <div class="body">
               <div>
                  <a href="imagesearch.jsp?action=${param.uploadAction}"><fmt:message key="images.title" /></a>
               </div>
            </div>
         </div>
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="images.upload.title" /></a>
               </div>
            </div>
         </div>
      </div>
      
      <div class="editor" style="height:500px">
          <div class="body">
              <html:form action="/editors/resources/ImageUploadAction.do" enctype="multipart/form-data" method="post">
                <input type="hidden" name="uploadAction" value="${param.uploadAction}"/>
                    <table border="0">
                       <tr>
                          <td><fmt:message key="images.upload.explanation" /></td>
                       </tr>
                       <tr>
                          <td><html:file property="file" /></td>
                       </tr>
                       <tr>
                          <td><input type="button" name="uploadButton" onclick="upload();" 
                                   value="<fmt:message key="images.upload.submit" />"/></td>
                       </tr>
                    </table>
             </html:form>
          </div>
         <div id="busy">
             <fmt:message key="uploading.message.wait"/><br />
         </div>
         <div id="notbusy">
            <c:if test="${param.uploadError || param.numberOfFailedFiles > 0}">
                <p><span style="color:#CC0000" >&nbsp;&nbsp;<fmt:message key="images.upload.error"/></span></p>
            </c:if>
            <c:if test="${param.numberOfFailedFiles >= 0}">
                <p>&nbsp;&nbsp;<fmt:message key="images.upload.failedtip"/> ${param.numberOfFailedFiles}<br />
                    <c:forEach var="failedFile" items="${sessionScope.failedFiles}">
                        <c:out value="${failedFile}" />&nbsp;&nbsp;&nbsp;
                    </c:forEach>
                    <c:remove var="failedFiles" scope="session"/>       
                </p>
            </c:if>
            <c:if test="${param.numberOfUploadedNodes >= 0}">
                <p>&nbsp;&nbsp;<fmt:message key="images.upload.uploadedtip"/> ${param.numberOfUploadedNodes}<br />
                    <c:forEach var="uploadedFile" items="${sessionScope.uploadedFiles}">
                        <c:out value="${uploadedFile}" />&nbsp;&nbsp;&nbsp;
                    </c:forEach>
                    <c:remove var="uploadedFiles" scope="session"/>
                </p>
            </c:if>
         </div>
          <div class="ruler_green"><div><fmt:message key="images.upload.results" /></div></div>
          <div class="body">
<c:if test="${param.numberOfUploadedNodes > 0}">
   <table>
      <tr class="listheader">
         <th></th>
         <th nowrap="true"><fmt:message key="imagesearch.titlecolumn" /></th>
         <th><fmt:message key="imagesearch.filenamecolumn" /></th>
         <th><fmt:message key="imagesearch.mimetypecolumn" /></th>
         <th></th>
      </tr>
      <tbody class="hover">
          <c:set var="useSwapStyle">true</c:set>

          <mm:listnodescontainer path="images" nodes="${param.uploadedNodes}">
              <mm:listnodes>

         <mm:field name="description" escape="js-single-quotes" jspvar="description">
            <mm:field name="title" escape="js-single-quotes" jspvar="title">
               <%description = ((String)description).replaceAll("[\\n\\r\\t]+"," "); 
               description = ((String)description).replaceAll("[\"]","@quot;");
               title = ((String)title).replaceAll("[\"]","@quot;");
               %>
                    <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<%=title%>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
            </mm:field>
         </mm:field>
         
              <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                 <td onclick="if(!blockSelect) {objClick(this);} blockSelect=false;">
                  <%-- use uploadedNodes and numberOfUploadedNodes in return url --%>
                  <c:set var="returnUrl">/editors/resources/imageupload.jsp?uploadedNodes=${param.uploadedNodes}&numberOfUploadedNodes=${param.numberOfUploadedNodes}&uploadAction=${param.uploadAction}</c:set>
             <c:choose>
                <c:when test="${param.uploadAction == 'select'}">
                        <a href="<mm:url page="SecondaryEditAction.do">
                                     <mm:param name="action" value="init"/>
                                     <mm:param name="number"><mm:field name="number" /></mm:param>
                                     <mm:param name="returnUrl" value="${returnUrl}"/>
                                 </mm:url>" onclick="blockSelect = true">
                    </c:when>
                    <c:otherwise>
                       <a href="<mm:url page="../WizardInitAction.do">
                                               <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                               <mm:param name="returnurl" value="${returnUrl }" />
                                            </mm:url>">
                    </c:otherwise>
                 </c:choose>
                           <img src="../gfx/icons/page_edit.png" title="<fmt:message key="images.upload.edit"/>" alt="<fmt:message key="images.upload.edit"/>"/></a>
               
                           <a href="<mm:url page="DeleteSecondaryContentAction.do" >
                                                  <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                                  <mm:param name="object_type" value="imagesupload" />
                                               </mm:url>">
                           <img src="../gfx/icons/delete.png" alt="<fmt:message key="imagesearch.icon.delete" />" title="<fmt:message key="imagesearch.icon.delete" />"/></a>

                  <a href="javascript:showInfo(<mm:field name="number" />);" onclick="blockSelect = true;">
                        <img src="../gfx/icons/info.png" title="<fmt:message key="images.upload.info"/>" alt="<fmt:message key="images.upload.info"/>"/></a>
                 </td>
                 <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                 <td onMouseDown="objClick(this);"><mm:field name="filename"/></td>
                 <td onMouseDown="objClick(this);"><mm:field name="itype"/></td>
                 <td onMouseDown="objClick(this);"><img src="<mm:image template="s(100x100)"/>" alt="" /></td>
              </tr>
              <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
              </mm:listnodes>
          </mm:listnodescontainer>

      </tbody>
   </table>
</c:if>

         </div>
      </div>
</mm:cloud>
</body>
</html:html>
</mm:content>