<br />
<div style="width:${movieWidth}px;height:${movieHeight}px;" id="<portlet:namespace />player"></div> 
<%-- this script block will install Flowplayer inside previous DIV tag 
	The defer="defer" will delay IE to load the this script after the full body is loaded --%>
<script type="text/javascript" defer="defer">
    flowplayer( 
        "<portlet:namespace />player",  
        "<cmsc:staticurl page='/flash/flowplayer/flowplayer-3.1.3.swf' />",  
        "${movieUrl}" 
    );
</script>