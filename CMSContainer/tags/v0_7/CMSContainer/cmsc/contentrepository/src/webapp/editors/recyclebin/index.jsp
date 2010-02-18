<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="recyclebin.title" /></title>
  <link rel="stylesheet" type="text/css" href="../style.css" />
  <script src="recyclebin.js" type="text/javascript"></script>
  <script src="../utils/rowhover.js" type="text/javascript"></script>
</head>
<body>

<mm:cloud jspvar="cloud" rank="administrator" method='http'>
      <mm:import id="parentchannel" jspvar="parentchannel"><%= RepositoryUtil.ALIAS_TRASH %></mm:import>
      <mm:import jspvar="returnurl" id="returnurl">/editors/recyclebin/index.jsp</mm:import>

   <table style="width: 100%;">
      <tr>
         <td>
	         <fmt:message key="recyclebin.channel" >
	         	<fmt:param ><mm:node number="$parentchannel"><mm:field name="name"/></mm:node></fmt:param>
	         </fmt:message>
            <br />
         </td>
      </tr>
   </table>

	<form action="DeleteAction.do" method="post" onsubmit="return deleteAll('<fmt:message key="recyclebin.removeallconfirm" />');">
		<input type="hidden" name="action" value="deleteall" />
		<input type="submit" value="<fmt:message key="recyclebin.clear" />" />
	</form>
   
   <hr/>
      <fmt:message key="recyclebin.content" /><br/>

     <mm:import id="lastotype"/>
     
<mm:node number="$parentchannel">
<mm:relatednodescontainer path="contentrel,contentelement"
  searchdirs="destination" element="contentelement">
  <mm:sortorder field="contentelement.otype" direction="up" />
  <mm:sortorder field="contentelement.title" direction="up" />

  <mm:listnodes jspvar="node">

        <mm:field name="otype" write="false" id="otype"/>
        <mm:field name="number" write="false" id="number"/>

        <mm:compare referid="lastotype" value="" inverse="true">
           </tr>
        </mm:compare>
        <mm:compare referid="otype" referid2="lastotype" inverse="true">
           <mm:compare referid="lastotype" value="" inverse="true">
              </table>
           </mm:compare>

           <mm:node referid="otype">
              <br />
	         <fmt:message key="recyclebin.type" >
	         	<fmt:param><mm:field name="name" id="nodename"><mm:nodeinfo nodetype="$nodename" type="guitype"/></mm:field></fmt:param>
	         </fmt:message>
           </mm:node>
           <mm:import id="lastotype" reset="true"><mm:write referid="otype"/></mm:import>
           <mm:import id="newotype">true</mm:import>

           <table class="listcontent">
        </mm:compare>

        <mm:url page="../repository/showitem.jsp" id="url" write="false" >
           <mm:param name="objectnumber" value="$number"/>
        </mm:url>
        <tr class="itemrow" onMouseOver="objMouseOver(this);"
                    onMouseOut="objMouseOut(this);"
                    href="<mm:write referid="url"/>"><td onMouseDown="objClickPopup(this, 500, 500);">
           <mm:field name="number"/>
        </td>
        <td onMouseDown="objClickPopup(this, 500, 500);" width="100%">
           <mm:field name="title"/>
        </td>

		<% if (RepositoryUtil.hasDeletionChannels(node)) { %>
	      <td style="padding:0px">
	      	<a href="javascript:restore('<mm:field name="number" />');">
	      		<img src="../img/restore.gif" width="15" height="15" alt="<fmt:message key="recyclebin.restore" />"/>
	      	</a>
	      </td>
        <% } %>
        <td style="padding:0px">
        	<a href="javascript:permanentDelete('<mm:field name="number" />', '<fmt:message key="recyclebin.removeconfirm" />');">
        		<img src="../img/remove.gif" width="15" height="15" alt="<fmt:message key="recyclebin.remove" />"/>
        	</a>
        </td>
        <mm:present referid="newotype">
           <td></td>
        </mm:present>

        <mm:remove referid="newotype"/>

        <mm:last>
           <mm:compare referid="lastotype" value="" inverse="true">
              </tr></table>
           </mm:compare>
        </mm:last>

  </mm:listnodes>
</mm:relatednodescontainer>
</mm:node>

</mm:cloud>
</body>
</html:html>
</mm:content>