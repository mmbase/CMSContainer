xinha_editors = null;
xinha_editors_lightbox = null;
xinha_init    = null;

xinha_init = xinha_init ? xinha_init : function() {
  var xinha_plugins = [
   'CharacterMap',
   'ContextMenu',
   'InsertAnchor',
   'EmbedCode',
//   'ListType',
//   'FullScreen',
//   'SpellChecker',
//   'Stylist',
   'SuperClean',
   'FindReplace',
   'PasteText',
   'TableOperations'
  ];
  
  // THIS BIT OF JAVASCRIPT LOADS THE PLUGINS, NO TOUCHING  :)
  if(!Xinha.loadPlugins(xinha_plugins, xinha_init)) return;
  var xinha_config = createDefaultConfig();
  xinha_editors = Xinha.makeEditors(xinha_editors, xinha_config, xinha_plugins);
  Xinha.startEditors(xinha_editors);
  
  var xinha_config_lightbox = createLightboxConfig();
  xinha_editors_lightbox = Xinha.makeEditors(xinha_editors_lightbox, xinha_config_lightbox, xinha_plugins);
  Xinha.startEditors(xinha_editors_lightbox);
}

createDefaultConfig = function() {
  var xinha_config = xinha_config ? xinha_config() : new Xinha.Config();

  registerInsertImageConfig(xinha_config);
  registerInsertLinkConfig(xinha_config);
	
  registerTableConfig(xinha_config);
  setupDefaultConfig(xinha_config);
  return xinha_config;
}

createLightboxConfig = function() {
  var xinha_config = xinha_config ? xinha_config() : new Xinha.Config();

  registerInsertImageConfig(xinha_config);
  registerInsertLinkConfig(xinha_config);
  registerTableConfig(xinha_config);
  setupDefaultConfig(xinha_config);

  xinha_config.URIs['insert_image'] =  _editor_url + 'modules/InsertImage/insertinline_image_lightbox.html';

  return xinha_config;
}

setupDefaultConfig = function(xinha_config) {
    xinha_config.registerButton({
	    id        : "my-validatesave",
	    tooltip   : Xinha._lc("Controleer de html"),
	    image     : _editor_url + xinha_config.imgURL +  "ed_validate_save.gif",
	    textMode  : true,
	    action    : myValidateSaveAction
	  });

	xinha_config.toolbar = [
      ["formatblock","bold","italic","underline","strikethrough"],
      ["separator","subscript","superscript"],
      ["linebreak","separator"],
      ["separator","insertorderedlist","insertunorderedlist"],
      ["separator","inlinelink","insertimage","insert-anchor","insertcharacter","embed-code"],
      ["separator","undo","redo","selectall","FR-findreplace"],(Xinha.is_gecko ? [] : ["cut","copy","paste"]),
      ["separator","killword","pastetext","clearfonts","removeformat"],
      ["separator","htmlmode","showhelp","my-validatesave","popupeditor"],
      ["separator","createtable"]
    ];

     xinha_config.formatblock = ({
  		"Normal": "p",	   
  	    "Heading 1": "h1",
  	    "Heading 2": "h2",
  	    "Heading 3": "h3",
  	    "Heading 4": "h4"
     });

    xinha_config.pageStyle="body, td {font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;color: #0000;font-size: 90%;}";
    xinha_config.pageStyle+="p {font-size: 100%;}";
    xinha_config.pageStyle+="h1 {font-weight: bold;font-size: 100%;}";
    xinha_config.pageStyle+="h2 {font-weight: bold;	color:#2222CC; font-size: 100%;	}";
    xinha_config.pageStyle+="h3 {font-weight: normal; color:#0000AA;	font-size: 100%; }";
    xinha_config.pageStyle+="h4 {font-weight: normal;	color:#000055;	font-size: 100%;}";
    xinha_config.pageStyle+="a {color: #0000FF; }";
}

registerInsertImageConfig = function(xinha_config) {
	xinha_config.registerButton({
	  id        : "insertimage",
	  tooltip   : Xinha._lc("Insert Image"),
	  image     : _editor_url + xinha_config.imgURL + "tango/16x16/mimetypes/image-x-generic.png",
	  textMode  : false,
	  action    : function(e) {e._insertImage();}
	});
    xinha_config.URIs['insert_image'] =  _editor_url + 'modules/InsertImage/insertinline_image.html';
}
registerInsertLinkConfig = function(xinha_config) {
	xinha_config.registerButton({
	  id        : "inlinelink",
	  tooltip   : Xinha._lc("Insert/Modify Link"),
	  image     : _editor_url + xinha_config.imgURL +  "tango/16x16/actions/insert-link.png",
	  textMode  : false,
	  action    : function(e) {e._createLink();}
	});
	xinha_config.URIs['link'] = _editor_url + "modules/CreateLink/insertinline_link.html"
}

registerTableConfig = function(xinha_config) {
    xinha_config.registerButton({
        id        : "createtable",
        tooltip   : Xinha._lc("Insert Table"),
        image     : _editor_url + xinha_config.imgURL +  "tango/16x16/actions/insert-table.png",
        textMode  : false,
        action    : function(e) {e._inserttable();}
      });

    xinha_config.URIs['insert_table'] =  _editor_url + 'popups/insert_table.html';
    xinha_config.width = ['98%'];
}


myValidateSaveAction = function(editor) {
  updateValue(editor);
  // editwizard validation
  validator.validate(editor._textArea);
}

// overrides editwizard.jsp
function doCheckHtml() {
  if (Xinha.checkSupportedBrowser()) {
    for (var editorname in xinha_editors) {
      editor = xinha_editors[editorname];
      updateValue(editor);
      // editwizard validation
      // It is possible to save a wizard when multiple htmlareas are not validated yet.
      if (requiresValidation(editor._textArea)) {
        validator.validate(editor._textArea);
      }
    }
    
    for (var editorname in xinha_editors_lightbox) {
        editor = xinha_editors_lightbox[editorname];
        updateValue(editor);
        // editwizard validation
        // It is possible to save a wizard when multiple htmlareas are not validated yet.
        if (requiresValidation(editor._textArea)) {
          validator.validate(editor._textArea);
        }
      }

  }
}

function updateValue(editor) {
  // cancel on view only editor
  if(editor._doc == null) {
	  return;
  }
  if(editor != null && editor.getHTML) {
	  setWidthForTables(editor);
     if(!Xinha.is_ie) {
        setDimensionForImages(editor);
     }
	  value = editor.outwardHtml(editor.getHTML());
	  // These two lines could cause editors to complain about responsetime
	  // when they leave a form with many large htmlarea fields.
	  // this is the case when doCheckHtml() is called by the editwizard.jsp with
	  // doSave, doSaveOnly, gotoForm and doStartWizard
	 
	  value = clean(value);

	  editor._textArea.value = value;

	  if (editor._editMode == "wysiwyg") {
	      var html = editor.inwardHtml(value);
	      editor.deactivateEditor();
	      editor.setHTML(html);
	      editor.activateEditor();
	  }
   }
}

function wizardClean(value) {
// editors in IE will maybe complain that it is very messy with
// <strong> and <b> tags mixed when they edit, but without this function
// they would also do when others would use Gecko browsers.
// Now we are backwards compatible with the old editwizard wysiwyg and the
// frontend only has to deal with <b> and <i>

  //replace <EM> by <i>
  value = value.replace(/<([\/]?)EM>/gi, "<$1i>");
  value = value.replace(/<([\/]?)em>/gi, "<$1i>");
  //replace <STRONG> by <b>
  value = value.replace(/<([\/]?)STRONG>/gi, "<$1b>");
  value = value.replace(/<([\/]?)strong>/gi, "<$1b>");
  //replace <BR> by <BR/>
  value = value.replace(/<BR>/gi, "<br/>");
  value = value.replace(/<br>/gi, "<br/>");

  return value;
}

function clean(value) {
  // Remove all SPAN tags
  value = value.replace(/<\/?SPAN[^>]*>/gi, "" );
  value = value.replace(/<\/?span[^>]*>/gi, "" );
  // Remove Class attributes
  value = value.replace(/<(\w[^>]*) class=([^ |>]*)([^>]*)/gi, "<$1$3");
  // Remove Style attributes
  value = value.replace(/<(\w[^>]*) style="([^"]*)"([^>]*)/gi, "<$1$3");
  // Remove Lang attributes
  value = value.replace(/<(\w[^>]*) lang=([^ |>]*)([^>]*)/gi, "<$1$3");
  // Remove XML elements and declarations
  value = value.replace(/<\\?\?xml[^>]*>/gi, "");
  // Remove Tags with XML namespace declarations: <o:p></o:p>
  value = value.replace(/<\/?\w+:[^>]*>/gi, "");
  // Replace the &nbsp;
  value = value.replace(/&nbsp;/gi, " " );

  return value;
}

function plainText(text) {
  var text = HTMLEncode(text);
  text = text.replace(/\n/g,'<BR>');
  return text;
}

function HTMLEncode(text) {
  text = text.replace(/&/g, "&amp;") ;
  text = text.replace(/"/g, "&quot;") ;
  text = text.replace(/</g, "&lt;") ;
  text = text.replace(/>/g, "&gt;") ;
  text = text.replace(/'/g, "&#146;") ;

  return text ;
}

function setWidthForTables(editor) {
	if(editor._doc != null) {
		var tables = editor._doc.getElementsByTagName('table');
		for (var i = 0 ; i < tables.length ; i++) {
			var table = tables[i];
			if (table.style.width)
				table.width = table.style.width;
		}
	}
}

function setDimensionForImages(editor) {
	if(editor._doc != null) {
		var images = editor._doc.getElementsByTagName('img');
		for (var i = 0 ; i < images.length ; i++) {
			var image = images[i];
			if (image.style.width) {
				image.width = image.style.width;
         }
         if (image.style.height)
         {
            image.height = image.style.height;
         }
		}
	}
}
function i18n(str) {
    return (Xinha._lc(str, 'cmscrichtext'));
  }


Xinha.prototype._createLink = function(link) {
	var editor = this;
	var outparam = null;
	if (typeof link == "undefined") {
      	link = this.getParentElement();
      	while (link) {
            	if (/^a$/i.test(link.tagName)) break; //Search for the enclosing A tag, if found: continue and use it.
            	if (/^body$/i.test(link.tagName)) { link = null; break } //Stop searching when Body-tag is found, don't go too deep.
            	link = link.parentNode;
      	}
	}
	var sel = editor._getSelection();
	var sel_value = sel;
	var range = this._createRange(sel);
	if(Xinha.is_ie) sel_value = range.text;
	if (link){
            var title="";
            if(/^a$/i.test(link.tagName) && /^img$/i.test(link.firstChild.tagName)) {
                title = link.firstChild.getAttribute("title"); 
            }else title = Xinha.is_ie ? link.innerText : link.textContent;
         	outparam = {
               	f_href   : Xinha.is_ie ? editor.stripBaseURL(link.href) : link.getAttribute("href"),
                f_destination : link.destination ,
                f_title   : title,
				f_tooltip : link.title,
               	f_target : link.target,
               	f_usetarget : editor.config.makeLinkShowsTarget
         	};
	}
	else{
            var html = this.getSelectedHTML();
            var titleNoLink="";
            if(Xinha.is_ie){
                re = /title=([\s\S]*?)(\ssrc|\sborder|\swidth|\sheight|\")/i;
                var matches = re.exec(html);
                if(matches != null) {
                    titleNoLink = matches[1];
                }
                if(titleNoLink=="") titleNoLink = sel_value;
            }
            else titleNoLink = sel_value;
         	outparam = {
               	f_href   : i18n("Search existing URL with \'URL\' or click \'New Url\'"),
                f_destination : null,
               	f_title   : titleNoLink,
				f_tooltip : '',
               	f_target : '',
               	f_usetarget : editor.config.makeLinkShowsTarget
         	};
	}
	this._popupDialog( editor.config.URIs.link, function(param) {
      	if (!param) { return false; } //user must have pressed cancel
		var a = link;
		if (!a){
            try {
			// Remove the possible spaces around a selection, before creating a <a>-tag
			// This prevents the createlink and getParentElement to fail.
			if (Xinha.is_ie) {
				var sel = editor._getSelection();
				if (sel.type == "Text") { 
					// When selecting an image inside the A-tag, type = control which fails.
					var range = editor._createRange(sel);
					range.findText(range.htmlText.trim()); // create a new range, without spaces
					range.select(); // select this new range to create a link with
				}
			}

			//Now let the browser create a link (a-tag)
			editor._doc.execCommand("createlink", false, param.f_href);

			var sel = editor._getSelection();
			var range = editor._createRange(sel);
			a = editor.getParentElement();
                  
			if(editor._selectionEmpty(sel)) {
				editor.insertHTML("<a href='" + param.f_href + "' title='" + param.f_tooltip + " 'destination='"+ param.f_destination + "'>" + param.f_title+ "</a>");
			}
          else {
        	if (!Xinha.is_ie) {
                if (a == null || !(/^a$/i.test(a.tagName))) {
                    a = range.startContainer;
                    if ( ! ( /^a$/i.test(a.tagName) ) ) {
                          a = a.nextSibling;
                          if ( a === null ) {
                                a = range.startContainer.parentNode; 
                          }
                    }
                }
            }
            else {//for ie
                while (a) {
                   if (/^a$/i.test(a.tagName)) break; //Search for the enclosing A tag, if found: continue and use it.
                   if (/^body$/i.test(a.tagName)) { a = null; break } //Stop searching when Body-tag is found, don't go too deep.
                   a = a.parentNode; //not found, so try a level higher in the tree.
                }
            }
        	if (a === null){
        		//The A-tag could not be found, so the new link should be removed to be sure.
        		editor._doc.execCommand("unlink", false, null);
        		editor.updateToolbar();
        		return false;
        	}
        	//deal with link and image node
            if(/^a$/i.test(a.tagName)){
                if(!(/^img$/i.test(a.firstChild.tagName))){
                    a.innerHTML = param.f_title.trim();
                }
            }
            }
			} catch(ex) {}
		}
		else //If a was true:
		{
			var href = param.f_href.trim();
			editor.selectNodeContents(a);
			if ( href === '' )
			{
      			  editor._doc.execCommand("unlink", false, null);
      			  editor.updateToolbar();
      			  return false;
			}
			else
			{
				if(/^a$/i.test(a.tagName)){
					a.href = href;
                    if(!(/^img$/i.test(a.firstChild.tagName))){
                    	a.innerHTML = param.f_title.trim();
                    }
				}
			}
		}
		if(a != null) {
			a.href = param.f_href.trim();
			a.target = param.f_target.trim();
			a.title = param.f_tooltip.trim();

	        if (Xinha.is_ie) {
	            a.destination = param.f_destination.trim();
	            if (!a.destination && a.relationID) {
	                a.relationID = "";
	            }
	        }
	        else {
	            a.setAttribute("destination", param.f_destination.trim());
	            if (!a.getAttribute("destination") && a.getAttribute("relationID")) {
	                a.removeAttribute("relationID");
	            }
	        }
	        if(!Xinha.is_ie){
	        	editor.selectNodeContents(a);
	        }
		}
		editor.updateToolbar();
	},
	outparam);
};

Xinha.prototype._insertImage = function(image) {
        var editor = this;	// for nested functions
        var outparam = null;
        if (typeof image == "undefined") {
                image = this.getParentElement();
                if (image && !/^img$/i.test(image.tagName))
                        image = null;
        }
        if (image) outparam = {
                f_url    : Xinha.is_ie ? editor.stripBaseURL(image.src) : image.getAttribute("src"),
                f_alt    : image.alt,
                f_border : image.border,
                f_align  : image.align,
                f_width  : image.width,
                f_height : image.height,
                f_destination : Xinha.is_ie ? image.destination : image.getAttribute("destination"),
                f_rel : Xinha.is_ie ? image.rel : image.getAttribute("rel"),
                f_grouprel : Xinha.is_ie ? image.grouprel : image.getAttribute("grouprel")
        };
        this._popupDialog(editor.config.URIs.insert_image, function(param) {
                if (!param) {// user must have pressed Cancel
                        return false;
                }
                var img = image;
                if (!img) {
                  if ( Xinha.is_ie && Xinha.ie_version < 8) {
                    var sel = editor._getSelection();
                    var range = editor._createRange(sel);
                    editor._doc.execCommand("insertimage", false, param.f_url);
        			img = range.parentElement();
                    // wonder if this works...
                    if ( img.tagName.toLowerCase() != "img" ) {
                      img = img.previousSibling;
        			}
                   }
                   else { //for Firefox and IE8+ and others
                    img = document.createElement('img');
                    img.src = param.f_url; //The SRC is already known, so set it.
                    if ( !img.tagName ) {
                      // if the cursor is at the beginning of the document
                      img = range.startContainer.firstChild;
                    }
                  }
                } else {
                  img.src = param.f_url;
                }
                for (field in param) {
                        var value = param[field];
                        switch (field) {
                            case "f_alt"    :  img.alt = value; img.title = value; break;
                            case "f_border" :  Xinha.is_ie ? img.border = parseInt(value || "0") : img.setAttribute("border", parseInt(value || "0")); break;
                            case "f_align"  :  Xinha.is_ie ? img.align = value : img.setAttribute("align", value); break;
                            case "f_rel"    :  Xinha.is_ie ? img.rel = value : img.setAttribute("rel", value); break;
                            case "f_grouprel"  :  Xinha.is_ie ? img.grouprel = value : img.setAttribute("grouprel", value); break;
                            case "f_width"  :  Xinha.is_ie ? img.width = parseInt(value || "100") : img.setAttribute("width", parseInt(value || "100")); break;
                            case "f_height" :
                              if (Xinha.is_ie) {
                                if (!value) {
                                  img.height = "100";
                                }
                                else {
                                  img.height = parseInt(value);
                                }
                              }
                              else {
                                if (!value) {
                                  img.removeAttribute("height");
                                }
                                else {
                                  img.setAttribute("height", parseInt(value));
                                }
                              }
                              break;
                            case "f_destination"  : Xinha.is_ie ? img.destination = value : img.setAttribute("destination", value); break;
                        }
                }
                //All attributes/parameters for img are set. Now add the HTML Node, so all is stored.
                //for Firefox and IE8+ and others. IE6 and IE7 already have the right attributes.
                if ( ! (Xinha.is_ie && Xinha.ie_version < 8)) {
                	editor.insertNodeAtSelection(img);
                }
        }, outparam);
};

// Called when the user clicks the Insert Table button
Xinha.prototype._inserttable = function()
{

  var editor = this;	// for nested functions
    var sel = editor._getSelection();
  var range = this._createRange(sel);
  this._popupDialog(
    editor.config.URIs.insert_table,
    function(param)
    {
      // user must have pressed Cancel
      if ( !param )
      {
        return false;
      }
      var doc = editor._doc;
      // create the table element
      var table = doc.createElement("table");
      // assign the given arguments

      for ( var field in param )
      {
        var value = param[field];
        if ( !value )
        {
          continue;
        }
        switch (field)
        {
          case "f_width":
            table.width = value + param.f_unit;
          break;
          case "f_align":
            table.align = value;
          break;
          case "f_border":
            table.border = parseInt(value, 10);
          break;
          case "f_spacing":
            table.cellSpacing = parseInt(value, 10);
          break;
          case "f_padding":
            table.cellPadding = parseInt(value, 10);
          break;
        }
      }
      var cellwidth = 0;
      if ( param.f_fixed )
      {
        cellwidth = Math.floor(100 / parseInt(param.f_cols, 10));
      }
      var tbody = doc.createElement("tbody");
      table.appendChild(tbody);   
      for ( var i = 0; i < param.f_rows; ++i )
      {
        var tr = doc.createElement("tr");
       tbody.appendChild(tr);
        for ( var j = 0; j < param.f_cols; ++j )
        {
          var td = doc.createElement("td");
          // @todo : check if this line doesnt stop us to use pixel width in cells
          if (cellwidth)
          {
            td.style.width = cellwidth + "%";
          }
          tr.appendChild(td);
          // Browsers like to see something inside the cell (&nbsp;).
          td.appendChild(doc.createTextNode('\u00a0'));
        }
      }
      editor.insertNodeAtSelection(table);
      return true;
    },
    null
  );
};

