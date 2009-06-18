<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="urls.title">
<script src="../repository/search.js" type="text/javascript"></script>
<script type="text/javascript">
  function showIcons(id){
    document.getElementById('thumbnail-icons-'+id).style.visibility = 'visible';
  }
  function hideIcons(id){
    document.getElementById('thumbnail-icons-'+id).style.visibility = 'hidden';
  }
   function setShowMode() {
	   var showMode = document.getElementsByTagName("option");
	   var assetShow;
       for(i = 0; i < showMode.length; i++){
          if(showMode[i].selected & showMode[i].id=="a_list"){
              assetShow="list";
          }else if(showMode[i].selected & showMode[i].id=="a_thumbnail"){
        	  assetShow="thumbnail";
          }
       }
      document.forms[0].assetShow.value = assetShow;
      document.forms[0].submit();
	}
	function showInfo(objectnumber) {
		openPopupWindow('urlinfo', '900', '500',
				'../resources/urlinfo.jsp?objectnumber=' + objectnumber);
	}

	function initParentHref(elem) {
		if(elem.id=='selected'){
			elem.parentNode.setAttribute('href', '');
			elem.id ='';
			return;
		}
		elem.parentNode.setAttribute('href', elem.getAttribute('href'));
		var oldSelected = document.getElementById('selected');
		if(oldSelected){
			oldSelected.id="";
		}
		elem.id ='selected';
	}

   function doSelectIt() {
      var href = document.getElementById('assetList').getAttribute('href')+"";
      if (href.length<10) {
          alert("You must select one url");
          return;
      }
      if (href.indexOf('javascript:') == 0) {
       eval(href.substring('javascript:'.length, href.length));
       return false;
      }
      document.location=href;
   }

   function doCancleIt(){
      window.top.close();
   }

   function selectElement(element, title, src) {
	      if(window.top.opener != undefined) {
	         window.top.opener.selectElement(element, title, src);
	         window.top.close();
	      }
	   }

	function selectChannel(channelid, path) {
		var urlMode = document.getElementsByTagName("option");
	       for(i = 0; i < urlMode.length; i++){
	          if(urlMode[i].selected & urlMode[i].id=="a_list"){
	              document.location.href = '../../repository/HighFrequencyAsset.do?action=often&offset=0&channelid='+channelid+'&assetShow=list&assettypes=urls&strict=${strict}';
	          }else if(urlMode[i].selected & urlMode[i].id=="a_thumbnail"){
	              document.location.href = '../../repository/HighFrequencyAsset.do?action=often&offset=0&channelid='+channelid+'&assetShow=thumbnail&assettypes=urls&strict=${strict}';
	          }
	       }
	}
	</script>
	   <link rel="stylesheet" type="text/css" href="../css/assetsearch.css" />
	   </cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either often or search --%>
<mm:import externid="assetShow">list</mm:import><%-- either list or thumbnail --%>

   <c:if test="${action eq 'search'}">
      <div class="tabs"><!-- actieve TAB -->
      <div class="tab_active">
      <div class="body">
      <div><a><fmt:message key="urls.title" /></a></div>
      </div>
      </div>
      </div>
   </c:if>

   <div class="editor" style="height:500px">
      <c:choose>
         <c:when test="${action eq 'search'}">
            <mm:import id="formAction">/editors/resources/UrlAction</mm:import>
            <mm:import id="channelMsg"><fmt:message key="urls.results" /></mm:import>
         </c:when>
         <c:otherwise>
            <mm:import id="formAction">/editors/repository/HighFrequencyAsset</mm:import>
            <c:if test="${param.channelid eq 'all' || param.channelid eq 'siteassets'}">
                <mm:import id="channelMsg"><fmt:message key="urls.channel.title"><fmt:param>ALL CHANNELS</fmt:param></fmt:message></mm:import>
            </c:if>
            <c:if test="${param.channelid ne 'all' && param.channelid ne 'siteassets'}">
               <mm:node number="${channelid}">
                  <mm:field name="path" id="path" write="false" />
                  <mm:import id="channelMsg">
                     <fmt:message key="urls.channel.title">
                        <fmt:param value="${path}" />
                     </fmt:message>
                  </mm:import>
               </mm:node>
            </c:if>
         </c:otherwise>
      </c:choose>
      <div class="body" <c:if test="${action == 'often'}">style="display:none"</c:if> >
         <html:form action="${formAction}" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="assetShow" value="${assetShow}"/>
            <html:hidden property="strict" value="${strict}"/>
            <html:hidden property="offset"/>
            <c:if test="${action eq 'often'}">
            <html:hidden property="assettypes" value="urls"/>
            <html:hidden property="channelid" value="${channelid}"/>
            </c:if>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <c:if test="${action eq 'search'}">
            <mm:import id="contenttypes" jspvar="contenttypes">urls</mm:import>
               <%@include file="urlform.jsp" %>
            </c:if>
         </html:form>
      </div>

   <div class="ruler_green">
         <div><c:out value="${channelMsg}" /></div>
   </div>
   <div class="show_mode_selector">
   <select name="urlMode" id="urlMode" onchange="javascript:setShowMode()">
      <c:if test="${assetShow eq 'list'}">
         <option id="a_list" selected="selected"><fmt:message key="asset.url.list"/></option>
         <option id="a_thumbnail"><fmt:message key="asset.url.thumbnail"/></option>
      </c:if>
      <c:if test="${assetShow eq 'thumbnail'}">
         <option id="a_list"><fmt:message key="asset.url.list"/></option>
         <option id="a_thumbnail" selected="selected"><fmt:message key="asset.url.thumbnail"/></option>
      </c:if>
   </select>
   </div>
   <div class="body" style="max-height:400px;overflow-y:auto; overflow-x:hidden;padding:10px 0px 0px 11px">
   <mm:import externid="results" jspvar="nodeList" vartype="List" />
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
         <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
         <c:if test="${resultCount > 0}">
            <%@include file="../repository/searchpages.jsp" %>

            <c:if test="${assetShow eq 'thumbnail'}">
            <div id="assetList" class="hover" style="width:100%" href="">
                  <mm:listnodes referid="results">
                     <c:if test="${strict == 'urls'}">
                       <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
                     </c:if>
                     <c:if test="${ empty strict}">
                       <mm:import id="url">javascript:selectElement('<mm:field name="number" />', '<mm:field name="title" escape="js-single-quotes"/>','<mm:field name="url" />');</mm:import>
                     </c:if>
                     <div class="grid" href="<mm:write referid="url"/>" onMouseOut="javascript:hideIcons(<mm:field name='number'/>)" onMouseOver="showIcons(<mm:field name='number'/>)">
                        <div id="thumbnail-icons-<mm:field name='number'/>" class="thumbnail-icons">
                            <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="urlsearch.icon.info" />" title="<fmt:message key="urlsearch.icon.info" />"/></a>
                        </div>
                        <div class="thumbnail" onclick="initParentHref(this.parentNode)">
                           <c:set var="thumbnail_alt"><mm:field name="url" /></c:set>
                           <img src="../gfx/url.gif" title="${thumbnail_alt}" alt="${thumbnail_alt}" width="120" height="100"/>
                        </div>
                        <div class="assetInfo" onclick="initParentHref(this.parentNode)">
                              <mm:field name="title" jspvar="title" write="false"/>
                              ${fn:substring(title, 0, 40)}<c:if test="${fn:length(title) > 40}">...</c:if>
                              <br/>
                              <mm:field name="valid" write="false" jspvar="isValidUrl"/>
                              <c:choose>
                                   <c:when test="${empty isValidUrl}">
                                       <fmt:message key="urlsearch.validurl.unknown" />
                                   </c:when>
                                   <c:when test="${isValidUrl eq false}">
                                       <fmt:message key="urlsearch.validurl.invalid" />
                                   </c:when>
                                   <c:when test="${isValidUrl eq true}">
                                       <fmt:message key="urlsearch.validurl.valid" />
                                   </c:when>
                                   <c:otherwise>
                                       <fmt:message key="urlsearch.validurl.unknown" />
                                   </c:otherwise>
                               </c:choose>
                        </div>
                     </div>
                  </mm:listnodes>
            </div>
            </c:if>

         <c:if test="${assetShow eq 'list'}">
            <table>
               <c:if test="${action == 'search'}">
                  <tr class="listheader">
                     <th width="55"></th>
                     <th nowrap="true"><a href="javascript:orderBy('title')" class="headerlink"><fmt:message
                        key="urlsearch.titlecolumn" /></a></th>
                     <th nowrap="true"><a href="javascript:orderBy('url')"
                        class="headerlink"><fmt:message key="urlsearch.urlcolumn" /></a></th>
                     <th nowrap="true"><a href="javascript:orderBy('valid')"
                        class="headerlink"><fmt:message
                        key="urlsearch.validcolumn" /></a></th>
                  </tr>
               </c:if>
               <tbody id="assetList" class="hover"  href="">
               <c:set var="useSwapStyle">true</c:set>
               <mm:listnodes referid="results">
               <c:if test="${strict == 'urls'}">
                  <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
               </c:if>
               <c:if test="${ empty strict}">
                  <mm:import id="url">javascript:selectElement('<mm:field name="number" />', '<mm:field name="title" escape="js-single-quotes"/>','<mm:field name="url" />');</mm:import>
               </c:if>
                  <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                     <td style="white-space:nowrap;">
                         <a href="javascript:showInfo(<mm:field name="number" />)">
                               <img src="../gfx/icons/info.png" title="<fmt:message key="urlsearch.icon.info" />" /></a>
                     </td>
                     <mm:field name="title" jspvar="title" write="false"/>
                     <td onMouseDown="initParentHref(this.parentNode)">${fn:substring(title, 0, 40)}<c:if test="${fn:length(title) > 40}">...</c:if></td>
                     <mm:field name="url" jspvar="url" write="false"/>
                     <td onMouseDown="initParentHref(this.parentNode);">${fn:substring(url, 0, 40)}<c:if test="${fn:length(url) > 40}">...</c:if></td>
                     <mm:field name="valid" write="false" jspvar="isValidUrl"/>
                     <td>
                         <c:choose>
                             <c:when test="${empty isValidUrl}">
                                 <fmt:message key="urlsearch.validurl.unknown" />
                             </c:when>
                             <c:when test="${isValidUrl eq false}">
                                 <fmt:message key="urlsearch.validurl.invalid" />
                             </c:when>
                             <c:when test="${isValidUrl eq true}">
                                 <fmt:message key="urlsearch.validurl.valid" />
                             </c:when>
                             <c:otherwise>
                                 <fmt:message key="urlsearch.validurl.unknown" />
                             </c:otherwise>
                         </c:choose>
                     </td>
                  </tr>
                  <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
               </mm:listnodes>
            </tbody>
         </table>
   </c:if>
</c:if>

<c:if test="${resultCount == 0 && param.name != null}">
<fmt:message key="urlsearch.noresult" />
</c:if>
<c:if test="${resultCount > 0}">
<%@include file="../repository/searchpages.jsp" %>
</c:if>
</div>
<c:if test="${action == 'often'}">
<div class="body">
<mm:url page="/editors/repository/select/SelectorChannel.do" id="select_channel_url" write="false" />
<mm:url page="/editors/resources/UrlInitAction.do?action=search&strict=${strict}" id="search_url_url" write="false" />
<mm:url page="/editors/resources/urlcreate.jsp?createdNode=0&channelid=${channelid}&strict=${strict}" id="new_url_url" write="false" />
<mm:url page="/editors/repository/HighFrequencyAsset.do?action=often&assetShow=${assetShow}&offset=0&channelid=all&assettypes=urls&strict=${strict}" id="often_show_urls" write="false"/>
<ul class="shortcuts">
   <li><a href="${often_show_urls}"><fmt:message key="urlselect.link.allchannel" /></a></li>
   <li><a onclick="openPopupWindow('selectchannel', 340, 400);" target="selectchannel" href="${select_channel_url}"><fmt:message key="urlselect.link.channel" /></a></li>
   <li><a href="${search_url_url}"><fmt:message key="urlselect.link.search" /></a></li>
   <li><a href="${new_url_url}"><fmt:message key="urlselect.link.new" /></a></li>
</ul>
</div>
</c:if>
</div>
<div id="commandbuttonbar" class="buttonscontent" style="clear:both">
     <div class="page_buttons_seperator">
        <div></div>
     </div>
     <div class="page_buttons">
         <div class="button">
             <div class="button_body">
                 <a class="bottombutton" title="Select the url." href="javascript:doSelectIt();"><fmt:message key="urlselect.ok" /></a>
             </div>
         </div>

         <div class="button">
             <div class="button_body">
                 <a class="bottombutton" href="javascript:doCancleIt();" title="Cancel this task, url will NOT be selected."><fmt:message key="urlselect.cancel" /></a>
             </div>
         </div>
         <div class="begin">
         </div>
     </div>
 </div>
</mm:cloud>
</body>
</html:html>
</mm:content>
