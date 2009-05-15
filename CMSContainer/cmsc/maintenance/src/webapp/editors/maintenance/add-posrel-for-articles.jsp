<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@ page import="org.mmbase.bridge.RelationManager"%>
<%@ page import="org.mmbase.bridge.NodeQuery"%>
<%@ page import="org.mmbase.bridge.NodeList"%>
<%@ page import="org.mmbase.bridge.Node"%>
<%@page import=" com.finalist.cmsc.mmbase.RelationUtil"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>Create new relation</title>
</head>
    <body>
       <h2>Add posrels</h2>
       <p>Add posrels between the existing content elements: add 1 posrel so that every element has 2 posrels as a result.
       <br/>Node : Related with issue CMSC-1020
       </p>
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<form method="post">
	<input type="hidden" name="action" value="createrelation"/> 
	<input type="submit" value="Create"/>
</form>
<c:if test="${not empty param.action}">
   <%
      RelationManager relManager = cloud.getRelationManager("posrel");
      NodeQuery query = relManager.createQuery();
      NodeList relations = query.getList();
      int count = 0 ;
      for(int i = 0 ; i < relations.size() ; i++) {
         Node relation = relations.getNode(i);
         int sNumber = relation.getIntValue("snumber");
         int dNumber = relation.getIntValue("dnumber");
         Node sNode = cloud.getNode(sNumber);
         Node dNode = cloud.getNode(dNumber);
           
         if("article".equalsIgnoreCase(sNode.getNodeManager().getName()) && "article".equalsIgnoreCase(dNode.getNodeManager().getName())) {
            NodeList counterRelations = RelationUtil.getRelations(cloud.getNodeManager("posrel"), dNumber, sNumber);
            if(counterRelations.size() < 1) {
               RelationUtil.createRelation(dNode, sNode, "posrel");
               count++;
            }
         }
      }
      out.println("Done!  "+count+" relation(s) be created!");
   %>
</c:if>
</mm:cloud>
</body>
</html>