<%@ tag body-content="empty" %>
<%@include file="taglib.tagf" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<%@ attribute name="totalElements" required="true" rtexprvalue="true"%>
<%@ attribute name="elementsPerPage" required="false" rtexprvalue="true"%>
<%@ attribute name="showPages" required="false" rtexprvalue="true"%>
<%@ attribute name="pagesIndex" required="false" rtexprvalue="true"%>
<%@ attribute name="search" required="false" rtexprvalue="true"%>
<%@ attribute name="offset" required="false" rtexprvalue="true"%>
<%@ attribute name="extraparams" required="false" rtexprvalue="true"%>
<%@ attribute name="jsMethod" required="false" rtexprvalue="true"%>
<%@ attribute name="pageId" required="false" rtexprvalue="true"%>
<c:if test="${empty elementsPerPage}">
   <c:set var="elementsPerPage"><%=com.finalist.cmsc.mmbase.PropertiesUtil.getProperty("repository.search.results.per.page")%></c:set>
</c:if>
<c:set var="showPages" value="${empty showPages ? 5 :showPages }"/>
<c:set var="pagesIndex" value="${empty pagesIndex ? 'center' :pagesIndex }"/>
<c:set var="offset" value="${empty offset ? 0 : offset }"/>
<c:set var="search" value="${empty search ? true : search }"/>
<c:set var="pagesSize" value="${ cmsc:ceil(totalElements/elementsPerPage)}"/>

<fmt:bundle basename="cmsc">

<fmt:message key="pages.message" var="error"/>
<fmt:message key="pages.go" var="go"/>
<fmt:message key="searchpages.showresults" var="searchresult">
   <fmt:param>${(totalElements>0)?(offset * elementsPerPage+1):0 }</fmt:param>
   <fmt:param>${(totalElements>((offset+1)*elementsPerPage))?((offset+1)*elementsPerPage):totalElements }</fmt:param>
   <fmt:param>${totalElements}</fmt:param>
</fmt:message>

<script type="text/javascript">
   function gotopage(targetfield, search) {

      var inputValue = document.getElementById(targetfield).value;
      var re = new RegExp("^[1-9][0-9]*$");

      if (re.test(inputValue) && inputValue <= Math.ceil(${pagesSize})) {
         if (search) {
	    <c:if test="${not empty jsMethod}">
		${jsMethod}(inputValue-1, (inputValue-1)*${elementsPerPage});	
	    </c:if>
	    <c:if test="${empty jsMethod}">
		setOffset(inputValue-1, (inputValue-1)*${elementsPerPage});	
	    </c:if>
         } else {
             var url = "?offset=" + (inputValue - 1) + "${extraparams}" + "&pager.offset=" + (inputValue-1)*${elementsPerPage};
             window.location.href = url;
         }
      } else {
         alert("${error}");
      }
   }

   function enterto(event, targetfield, search) {
      if (event.keyCode == 13) {
         gotopage(targetfield, search)
      }
   }
</script>

<table style="border:0; width:100%; clear:both;">
   <tr>
      <td style="width:30%;padding:10px 0px 0px 11px">
            ${searchresult}
      </td>
      <c:if test="${pagesSize>1}">
         <td style="text-align:right;width:70%;">
            <fmt:message key="searchpages.page"/>:
                <pg:pager id="${empty pageId?'pager':pageId}" items="${totalElements}" maxPageItems="${elementsPerPage}" maxIndexPages="${showPages}" 
                 index="${pagesIndex}" isOffset="true" export="currentPage=pageNumber">
             
               <c:if test="${not search}">
                  <c:url var="previousPageUrl" value="<%=com.finalist.cmsc.paging.PagingUtils.previousPage((javax.servlet.jsp.PageContext)jspContext)%>"/>
                  <c:url var="nextPageUrl" value="<%=com.finalist.cmsc.paging.PagingUtils.nextPage((javax.servlet.jsp.PageContext)jspContext)%>"/>
                  <c:url var="FirstPageUrl" value="<%=com.finalist.cmsc.paging.PagingUtils.firstPage((javax.servlet.jsp.PageContext)jspContext)%>"/>
                  <c:url var="LastPageUrl" value="<%=com.finalist.cmsc.paging.PagingUtils.lastPage((javax.servlet.jsp.PageContext)jspContext)%>"/>
               </c:if>
               <c:if test="${search}">
		 <c:if test="${not empty jsMethod}">
		   <c:url var="previousPageUrl" value="javascript:${jsMethod}('${currentPage - 2}', '${(currentPage-2)*elementsPerPage}');"/>
		   <c:url var="nextPageUrl" value="javascript:${jsMethod}('${currentPage}', '${currentPage*elementsPerPage}');"/>
		   <c:url var="FirstPageUrl" value="javascript:${jsMethod}('0', '0');"/>
		   <c:url var="LastPageUrl" value="javascript:${jsMethod}('${pagesSize - 1}', '${(pagesSize-1)*elementsPerPage}');"/>
	         </c:if>
		 <c:if test="${empty jsMethod}">
		   <c:url var="previousPageUrl" value="javascript:setOffset('${currentPage - 2}', '${(currentPage-2)*elementsPerPage}');"/>
		   <c:url var="nextPageUrl" value="javascript:setOffset('${currentPage}', '${currentPage*elementsPerPage}');"/>
		   <c:url var="FirstPageUrl" value="javascript:setOffset('0', '0');"/>
		   <c:url var="LastPageUrl" value="javascript:setOffset('${pagesSize - 1}', '${(pagesSize-1)*elementsPerPage}');"/>
	         </c:if>
	       </c:if>
               <pg:first unless="indexed">
                  <a href="${FirstPageUrl}" class="page_list_navtrue">&lt;&lt;<fmt:message key="pages.first"/></a>
               </pg:first>
               <pg:prev ifnull="false">
                  <a href="${previousPageUrl}" class="page_list_navtrue">&lt;<fmt:message key="pages.previous"/></a>
               </pg:prev>
               <pg:pages export="count=pageNumber">
                  <c:if test="${not search}">
                     <%=com.finalist.cmsc.paging.PagingUtils.generatePageUrl((javax.servlet.jsp.PageContext)jspContext)%>
                  </c:if>
                  <c:if test="${search}">
                     <c:choose>
                        <c:when test="${count == currentPage}">
                           ${count}
                        </c:when>
                        <c:otherwise>
			  <c:if test="${not empty jsMethod}">
                            <a href="javascript:${jsMethod}('${count - 1}', '${(count-1)*elementsPerPage}');">${count}</a>
                          </c:if>
			  <c:if test="${empty jsMethod}">
                            <a href="javascript:setOffset('${count - 1}', '${(count-1)*elementsPerPage}');">${count}</a>
                          </c:if>
                        </c:otherwise>
                     </c:choose>
                  </c:if>
                  |
               </pg:pages>
               <pg:next ifnull="false">
                  <a href="${nextPageUrl}" class="page_list_navtrue"><fmt:message key="pages.next"/>&gt;</a>
               </pg:next>
               <pg:last unless="indexed">
                  <a href="${LastPageUrl}" class="page_list_navtrue"><fmt:message key="pages.last"/>&gt;&gt;</a>
               </pg:last>
               <c:if test="${pagesSize>13}">
                  <c:set var="targetfield">
                     <%=org.apache.commons.lang.RandomStringUtils.randomAlphabetic(5)%>
                  </c:set>
                  <input type="text" name="targetpage" id="${targetfield}" size="4" onKeyPress="enterto(event,'${targetfield}',${search})"/>
                  <input type="button" id="goto" value="${go}" onclick="gotopage('${targetfield}',${search})"/>
               </c:if>
            </pg:pager>
         </td>
      </c:if>
   </tr>
</table>

</fmt:bundle>