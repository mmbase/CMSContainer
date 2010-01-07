package com.finalist.cmsc.maintenance.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mmbase.bridge.BridgeException;

import com.finalist.cmsc.sql.SqlAction;

public class CheckIntegrity extends SqlAction {

   private int otype;
   
   private String nodeManager;
   
   private List corruptNodes;
   
   private String otypeNodeName;
   
   public CheckIntegrity(int otype, String nodeManager, List corruptNodes, String otypeNodeName) {
      this.otype = otype;
      this.nodeManager = nodeManager;
      this.corruptNodes = corruptNodes;  
      this.otypeNodeName = otypeNodeName;
   }
   @Override
   public String getSql() {
      String sql = "select number from "+this.getTable("object")+ " where otype="+otype+" and number not in(select number from "+this.getTable(nodeManager)+")";
      
      return sql;
   }

   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      while (rs.next()) {
         int nodeNumber = rs.getInt(getFieldname("number"));
         if(otypeNodeName.equalsIgnoreCase(nodeManager)) {
            corruptNodes.add(nodeManager+"("+nodeNumber+")");
         }
         else {
            corruptNodes.add(nodeManager+"("+nodeNumber+")("+otypeNodeName+")");
         }
      }
      return null;
   }

}
