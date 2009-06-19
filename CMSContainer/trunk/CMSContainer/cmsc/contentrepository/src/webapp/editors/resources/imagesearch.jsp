<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="images.title">
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
      openPopupWindow('imageinfo', '900', '500',
            '../resources/imageinfo.jsp?objectnumber=' + objectnumber);
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
          alert("You must select one image");
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
</script>
   <link rel="stylesheet" type="text/css" href="../css/assetsearch.css" />
   </cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either often or search --%>
<mm:import externid="assetShow">list</mm:import><%-- either list or thumbnail --%>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>

<div class="tabs"><!-- actieve TAB -->
   <div class="tab_active">
      <div class="body">
        <div><a><fmt:message key="images.title" /></a></div>
      </div>
   </div>
</div>

   <div class="editor" style="height:555px">
      <mm:import id="formAction">/editors/resources/ImageAction</mm:import>
      <mm:import id="channelMsg"><fmt:message key="images.results" /></mm:import>

      <div class="body">
         <html:form action="${formAction}" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="assetShow" value="${assetShow}"/>
            <html:hidden property="strict" value="${strict}"/>
            <html:hidden property="offset"/>
            <html:hidden property="pager.offset" value="${pagerDOToffset}"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <mm:import id="contenttypes" jspvar="contenttypes">images</mm:import>
               <%@include file="imageform.jsp" %>
         </html:form>
      </div>
      <div class="ruler_green">
         <div><c:out value="${channelMsg}" /></div>
   `  </div>
      <div class="show_mode_selector">
      <select name="imageMode" id="imageMode" onchange="javascript:setShowMode()">
         <c:if test="${assetShow eq 'list'}">
            <option id="a_list" selected="selected"><fmt:message key="asset.image.list"/></option>
            <option id="a_thumbnail"><fmt:message key="asset.image.thumbnail"/></option>
         </c:if>
         <c:if test="${assetShow eq 'thumbnail'}">
            <option id="a_list"><fmt:message key="asset.image.list"/></option>
            <option id="a_thumbnail" selected="selected"><fmt:message key="asset.image.thumbnail"/></option>
         </c:if>
      </select>
      </div>

         <mm:import externid="results" jspvar="nodeList" vartype="List"/>
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
         <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
         <c:if test="${resultCount == 0 && param.title != null}">
        <pre>
  <fmt:message key="imagesearch.noresult" />
       </pre>
         </c:if>
         <div class="body" style="max-height:400px;overflow-y:auto; overflow-x:hidden"> 
         <c:if test="${resultCount > 0}">
            <edit:pages search="true" totalElements="${resultCount}" offset="${offset}"/>

            <c:if test="${assetShow eq 'thumbnail'}">
            <div id="assetList" class="hover" style="width:100%" href="">
                  <mm:listnodes referid="results">
                     <mm:field name="description" escape="js-single-quotes" jspvar="description">
                        <%
                           description = ((String) description).replaceAll("[\\n\\r\\t]+", " ");
                        %>
                        <c:if test="${strict == 'images'}">
                          <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
                        </c:if>
                        <c:if test="${ empty strict}">
                           <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
                        </c:if>
                     </mm:field>
                     <div class="grid" href="<mm:write referid="url"/>" onMouseOut="javascript:hideIcons(<mm:field name='number'/>)" onMouseOver="showIcons(<mm:field name='number'/>)">
                        <div id="thumbnail-icons-<mm:field name='number'/>" class="thumbnail-icons">
                            <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="imagesearch.icon.info" />" title="<fmt:message key="imagesearch.icon.info" />"/></a>
                        </div>
                        <div class="thumbnail" onclick="initParentHref(this.parentNode)"><mm:image mode="img" template="s(128x128)"/></div>
                        <div class="assetInfo" onclick="initParentHref(this.parentNode)">
                              <mm:field id="title" write="false" name="title"/>
                              <c:if test="${fn:length(title) > 15}">
                                 <c:set var="title">${fn:substring(title,0,14)}...</c:set>
                              </c:if>${title}
                              <br/><mm:field name="itype" />
                        </div>
                     </div>
                  </mm:listnodes>
            </div>
            </c:if>

         <c:if test="${assetShow eq 'list'}">
            <table>
               <tr class="listheader">
                  <th width="55"></th>
                  <th nowrap="true"><a href="javascript:orderBy('title')"
                     class="headerlink"><fmt:message key="imagesearch.titlecolumn" /></a></th>
                  <th nowrap="true"><a href="javascript:orderBy('filename')"
                     class="headerlink"><fmt:message
                     key="imagesearch.filenamecolumn" /></a></th>
                  <th nowrap="true"><a href="javascript:orderBy('itype')"
                     class="headerlink"><fmt:message
                     key="imagesearch.mimetypecolumn" /></a></th>
                  <th></th>
               </tr>
					<tbody id="assetList" class="hover"  href="">
						<c:set var="useSwapStyle">true</c:set>
						<mm:listnodes referid="results">
                     <mm:field name="description" escape="js-single-quotes" jspvar="description">
                        <%
                           description = ((String) description).replaceAll("[\\n\\r\\t]+", " ");
                        %>
                        <c:if test="${strict == 'images'}">
                           <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
                        </c:if>
                        <c:if test="${ empty strict}">
                           <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
                        </c:if>
                     </mm:field>
							<tr <c:if test="${useSwapStyle}">class="swap"</c:if>
								href="<mm:write referid="url"/>">
								<td style="white-space: nowrap;">
                        <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="imagesearch.icon.info" />" title="<fmt:message key="imagesearch.icon.info" />" /></a>
                        </td>
                        <td onMouseDown="initParentHref(this.parentNode)">
                           <mm:field id="title" write="false" name="title"/>
                           <c:if test="${fn:length(title) > 50}">
                              <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                           </c:if>
                           ${title}
                        </td>
                        <td onMouseDown="initParentHref(this.parentNode)">
                           <mm:field name="filename"/>
                        </td>
								<td onMouseDown="initParentHref(this.parentNode)"><mm:field name="itype" /></td>
								<td></td>
							</tr>
							<c:set var="useSwapStyle">${!useSwapStyle}</c:set>
						</mm:listnodes>
					</tbody>
				</table>
			</c:if>

      </c:if>
         <div style="clear:both" ></div>
         <c:if test="${resultCount > 0}">
            <edit:pages search="true" totalElements="${resultCount}" offset="${offset}"/>
         </c:if>
      </div>
   </div>
       <div id="commandbuttonbar" class="buttonscontent" style="clear:both">
            <div class="page_buttons_seperator">
               <div></div>
            </div>
            <div class="page_buttons">
                <div class="button">
                    <div class="button_body">
                        <a class="bottombutton" title="Select the image." href="javascript:doSelectIt();"><fmt:message key="imageselect.ok" /></a>
                    </div>
                </div>
               
                <div class="button">
                    <div class="button_body">
                        <a class="bottombutton" href="javascript:doCancleIt();" title="Cancel this task, image will NOT be selected."><fmt:message key="imageselect.cancel" /></a>
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