   function restoreAsset(objectnumber, offset, pagerDOToffset) {
       return restore(objectnumber, offset, pagerDOToffset, "RestoreAssetAction.do");
   }
   
   function restoreContent(objectnumber, offset, pagerDOToffset) {
	   return restore(objectnumber, offset, pagerDOToffset, "RestoreAction.do");
   }

   function restore(objectnumber, offset, pagerDOToffset, url) {
       url += "?objectnumber=" + objectnumber;
       url += "&returnurl=" + escape(document.location);
       url += "&offset=" + offset;
       url += "&pager.offset=" + pagerDOToffset;
       document.location.href = url;
   }
   
   function infoAsset(objectNumber, type) {
	   type = type.toLowerCase();
	   // The info jsp's are called with the singular name and not the plural name
	   // The nodetype is for the below types in plural
	   if (type == 'images') type = 'image';
	   if (type == 'attachments') type = 'attachment';
	   if (type == 'urls') type = 'url';
	   
	   url = '../resources/';
	   url += type;
	   url += 'info.jsp?objectnumber=';
	   url += objectNumber;
	   openPopupWindow('imageinfo', '900', '500', url);
   }
   
   function infoContent(objectNumber, type) {
      url = "../repository/showitem.jsp";
      url += "?objectnumber=" + objectNumber;
      openPopupWindow('contentinfo', '500', '500', url);
   }
    
    function permanentDelete(objectnumber, message, offset, pagerDOToffset, type) {
       if (confirm(message)) {
          var url = "DeleteAction.do";
          url += "?objectnumber=" + objectnumber;
          url += "&returnurl=" + escape(document.location);
          url += "&offset=" + offset;
          url += "&type=" + type;
          url += "&pager.offset=" + pagerDOToffset;

          document.location.href = url;
       }
    }
    
    function deleteAll(message) {
       if(confirm(message)) {
       		document.forms["deleteForm"].submit();
       }
    }    
	function refreshChannels() {
		refreshFrame('channels');
		if (window.opener) {
			window.close();
		}
	}