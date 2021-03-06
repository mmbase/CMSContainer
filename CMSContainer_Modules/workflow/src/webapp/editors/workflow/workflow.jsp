<%--@elvariable id="status" type="String"--%>

<%@ page import="com.finalist.cmsc.workflow.forms.Utils"%>
<%@ page import="static com.finalist.cmsc.workflow.forms.Utils.tabClass"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ include file="globals.jsp"%>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html:html xhtml="true">
	<cmscedit:head title="workflow.module.title">
      <script src="../../js/prototype.js" type="text/javascript"></script>
		<script src="workflow.js" type="text/javascript"></script>
		<link href="../css/workflow.css" rel="stylesheet" type="text/css" />
      <c:url value="/editors/workflow/WorkflowTreeStatusAction.do" var="treeStatusAction"/>
		<script type="text/javascript">
		function showAssetInfo(assetType, objectnumber) {
            var infoURL;
            infoURL = '../resources/';
            infoURL += assetType.substring(0,assetType.length-1);
            infoURL += 'info.jsp?objectnumber=';
            infoURL += objectnumber;
            openPopupWindow(assetType.toLowerCase()+'info', '900', '500', infoURL);
         }
       function showInfo(elementType, objectNumber) {
            var assetType = elementType.toLowerCase();
            if(assetType == 'attachments' || assetType == 'images' || assetType == 'urls'){
                showAssetInfo(assetType, objectNumber);
            }
            else{
                openPopupWindow("info", 500, 500, "../repository/showitem.jsp?objectnumber=" + objectNumber);
            }
         }
      var treeHandler = {
          toggle: function (oItem) { 
		      var parentTableId = oItem.id.replace('-plus', '');
		      var originImgSrc = oItem.src;
		      if(originImgSrc.toLowerCase().indexOf('plus.png')>=0){
		         oItem.src = originImgSrc.replace('plus', 'minus');
		         document.getElementById(parentTableId + '-cont').style.display = 'block';
		      }else{
		         oItem.src = originImgSrc.replace('minus', 'plus');
		         document.getElementById(parentTableId + '-cont').style.display = 'none';
		      }
		      new Ajax.Request(
		              "${treeStatusAction}",
		              {
		                 method:'put',
		                 parameters: 'treeItem=' + parentTableId.replace('tree-', ''),
		                 asynchronous : true
		              }
		           );
		      }
      };
      
   </script>
	</cmscedit:head>

	<body>
	<div id="left"><cmscedit:sideblock title="workflow.status.header">
		<mm:import externid="statusInfo" required="true" />
      <c:set var="treeStatus" value="${sessionScope.workflowTreeStatus}" />

   <div id="tree">
      <div id="tree-head">
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
				<tr>
				   <td class="indent" />
				   <td class="indent" />
					<td class="indent" />
					<td class="leftData"></td>
					<th><fmt:message key="workflow.status.draft" /></th>
					<th><fmt:message key="workflow.status.finished" /></th>
					<c:if test="${acceptedEnabled}">
						<th><fmt:message key="workflow.status.approved" /></th>
					</c:if>
					<th><fmt:message key="workflow.status.published" /></th>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-allcontent">
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
				<tr>
					<td class="indent"><img id="tree-allcontent-plus"
						onclick="treeHandler.toggle(this);"
						<c:if test="${treeStatus.allcontent eq 1}">src="../utils/ajaxtree/images/minus.png"</c:if>
						<c:if test="${treeStatus.allcontent eq 0}">src="../utils/ajaxtree/images/plus.png"</c:if> /></td>
					<td class="leftData"><fmt:message
						key="workflow.status.allcontent" /></td>
					<td class="indent" />
					<td class="indent" />
					<td><a href="AllcontentWorkflowAction.do?status=draft">${statusInfo.allcontentDraft}</a></td>
					<td><a href="AllcontentWorkflowAction.do?status=finished">${statusInfo.allcontentFinished}</a></td>
					<c:if test="${acceptedEnabled}">
						<td><a href="AllcontentWorkflowAction.do?status=approved">${statusInfo.allcontentApproved}</a></td>
					</c:if>
					<td><a href="AllcontentWorkflowAction.do?status=published">${statusInfo.allcontentPublished}</a></td>
				</tr>
			</tbody>
		</table>
		</div>
		<div id="tree-allcontent-cont" <c:if test="${treeStatus.allcontent eq 0}">style="display:none"</c:if> >
		<div id="tree-content">
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
<%--            <c:if test="${ statusInfo.contentDraft != 0 || statusInfo.contentFinished != 0 || statusInfo.contentApproved != 0 || statusInfo.contentPublished != 0}">
--%>
					<tr>
						<td class="indent" />
						<td class="indent"><img id="tree-content-plus"
							onclick="treeHandler.toggle(this);"
							<c:if test="${treeStatus.content eq 1}">src="../utils/ajaxtree/images/Tminus.png"</c:if>
				         <c:if test="${treeStatus.content eq 0}">src="../utils/ajaxtree/images/Tplus.png"</c:if> /></td>
						<td class="leftData"><fmt:message
							key="workflow.status.content" /></td>
						<td class="indent" />
						<td><a href="ContentWorkflowAction.do?status=draft">${statusInfo.contentDraft}</a></td>
						<td><a href="ContentWorkflowAction.do?status=finished">${statusInfo.contentFinished}</a></td>
						<c:if test="${acceptedEnabled}">
							<td><a href="ContentWorkflowAction.do?status=approved">${statusInfo.contentApproved}</a></td>
						</c:if>
						<td><a href="ContentWorkflowAction.do?status=published">${statusInfo.contentPublished}</a></td>
					</tr>
<%--              </c:if>
--%>
			</tbody>
		</table>
		</div>
		<div id="tree-content-cont" <c:if test="${treeStatus.content eq 0}">style="display:none"</c:if>>
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
				<c:forEach var="contentType" items="${statusInfo.contentChildTypes}" varStatus="itemStatus">
               <c:if test="${ statusInfo.contentChildrenDraft[contentType.value] != 0 || statusInfo.contentChildrenFinished[contentType.value] != 0
                             || statusInfo.contentChildrenApproved[contentType.value]!=0 || statusInfo.contentChildrenPublished[contentType.value] != 0}">
						<tr>
							<td class="indent"/>
							<td class="indent"><img src="../utils/ajaxtree/images/I.png" /></td>
	                  <c:if test="${not itemStatus.last}">
	                  <td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
	                  </c:if>
	                  <c:if test="${itemStatus.last}">
	                  <td class="indent"><img src="../utils/ajaxtree/images/L.png" /></td>
	                  </c:if>
                     <td class="leftData2">
                        <c:if test="${(fn:indexOf(contentType.label, ' ') > -1) or (fn:length(contentType.label) <= 11)}" >
                           ${contentType.label}
                        </c:if>
                        <c:if test="${ (fn:indexOf(contentType.label, ' ') == -1) and (fn:length(contentType.label) > 11) }" >
                           ${fn:substring(contentType.label,0,11)}...
                        </c:if>
                     </td>
							<td><a
								href="ContentWorkflowAction.do?workflowNodetype=${contentType.value}&status=draft">${statusInfo.contentChildrenDraft[contentType.value]}</a></td>
							<td><a
								href="ContentWorkflowAction.do?workflowNodetype=${contentType.value}&status=finished">${statusInfo.contentChildrenFinished[contentType.value]}</a></td>
							<c:if test="${acceptedEnabled}">
								<td><a
									href="ContentWorkflowAction.do?workflowNodetype=${contentType.value}&status=approved">${statusInfo.contentChildrenApproved[contentType.value]}</a></td>
							</c:if>
							<td><a
								href="ContentWorkflowAction.do?workflowNodetype=${contentType.value}&status=published">${statusInfo.contentChildrenPublished[contentType.value]}</a></td>
						</tr>
               </c:if>
				</c:forEach>
			</tbody>
		</table>
		</div>
		<div id="tree-asset">
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
<%--              <c:if test="${ statusInfo.assetDraft != 0 || statusInfo.assetFinished != 0 || statusInfo.assetApproved != 0 || statusInfo.assetPublished != 0}">
--%>
					<tr>
						<td class="indent" />
						<td class="indent"><img id="tree-asset-plus"
							onclick="treeHandler.toggle(this);"
							<c:if test="${treeStatus.asset eq 1}">src="../utils/ajaxtree/images/Lminus.png"</c:if>
				         <c:if test="${treeStatus.asset eq 0}">src="../utils/ajaxtree/images/Lplus.png"</c:if> /></td>
						<td class="leftData"><fmt:message key="workflow.status.asset" /></td>
						<td class="indent" />
						<td><a href="AssetWorkflowAction.do?status=draft">${statusInfo.assetDraft}</a></td>
						<td><a href="AssetWorkflowAction.do?status=finished">${statusInfo.assetFinished}</a></td>
						<c:if test="${acceptedEnabled}">
							<td><a href="AssetWorkflowAction.do?status=approved">${statusInfo.assetApproved}</a></td>
						</c:if>
						<td><a href="AssetWorkflowAction.do?status=published">${statusInfo.assetPublished}</a></td>
					</tr>
<%--              </c:if>
--%>
			</tbody>
		</table>
		</div>
		<div id="tree-asset-cont" <c:if test="${treeStatus.asset eq 0}">style="display:none"</c:if> >
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
            <c:forEach var="assetType" items="${statusInfo.assetChildTypes}" varStatus="itemStatus">
               <c:if test="${ statusInfo.assetChildrenDraft[assetType.value] != 0 || statusInfo.assetChildrenFinished[assetType.value] != 0
	                          || statusInfo.assetChildrenApproved[assetType.value]!=0 || statusInfo.assetChildrenPublished[assetType.value] != 0}">
	               <tr>
	                  <td class="indent"/>
	                  <td class="indent2" />
	                  <c:if test="${not itemStatus.last}">
	                  <td class="indent"><img src="../utils/ajaxtree/images/T.png" /></td>
	                  </c:if>
	                  <c:if test="${itemStatus.last}">
	                  <td class="indent"><img src="../utils/ajaxtree/images/L.png" /></td>
	                  </c:if>
	                  <td class="leftData2">
                        <c:if test="${(fn:indexOf(assetType.label, ' ') > -1) or (fn:length(assetType.label) <= 11)}" >
                           ${assetType.label}
                        </c:if>
                        <c:if test="${ (fn:indexOf(assetType.label, ' ') == -1) and (fn:length(assetType.label) > 11) }" >
                           ${fn:substring(assetType.label,0,11)}...
                        </c:if>
                     </td>
	                  <td><a
	                     href="AssetWorkflowAction.do?workflowNodetype=${assetType.value}&status=draft">${statusInfo.assetChildrenDraft[assetType.value]}</a></td>
	                  <td><a
	                     href="AssetWorkflowAction.do?workflowNodetype=${assetType.value}&status=finished">${statusInfo.assetChildrenFinished[assetType.value]}</a></td>
	                  <c:if test="${acceptedEnabled}">
	                     <td><a
	                        href="AssetWorkflowAction.do?workflowNodetype=${assetType.value}&status=approved">${statusInfo.assetChildrenApproved[assetType.value]}</a></td>
	                  </c:if>
	                  <td><a
	                     href="AssetWorkflowAction.do?workflowNodetype=${assetType.value}&status=published">${statusInfo.assetChildrenPublished[assetType.value]}</a></td>
	               </tr>
               </c:if>
            </c:forEach>
			</tbody>
		</table>
		</div>
    </div>
		<div id="tree-page">
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
<%--            <c:if test="${statusInfo.pageDraft != 0 || statusInfo.pageFinished != 0 || statusInfo.pageApproved != 0 || statusInfo.pagePublished != 0}">
--%>
					<tr>
						<td class="leftData"><fmt:message key="workflow.status.page" /></td>
						<td class="indent" />
						<td class="indent" />
						<td class="indent2" />
						<td><a href="PageWorkflowAction.do?status=draft">${statusInfo.pageDraft}</a></td>
						<td><a href="PageWorkflowAction.do?status=finished">${statusInfo.pageFinished}</a></td>
						<c:if test="${acceptedEnabled}">
							<td><a href="PageWorkflowAction.do?status=approved">${statusInfo.pageApproved}</a></td>
						</c:if>
						<td><a href="PageWorkflowAction.do?status=published">${statusInfo.pagePublished}</a></td>
					</tr>
<%--            </c:if>
--%>
			</tbody>
		</table>
		</div>
		<div id="tree-link">
		<table class="centerData" cellspacing="0" cellpadding="0">
			<tbody>
<%--            <c:if test="${ statusInfo.linkFinished != 0 || statusInfo.linkApproved != 0 || statusInfo.linkPublished != 0}">
--%>
					<tr>
						<td class="leftData"><fmt:message key="workflow.status.link" /></td>
						<td class="indent" />
						<td class="indent" />
						<td class="indent2" />
						<td></td>
						<td><a href="LinkWorkflowAction.do?status=finished">${statusInfo.linkFinished}</a></td>
						<c:if test="${acceptedEnabled}">
							<td><a href="LinkWorkflowAction.do?status=approved">${statusInfo.linkApproved}</a></td>
						</c:if>
						<td><a href="LinkWorkflowAction.do?status=published">${statusInfo.linkPublished}</a></td>
					</tr>
<%--            </c:if>
--%>
			</tbody>
		</table>
		</div>
	</div>
	</cmscedit:sideblock></div>

	<div id="content"><mm:cloud jspvar="cloud" loginpage="login.jsp">
		<mm:import externid="status">draft</mm:import>
		<mm:import externid="results" jspvar="nodeList" vartype="List" />

		<div class="content">
		<div class="tabs" id="${status}">
		<a href="#" onclick="selectTab('draft','${workflowNodetype}');">
		<div class="<%=tabClass(pageContext,"draft")%>">
		<div class="body">
		<div class="title"><fmt:message
			key="workflow.tab.draft" /></div>
		</div>

		</div>
		</a>
	   <a href="#" onclick="selectTab('finished','${workflowNodetype}');">
		<div class="<%=tabClass(pageContext,"finished")%>">
		<div class="body">
		<div class="title"><fmt:message
			key="workflow.tab.finished" /></div>
		</div>
		</div>
		</a>
		<c:if test="${acceptedEnabled}">
		<a href="#" onclick="selectTab('approved','${workflowNodetype}');">
			<div class="<%=tabClass(pageContext,"approved")%>">
			<div class="body">
			<div class="title"><fmt:message
				key="workflow.tab.approved" /></div>
			</div>
			</div>
			</a>
		</c:if>
		<a href="#" onclick="selectTab('published','${workflowNodetype}');">
			<div class="<%=tabClass(pageContext,"published")%>">
			<div class="body">
			<div class="title"><fmt:message
				key="workflow.tab.published" /></div>
			</div>
			</div>
		</a>
		</div>
		</div>

		<div class="editor"><c:if test="${not empty errors}">
			<mm:import externid="errors" vartype="List" />

			<div class="messagebox_red">
			<div class="box">
			<div class="top">
			<div></div>
			</div>
			<div class="body">
			<p><fmt:message key="workflow.publish.failed" /></p>
			</div>
			<div class="bottom">
			<div></div>
			</div>
			</div>
			</div>
         <div class="body" style="background-color:#CC0000;">
         <form name="errorWorkflowForm" action='?' method="post" onsubmit="return submitValid(this, false);">
            <input type="hidden" name="orderby" value="${orderby}" /> 
            <input type="hidden" name="status" value="${status}" />
            <input type="hidden" name="workflowNodetype" value="${workflowNodetype}"/>
            <c:if test="${fn:length(errors) > 0}">
            <%@ include file="error_workflow_list_table_fragment.jsp"%>
            </c:if>
            &nbsp;&nbsp;&nbsp; <input type="checkbox" name="checkAll" onclick="checkAllElement(this, 'errorWorkflowForm', '')" /> <fmt:message key="workflow.select_all" />
            <input type="hidden" name="actionvalue" value="publish" /><br />
            <input type="submit" name="action" value="<fmt:message key="workflow.action.publish" />" style="background-color:#CC0000;"/>
         </form>
         </div>
		</c:if>
      <c:if test="${not empty param.errMessage}">
         <fmt:message key="${param.errMessage}" />
		</c:if>
		<div class="ruler_green">
			<div> 
		       <c:if test="${not empty workflowType and empty workflowNodetype}">
		         <fmt:message key="workflow.title.${workflowType}" />
		      </c:if>
		      <c:if test="${not empty workflowType and not empty workflowNodetype and not empty workflowNodetypeGUI}">
		         ${workflowNodetypeGUI}
		      </c:if>
	      </div>
		</div>

		<div class="body" style="display: none;" id="workflow-wait"><fmt:message
			key="workflow.wait" /></div>
		<div class="body" id="workflow-canvas"><c:set var="orderby"
			value="${param.orderby}" />
		<form name="workflowForm" action='?' method="post" onsubmit="return submitValid(this, false);">
         <input type="hidden" name="orderby" value="${orderby}" /> 
         <input type="hidden" name="status" value="${status}" />
         <input type="hidden" name="laststatus" />
         <input type="hidden" name="workflowNodetype" value="${workflowNodetype}"/>
      <c:set var="lastvalue" value='<%=request.getAttribute("laststatus")%>' />
      <c:set var="offset" value="${param.offset}" />
      <c:set var="listSize">${fn:length(nodeList)}</c:set>
      <c:set var="extraparams" value="&orderby=${orderby}&status=${status}&workflowNodetype=${workflowNodetype}"/>

		<c:if test="${fn:length(results) > 0}">
			<edit:pages search="false" totalElements="${listSize}" offset="${offset}" extraparams="${extraparams}"/>
			<%@ include file="workflow_list_table_fragment.jsp"%>
			<edit:pages search="false" totalElements="${listSize}" offset="${offset}" extraparams="${extraparams}"/>
		</c:if> <c:set var="remark">
			<fmt:message key="workflow.action.reject.remark" />
		</c:set> &nbsp;&nbsp;&nbsp; <input type="checkbox" name="checkAll"
			onclick="checkAllElement(this, 'workflowForm', '')" /> <fmt:message
			key="workflow.select_all" /> <input type="hidden" name="actionvalue"
			value="" /> <input type='hidden' id="remark" name="remark"
			value="[unchanged-item]" /> <br />
		<c:if test="${status == 'draft' }">
			<input name="action"
				value="<fmt:message key="workflow.action.finish" />"
				onclick="return setActionValue('finish')" type="submit" />
		</c:if> <c:if test="${status == 'finished' }">
			<input name="action"
				value="<fmt:message key="workflow.action.reject" />"
				onclick="return setActionValue('reject','','${remark}')"
				type="submit" />
			<c:if test="${acceptedEnabled}">
				<input name="action"
					value="<fmt:message key="workflow.action.accept" />"
					onclick="return setActionValue('accept')" type="submit" />
			</c:if>
			<input name="action"
				value="<fmt:message key="workflow.action.publish" />"
				onclick="return setActionValue('publish')" type="submit" />
		</c:if> <c:if test="${status == 'approved' }">
			<input name="action"
				value="<fmt:message key="workflow.action.reject" />"
				onclick="return setActionValue('reject','','${remark}')"
				type="submit" />
			<input name="action"
				value="<fmt:message key="workflow.action.publish" />"
				onclick="return setActionValue('publish')" type="submit" />
		</c:if> <c:if test="${status == 'published' }">
			<input name="action"
				value="<fmt:message key="workflow.action.reject" />"
				onclick="return setActionValue('reject','','${remark}')"
				type="submit" />
		</c:if></form>

		</div>
		</div>
	</mm:cloud></div>
	</body>
	</html:html>
</mm:content>
