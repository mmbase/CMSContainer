<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="knownvisitor-ntlm.title" />
<body>
  <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="tabs">
         <div class="tab_active">
   	   	      <div class="body">
   	            <div>
  		            <a href="#"><fmt:message key="knownvisitor-ntlm.title" /></a>
               </div>
    		</div>
  		   </div>
      </div>

     <div class="editor" style="height:500px">
      	<div class="body">
      	
	      	<c:forEach var="p" items="${param}">
	      		<c:if test="${fn:indexOf(p.key,'new_') == 0}">
	      			<c:set var="newValue" value="${p.value}"/>
	      			<c:set var="key" value="${fn:substring(p.key,4,fn:length(p.key))}"/>
 			      			<c:set var="oldValueKey" value="old_${key}"/>
 			      			<c:set var="oldValue" value="${param[oldValueKey]}"/>
 			      			<c:if test="${newValue != oldValue}">
 			      				<cmsc:setproperty key="${key}" value="${newValue}"/>
 			      			</c:if>
	      		</c:if>
	      	</c:forEach>
      	

				<form method="post">
					<cmsc:moduleproperties module="knownvisitor-ntlm" var="properties"/>
					<c:forEach var="property" items="${properties}">
						<label style="width:200px"><fmt:message key="${property.key}" /></label>
						
						<c:choose>
							<c:when test="${fn:contains(property.key,'password')}">
								<input type="hidden" name="old_${property.key}" value="*****"/>
								<input type="password" name="new_${property.key}" value="*****"/>
							</c:when>
							<c:when test="${fn:contains(property.key,'enabled')}">
								<input type="hidden" name="old_${property.key}" value="${property.value}"/>
								<select name="new_${property.key}">
									<option value="false"><fmt:message key="knownvisitor-ntlm.enabled.false"/></option>
									<option value="true" <c:if test="${property.value=='true'}">selected</c:if>><fmt:message key="knownvisitor-ntlm.enabled.true"/></option>
								</select>
							</c:when>
							<c:otherwise>
								<input type="hidden" name="old_${property.key}" value="${property.value}"/>
								<input type="text" name="new_${property.key}" value="${property.value}"/>
							</c:otherwise>
						</c:choose>
						
						<br/>
					</c:forEach>
					
					<c:set var="submitText"><fmt:message key="knownvisitor-ntlm.save" /></c:set>
					<input type="submit" value="${submitText}"/>
				</form>

			</div>
		</div>
	</mm:cloud>
</body>
</html:html>
</mm:content>
	
