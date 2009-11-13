package com.finalist.cmsc.maintenance.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeManagerList;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.maintenance.sql.CheckIntegrity;
import com.finalist.cmsc.sql.SqlExecutor;

public class CheckIntegrityBean {
   
   private Cloud cloud;
   
   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(CheckIntegrityBean.class.getName());

   public CheckIntegrityBean(Cloud cloud) {
      this.cloud = cloud;
   }
   
   public List execute() {
      NodeManagerList  nodeManagerList = cloud.getNodeManagers();
      List<String> corruptNodes = new ArrayList<String>();
      for(NodeManager nodeManager : nodeManagerList) {
         String nodeManagerName = nodeManager.getName();
         List<String> list = new ArrayList<String>();
         

         if (!"object".equalsIgnoreCase(nodeManagerName)) {
          //  NodeManager parentNodeManager = nodeManager.getParent();
           // list.add(nodeManager.getName());
            while(nodeManager != null ) {
               list.add(nodeManager.getName());
               try {
                  nodeManager = nodeManager.getParent();
               }
               catch (NotFoundException nfe) {
                  break;
               }
            }
            String otypeNodeManager = list.get(0);
            NodeManager typeDef = cloud.getNodeManager("typedef");
            NodeList nodeList = typeDef.getList("name='"+otypeNodeManager+"'", "", "");
            int otype = 0;
            if(!nodeList.isEmpty()) {
               otype = nodeList.getNode(0).getNumber();
            }
            log.debug("otype======"+otype);
            for (int i = 0 ; i < list.size()-1; i++) {
               String managerName = list.get(i);
               CheckIntegrity checkIntegrity = new CheckIntegrity(otype,managerName,corruptNodes,otypeNodeManager);
               new SqlExecutor().execute(checkIntegrity);
            }         
         }
      }
     
      return corruptNodes;
   }
}
