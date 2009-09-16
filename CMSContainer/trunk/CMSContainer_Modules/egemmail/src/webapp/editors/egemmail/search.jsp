<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="egemmail.title">
  <script src="../repository/search.js"type="text/javascript"></script>
  <script type="text/javascript">  
    function doForward(to) {
      var elem = document.getElementById("exportForm");
      if (elem != null) {
        elem.forward.value = to;
        elem.submit();
        return false;
      }
      return true;
    }
    
    function doChangePage(newPage) {
      var elem = document.getElementById('exportForm');
      if (elem != null) {
        elem.page.value = newPage;
        return doForward('search');
      }
      return false;
    }

    function toggleValues(id){
        var check1 = document.getElementById('modified');
        var check2 = document.getElementById('created');
        if (check1 != null && check2 != null){
          if (id == 'modified' && check1.checked && check2.checked)
          	check2.checked = false;
          if (id == 'created' && check1.checked && check2.checked)
              check1.checked = false;
        }
    }
  </script>
</cmscedit:head>
<body>
    <div class="tabs">
	  <a href="#">
        <div class="tab_active">
            <div class="body">
                <div class="title">
                    <fmt:message key="egemmail.title" />
                </div>
            </div>
        </div>
		</a>
    </div>
<mm:cloud>
	<div class="editor">
		<div class="body">
         <html:form action="/editors/egemmail/EgemSearchAction">            
            
			<html:checkbox onchange="toggleValues('modified');" property="limitToLastWeekModified" styleId="modified"><fmt:message key="egemmail.field.lastweek.modified" /></html:checkbox><br/>
         <html:checkbox onchange="toggleValues('created');" property="limitToLastWeekCreated" styleId="created"><fmt:message key="egemmail.field.lastweek.created" /></html:checkbox><br/>
			<br/>
   <table border="0">
   <tr>
      <td style="width: 105px"><fmt:message key="egemmail.field.title" />:</td>
      <td><html:text style="width: 200px" property="title"/></td>
   </tr>
   <tr>
      <td style="width: 105px"><fmt:message key="egemmail.field.keywords" />:</td>
      <td><html:text style="width: 200px" property="keywords"/></td>
   </tr>
   <tr>
      <td style="width: 105px"><fmt:message key="egemmail.field.author" />:</td>
      <td>
      <html:select property="author">
			<html:option value=""><fmt:message key="egemmail.all_users" /></html:option> 
                    
         <mm:listnodes type="user" orderby="username">
   			<c:set var="username"><mm:field name="username"/></c:set>
   		   <c:if test="${username != 'anonymous'}">
               <mm:field name="username" id="userFullname" write="false"/>
               <html:option value="${userFullname}"> 
               <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /> 
               </html:option>
   			</c:if>
		   </mm:listnodes>					
	   </html:select>
      </td>
   </tr>   
   <tr>
      <td></td>
      <td><html:submit><fmt:message key="egemmail.button.search" /></html:submit></td>
   </tr>
</table>
</html:form>


      <mm:present referid="results">
      <html:form action="/editors/egemmail/EgemExportAction" styleId="exportForm">
      <html:hidden property="title" />
      <html:hidden property="keywords" />
      <html:hidden property="author" />
      <html:hidden property="page" />
      <html:hidden property="forward" />
      <html:hidden property="limitToLastWeekModified" />
      <html:hidden property="limitToLastWeekCreated" />
		<mm:list referid="results" max="${resultsPerPage}" offset="${offset*resultsPerPage}">
		    <mm:first>
		        <egem:paging offset="${offset}" resultsPerPage="${resultsPerPage}" totalNumberOfResults="${totalNumberOfResults}" />
		        <table>
		            <thead>
		             <tr>
		                 <th><input type="checkbox" onChange="selectAll(this.checked, 'exportForm', 'export_');" value="on" name="selectall"/></th>
		                 <th><fmt:message key="egemmail.field.title" /></th>
		                 <th><fmt:message key="egemmail.field.type" /></th>
		                 <th><fmt:message key="egemmail.field.author" /></th>
		                 <th><fmt:message key="egemmail.field.number" /></th>
		             </tr>
		         </thead>
		     </mm:first>
		     <tbody>
		         <tr <mm:even inverse="true">class="swap"</mm:even>>
		             <mm:field name="number" jspvar="number" write="false"/>
                       <c:choose>
                          <c:when test="${EgemExportForm.selectedNodes[number]}">
		                    <td><input type="checkbox" name="export_${number}" checked="checked"/></td>
		                </c:when>
		                <c:otherwise>
		                    <td><input type="checkbox" name="export_${number}"/></td>
		                </c:otherwise>
		                </c:choose>
		             <td>
		                <mm:field jspvar="title" write="false" name="title" />
		                <c:if test="${fn:length(title) > 50}">
		                    <c:set var="title">${fn:substring(title,0,49)}...</c:set>
		                </c:if>
		                    ${title}
		                </td>
		                <td><mm:nodeinfo type="guitype"/></td>
		                <td width="50" style="white-space: nowrap;">
		                    <mm:field name="lastmodifier" jspvar="lastmodifier" write="false"/>
		                    <mm:listnodes type="user" constraints="username = '${lastmodifier}'">
		                        <c:set var="lastmodifierFull">
		                            <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" />
		                        </c:set>
		                        <c:if test="${lastmodifierFull != ''}">
		                            <c:set var="lastmodifier" value="${lastmodifierFull}"/>
		                        </c:if>
		                    </mm:listnodes>
		                    ${lastmodifier}
		                </td>
		                <td>${number}</td>
		         </tr>
		     </tbody>
		     <mm:last>
	            <tfoot>
	                <tr>
	                  <td><input type="checkbox" onChange="selectAll(this.checked, 'exportForm', 'export_');" value="on" name="selectall"/></td>
                     <td><b><fmt:message key="egemmail.field.title" /></b></td>
                     <td><b><fmt:message key="egemmail.field.type" /></b></td>
                     <td><b><fmt:message key="egemmail.field.author" /></b></td>
                     <td><b><fmt:message key="egemmail.field.number" /></b></td>
	                </tr>
               </tfoot>
		        </table>
                  <egem:paging offset="${offset}" resultsPerPage="${resultsPerPage}" totalNumberOfResults="${totalNumberOfResults}" />				        
		        <html:submit onclick="return doForward('export');"><fmt:message key="egemmail.button.export" /></html:submit>
		     </mm:last>
		 </mm:list>
      </html:form>
      </mm:present>
		</div>
		<div class="side_block_end"></div>
	</div>	
</mm:cloud>
</body>
</html:html>
</mm:content>