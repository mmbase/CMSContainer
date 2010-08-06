<?xml version="1.0" encoding="UTF-8" ?>
<%@page language="java" contentType="text/xml; charset=utf-8" 
%><%@page session="false" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/xml" encoding="UTF-8">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="title" jspvar="title" />
<mm:cloud jspvar="cloud" >
<rss version="2.0">
    <channel>
        <title>${title}</title>
        <link>${link}</link>
        <language>nl</language>
        <description>${description}</description>
        <c:if test="${not empty copyright}">
            <copyright>${copyright}</copyright>
        </c:if>
        <c:if test="${not empty managingEditor}">
         <managingEditor>${managingEditor}</managingEditor>
        </c:if>
        <c:if test="${not empty webMaster}">
         <webMaster>${webMaster}</webMaster>
        </c:if>
        <mm:listnodes referid="results">
           <mm:first>
              <mm:relatednodes type="images" role="imagerel" searchdirs="destination" orderby="imagerel.pos" max="1">
                  <image>
                     <url>${pageContext.request.scheme}://${pageContext.request.serverName}<mm:image template="s(144x400>)" mode="url"/></url>
                     <title><mm:field name="title"/></title>
                     <link><cmsc:contenturl absolute="true" /></link>
                  </image>
               </mm:relatednodes>
            </mm:first>
            <item>
               <title><mm:field name="title"/></title>
               <link><cmsc:contenturl absolute="true" id="contentUrl"/></link>
               <mm:field name="intro" jspvar="intro" write="false"/>
               <cmsc:removehtml var="cleanIntro" html="${intro}"/>
               <description><c:out value="${cleanIntro}" escapeXml="true"/></description>
               <pubDate><mm:field name="publishdate"><mm:time format="rfc822" /></mm:field></pubDate>
               <guid><cmsc:contenturl absolute="true" /></guid>
             </item>
        </mm:listnodes>
    </channel>
</rss>
</mm:cloud>
</mm:content>