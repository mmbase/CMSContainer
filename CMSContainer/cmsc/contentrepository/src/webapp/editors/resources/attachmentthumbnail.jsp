<c:set var="filename"><mm:field name="filename"/></c:set>
<c:set var="subfix">${fn:toLowerCase(fn:substringAfter(filename, '.'))}</c:set>
<mm:haspage page="../gfx/${subfix}${'.gif'}" inverse="false">
   <img src="../gfx/${subfix}${'.gif'}" alt="" width="120px" height="100px"/>
</mm:haspage> 
<mm:haspage page="../gfx/${subfix}${'.gif'}" inverse="true">
   <c:choose>
      <c:when test="${subfix=='htm'}">
         <img src="../gfx/html.gif" alt="" width="120px" height="100px"/>
      </c:when>
      <c:when test="${subfix=='docx'}">
         <img src="../gfx/doc.gif" alt="" width="120px" height="100px"/>
      </c:when>
      <c:when test="${subfix=='xlsx'}">
         <img src="../gfx/xls.gif" alt="" width="120px" height="100px"/>
      </c:when>
      <c:when test="${subfix=='pptx'}">
         <img src="../gfx/ppt.gif" alt="" width="120px" height="100px"/>
      </c:when>
      <c:when test="${subfix=='mpeg'||subfix=='mpg'||subfix=='dat'||subfix=='avi'||subfix=='ra'||
         subfix=='rm'||subfix=='ram'||subfix=='mov'||subfix=='asf'||subfix=='wmv'||subfix=='divx'}">
         <img src="../gfx/video.gif" alt="" width="120px" height="100px"/>
      </c:when>
      <c:when test="${subfix=='wav'||subfix=='mp3'||subfix=='wma'||subfix=='wav'||subfix=='ogg'||
         subfix=='ape'||subfix=='aac'}">
         <img src="../gfx/audio.gif" alt="" width="120px" height="100px"/>
      </c:when>
      <c:otherwise>
            <img src="../gfx/otherAttach.gif" alt="" width="120px" height="100px"/>
      </c:otherwise>
   </c:choose>
</mm:haspage>