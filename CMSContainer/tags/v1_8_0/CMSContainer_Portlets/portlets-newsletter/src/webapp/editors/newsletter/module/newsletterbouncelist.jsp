<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="newsletter.bounce.title">
  <c:url var="actionUrl" value="/editors/newsletter/module/NewsletterBounceAction.do"/>
  <script type="text/javascript" src="../newsletter.js"></script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
  <edit:ui-tabs>
	 <c:if test="${not empty sessionScope.bounce_page_from && sessionScope.bounce_page_from != 'mainboard'}">
		 <edit:ui-tab key="newsletter.publication.tabs.edit" >
			../NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}
		 </edit:ui-tab>
		 <edit:ui-tab key="newsletter.publication.tabs.statistics" >
			../NewsletterPublicationStatisticSearch.do?newsletterId=${requestScope.newsletterId}
		 </edit:ui-tab>
		 <edit:ui-tab key="newsletter.publication.tabs.subscribers">
			../NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}
		 </edit:ui-tab>
	 </c:if>
	 <edit:ui-tab key="newsletter.publication.tabs.bounces" active="true">
		#
	 </edit:ui-tab>
	 <c:if test="${not empty sessionScope.bounce_page_from && sessionScope.bounce_page_from != 'mainboard'}">
		 <edit:ui-tab key="newsletter.publication.tabs.terms">
		   ../NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}
		</edit:ui-tab>
	 </c:if>
  </edit:ui-tabs>

<div class="editor" style="height:500px">

<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
<form action="${actionUrl}" name="termForm" method="post">
<input type="hidden" name="method" value="list"/>
<input type="hidden" name="offset" value="${offset}"/>
<input type="hidden" name="pager.offset" value="${pagerDOToffset}"/>
<input type="hidden" name="direction" value="${direction}"/>
<input type="hidden" name="order" value="${order}"/>
<input type="hidden" name="type" value="${action}"/>
<mm:import jspvar="resultCount" vartype="Integer">${resultCount}</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">${offset}</mm:import>
<mm:import externid="direction" jspvar="direction" vartype="String">${direction}</mm:import>
<mm:import externid="order" jspvar="order" vartype="String">${order}</mm:import>
<br/>
 <div style="margin-left:10px;margin-right:50px;font-size:12px;float:left"><fmt:message key="newsletter.bounce.newsletter.title"/></div>
 <cmsc:select var="newsletterId"   default="${newsletterId}" onchange="document.forms[0].submit();">
   <c:if test="${not empty sessionScope.bounce_page_from && sessionScope.bounce_page_from == 'mainboard'}">
  <option value="all"><fmt:message key="newsletter.bounce.newsletter.all"/></option>
   </c:if>

  <mm:listnodes type="newsletter" orderby="subject">
	 <mm:field name="number" id="newsletternumber" write="false" vartype="String" />
	 <cmsc:option value="${newsletternumber}" name="${_node.title}" />
  </mm:listnodes>
  </cmsc:select>
  <div class="ruler_green"><div><fmt:message key="newsletter.term.search.result" /></div></div>
<div class="body">
<c:if test="${resultCount > 0}">
<edit:pages search="true" totalElements="${resultCount}" offset="${offset}"/>

	<input type="button" class="button" style="margin-top:10px;margin-left:6px;margin-bottom:10px" value='<fmt:message key="newsletter.bounce.delete.bounce"/>' onclick="bounceDelete('<fmt:message key="newsletter.bounce.delete.confirm" />', 'termForm','bounce')"/>
	<input type="button" class="button" style="margin-top:10px" value='<fmt:message key="newsletter.bounce.delete.member"/>' onclick="bounceDelete('<fmt:message key="newsletter.bounce.delete.confirm" />', 'termForm','member')"/>

   <table>
      <tr class="listheader">
	  	<th width="80px"> <c:if test="${resultCount >1}">
        <input type="checkbox"  name="selectall" class="checkbox" onclick="selectAll(this.checked, 'termForm', 'chk_');" value="on" style="margin:0px 0px 0px 4px !important;> margin:0px 0px !important;margin:0px 0px;"/><span style="padding-left:4px;position:absolute;text-transform:none"><fmt:message key="newsletter.bounce.selectall" /></span>
        </c:if></th>
         <th><a href="javascript:sortBy('userName')"> <fmt:message key="newsletter.bounce.subscriber" /></a> </th>
         <th><a href="javascript:sortBy('newsLetterTitle')"><fmt:message key="newsletter.bounce.newsletter" /></a></th>
         <th><a href="javascript:sortBy('bouncedate')"><fmt:message key="newsletter.bounce.bouncedate" /></a></th>
         <th><a href="javascript:sortBy('content')"><fmt:message key="newsletter.bounce.bouncecontent" /></a></th>
      </tr>
      <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
                <c:forEach var="bounce" items="${resultList}" >
               <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
			   	    <td>
					  <c:if test="${resultCount >1}">
					  <input type="checkbox"  name="chk_items" class="checkbox" value="${bounce.id}" onClick="document.forms['termForm'].elements.selectall.checked=false;"/>
					  </c:if>
                   <td >
                   <td >
                   <c:out  value="${bounce.userName}"/> 
                   </td>
                   <td>
                   <c:out  value="${bounce.newsLetterTitle}"/>
                   </td>
                   <td >
                   <c:out  value="${bounce.bounceDate}"/> 
                   </td>
                   <td>
                      <a href="javascript:showItem('${bounce.id}')">
                         <c:out  value="${fn:length(bounce.bounceContent) >50?fn:substring(bounce.bounceContent,0,60):bounce.bounceContent}"/>
                      </a>
                   </td>
               </tr>
           <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
        	</c:forEach>
      </tbody>
   </table>
</c:if>
</form>
</div>
<c:if test="${resultCount == 0}">
<fmt:message key="newsletter.bounce.noresult" />
</c:if>
<c:if test="${resultCount > 0}">
<edit:pages search="true" totalElements="${resultCount}" offset="${offset}"/>
</c:if>
</mm:cloud>
</body>
</html:html>
</mm:content>