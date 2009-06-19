<c:set var="filename"><mm:field name="filename"/></c:set>
<c:forTokens var="type" items="${filename}" delims=".">
   <c:set var="subfix" value="${fn:toLowerCase(type)}" />
</c:forTokens>
<mm:haspage page="../gfx/${subfix}${'.gif'}" inverse="false">
   <img src="../gfx/${subfix}${'.gif'}" alt=""/>
</mm:haspage> 
<mm:haspage page="../gfx/${subfix}${'.gif'}" inverse="true">
   <c:choose>
      <c:when test="${subfix=='htm'}">
         <img src="../gfx/html.gif" alt=""/>
      </c:when>
      <c:when test="${subfix=='docx'}">
         <img src="../gfx/doc.gif" alt=""/>
      </c:when>
      <c:when test="${subfix=='xlsx'}">
         <img src="../gfx/xls.gif" alt=""/>
      </c:when>
      <c:when test="${subfix=='pptx'}">
         <img src="../gfx/ppt.gif" alt=""/>
      </c:when>
      <c:when test="${subfix=='mpeg'||subfix=='mpg'||subfix=='dat'||subfix=='avi'||subfix=='ra'||
         subfix=='rm'||subfix=='ram'||subfix=='mov'||subfix=='asf'||subfix=='wmv'||subfix=='divx'}">
         <img src="../gfx/video.gif" alt=""/>
      </c:when>
      <c:when test="${subfix=='wav'||subfix=='mp3'||subfix=='wma'||subfix=='wav'||subfix=='ogg'||
         subfix=='ape'||subfix=='aac'}">
         <img src="../gfx/audio.gif" alt=""/>
      </c:when>
      <c:otherwise>
            <img src="../gfx/otherAttach.gif" alt=""/>
      </c:otherwise>
   </c:choose>
</mm:haspage>