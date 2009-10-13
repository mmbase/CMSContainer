<tr>
	<td style="width:80px">
     <fmt:message key="asset.search.selectchannel.title" />
	</td>
   <td style="width:15px;align:right">
      <a style="align:right" href="javascript:erase('contentChannel');erase('contentChannelPath')">
			<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" title="<fmt:message key="asset.search.selectchannel.erase"/>" alt="<fmt:message key="asset.search.selectchannel.erase"/>"/>
		</a></td>
	<td>
		<mm:cloud>
			<mm:node number="${contentChannel}" notfound="skip">
				<mm:field name="path" id="contentChannelPath" write="false" />
			</mm:node>
		</mm:cloud>
      <html:hidden property="contentChannel"/>
		<input type="text" name="contentChannelPath" value="${contentChannelPath}" disabled="true" style="width: 200px"/>   
       <a style="margin-left:5px" href="<c:url value='/editors/repository/select/SelectorChannel.do?channel=${searchForm.contentChannel}' />" target="selectchannel"    onclick="openPopupWindow('selectchannel', 340, 400)"> 
			<fmt:message key="asset.search.selectchannel" />
		</a> 
      <img src="<cmsc:staticurl page='/editors/gfx/button_side_block.gif'/>" alt=""/>
	</td>
</tr>