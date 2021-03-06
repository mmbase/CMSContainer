<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
   <form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
      <table class="editcontent">
      <script type="text/javascript">
         function validateColumn(){
            var column = document.forms[0].column.value;
            if(!/^\s*[1-9]\d*\s*$/.test(column)){
               alert("Column value must an integer greater than 0!");
               document.forms[0].column.focus();
               return false;
            } else {
               document.forms[0].column.value = column;
               return true;
            }
         }
      </script>
         <%-- Save button --%>
         <tr>
            <td colspan="3">
               <a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button" onclick="return validateColumn();">
                  <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" />
               </a>
            </td>
         </tr>
      
         <%-- Portletdefinition display --%>
         <c:import url="sections/definitiondisplay.jsp" />
      
         <%-- View selector --%>
         <c:import url="sections/viewselector.jsp" />
      
         <%-- Select content channel selector --%>
         <c:import url="sections/selectchannel.jsp" />
      
         <tr>
            <td colspan="3">
               <h4><fmt:message key="edit_defaults.content" /></h4>
            </td>
         </tr>
         
         <tr>
            <td><fmt:message key="edit_defaults.contentelement" />:</td>
            <td align="right">
               <a href="<c:url value='/editors/repository/select/index.jsp?contentnumber=${contentelement}&portletId=${portletId}' />" target="selectcontentelement" onclick="openPopupWindow('selectcontentelement', 900, 400)"> 
                  <img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.contentselect" />" />
               </a>
               <a href="javascript:erase('contentelement');erase('contentelementtitle')">
                  <img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
               </a>
            </td>
            <td>
               <mm:cloud>
                  <mm:node number="${contentelement}" notfound="skip">
                     <mm:field name="title" id="contentelementtitle" write="false" />
                  </mm:node>
               </mm:cloud>
               <input type="hidden" name="contentelement" value="${contentelement}" />
               <input type="text" name="contentelementtitle" value="${contentelementtitle}" disabled="true" />
            </td>
         </tr>
      
         <%-- Set image width--%>
         <tr>
            <td colspan="2"><fmt:message key="edit_defaults.width" />:</td>
            <td>
               <cmsc:select var="width" default="110">
                  <cmsc:option value="50" message="edit_defaults.width.small"/>
                  <cmsc:option value="110" message="edit_defaults.width.default"/>
                  <cmsc:option value="150" message="edit_defaults.width.large"/>
                  <cmsc:option value="200" message="edit_defaults.width.xlarge"/>
               </cmsc:select>
            </td>
         </tr>
      
         <%-- Order by option--%>
         <tr>
            <td colspan="2"><fmt:message key="edit_defaults.orderby" />:</td>
            <td>
               <cmsc:select var="orderby">
                  <cmsc:option value="filename" message="edit_defaults.orderby.filename" />
                  <cmsc:option value="creationdate" message="edit_defaults.orderby.creationdate" />
               </cmsc:select>
            </td>
         </tr>
         <tr>
            <td colspan="2"><fmt:message key="edit_defaults.direction" />:</td>
            <td>
               <cmsc:select var="direction">
                  <cmsc:option value="DOWN" message="edit_defaults.descending" />
                  <cmsc:option value="UP" message="edit_defaults.ascending" />
               </cmsc:select>
            </td>
         </tr>
         
         <%-- Paging --%>
         <c:import url="sections/paging.jsp" />
         
         <%-- input the amount of columns --%>
         <tr>
            <td colspan="2"><fmt:message key="edit_defaults.column" />:</td>
            <td><cmsc:text var="column" /></td>
         </tr>
      
         <%-- Save button --%>
         <tr>
            <td colspan="3">
               <a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button" onclick="return validateColumn();">
                  <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" />
               </a>
            </td>
         </tr>
         
      </table>
   </form>
</div>