<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div class="kolomfoto">
<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
    <mm:import externid="contentchannel" required="true" />
    <mm:node number="${contentchannel}" notfound="skip">

        <%-- normal view titel --%>
       <mm:node number="${contentelement}" notfound="skip">
         <h2><mm:field name="title"/></h2>
         <p><mm:field name="intro" escape="none"/></p>
       </mm:node>

       <mm:relatednodescontainer type="images" role="creationrel" searchdirs="source">

           <%-- url to fullsize page used by pageindex of fullsize mode 
                AND link to fullsize image in list mode
           --%>
           <cmsc:renderURL page="${page}" window="${window}"  var="fullsizeUrl">
               <cmsc:param name="contentchannel" value="${contentchannel}" />
               <cmsc:param name="contentelement" value="${contentelement}" />
               <cmsc:param name="mode" value="fullsize" />
           </cmsc:renderURL>

           <%-- settings for list mode --%>
           <c:if test="${empty param.mode}">
            <c:set var="usePaging" value="${empty usePaging? false: usePaging}"/>
            <c:set var="elementsPerPage" value="${elementsPerPage}"/>
            <c:if test="${empty elementsPerPage}">
            <c:set var="elementsPerPage"><mm:size id="totalitems"/></c:set>
            </c:if>
            <c:set var="width" value="${empty width? '110': width}"/>
            <c:set var="column" value="${empty column? elementsPerPage: column}"/>
            <c:set var="pageName" value="pagina"/>
            <cmsc:renderURL var="renderUrl"/>
           </c:if>

           <%-- settings for fullsize mode --%>      
           <c:if test="${not empty param.mode}">        
            <c:set var="usePaging" value="${true}"/>
            <c:set var="elementsPerPage" value="1"/>
            <c:set var="pageName" value="foto"/>
            <c:set var="renderUrl" value="${fullsizeUrl}"/>
            <c:set var="mode" value="fullsize"/>
           </c:if>

           <%-- settings for both modes --%>
           <c:set var="orderby" value="${empty orderby? 'filename': orderby}"/>
           <c:set var="direction" value="${empty direction? 'up': direction}"/>
           <c:set var="totalElements"><mm:size id="totalitems"/></c:set>
           <c:set var="position" value="${position}"/>
           <c:set var="pagesIndex" value="${pagesIndex}"/>
           <c:set var="showPages" value="${showPages}"/>

           <pg:pager url="${renderUrl}" maxPageItems="${elementsPerPage}" items="${totalElements}" index="${pagesIndex}" 
               maxIndexPages="${showPages}" isOffset="true" export="offset,currentPage=pageNumber">

               <c:if test="${not empty param.mode}">
                <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="${orderby}" directions="${direction}">
                  <mm:field name="title" jspvar="imageTitle" write="false"/>
                </mm:relatednodes>  
                <div id="fotodetail">
                  <c:if test="${usePaging && (position == 'top' || position == 'both') && totalElements+1 > elementsPerPage+1}">
                    <%@include file="/WEB-INF/templates/view/gallery2/pagerindex.jsp" %>
                  </c:if>
                  <c:set var="count" value="0"/>
                  <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="${orderby}" directions="${direction}">
                    <img border="0" src="<mm:image width="727"/>" /><br/><br/>
                    <c:set var="count" value="${count + 1}"/>
                  </mm:relatednodes>
                  <c:if test="${usePaging && (position == 'bottom' || position == 'both') && totalElements+1 > elementsPerPage+1}">
                    <%@include file="/WEB-INF/templates/view/gallery2/pagerindex.jsp" %>
                  </c:if>
                </div>
               </c:if>

               <c:if test="${empty param.mode}">
                  <c:if test="${usePaging && (position == 'top' || position == 'both') && totalElements+1 > elementsPerPage+1}">
                   <%@include file="/WEB-INF/templates/view/gallery2/pagerindex.jsp" %>
                  </c:if>
                  <c:set var="count" value="0"/>
                  <div class="fotolijst">
                  <table>
                  <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="${orderby}" directions="${direction}">
                     <c:if test="${(count%column)==0}">
                        <tr>
                     </c:if>
                     <td>
                        <a href="${fullsizeUrl}?pager.offset=${offset + count}"> <img src="<mm:image width="${width}"/>" alt="<mm:field name="description" escape="none"/>"/> </a>
                     </td> 
                     <c:if test="${((count+1)/column)==0}">
                        </tr>
                     </c:if>
                     <c:set var="count" value="${count + 1}"/>
                  </mm:relatednodes>
                  </table>
                  </div>
                  <div class="clear"></div>
                  <c:if test="${usePaging && (position == 'bottom' || position == 'both') && totalElements+1 > elementsPerPage+1}">
                   <%@include file="/WEB-INF/templates/view/gallery2/pagerindex.jsp" %>
                  </c:if>
               </c:if>

           </pg:pager>

       </mm:relatednodescontainer>

    </mm:node> 
</mm:cloud>
</mm:content>
</div>
<div class="clear"></div>