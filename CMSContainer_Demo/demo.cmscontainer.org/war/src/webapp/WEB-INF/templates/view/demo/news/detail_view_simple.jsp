<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:cloud>
  <mm:import externid="elementId" required="true" />
  <mm:node number="${elementId}" notfound="skip">
	<cmsc:portletmode name="edit">
		<mm:relatednodes type="contentchannel" role="creationrel">
			<mm:field name="number" write="false" jspvar="channelnumber"/>
			<cmsc:isallowededit channelNumber="${channelnumber}">
				<c:set var="edit" value="true"/>
			</cmsc:isallowededit>
		</mm:relatednodes>
	</cmsc:portletmode>
		  
	<c:if test="${edit}">
		<form name="contentportlet" method="post" 
	  		  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
	</c:if>

    <div class="heading">
      <cmsc:field elementid="${elementId}" name="title" edit="${edit}" container="h2"/>
    </div>
    <div class="content">
      <!-- top images -->      
      <cmsc-bm:linkedimages width="525" position="top" style="display: block; clear: both; padding-bottom: 20px;" />
      <cmsc-bm:linkedimages width="220" position="top-left" style="float: left; padding: 0px 20px 20px 0px;" />
      <cmsc-bm:linkedimages width="220" position="top-right" style="float: right; padding: 0px 0px 20px 20px;" />

   <cmsc:field elementid="${elementId}" name="intro" edit="${edit}" options="minHeight:300, htmlarea:true, formId:'contentportlet'"/>

   <cmsc:field elementid="${elementId}" name="body" edit="${edit}" options="minHeight:300, htmlarea:true, formId:'contentportlet'"/>
       
      <div class="divider3"></div>
       
      <mm:listrelationscontainer type="images" role="imagerel" searchdir="destination">
        <mm:sortorder field="order" />
        <mm:constraint field="pos" value="gallery" />
        
        <mm:listrelations>
          <mm:first>
            <div style="width: 100%;">
            <h3 style="font-size: 1em;"><fmt:message key="view.article.gallery" /></h3>
          </mm:first>
          <mm:node number="${_node.dnumber}">
            <a href=" <mm:image template="s(600)" />" rel="lightbox[gallery]" title="${_node.description}">
            <%--
              thumbnails are rendered by the basic CMSC image tag,
              popup is set to 'false' since a lightbox-popup effect is used.
            --%>
              <cmsc-bm:image width="125" popup="false" />
            </a>
          </mm:node>

          <mm:last>
            </div>
          </mm:last>
        </mm:listrelations>
      </mm:listrelationscontainer>
       
      <div class="divider3"></div>
       
      <%-- related articles --%>
      <mm:relatednodes type="contentelement" role="posrel" orderby="posrel.pos" searchdir="destination">
        <mm:field name="number" id="elementNumber" write="false" />
        
        <mm:first>
          <h3 style="font-size: 1em;"><fmt:message key="view.article.related.articles" /></h3>
          <ul>
        </mm:first>
        
        <li>
          <a 
            href="<cmsc:contenturl number="${elementNumber}"/>" 
            title="<mm:field name="title" escape="text/html/attribute" />"
          ><mm:field name="title" escape="text/xml" /></a>
        </li>
          
        <mm:last></ul></mm:last>          
      </mm:relatednodes>

      <%-- related urls --%>
      <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos" searchdirs="destination">
        <mm:first>
          <h3 style="font-size: 1em;"><fmt:message key="view.article.related.urls" /></h3>
          <ul>
        </mm:first>
          
          <li>
            <a 
              href="<mm:field name="url" escape="text/html/attribute" />" 
              title="<mm:field name="description" escape="text/html/attribute" />"
              target="_blank"
            ><mm:field name="title" escape="text/xml" /></a>
          </li>
          
        <mm:last></ul></mm:last>          
      </mm:relatednodes>

      <%-- related attachments --%>
      <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos" searchdirs="destination">
        <%-- pretty print the file sizes --%>
        <mm:field name="size" jspvar="size" write="false" />
        <c:choose>
          <c:when test="${( size div 1048576 ) gt 1.0 }">
            <c:set var="sizeString">
              <fmt:formatNumber value="${size div 1048576}" minFractionDigits="1" maxFractionDigits="1" /> MB          
            </c:set>
          </c:when>
          <c:when test="${( size div 1024 ) gt 1.0 }">
            <c:set var="sizeString">
              <fmt:formatNumber value="${size div 1024}" minFractionDigits="1" maxFractionDigits="1" /> KB          
            </c:set>
          </c:when>
          <c:otherwise>
            <c:set var="sizeString">
              <fmt:formatNumber value="${size}" minFractionDigits="1" maxFractionDigits="1" /> B          
            </c:set>
          </c:otherwise>
        </c:choose>

        <mm:first>
          <h3 style="font-size: 1em;"><fmt:message key="view.article.related.attachments" /></h3>
          <ul>
        </mm:first>
        
        <li>
          <a href="<mm:attachment />" title="<mm:field name="description" escape="text/html/attribute" />">
            <mm:field name="title" escape="text/xml" />
          </a>
          (${sizeString})
        </li>
        
        <mm:last></ul></mm:last>
      </mm:relatednodes>

      <!-- bottom images -->
      <cmsc-bm:linkedimages width="220" position="bottom-left" style="float: left; padding: 20px 20px 0px 0px;" />
      <cmsc-bm:linkedimages width="220" position="bottom-right" style="float: right; padding: 20px 0px 0px 20px;" />
      <cmsc-bm:linkedimages width="525" position="bottom" style="display: block; clear: both; padding-top: 20px;" />
      <div class="clear"></div>
      <div class="divider"></div>
    </div>
    
	<c:if test="${edit}">
		<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
		</form>
	</c:if>
    
  </mm:node>
</mm:cloud>