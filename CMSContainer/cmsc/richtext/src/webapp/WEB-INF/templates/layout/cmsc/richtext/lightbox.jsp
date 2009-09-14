<link rel="stylesheet" href="<cmsc:staticurl page='/lightbox/css/lightbox.css'/>" type="text/css" media="screen" />	
<c:set var="closeImage"><fmt:message key="lightbox.closeImage"/></c:set>
<script src="<cmsc:staticurl page='/lightbox/js/lightbox.js'/>" type="text/javascript"></script>
<script type="text/javascript">
var slideShowText3 = "<fmt:message key='lightbox.slideShow.multiple3'/>";
document.observe('dom:loaded', function () { 
	window.LightboxOptions.fileLoadingImage = "<cmsc:staticurl page='/lightbox/images/loading.gif'/>";		
	window.LightboxOptions.fileBottomNavCloseImage = "<cmsc:staticurl page='${closeImage}'/>";
	window.LightboxOptions.labelImage = "<fmt:message key='lightbox.slideShow.multiple1'/>";
	window.LightboxOptions.labelOf = "<fmt:message key='lightbox.slideShow.multiple2'/>";
});
</script>