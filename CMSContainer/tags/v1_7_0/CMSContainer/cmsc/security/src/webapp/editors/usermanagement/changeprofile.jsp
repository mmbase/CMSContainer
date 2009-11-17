<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="changeprofile.title">
</cmscedit:head>
<c:choose>
<c:when test="${param.succeeded}">
      <body onload="alert('<fmt:message key="changeprofile.succeeded" />')">
      </body>
</c:when>
<c:otherwise>
	<body>
	<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="tabs">
         <!-- actieve TAB -->
		  <a name="activetab">
			 <div class="tab_active">
				<div class="body">
				   <div class="title">
					 <fmt:message key="changeprofile.title" />
				   </div>
				</div>
			 </div>
		 </a>
      </div>

    <div class="editor">
      <div class="body">



<html:form action="/editors/usermanagement/ChangeProfileAction">
   <table class="formcontent">
      <tr>
            <td><fmt:message key="user.notice" /></td>
         </tr>
      <tr>
         <td class="fieldname" width='180'><fmt:message key="user.account" /></td>
         <td>
             <bean:write name="ChangeProfileForm" property="username" />
         </td>
      </tr>
      <tr>
         <td class="fieldname"><fmt:message key="user.firstname" /></td>
         <td><html:text property="firstname" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
      </tr>
      <tr>
         <td class="fieldname"><fmt:message key="user.prefix" /></td>
         <td><html:text property="prefix" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
      </tr>
      <tr>
         <td class="fieldname"><fmt:message key="user.surname" /></td>
         <td><html:text property="surname" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
      </tr>
      <tr>
         <td class="fieldname"><fmt:message key="user.email" />*</td>
         <td><html:text property="email" size='30' maxlength='255' />
            <span class="notvalid"><html:errors bundle="SECURITY" property="email" /></span>
         </td>
      </tr>
      <tr>
         <td class="fieldname"><fmt:message key="user.company" /></td>
         <td><html:text property="company" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
      </tr>
      <tr>
         <td class="fieldname"><fmt:message key="user.department" /></td>
         <td><html:text property="department" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
      </tr>
      </tr>
         <tr><td class="fieldname" nowrap><fmt:message key="user.emailsignal" /></td>
         <td class="field"><html:checkbox property="emailSignal" style="width: auto;"/></td>
      </tr>
      <tr>
         <td class="fieldname" nowrap width="105px"><fmt:message key="changeprofile.current" /></td>
	      <td class="fieldname">
	         <html:password property="password1" size='15' maxlength='15' style="width:200px"/>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="password1"/></span>
	      </td>
	   </tr>
	   <tr>
         <td class="fieldname" nowrap><fmt:message key="changeprofile.new" /></td>
	      <td class="fieldname">
	         <html:password property="newpassword" size='15' maxlength='15'  style="width:200px"/>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="newpassword"/></span>
	      </td>
	   </tr>
      <tr>
   	   <td class="fieldname" nowrap><fmt:message key="changeprofile.confirm" /></td>
	      <td class="fieldname">
	         <html:password property="confirmnewpassword" size='15' maxlength='15'  style="width:200px"/>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="confirmnewpassword"/></span>
         </td>
	   </tr>
	   <tr>
	      <td >&nbsp;</td>
	      <td>
	      <html:submit ><fmt:message key="changeprofile.submit" /></html:submit>
 		  <html:cancel ><fmt:message key="user.cancel"/></html:cancel>
	      </td>
	   </tr>
	</table>
</html:form>
</mm:cloud>
      </div>
   </div>
</body>
</c:otherwise>
</c:choose>
</html:html>
</mm:content>