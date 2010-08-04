<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<c:set var="TEXTBOX">1</c:set>
<c:set var="SELECT">2</c:set>

<mm:cloud method="asis">
    <mm:node number="${portletId}" notfound="skip">
        <mm:relatednodes type="portletparameteroption" role="parameterrel">

            <mm:field name="key" jspvar="key" write="false" />
            <mm:field name="type" jspvar="type" write="false" />
            <mm:field name="value" jspvar="value" write="false" />

            <mm:import externid="${key}" from="request" id="storedValue" reset="true" />

            <mm:first>
                <tr>
                    <td colspan="3">
                        <h4><fmt:message key="edit_defaults.parameters" /></h4>
                    </td>
                </tr>
            </mm:first>
            <tr>
                <td><mm:field name="label" /></td>
                <td>&nbsp;</td>
                <td>
                    <c:if test="${type eq TEXTBOX}">
                        <input type="text" name="nodeparam_${key}" value="${storedValue}" />
                    </c:if>
                    <c:if test="${type eq SELECT}">
                        <c:set var="values" value="${fn:split(value, ',')}" />
                        <select name="nodeparam_${key}">
                        <c:forEach var="item" items="${values}">
                            <c:set var="option" value="${fn:split(item, '=')}" />
                            <c:choose>
                                <c:when test="${option[1] eq storedValue}">
                                    <option selected="selected" value="${option[1]}">${option[0]}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${option[1]}">${option[0]}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        </select>
                    </c:if>
                </td>
            </tr>
        </mm:relatednodes>
    </mm:node>
</mm:cloud>