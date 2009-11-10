/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.mmbase;

import java.util.*;

import org.mmbase.cache.Cache;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public final class TypeUtil {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(TypeUtil.class.getName());
   
   private TypeUtil() {
      // utility
   }

   private static final List<String> mmbaseTypes = new ArrayList<String>();
   private static final List<String> securityTypes = new ArrayList<String>();
   private static final List<String> publishTypes = new ArrayList<String>();

   private static final List<String> systemTypes = new ArrayList<String>();
   static {
      mmbaseTypes.add("typedef");
      mmbaseTypes.add("reldef");
      mmbaseTypes.add("typerel");
      mmbaseTypes.add("mmservers");
      mmbaseTypes.add("oalias");
      mmbaseTypes.add("daymarks");
      mmbaseTypes.add("syncnodes");
      mmbaseTypes.add("icaches");
      mmbaseTypes.add("versions");

      securityTypes.add("user");
      securityTypes.add("mmbasegroups");
      securityTypes.add("mmbaseranks");
      securityTypes.add("mmbaseusers");
      securityTypes.add("mmbasecontexts");
      securityTypes.add("rightsrel");

      publishTypes.add("remotenodes");
      publishTypes.add("cloud");
      publishTypes.add("publishqueue");

      systemTypes.addAll(mmbaseTypes);
      systemTypes.addAll(securityTypes);
      systemTypes.addAll(publishTypes);

      systemTypes.add("editwizards");
      systemTypes.add("workflowitem");
      systemTypes.add("properties");

      systemTypes.add("cronjobs");
      systemTypes.add("email");
   }


   public static boolean isMmbaseType(String name, boolean includeRootTypes) {
      if (includeRootTypes && ("object".equals(name) || "insrel".equals(name))) {
         return true;
      }
      return mmbaseTypes.contains(name);
   }


   public static boolean isMmbaseType(String name) {
      return isMmbaseType(name, false);
   }


   public static boolean isSecurityType(String name) {
      return securityTypes.contains(name);
   }


   public static boolean isPublishType(String name) {
      return publishTypes.contains(name);
   }


   public static boolean isSystemType(String name, boolean includeRootTypes) {
      if (includeRootTypes && ("object".equals(name) || "insrel".equals(name))) {
         return true;
      }
      return systemTypes.contains(name);
   }


   public static boolean isSystemType(String name) {
      return isSystemType(name, false);
   }

   public static  void fillTypeCache(MMObjectBuilder builder) {
      MMBase mmbase = builder.getMMBase();
      try {
         Cache typeCache = Cache.getCache("TypeCache");
         if (typeCache != null) {
            BasicSearchQuery query = new BasicSearchQuery();
            Step step = query.addStep(builder);
            query.addField(step, builder.getField("number"));
            query.addField(step, builder.getField("otype"));
            
            List nodes = mmbase.getSearchQueryHandler().getNodes(query, new ResultBuilder(mmbase, query));
            if (nodes != null) {
               for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
                  MMObjectNode tempNode = (MMObjectNode) iterator.next();
                  Integer otype = Integer.valueOf(tempNode.getIntValue(MMObjectBuilder.FIELD_OBJECT_TYPE));
                  Integer number = Integer.valueOf(tempNode.getNumber());
                  typeCache.put(number, otype);
               }
            }
         }
      }
      catch (SearchQueryException e) {
         log.info("failed to preload typeCache fir " + builder.getTableName(), e);
      }
   }
}
