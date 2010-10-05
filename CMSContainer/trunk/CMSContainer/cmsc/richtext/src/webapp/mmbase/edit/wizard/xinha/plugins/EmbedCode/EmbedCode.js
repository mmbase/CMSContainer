function EmbedCode(editor) {
  this.editor = editor;
  var cfg = editor.config;
  var self = this;
 
  this.placeholderImg = '<img class="EC_placeholder" src="'+Xinha.getPluginDir("EmbedCode")+'/img/placeholder.gif" />';
 
  // register the toolbar buttons provided by this plugin
  cfg.registerButton({
  id       : "embed-code",
  tooltip  : this._lc("Insert embed content"),
  image    : editor.imgURL("embed-code.gif", "EmbedCode"),
  textMode : false,
  action   : function(editor)
                {
			self.buttonPress(editor);
                }
  });
  cfg.URIs['embed_code'] =  _editor_url + 'popups/embed_code.html';
  cfg.addToolbarElement("embed-code", "createlink", 1);
}

EmbedCode._pluginInfo = {
		name          : "EmbedCode",
		version       : "1.0",
		developer     : "Rain Tang",
		developer_url : "",
		sponsor       : "",
		sponsor_url   : "",
		c_owner       : "Rain Tang",
		license       : "none"
	};

EmbedCode.prototype._lc = function(string) {
    return Xinha._lc(string, 'EmbedCode');
};

EmbedCode.prototype.onGenerate = function() {
  //this.editor.addEditorStylesheet(Xinha.getPluginDir("EmbedCode") + '/embed-code.css');
 
};
//inwardHtml & outwardHtml are used for change video to image while switching modes
//EmbedCode.prototype.inwardHtml = function(html)
//{
//        return html;
//}
//EmbedCode.prototype.outwardHtml = function(html)
//{
//        return html;
//}

EmbedCode.prototype.buttonPress = function(editor){
    editor._popupDialog(_editor_url + "/plugins/EmbedCode/popups/embed_code.html", function(param){
        if (param['embededCode']) {
            var code = param['embededCode'];
			var re = /<embed([\s\S]*?)<\/embed>/g;
			code = code.match(re);
            var v = document.createElement("p");
            v.innerHTML = code;
            editor.insertNodeAtSelection(v);
        }
    }, null);
}
