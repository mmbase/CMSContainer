var selectedItems = [];

function showIcons(id) {
   document.getElementById('thumbnail-icons-' + id).style.visibility = 'visible';
}
function hideIcons(id) {
   document.getElementById('thumbnail-icons-' + id).style.visibility = 'hidden';
}
function setShowMode() {
   var showMode = document.getElementsByTagName("option");
   var assetShow;
   for (i = 0; i < showMode.length; i++) {
      if (showMode[i].selected & showMode[i].id == "a_list") {
         assetShow = "list";
      } else if (showMode[i].selected & showMode[i].id == "a_thumbnail") {
         assetShow = "thumbnail";
      }
   }
   document.forms[0].assetShow.value = assetShow;
   document.forms[0].submit();
}

function addItem(elem, number, strict) {
   if (!(elem.id)) {
      if (!strict) {
         if (selectedItems[0]) {
            var oldSelected = document.getElementById(selectedItems[0]);
            if (oldSelected) {
               oldSelected.id = '';
               oldSelected.setAttribute('class', 'grid');
            }
         }
         selectedItems[0] = number;
      } else {
         selectedItems[selectedItems.length] = number;
      }
      elem.id = number;
      elem.setAttribute('class', 'selected');
   } else {
      for ( var i = selectedItems.length-1; i >=0; i--) {
         if (selectedItems[i] == number) {
            selectedItems.splice(i,1);
            elem.id = '';
            elem.setAttribute('class', 'grid');
            break;
         }
      }
   }
}

function doSelectIt(strict) {
   if (selectedItems.length < 1) {
      alert("You must select one item.");
   } else {
      if (!strict) {
         var href = document.getElementById(selectedItems[0]).getAttribute(
               'href')
               + "";
         if (href.indexOf('javascript:') == 0) {
            eval(href.substring('javascript:'.length, href.length));
         } else {
            document.location = href;
         }
      } else {
         window.top.opener.selectContent(selectedItems.join("|"), '', '');
         window.top.close();
      }
   }
}

function doCancleIt() {
   window.top.close();
}

function selectElement(element, title, src, width, height, description) {
   if (window.top.opener != undefined) {
      window.top.opener.selectElement(element, title, src, width, height,
            description);
      window.top.close();
   }
}

function erase(field) {
   document.forms[0][field].value = '';
}

function selectChannel(channel, path) {
   document.forms[0].contentChannel.value = channel;
   document.forms[0].contentChannelPath.value = path;
}