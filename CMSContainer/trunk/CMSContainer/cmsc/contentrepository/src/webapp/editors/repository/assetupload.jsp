<%@ page import="com.finalist.util.http.BulkUploadUtil"
%>
<script type="text/javascript">
       function upload() {
           setTimeout('sayWait();',0);
       }
   
       function sayWait() {
           document.getElementById("busy").style.visibility="visible";
       }
</script>

<html:form action="/editors/repository/AssetUploadAction.do" enctype="multipart/form-data" method="post">
<input type="hidden" id="assetType" name="assetType" value="attachments"/>
<input type="hidden" id="parentchannel" name="parentchannel" value="${parentchannel}"/>
<table border="0">
   <tr>
      <td><fmt:message key="asset.upload.explanation" /></td>
   </tr>
   <tr>
      <td><html:file property="file" /></td>
   </tr>
   <tr>
      <td><html:submit property="uploadButton" onclick="upload();">
         <fmt:message key='assets.upload.submit' /></html:submit></td>
   </tr>
   <c:if test="${param.uploadingDone eq 'yes'}">
   <tr>
      <td><fmt:message key="asset.upload.failed.results"/> ${param.failed}</td>
   </tr>
   <c:if test="${param.failed != 0}" >
    <tr>
      <td>
      <c:forEach var="fileName" items="${notUploadedFiles}" varStatus="fileAmount">
      ${fileName}&nbsp;&nbsp;&nbsp;
      </c:forEach>
      <c:remove var="notUploadedFiles" scope="session"/>
      </td>
    </tr>
   </c:if>
   <tr>
      <td><fmt:message key="asset.upload.uploaded.results"/> ${param.uploaded}</td>
   </tr>
   <c:if test="${param.uploaded != 0}" >
   <tr>
     <td>
     <c:forEach var="fileName" items="${uploadedFiles}" varStatus="fileAmount" >
        ${fileName}&nbsp;&nbsp;&nbsp;
     </c:forEach>
      <c:remove var="notUploadedFiles" scope="session"/>
     </td>
   </tr>
   </c:if>
   </c:if>
   <c:if test="${param.exceed eq 'yes' }">
   <tr>
      <td><fmt:message key="asset.upload.size.exceed"/></td>
   </tr>
   </c:if>
   <c:if test="${param.emptyFile eq 'yes' }">
   <tr>
      <td><fmt:message key="asset.upload.size.zero"/></td>
   </tr>
   </c:if>
   <c:if test="${param.emptyFileName eq 'yes' }">
   <tr>
      <td><fmt:message key="asset.upload.emptyfile"/></td>
   </tr>
   </c:if>
</table>
</html:form>
<div id="busy"><fmt:message key="uploading.message.wait" /><br />
</div>