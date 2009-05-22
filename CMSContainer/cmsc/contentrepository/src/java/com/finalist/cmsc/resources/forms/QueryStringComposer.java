package com.finalist.cmsc.resources.forms;

import java.util.List;

import org.mmbase.bridge.NodeManager;

public class QueryStringComposer {

   private StringBuffer queryString = null;


   public void addParameter(String key, String value) {
      if (value == null || key == null) {
         return;
      }

      if (queryString == null) {
         queryString = new StringBuffer("?");
      }
      else {
         queryString.append("&");
      }

      queryString.append(key);
      queryString.append("=");
      queryString.append(value);
   }
   
   public void addParameterMap(String key, List<NodeManager> values) {
      if (values == null || key == null || values.size() <= 0) {
         return;
      }

      if (queryString == null) {
         queryString = new StringBuffer("?");
      }
      else {
         queryString.append("&");
      }

      if(values.size() == 1){
          queryString.append(key);
          queryString.append("=");
          queryString.append(values.get(0).getName());
      } else if(values.size() > 1) {
    	  queryString.append(key);
          queryString.append(" in(");
          queryString.append(values.get(0).getName());
          for(int i = 1; i < values.size(); i++){
        	  queryString.append("," + values.get(i).getName());
          }
          queryString.append(" )");
      }
   }

   public String getQueryString() {
      if (queryString != null) {
         return queryString.toString();
      }
      else {
         return "";
      }
   }
}
