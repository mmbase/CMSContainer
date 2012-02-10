<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib tagdir="/WEB-INF/tags/googlemap/" prefix="googlemap" %>
<c:choose>
	<c:when test="${not empty width and not empty height}"><div id="map" style="width: ${width}; height: ${height};"></div>	</c:when>
	<c:otherwise><div id="map" style="width: 370px; height: 260px;"></div></c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty widthDirections and not empty heightDirections}"><div id="directions" style="width: ${widthDirections}; height: ${heightDirections};"></div>	</c:when>
	<c:otherwise><div id="directions"></div></c:otherwise>
</c:choose>
<googlemap:initmaps />
<script type="text/javascript">
    //<![CDATA[
    function setDirections(fromAddress, toAddress, locale) {
        gdir.load("from: " + fromAddress + " to: " + toAddress,
                { "locale": locale });
    }
    function mapsLoaded() {
    	initializePanels('map','directions');
    	addAddress('${address}', '${info}')
    }
    function submitDirections(form) {
    	setDirections(form.from.value, form.to.value, form.locale.value); 
    	return false;
    }
    //]]>
</script>
<googlemap:loadmaps key="${key}" callback="mapsLoaded" />

<div id="directions-form">
  <form action="#" onsubmit="return submitDirections(this);">
  <table>
   <tr>
   <th align="right">From:&nbsp;</th>
   <td>
     <input type="text" size="25" id="fromAddress" name="from" value=""/>
   </td>
   </tr>
   <tr>
   <th align="right">&nbsp;&nbsp;To:&nbsp;</th>
   <td align="right">
     <input type="text" size="25" id="toAddress" name="to" value="${address}" />
   </td>
   </tr><tr>
   <th>Language:&nbsp;</th>
   <td colspan="3">
     <select id="locale" name="locale">
			<option value="af">Afrikaans</option>
			<option value="sq">Albanian</option>
			<option value="ar">Arabic</option>
			<option value="be">Belarusian</option>
			<option value="bg">Bulgarian</option>
			<option value="ca">Catalan</option>
			<option value="zh-CN">Chinese (Simplified)</option>
			<option value="zh-TW">Chinese (Traditional)</option>
			<option value="hr">Croatian</option>
			<option value="cs">Czech</option>
			<option value="da">Danish</option>
			<option value="nl">Dutch</option>
			<option selected="selected" value="en">English</option>
			<option value="et">Estonian</option>
			<option value="tl">Filipino</option>
			<option value="fi">Finnish</option>
			<option value="fr">French</option>
			<option value="gl">Galician</option>
			<option value="de">German</option>
			<option value="el">Greek</option>
			<option value="ht">Haitian Creole ALPHA</option>
			<option value="iw">Hebrew</option>
			<option value="hi">Hindi</option>
			<option value="hu">Hungarian</option>
			<option value="is">Icelandic</option>
			<option value="id">Indonesian</option>
			<option value="ga">Irish</option>
			<option value="it">Italian</option>
			<option value="ja">Japanese</option>
			<option value="ko">Korean</option>
			<option value="lv">Latvian</option>
			<option value="lt">Lithuanian</option>
			<option value="mk">Macedonian</option>
			<option value="ms">Malay</option>
			<option value="mt">Maltese</option>
			<option value="no">Norwegian</option>
			<option value="fa">Persian</option>
			<option value="pl">Polish</option>
			<option value="pt">Portuguese</option>
			<option value="ro">Romanian</option>
			<option value="ru">Russian</option>
			<option value="sr">Serbian</option>
			<option value="sk">Slovak</option>
			<option value="sl">Slovenian</option>
			<option value="es">Spanish</option>
			<option value="sw">Swahili</option>
			<option value="sv">Swedish</option>
			<option value="th">Thai</option>
			<option value="tr">Turkish</option>
			<option value="uk">Ukrainian</option>
			<option value="vi">Vietnamese</option>
			<option value="cy">Welsh</option>
			<option value="yi">Yiddish</option>
		</select>
     <input name="submit" type="submit" value="Get Directions!" />
   </td>
   </tr>
   </table>
  </form>
</div>