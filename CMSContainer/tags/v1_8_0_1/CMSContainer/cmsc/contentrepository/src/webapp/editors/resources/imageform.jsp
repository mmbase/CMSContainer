<html:hidden property="contenttypes" value="images" />
<table border="0">
   <tr>
      <td style="width: 105px" colspan="2"><fmt:message key="imageform.title" /></td>
      <td><html:text style="width: 200px" property="title"/></td>
   </tr>
   <tr>
      <td colspan="2"><fmt:message key="imageform.description" /></td>
      <td><html:text style="width: 200px" property="description"/></td>
   </tr>
   <tr>
      <td colspan="2"><fmt:message key="attachmentform.filename" /></td>
      <td><html:text style="width: 200px" property="filename"/></td>
   </tr>
   <c:set var="contentChannel">${ImageForm.contentChannel}</c:set>
   <%@include file="selectchannel.jsp" %>
   <tr>
      <td colspan="2"></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
               value="<fmt:message key="imageform.submit" />"/></td>
   </tr>
</table>