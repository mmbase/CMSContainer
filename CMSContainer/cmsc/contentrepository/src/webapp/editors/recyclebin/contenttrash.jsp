<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><%@page import="com.finalist.cmsc.repository.*" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="recyclebin.title">
  <script src="recyclebin.js" type="text/javascript"></script>
  <c:if test="${not empty fresh && fresh == 'true'}">
     <script type="text/javascript">     
       refreshChannels();
     </script>
  </c:if>
</cmscedit:head>
<mm:import externid="direction" jspvar="direction">up</mm:import>
<mm:import externid="sortBy" jspvar="sortBy">title</mm:import>
<c:set var="direction">
   <c:out value="${direction =='up'?'down':'up' }"/>
</c:set>
<c:set var="pagerDOToffset"><%=request.getParameter("pager.offset")%></c:set>
<body>
<div class="tabs">
    <!-- active TAB -->
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="contenttrash.jsp"><fmt:message key="recyclebin.content.title" /></a>
         </div>
      </div>
   </div>
   <div class="tab">
      <div class="body">
         <div>
                <a href="assettrash.jsp"><fmt:message key="recyclebin.asset.title"/></a>
         </div>
      </div>
   </div>
</div>

   <div class="editor">
      <mm:cloud jspvar="cloud" rank="basic user" method='http'>
               
         <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
            <mm:field name="number" jspvar="trashNumber" vartype="Integer">
            
               <cmsc:rights nodeNumber="<%=trashNumber.intValue()%>" var="rolename"/>
               <c:choose>
                  <c:when test="${rolename eq 'webmaster'}">

                     <mm:import id="parentchannel" jspvar="parentchannel"><%= RepositoryUtil.ALIAS_TRASH %></mm:import>
                     <mm:import jspvar="returnurl" id="returnurl">/editors/recyclebin/index.jsp</mm:import>
                     
                     <div class="body">
                         <p>
                             <fmt:message key="recyclebin.channel" />
                         </p>
                        <form name="deleteForm" action="DeleteAction.do" method="post">
                           <input type="hidden" name="action" value="deleteall" />
                           <input type="hidden" name="type" value="content" />
                           <ul class="shortcuts">
                                 <li class="trashbinempty">
                                 <a href="javascript:deleteAll('<fmt:message key="recyclebin.removeallconfirm" />');"><fmt:message key="recyclebin.clear" /></a>
                              </li>
                           </ul>
                        </form>
                        <div style="clear:both; height:10px;"></div>
                      </div>
                        
                     <div class="ruler_green">
                         <div><fmt:message key="recyclebin.content" /></div>
                     </div>
               
                     <div class="body">   
                      <mm:node number="$parentchannel">
                           <mm:relatednodescontainer path="contentrel,contentelement" searchdirs="destination" element="contentelement">
                             <mm:sortorder field="contentelement.${sortBy}" direction="${direction}" />
            
                              <c:set var="listSize"><mm:size/></c:set>
                              <c:set var="offset" value="${not empty param.offset ? param.offset : '0'}"/>
                              <c:set var="extraparams" value="&sortBy=${sortBy}&direction=${direction}"/>

                              <mm:listnodes jspvar="node" max="${resultsPerPage}" offset="${offset*resultsPerPage}">
                                 <mm:first>
                                   
                                 <edit:pages search="false" totalElements="${listSize}" offset="${offset}" extraparams="${extraparams}"/>
                                     <table>
                                       <thead>
                                          <tr>
                                             <th style="width: 56px;"></th>
                                             <th style="width: 68px;"><a href="contenttrash.jsp?sortBy=otype&direction=${direction}&offset=${offset}&pager.offset=${pagerDOToffset}" class="headerlink"><fmt:message key="locate.typecolumn" /></a></th>
                                             <th><a href="contenttrash.jsp?sortBy=title&direction=${direction}&offset=${offset}&pager.offset=${pagerDOToffset}" class="headerlink"><fmt:message key="locate.titlecolumn" /></a></th>
                                             <th style="width: 50px;"><a href="contenttrash.jsp?sortBy=creator&direction=${direction}&offset=${offset}&pager.offset=${pagerDOToffset}" class="headerlink"><fmt:message key="locate.authorcolumn" /></a></th>
                                             <th style="width: 120px;"><a href="contenttrash.jsp?sortBy=lastmodifieddate&direction=${direction}&offset=${offset}&pager.offset=${pagerDOToffset}" class="headerlink"><fmt:message key="locate.lastmodifiedcolumn" /></a></th>
                                             <th style="width: 60px;"><a href="contenttrash.jsp?sortBy=number&direction=${direction}&offset=${offset}&pager.offset=${pagerDOToffset}" class="headerlink"><fmt:message key="locate.numbercolumn" /></a></th>
                                          </tr>
                                       </thead>
                                       <tbody class="hover">
                                 </mm:first>
                           
                                 <tr <mm:even inverse="true">class="swap"</mm:even>>
                                    <td nowrap>
                                       <a href="javascript:infoContent('<mm:field name="number" />', '<mm:nodeinfo type="type"/>')"><img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="recyclebin.info" />" title="<fmt:message key="recyclebin.info" />"/></a>
                                       <a href="javascript:permanentDelete('<mm:field name="number" />', '<fmt:message key="recyclebin.removeconfirm" />', '${offset}', '${pagerDOToffset}', 'content');"><img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="recyclebin.remove" />" title="<fmt:message key="recyclebin.remove" />"/></a>
                                       <a href="javascript:restoreContent('<mm:field name="number" />', '${offset}', '${pagerDOToffset}');"><img src="../gfx/icons/restore.png" width="16" height="16" alt="<fmt:message key="recyclebin.restore" />" title="<fmt:message key="recyclebin.restore" />"/></a>
                                    </td>
                                    <td>
                                      <mm:nodeinfo type="guitype"/>
                                    </td>
                                    <td>
                                       <mm:field name="title"/>
                                    </td>
                                    <td>
                                       <mm:field name="lastmodifier" />
                                    </td>
                                    <td nowrap>
                                       <mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field>
                                    </td>
                                    <td>
                                       <mm:field name="number"/> 
                                    </td>
                                 </tr>
                           
                              <mm:last>
                                    </tbody>
                                 </table>
                                 <edit:pages search="false" totalElements="${listSize}" offset="${offset}" extraparams="${extraparams}"/>
                              </mm:last>
                          </mm:listnodes>
                        </mm:relatednodescontainer>
                     </mm:node>

                     </div>

                  </c:when>
                  <c:otherwise>
                     <div class="body">
                        <fmt:message key="recyclebin.no.access" />
                     </div>

                  </c:otherwise>
               </c:choose>
               
            </mm:field>
         </mm:node>

      </mm:cloud>
      <div class="side_block_end"></div>
   </div>   

</body>
</html:html>
</mm:content>