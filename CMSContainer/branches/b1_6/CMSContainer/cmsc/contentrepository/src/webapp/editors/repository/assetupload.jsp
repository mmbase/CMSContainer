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
</table>
<div>
   <c:if test="${param.uploadingDone eq 'yes'}">
      <c:if test="${param.failed != 0}" >
         <span style="color:#cc0000"><fmt:message key="assets.upload.error"/></span>
         <fmt:message key="asset.upload.failed.results"/> ${param.failed}
         <br/>
         <c:forEach var="fileName" items="${notUploadedFiles}" varStatus="fileAmount">
         ${fileName}&nbsp;&nbsp;&nbsp;
         </c:forEach>
         <c:remove var="notUploadedFiles" scope="session"/>
      </c:if>
      <c:if test="${param.uploaded != 0}" >
         <br/><br/>
         <fmt:message key="asset.upload.uploaded.results"/> ${param.uploaded}
         <br/>
         <c:forEach var="fileName" items="${uploadedFiles}" varStatus="fileAmount" >
         ${fileName}&nbsp;&nbsp;&nbsp;
         </c:forEach>
         <c:remove var="notUploadedFiles" scope="session"/>
      </c:if>
   </c:if>
</div>
</html:form>
<div id="busy"><fmt:message key="uploading.message.wait" /><br />
</div>