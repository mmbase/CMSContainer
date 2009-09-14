<%-- 
	the configuration of this player is abit hard, it expects a complete 
	JSON object as a config flashvar. We'll have to construct it here. 
	
	More details on the configuration of the FlowPlayer can be found here:
	http://flowplayer.org/player/configuration.html
--%>
<%--
<c:set var="config" value="{ videoFile: '${movieUrl}'" />
<c:set var="config" value="${config}, useNativeFullScreen: true" />
<c:set var="config" value="${config}, controlsOverVideo: 'ease'" />
<c:set var="config" value="${config}, initialScale: 'fit'" />
<c:set var="config" value="${config} }" />

<c:url value="/flash/FlowPlayerDark.swf" var="flvPlayerUrl" />
<flash:flash swfUrl="${flvPlayerUrl}" width="${movieWidth}" height="${movieHeight}" styleClass="movie-flow">
	<flash:param name="allowscriptaccess" value="always" />
	<flash:param name="allowfullscreen" value="${allowfullscreen}" />
	<flash:flashvar name="config" value="${config}" />
</flash:flash>
 --%>
<br>
<br>
<div style="width:${movieWidth}px;height:${movieHeight}px;" id="<portlet:namespace />player"></div> 
<%-- this script block will install Flowplayer inside previous DIV tag --%>
<script language="JavaScript">
    flowplayer( 
        "<portlet:namespace />player",  
        "<cmsc:staticurl page='/flash/flowplayer/flowplayer-3.1.3.swf' />",  
        "${movieUrl}" 
    ); 
</script>