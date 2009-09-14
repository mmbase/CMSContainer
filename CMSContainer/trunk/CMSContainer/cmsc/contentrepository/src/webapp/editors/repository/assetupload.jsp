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
   <c:if test="${uploadingDone eq 'yes'}">
      <c:if test="${param.failed ne '0'}" >
		<c:if test="${param.isZip eq 'isZip'}">
         <span style="color:#cc0000"><fmt:message key="assets.upload.error.multiple"/></span>
         <fmt:message key="asset.upload.failed.results"/> ${param.failed}
         <br/>
         <c:forEach var="fileName" items="${notUploadedFiles}" varStatus="fileAmount">
         ${fileName}&nbsp;&nbsp;&nbsp;
         </c:forEach>
		</c:if>
		<c:if test="${param.isZip ne 'isZip'}">
		 <fmt:message key="asset.upload.failed.results"/>&nbsp;
         <c:forEach var="fileName" items="${notUploadedFiles}" varStatus="fileAmount">
         ${fileName}<br/>
         </c:forEach>
			<c:if test="${param.big eq 'big'}">
				<span style="color:#cc0000"><fmt:message key="assets.upload.error.big"><fmt:param>${param.maxAllowFileSize}</fmt:param></fmt:message></span>
			</c:if>
			<c:if test="${param.exsit eq 'exsit'}">
				<span style="color:#cc0000"><fmt:message key="assets.upload.error.exsit"/></span><br/>
				<a href="../repository/Asset.do?type=asset&parentchannel=${param.exsitChannelId}&direction=down">
					${exsitChannel}
				</a>&nbsp;&nbsp;
				<fmt:message key="assets.upload.error.exsit.filename"><fmt:param>${exsitAssetTitle}</fmt:param></fmt:message>
		        <c:remove var="exsitChannel" scope="session"/>
		        <c:remove var="exsitAssetTitle" scope="session"/>
			</c:if>
		</c:if>
         <c:remove var="notUploadedFiles" scope="session"/>
         <br/>
      </c:if>
      <c:if test="${param.uploaded ne '0'}" >
         <fmt:message key="asset.upload.uploaded.results"/> ${param.uploaded}
         <br/>
         <c:forEach var="fileName" items="${uploadedFiles}" varStatus="fileAmount" >
         ${fileName}&nbsp;&nbsp;&nbsp;
         </c:forEach>
         <c:remove var="notUploadedFiles" scope="session"/>
      </c:if>
   </c:if>
   <c:remove var="uploadingDone" scope="session"/>
</div>
</html:form>
<div id="busy"><fmt:message key="uploading.message.wait" /><br />
</div>