<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="group.title">
	<script src="../../js/selectbox.js" type="text/javascript"></script>
   <script type="text/javascript">
      function selectChannel(channel, path) {
         document.forms['GroupForm'].contentchannel.value = channel;
         document.forms['GroupForm'].contentchannelpath.value = path;
      }

      function erase(field) {
         document.forms['GroupForm'][field].value = '';
      }
   </script>
	<style>
		input.select { font-height: 4px;}
	</style>
</cmscedit:head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" loginpage="../login.jsp" rank='administrator'>
<cmscedit:contentblock title="group.title" titleClass="content_block_pink">
	<html:form action="/editors/usermanagement/GroupAction" 
		 onsubmit="return selectboxesOnSubmit('users', 'members');">
		<html:hidden property="id" />
		<div id="group">
		<table>
			<tr>
				<td class="fieldname" width="120px" nowrap="nowrap"><fmt:message key="group.name" /></td>
				<td width='60px'></td>
				<td>
					<logic:equal name="GroupForm" property="id" value="-1">
						<html:text property="name" size='15' maxlength='15' />
						<span class="notvalid"><html:errors bundle="SECURITY" property="groupname" /></span>
					</logic:equal> 
					<logic:notEqual name="GroupForm" property="id" value="-1">
						<bean:write name="GroupForm" property="name" />
					</logic:notEqual>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="fieldname"><fmt:message key="group.description" /></td>
				<td><html:text property="description" size='30' /></td>
			</tr>
         <tr>
            <td><fmt:message key="group.simpleeditorchannel" /></td>
            <td align="right">
               <a href="<c:url value='/editors/repository/select/SelectorChannel.do?channel=${contentchannel}&portletId=${portletId}' />" target="selectchannel" onclick="openPopupWindow('selectchannel', 340, 400)"> 
                  <img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.channelselect" />"/>
               </a>
               <a href="javascript:erase('contentchannel');erase('contentchannelpath')">
                  <img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
               </a>
            </td>
            <td>
               <mm:cloud>
                  <mm:node number="${contentchannel}" notfound="skip">
                     <mm:field name="path" id="contentchannelpath" write="false" />
                  </mm:node>
               </mm:cloud>
               <input type="hidden" name="contentchannel" value="${contentchannel}" />
               <input type="text" name="contentchannelpath" value="${contentchannelpath}" disabled="true" size='30' />
            </td>
         </tr>
		</table>
		</div>

		<table>
			<tr>
				<td width="180">
					<fmt:message key="group.nonmembers" /><br />
					<html:select property="users" size="25" styleId="users" multiple="true" style="width: 180px">
						<html:optionsCollection name="usersList" value="value" label="label"/> 
					</html:select> 
				</td>
				<td style="vertical-align:middle" width="30">
					<input type="button" class="flexbutton" value="&gt;&gt;" onClick="one2two('users', 'members', true)" />
					<br/>
					<input type="button" class="flexbutton" value="&lt;&lt;" onClick="two2one('users', 'members', true)"/>
				</td>
				<td>
					<fmt:message key="group.members" /><br />
					<html:select property="members" size="25" styleId="members" multiple="true" style="width: 180px">
						<html:optionsCollection name="membersList" value="value" label="label"/> 
					</html:select> 
				</td>
			</tr>
		</table>

		<br />
		<div style="padding: 5px;">
			<html:submit style="width:90"><fmt:message key="group.submit"/></html:submit>
         <html:cancel style="width:90"><fmt:message key="group.cancel"/></html:cancel>
		</div>
	</html:form>
	</cmscedit:contentblock>	
</mm:cloud>
</body>
</html:html>
</mm:content>
