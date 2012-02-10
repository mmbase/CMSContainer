package com.finalist.cmsc.maintenance.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;

import com.finalist.cmsc.sql.SqlAction;

public class DoublePortlets extends SqlAction {

   public DoublePortlets() {
   }

   @Override
   public String getSql() {
      // select page.number, portletrel.name, count(*) as cnt from mm_page as page, mm_portletrel as portletrel where portletrel.snumber = page.number group by page.number, portletrel.name having count(*) > 1;
      return "SELECT page.number, portletrel.name, count(*) as cnt FROM " + getTable("page") + " as page, " + getTable("portletrel")
            + " as portletrel " + " WHERE portletrel.snumber = page.number GROUP BY page.number, portletrel.name having count(*) > 1";
   }

   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuilder result = new StringBuilder();
      int records = 0;
      while (rs.next()) {
         records++;
         int nodeNumber = rs.getInt(getFieldname("number"));
         String portletName = rs.getString(getFieldname("name"));
         int cnt = rs.getInt(getFieldname("cnt"));
         result.append(" page-number:" + nodeNumber + ", portlet name: " + portletName + ", count=" + cnt + "<br />");
      }
      result.append("<br/>Number of page nodes with double portlets at same position found = " + records);
      return result.toString();
   }

}
