<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="versioning.title.content" />
<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">
   <div class="content">
      <div class="tabs">
          <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="versioning.title.content" /></a>
               </div>
            </div>
         </div>
      </div>
   </div>
   <div class="editor">
      <div class="body">
         </div>
         <div class="ruler_green">
            <div><fmt:message key="versioning.title.diff" /></div>
         </div>
      <div class="body">        
         <c:out value="${diffs}" escapeXml="false"/>
      </div>
   </div>
</mm:cloud>
</body>
</html:html>
</mm:content>