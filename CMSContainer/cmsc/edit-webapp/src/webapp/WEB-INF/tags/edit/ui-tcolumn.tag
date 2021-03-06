<%@ tag body-content="scriptless" %>
<%@ attribute name="titlekey" rtexprvalue="true" required="false" %>
<%@ attribute name="title" rtexprvalue="true" required="false" %>
<%@ attribute name="sort" rtexprvalue="true" required="false" %>
<%@ attribute name="width" rtexprvalue="true" required="false" %>
<%@ attribute name="checkboxlabel" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="dir" value="${param.dir ne 'asc' ? 'asc' : 'desc' }"/>
<c:choose>
   <%--@elvariable id="tag_op_status" type="String"--%>
   <c:when test="${tag_op_status eq 'header'}">
      <c:set var="content">
         <c:choose>
            <c:when test="${not empty title}">
               ${title}
            </c:when>
            <c:when test="${not empty titlekey}">
               <fmt:message key="${titlekey}"/>
            </c:when>
            <c:when test="${'' eq title}">
               <c:if test="${not empty bulkbox && size > 1}">
                  <input type="checkbox"  name="selectall"  onclick="selectAll(this.checked, 'selectform', 'chk_');" value="on"  style="margin:0px 0px 0px 4px !important;> margin:0px 0px !important;margin:0px 0px;"/>
		  <c:if test="${not empty checkboxlabel}">
		     <span style="padding-left:4px;position:absolute;text-transform:none"><fmt:message key="${checkboxlabel}" /></span>
		  </c:if>
             </c:if>
             <c:if test="${empty bulkbox}">
                &nbsp;
             </c:if>
            </c:when>
         </c:choose>
      </c:set>
      <th width="${not empty width ? width : ''}">
         <c:choose>
            <c:when test="${not empty sort}">
               <a href="<%=request.getContextPath()%>${requestScope.sortlink}page=${page}&sortby=${sort}&dir=${dir}">
                     ${content}
               </a>
            </c:when>
            <c:otherwise>
               ${content}
            </c:otherwise>
         </c:choose>
      </th>
   </c:when>
   <c:otherwise>
      <td>
         <jsp:doBody/>
      </td>
   </c:otherwise>
</c:choose>