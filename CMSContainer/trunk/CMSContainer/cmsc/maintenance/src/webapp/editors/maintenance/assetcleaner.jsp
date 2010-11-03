<%@page language="java" contentType="text/html;charset=UTF-8" %>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<%@ page import="com.finalist.cmsc.maintenance.sql.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html:html xhtml="true">
    <cmscedit:head title="maintenance.assetcleaner">
        <link href="style.css" type="text/css" rel="stylesheet"/>
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script type="text/javascript">
        $(document).ready(function() {
            $("#checkall").click(function() {
                var checked_status = this.checked;
                $(".deletebox").each(function() {
                    this.checked = checked_status;
                });
            });                    
        });
        </script>
    </cmscedit:head>

    <body>
        <mm:cloud jspvar="cloud" loginpage="../login.jsp" rank="administrator">
            <div class="tabs">
                <a href="#">
                    <div class="tab_active">
                        <div class="body">
                            <div class="title"><fmt:message key="maintenance.assetcleaner" /></div>
                        </div>
                    </div>
                </a>
            </div>

            <div class="editor">
                <div class="body">

<%
  String[] nodes = request.getParameterValues("delete");
  if (nodes != null) {
    for (int n = 0; n < nodes.length; n++) {
      String nid = nodes[n];
      org.mmbase.bridge.Node node = cloud.getNode(nid);
      String type = node.getNodeManager().getName();
      String title = node.getStringValue("title");
      node.deleteRelations();
      node.delete();
      out.println("Verwijderd " + type + ": " + title + "<br />");
    }
  }
%>

<mm:import externid="offset" jspvar="offset">0</mm:import>
<form method="post">
  Offset <input type="text" name="offset" value="${offset}"/>
  <input type="submit" name="action" value="Refresh"/><br/>
</form>

        <form method="post" action="#">
            <br/>
            <table>
                <tr><th colspan="3">Bijlagen</th></tr>
                <tr>
                    <th><input type="checkbox" name="checkall" id="checkall" /></th>
                    <th>Titel</th>
                    <th width="100" align="right">Grootte</th>
                </tr>
                <mm:listnodes type="attachments" orderby="size" directions="DOWN" max="150" offset="${offset}">
                    <mm:countrelations type="contentelement" write="false" jspvar="relations" />
                    <mm:compare referid="relations" value="0">
                        <mm:field name="number" write="false" jspvar="number" />
                        <tr>
                            <td><input type="checkbox" name="delete" value="${number}" class="deletebox" /></td>
                            <td>
                                <a href="#" onclick="window.open('../resources/attachmentinfo.jsp?objectnumber=${number}','details','toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=500,height=500,left=430,top=23'); return false;">
                                  <mm:field name="title">
                                    <mm:isnotempty>
                                      <mm:write />
                                    </mm:isnotempty>
                                    <mm:isempty>
                                      Geen titel
                                    </mm:isempty>
                                </mm:field>
                            </a>
                            </td>
                            <td align="right"><mm:field name="size" /> bytes</td>
                        </tr>
                    </mm:compare>
                </mm:listnodes>

                <tr><td colspan="3">&nbsp;</td></tr>
                <tr><th colspan="3">Afbeeldingen</th></tr>

                <mm:listnodes type="images" orderby="filesize" directions="DOWN" max="150" offset="${offset}">
                    <mm:countrelations type="contentelement" write="false" jspvar="relations" />
                    <mm:compare referid="relations" value="0">
                        <mm:field name="number" write="false" jspvar="number" />
                        <tr valign="top">
                            <td><input type="checkbox" name="delete" value="${number}" class="deletebox" /></td>
                            <td>
                                <a href="#" onclick="window.open('../resources/imageinfo.jsp?objectnumber=${number}','details','toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=500,height=500,left=430,top=23'); return false;">
                                  <img src="<mm:image width="100" />" style="float: left;" />&nbsp;&nbsp;&nbsp;
                                  <mm:field name="title">
                                    <mm:isnotempty>
                                      <mm:write />
                                    </mm:isnotempty>
                                    <mm:isempty>
                                      Geen titel
                                    </mm:isempty>
                                </mm:field>
                                </a>
                            </td>
                            <td align="right"><mm:field name="filesize" /> bytes</td>
                        </tr>
                    </mm:compare>
                </mm:listnodes>

            </table>
            <input type="submit" name="action" value="Verwijderen"/>
        </form>

                </div>
            </div>
        </mm:cloud>

    </body>
</html:html>
</mm:content>