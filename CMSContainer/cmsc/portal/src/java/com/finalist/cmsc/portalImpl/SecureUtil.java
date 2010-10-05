package com.finalist.cmsc.portalImpl;

import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.log4j.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.services.community.Community;

public class SecureUtil {
   private static final Logger log = Logger.getLogger(SecureUtil.class);
   
	public static boolean isAllowedToSee(NavigationItem item) {
	   return isReadable(Integer.toString(item.getId()));
	}

   public static String getEnvironment(String name, String defaultValue) {
      String value = null;
      Context env = null;
      Context context = null;
      try {
         context = new InitialContext();
         env = (Context) context.lookup("java:comp/env");
      } 
      catch (NamingException e) {
         log.error("Get env error"+e);
      }  
      try {
         if(env != null) {
            Map<?, ?> environment = env.getEnvironment();
            if (environment != null) {
               value = (String) environment.get(name);
            }
            if (value == null) {
               value = (String) env.lookup(name);
            }
         }
      } catch (NamingException e) {
         if(defaultValue == null) {
            log.error("Get environment "+name+" error :"+e);
         }
         else {
            return defaultValue;
         }
      }
      return value;
   }   

   public static boolean isReadable(String pageId) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node node = cloud.getNode(pageId);
      NodeList groups = node.getRelatedNodes("pagegroup");
      if(groups.size() == 0) {
         return true;
      }     
      for(NodeIterator i = groups.nodeIterator(); i.hasNext(); ) {
         Node group = i.nextNode();
         if(Community.hasAuthority(group.getStringValue("name"))) {
            return true;
         }
      }
      return false;
   }
}
