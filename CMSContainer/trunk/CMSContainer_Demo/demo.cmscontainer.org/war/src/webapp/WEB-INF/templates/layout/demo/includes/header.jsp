  <title><cmsc:title /></title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <cmsc:headercontent dublin="true" />
  <link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
  <link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
  
  <cmsc:insert-stylesheet var="stylesheet" />
  <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/stijl.css'/>" media="screen,projection,print" />
  <c:forEach var="style" items="${stylesheet}">
  <c:choose>  
    <c:when test="${empty style.resource}"> 
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/stylesheet/${style.id}.css" media="${style.media}" />
    </c:when> 
    <c:otherwise>
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/${style.resource}'/>" media="${style.media}" />
    </c:otherwise>
  </c:choose>   
  </c:forEach>
  <!--[if IE]>
     <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/stijl_ie.css'/>" media="screen,projection,print" />
  <![endif]-->
  <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/print.css'/>" media="print" />

  <cmsc:feeds />

  <cmsc:protected inverse="true">
    <script src="<cmsc:staticurl page='/js/prototype.js" type="text/javascript'/>"></script>
    <script src="<cmsc:staticurl page='/js/scriptaculous/scriptaculous.js?load=effects,builder'/>" type="text/javascript"></script>
  </cmsc:protected>
  <cmscf:editresources />
  <%@include file="/WEB-INF/templates/layout/cmsc/richtext/lightbox.jsp" %>