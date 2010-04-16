<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@page import="org.mmbase.util.LRUHashtable.LRUEntry"%>

<c:set var="isProduction"><%=com.finalist.cmsc.util.ServerUtil.isProduction()%></c:set>

<mm:cloud jspvar="cloud">

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
        <head>
            <title>Support cleaner</title>
            <meta http-equiv="pragma" value="no-cache" />
            <meta http-equiv="expires" value="0" />
            <link href="../style.css" type="text/css" rel="stylesheet" />
        </head>
        <body class="basic">
        
            <%-- Only available when not on production --%>
            <c:if test="${isProduction eq 'false'}">
            
                <%-- When the sazve button is pressed --%>
                <mm:import externid="save" from="parameters" />
                <mm:present referid="save">
                
                    <%-- Update URL fragments --%>
                    <mm:listnodes type="site">
                        <mm:field name="title" write="false" jspvar="title" />
                        <mm:import externid="${_node.number}_urlfragment" from="parameters" id="urlfragment" reset="true" />
                        <mm:present referid="urlfragment">
                            <c:if test="${not empty urlfragment}">
                                <mm:setfield name="urlfragment">${urlfragment}</mm:setfield>
                                <p>Set URL fragment of ${title} to ${urlfragment}</p>
                            </c:if>
                        </mm:present>
        
                        <mm:import externid="${_node.number}_stagingfragment" from="parameters" id="stagingfragment" reset="true" />
                        <mm:present referid="stagingfragment">
                            <c:if test="${not empty stagingfragment}">
                                <mm:setfield name="stagingfragment">${stagingfragment}</mm:setfield>
                                <p>Set staging fragment of ${title} to ${stagingfragment}</p>
                            </c:if>
                        </mm:present>
                    </mm:listnodes>
        
                    <%-- Change the email of dynamic forms --%>
                    <mm:import externid="email" from="parameters" id="email" />
                    <mm:present referid="email">
                        <c:if test="${not empty email}">
                            <mm:listnodes type="responseform">
                                <mm:field name="title" write="false" jspvar="title" />
                                <mm:setfield name="emailaddresses">${email}</mm:setfield>
                                <p>Set emailaddress of ${title} to ${email}</p>
                            </mm:listnodes>
                        </c:if>
                    </mm:present>
                </mm:present>
                
                <%-- List all sites --%>
                <form method="post" action="supportcleaner.jsp" name="save">
                    <h3>URL's</h3>
                    <table>
                        <mm:listnodes type="site">
                            <mm:import externid="${_node.number}_urlfragment" from="parameters" id="urlfragment" reset="true" />
                            <mm:import externid="${_node.number}_stagingfragment" from="parameters" id="stagingfragment" reset="true" />
                            <tr>
                                <th colspan="3" align="left" colspan="2"><mm:field name="title" />
                                </td>
                            </tr>
                            <tr>
                                <td>URL fragment</td>
                                <td><mm:field name="urlfragment" /></td>
                                <td><input type="text" name="${_node.number}_urlfragment" value="${urlfragment}" /></td>
                            </tr>
                            <tr>
                                <td>Staging fragment</td>
                                <td><mm:field name="stagingfragment" /></td>
                                <td><input type="text" name="${_node.number}_stagingfragment" value="${stagingfragment}" /></td>
                            </tr>
                            <tr>
                                <td>Google Analytics ID</td>
                                <td><mm:field name="googleanalyticsid" /></td>
                                <td><input type="text" name="${_node.number}_googleanalyticsid" /></td>
                            </tr>
                            <tr>
                                <td colspan="3">&nbsp;</td>
                            </tr>
                        </mm:listnodes>
                    </table>
                    <br />
            
                    <h3>E-mail</h3>
                    <table>
                        <tr>
                            <td>E-mail for dynamic forms</td>
                            <td><input type="text" name="email" value="support@finalist.com" /></td>
                        </tr>
                    </table>
                    <br />
                    <table>
                        <tr>
                            <td><input type="submit" name="save" value="Save" /></td>
                        </tr>
                    </table>
                </form>
            </c:if>
        
            <%-- If production, not available --%>
            <c:if test="${isProduction eq 'true'}">
                <p>This script is not available on production servers for safety reasons.</p>
            </c:if>
        </body>
    </html>
</mm:cloud>