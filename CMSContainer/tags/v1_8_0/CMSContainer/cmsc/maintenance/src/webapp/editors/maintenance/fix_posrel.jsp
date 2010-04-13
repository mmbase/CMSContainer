<%@page import="org.mmbase.bridge.*,org.mmbase.util.logging.*"%>
<%@page import="java.util.*"%>
<%@include file="globals.jsp"%>
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
   <title>Fix posrels which are null</title>
   <link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
</head>
<body>
   <div class="editor">
   <div class="body">
   <h2>Fix posrels which are null</h2>
   <mm:cloud rank="administrator">
      <mm:listnodes type="posrel" constraints="pos is null">
         <mm:first>Number of nodes: <mm:size />
            <hr />
         </mm:first>
         <mm:setfield name="pos">1</mm:setfield>
      </mm:listnodes>
   </mm:cloud>
   <br/> 
   Done!<br/>
   </div>
   </div>
</body>
</html>