<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib tagdir="/WEB-INF/tags/googlemap/" prefix="googlemap" %>
<c:choose>
	<c:when test="${not empty width and not empty height}"><div id="map" style="width: ${width}; height: ${height};"></div>	</c:when>
	<c:otherwise><div id="map" style="width: 370px; height: 260px;"></div></c:otherwise>
</c:choose>
<googlemap:initmaps />
<script type="text/javascript">
    //<![CDATA[
    function mapsLoaded() {
    	initializePanels('map');
    	addAddress('${address}', '${info}')
    }
    //]]>
</script>
<googlemap:loadmaps key="${key}" callback="mapsLoaded" />
