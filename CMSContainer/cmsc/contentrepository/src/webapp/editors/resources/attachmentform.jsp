<html:hidden property="contenttypes" value="attachments" />
<table border="0">
   <tr>
      <td style="width: 105px" colspan="2"><fmt:message key="attachmentform.title" /></td>
      <td><html:text style="width: 200px" property="title"/></td>
   </tr>
   <tr>
      <td colspan="2"><fmt:message key="attachmentform.description" /></td>
      <td><html:text style="width: 200px" property="description"/></td>
   </tr>
   <tr>
      <td colspan="2"><fmt:message key="attachmentform.filename" /></td>
      <td><html:text style="width: 200px" property="filename"/></td>
   </tr>
   <c:set var="contentChannel">${AttachmentForm.contentChannel}</c:set>
   <%@include file="selectchannel.jsp" %>
   <tr>
      <td colspan="2"></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               value="<fmt:message key="attachmentform.submit" />"/></td>
   </tr>
</table>