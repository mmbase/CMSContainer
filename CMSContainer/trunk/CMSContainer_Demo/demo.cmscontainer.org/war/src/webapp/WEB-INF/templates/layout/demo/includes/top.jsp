<%-- 
  top visual: don't indent it in such a way that there's spacing between
  the image and the <div>, IE doesn't like it.
--%><cmsc:insert-page-image var="pageImage" name="top" inherit="true" 
/><c:choose>
<c:when test="${not empty pageImage  && pageImage != ''}"><mm:cloud method="asis"><mm:node number="${pageImage}"><img alt="" src="<mm:image/>"/></mm:node></mm:cloud></c:when>
<c:otherwise><div id="top_img"><img alt="" src="<cmsc:staticurl page='/gfx/top.jpg'/>"/></div></c:otherwise>
</c:choose>	