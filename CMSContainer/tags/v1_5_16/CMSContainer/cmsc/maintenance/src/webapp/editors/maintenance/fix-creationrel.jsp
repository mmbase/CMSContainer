<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<%--
::: Find and fix contentelements that lack creationrel and/or contentrel :::
For all contentelements without a deletionrel this script fix elements that lack cretionrel and/or contentrel.

* Items without both creationrel and contentrel will be put in a special LostAndFound channel.
 - This channel will be made (under the repo.) automatically if needed.
* Items without creationrel will have its cretionrel set to the first listed contentrel.
* Items without a contentrel will have its contentrel set to the creationrel. 
--%>	
	<head>
		<title>Fix contentelements without creationrel and/or contentrel</title>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
      <link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
      <link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
	</head>
	<body>
	<c:choose>
	   <c:when test="${not empty param.run}">
   <mm:cloud jspvar="cloud" rank="basic user" method="http">
   <mm:node number="repository.root">
      <c:set var="rootNumber"><mm:field name="number"/></c:set>
   </mm:node>
   <mm:node number="repository.trash">
      <c:set var="trashNumber"><mm:field name="number"/></c:set>
   </mm:node>
   <mm:node number="repository.lostAndFound" notfound="skip"  id="lostAndFoundNode">
      Found: lost and found channel<br/>
      <c:set var="lostAndFoundNumber"><mm:field name="number"/></c:set>
   </mm:node>

   
   <mm:listnodes type="contentelement">
      <c:set var="number"><mm:field name="number"/></c:set>
      <c:set var="nodeTitle"><mm:field name="title"/></c:set>
      
      <c:set var="hasCreationrel" value="${false}"/>
      <c:set var="hasContentrel" value="${false}"/>
      <c:set var="hasDeletionrel" value="${false}"/>
      
      <mm:relatednodes role="deletionrel" type="contentchannel" searchdir="destination" max="1">
         <c:set var="hasDeletionrel" value="${true}"/>
         <c:set var="DeletionRelNumber"><mm:field name="number"/></c:set>
      </mm:relatednodes>
      
      <mm:relatednodes role="creationrel" type="contentchannel" searchdir="destination" max="1">
         <c:set var="hasCreationrel" value="${true}"/>
         <c:set var="creationRelNumber"><mm:field name="number"/></c:set>
      </mm:relatednodes>
      
      <mm:relatednodes role="contentrel" type="contentchannel" searchdir="source" max="1">
         <c:set var="hasContentrel" value="${true}"/>
         <c:set var="contentRelNumber"><mm:field name="number"/></c:set>
      </mm:relatednodes>
      
      <mm:node number="${number}" id="currentNode"/>
      <%-- Not intersted in deleted contentelement --%>
      <c:if test="${!hasDeletionrel}">
         
         <%-- checks for missing creationrel and missing contentrel, then we but them in LostAndFound --%>
         <c:if test="${!hasCreationrel && !hasContentrel}">
            <%-- Checks if we have a lostandfoundchannel, if not then we make it --%>
            <c:if test="${empty lostAndFoundNumber}">
               No lost and found channel, creating<br/>
               <mm:createnode id="lostAndFoundNode" type="contentchannel">
                  <mm:setfield name="name">Lost & found</mm:setfield>
               </mm:createnode>
               <mm:node number="${rootNumber}" id="rootNode"/>
               <mm:createrelation role="childrel" source="rootNode" destination="lostAndFoundNode" />
               <mm:node referid="lostAndFoundNode">
                  <mm:createalias>repository.lostAndFound</mm:createalias>
                  <c:set var="lostAndFoundNumber"><mm:field name="number"/></c:set>
               </mm:node>
            </c:if>
            
            Item with title: ${nodeTitle} - Did not have a creationrel, nor a contentrel and is moved to LostAndFound<br >
            <mm:createrelation role="creationrel" source="lostAndFoundNode" destination="currentNode" />
            <mm:createrelation role="contentrel" source="lostAndFoundNode" destination="currentNode" />
            <br/>
         </c:if>
         
         <%-- checks for missing creationrel but found a contentrel --%>
         <c:if test="${!hasCreationrel && hasContentrel}">
            Item with title: ${nodeTitle}  - Did not have a creationrel. Creationrel is now set to its contentrel<br >
            <mm:node number="${contentRelNumber}" id="contentrelNode"/>
            <mm:createrelation role="creationrel" source="contentrelNode" destination="currentNode" /> 
         </c:if>
         
         <%-- checks for missing creationrel but found a contentrel --%>
         <c:if test="${hasCreationrel && !hasContentrel}">
            Item with title: ${nodeTitle} - Did not have a contentrel. Contentrel is now set to its creationrel<br >
            <mm:node number="${creationRelNumber}" id="creationrelNode"/>
            <mm:createrelation role="contentrel" source="creationrelNode" destination="currentNode" />
         </c:if>
      
      <%-- end if !hasDeletionrel --%>
      </c:if>
      
   </mm:listnodes>

</mm:cloud>
Done<br>
	   </c:when>
	   <c:otherwise>
         <h1>Find and fix contentelements that lack creationrel and/or contentrel</h1>
         <p>For all contentelements without a deletionrel this script fix elements that lack cretionrel and/or contentrel.</p>

	     <ol>
           <li>Items without both creationrel and contentrel will be put in a special LostAndFound channel.<br/>
               - This channel will be made (under the repo.) automatically if needed.</li>
           <li>Items without creationrel will have its cretionrel set to the first listed contentrel.</li>
           <li>Items without a contentrel will have its contentrel set to the creationrel.</li>
	     </ol>
	     <a href="?run=true">start</a>
	   </c:otherwise>
	</c:choose>
	</body>
</html:html>
</mm:content>