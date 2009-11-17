package com.finalist.cmsc.maintenance.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mmbase.bridge.BridgeException;

import com.finalist.cmsc.sql.SqlAction;

public class ShowDatabaseProcessList extends SqlAction {

   private Map<Integer,List> map;
   public ShowDatabaseProcessList(Map map) {
      this.map = map;
   }
   @Override
   public String getSql() {
      return "show processlist;";
   }

   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      while(rs.next()) {
         List record = new ArrayList();
         record.add(rs.getInt("Id"));
         record.add(rs.getString("User"));
         record.add(rs.getString("Host"));
         record.add(rs.getString("db"));
         record.add(rs.getString("Command"));
         record.add(rs.getLong("Time"));
         record.add(rs.getString("State"));
         record.add(rs.getString("Info"));
         map.put(rs.getInt("Id"), record);
      }
      return null;
   }

}
