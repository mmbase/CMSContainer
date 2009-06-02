//Refers to the function 'validateElement' in validator.js 
 function validateInputs() {
    var err = "";
    var form = document.forms[0];
    for (i=0;i<form.elements.length;i++){ 
       var el = form.elements[i];
       var dttype = el.getAttribute("dttype");
       var v = el.value;
       if(!isEmpty(dttype)&&!isEmpty(v)){
          switch (dttype.toLowerCase()) {
          case "long":
              ;
          case "integer":
              err += validateInt(el, form, v);
              break;
          case "float":
              ;
          case "double":
              err += validateFloat(el, form, v);
              break;
          case "enum":
              //Maybe to be implemented in the future
              //err += validateEnum(el, form, v);
              break;
          case "datetime":
              //Maybe to be implemented in the future
              //err += validateDatetime(el, form, v);
              break;
          case "boolean":
              //Maybe to be implemented in the future
              //err += validateBoolean(el, form, v);
              break;
          default :
              break;
          }
       }
       if(!isEmpty(err)){
          alert(err);
          el.style.borderColor = "#CC0000";
          el.focus();
          return false;
       }
    }
    setOffset(0, 0);
 }

//Refers to the function 'validateInt' in validator.js 
 function validateInt(el, form, v) {
    if (isNaN(v) || parseInt(v) == null) {
       return "value '" + v + "' is not a valid integer number";
    }
    return "";
 }

//Refers to the function 'validateFloat' in validator.js 
function validateFloat(el, form, v) {
   if (isNaN(v) || parseFloat(v) == null) {
      return "value '" + v + "' is not a valid float number";
   }
   return "";
}

 function setOffset(offset, pagerOffset) {
    document.forms[0].offset.value = offset;
    document.forms[0]['pager.offset'].value = Math.ceil(pagerOffset);
    document.forms[0].submit();
 }

 function orderBy(orderColumn) {
    var oldOrderColumn = document.forms[0].order.value;
    
    if (oldOrderColumn == orderColumn) {
       // order column is not changed so change direction
       var oldDirection = document.forms[0].direction.value;
       if (oldDirection == '1') {
          document.forms[0].direction.value = '2';
       }
       else {
          document.forms[0].direction.value = '1';
       }
    }
    else {
       document.forms[0].order.value = orderColumn;
       document.forms[0].direction.value = '1';
    }
    
    document.forms[0].submit();
 }

 function selectContenttype(initUrl) {
    // parentchannel is only there when linking is active...
    if(document.forms[0].parentchannel){
        try {
            document.forms[0].parentchannel.value='';
        } catch (e) {
            ;
        }
    }
    // This doesn't work in IE...
    // document.forms[0].action=initUrl;
    document.forms[0].submit();
 }

 function selectTab(mode) {
    document.forms[0].mode.value=mode;
    document.forms[0].search.value='false';
    document.forms[0].submit();
 }

function selectAll(value, formName, elementPrefix) {
   var elements = document.forms[formName].elements;
   for (var i = 0; i < elements.length; i++) {
      if (elements[i].name.indexOf(elementPrefix) == 0) {
          elements[i].checked = value;
      }
   }
}

function selectChannel(channel, path) {
   document.forms[0].parentchannel.value=channel;
   document.forms[0].parentchannelpathdisplay.value=path;
}

function selectElement(element, title, url, width, height, description) {
   window.top.opener.selectElement(element, title, url, width, height, description);
   window.top.close();
}

function selectAssettype(initUrl) {
    // parentchannel is only there when linking is active...
    if(document.forms[0].parentchannel){
        try {
            document.forms[0].parentchannel.value='';
        } catch (e) {
            ;
        }
    }
    // This doesn't work in IE...
    // document.forms[0].action=initUrl;
    document.forms[0].submit();
 }

var moveContentNumber;

function massMoveFromSearch(url) {
   var checkboxs = document.getElementsByTagName("input");
   var objectnumbers = '';
   for(i = 0; i < checkboxs.length; i++) {
      if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
         var num1 = checkboxs[i].value;
         var num2 = num1.split(":");
         objectnumbers += num2[1]+",";
      }
   }
   if(objectnumbers == ''){
      return ;
   }
   moveContentNumber = objectnumbers.substr(0,objectnumbers.length - 1);
   openPopupWindow('selectchannel', 340, 400,url);
}