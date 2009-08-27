<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="../globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:import externid="bottomurl" from="parameters"/>
<html:html xhtml="true">
<cmscedit:head title="topmenu.title">
   <link href="<cmsc:staticurl page='/editors/css/topmenu.css'/>" type="text/css" rel="stylesheet" />
</cmscedit:head>
<body>
      <mm:cloud loginpage="login.jsp" rank="basic user">

         <div id="header">
	         <div class="title_image">
               <c:set var="logotext"><fmt:message key="logo.title" /> <cmsc:version type="cmsc"/></c:set>
               <img src="../gfx/logo_editors.png" alt='${logotext}' title='${logotext}'/>
            </div>
			<div class="title">	
				<a href="<mm:url page="index.jsp"/>" target="_top" style="text-decoration:none;color:#DEE204">
				   <fmt:message key="editors.title" />
				</a>
			</div>
            <mm:cloudinfo type="user" id="cloudusername" write="false" />
            <mm:listcontainer path="user">
               <mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
               <mm:list>
                  <mm:import id="fullname" jspvar="fullname"><mm:field name="user.firstname"/> <mm:field name="user.prefix"/> <mm:field name="user.surname"/></mm:import>
                  <div class="userinfo">
                     <fmt:message key="topmenu.user.title" />
                     <% if ("".equals(fullname.trim())) { %>
                        <mm:write referid="cloudusername"/>
                     <% } else { %>
                        <mm:write referid="fullname"/>
                     <% } %>
                     <mm:haspage page="/editors/help/">
                        | <a href="<mm:url page="help/"/>" target="bottompane" id="tutorial"><fmt:message key="topmenu.help" /></a>
                     </mm:haspage>
                     <mm:haspage page="/editors/simple/logout.jsp">
                        | <a href="<mm:url page="logout.jsp"/>" target="_top" id="logout"><fmt:message key="topmenu.logout" /></a>
                    </mm:haspage>
                  </div>
               </mm:list>
            </mm:listcontainer>         </div>     

            <div style="float:right;"></div>
         </div>
      </mm:cloud>
   </body>
</html:html>
</mm:content>