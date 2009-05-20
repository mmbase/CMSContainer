function editRemark(id, oldRemark) {
   var remark = prompt("Opmerking ?",oldRemark);
   if(remark != null) {
	   var form = document.forms['workflowForm'];
	   checkAllBoolean(false, 'workflowForm', ''); 
	   form["check_"+id].checked = true;
	   form["remark"].value = remark;
	   form["actionvalue"].value = 'rename';
	   form.submit();
   }
}

function selectTab(val, workflowNodetype, orderby, laststatus) {
   document.forms['workflowForm'].orderby.value = orderby;
   document.forms['workflowForm'].workflowNodetype.value = workflowNodetype;
   document.forms['workflowForm'].status.value = val;
   if(laststatus==null ||laststatus=="")
		document.forms['workflowForm'].laststatus.vlaue="true";
   else
   		document.forms['workflowForm'].laststatus.value=laststatus;
   document.forms['workflowForm'].submit();
}

function returnOrderBy() {
  return document.forms['workflowForm'].orderby.value;
}
var isAction = false;

function checkAllElement(element, formName, type) {
   var what = element.checked;
   checkAllBoolean(what, formName, type);
}

function checkAllBoolean(what, formName, type) {
   var namesub = 6 + type.length;

   var el=document.forms[formName].elements;
   for (i=0; i<el.length; i++) {
      var e = el[i];
      if (e.name.substr(0,namesub)=="check_" + type) {
         e.checked = what;

         if (what == false) {
            var theElement = document.getElementById(e.name);
            if (theElement != null) {
               theElement.value = null;
            }
         }
      }
   }
}

function setActionValue(value, status, remark) {
   if (submitValid(true)) {
      document.forms['workflowForm'].actionvalue.value=value;
      if(status) {
        document.forms['workflowForm'].status.value=status;
      }
      if (value == 'reject') {
         var comment = prompt(remark,"");
         if (comment == null) {
            return false;
         }

         if (comment != ""){ // OK pressed
            document.getElementById("remark").value = comment;
         }
      }
   }
   isAction = true;
   return true;
}

function submitValid(form, silent) {
 if (isAction || silent) {
      var el=form.elements;
      for (i=0; i < el.length; i++) {
         var e=el[i];
         if (e.name.substr(0,6) == "check_") {
            if (e.checked) {
               if (isAction) {
                  sayWait();
               }
               return true;
            }
            if (e.type=="hidden" && e.value == "on") {
               if (isAction) {
                  sayWait();
               }
               return true;
            }
         }
      }
      if (!silent) {
         alert("Geen workflow item geselecteerd");
      }
      isAction = false;
      return false;
   }
   return true;
}

function sayWait() {
    document.getElementById("workflow-canvas").style.display="none";
    document.getElementById("workflow-wait").style.display="block";
}
