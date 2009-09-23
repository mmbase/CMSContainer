package com.finalist.util.version;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VersionUtil {

   private static final String CMSC_PORTAL_START = "cmsc-portal";
   // check against the editwizard jar, because otherwise we will get a
   // version starting with email-1. etc
   private static final String MMBASE_START = "mmbase";

   private static Log log;
   private static String applicationVersion;
   private static String cmscVersion;
   private static String mmbaseVersion;
   private static Map<String, String> libVersions;

   protected static Log getLogger() {
      if (log == null) {
         log = LogFactory.getLog(VersionUtil.class);
      }
      return log;
   }

   public static synchronized String getCmscVersion(ServletContext servletContext) {
      if (cmscVersion == null) {
         if (libVersions == null) {
            initLibVersions(servletContext);
         }
         cmscVersion = libVersions.get(CMSC_PORTAL_START);
      }
      return cmscVersion;
   }

   public static synchronized String getMmbaseVersion(ServletContext servletContext) {
      if (mmbaseVersion == null) {
         if (libVersions == null) {
            initLibVersions(servletContext);
         }
         mmbaseVersion = libVersions.get(MMBASE_START);
      }
      return mmbaseVersion;
   }

   private static synchronized void initLibVersions(ServletContext servletContext) {

      Set<String> paths = servletContext.getResourcePaths("/WEB-INF/lib");
      libVersions = new TreeMap<String, String>();
      for (String path : paths) {

         int start = path.lastIndexOf("/") + 1;
         int end = path.lastIndexOf("-");

         if (start != -1 && end != -1 && end > start) {
            // Check if the right version part is found
            if (path.charAt(end - 1) >= '0' && path.charAt(end - 1) <= '9') {
               int newEnd = path.lastIndexOf("-", end - 1);

               // Only use the newEnd if it is still valid. Otherwise
               // 'c3p-1.23.3.jar' fails
               if (newEnd > start) {
                  end = newEnd;
               }
            }

            // TODO Replace this checking for version number by Pattern matching
            // A quickly regexp, not tested, should be something as:
            // "[.+- (.\\d .*) \\.jar]"

            String lib = path.substring(start, end);
            String version = path.substring(end + 1, path.lastIndexOf("."));

            libVersions.put(lib, version);
         }
      }

   }

   public static Map<String, String> getLibVersions(ServletContext servletContext) {
      if (libVersions == null) {
         initLibVersions(servletContext);
      }
      return libVersions;
   }

   public static synchronized String getApplicationVersion(ServletContext servletContext) {
      if (applicationVersion == null) {
         applicationVersion = "unknown";
         Set<String> allResources = new HashSet<String>();
         getAllResourcesFromContextPath(servletContext, allResources, "/META-INF/maven/");
         for (String resource : allResources) {
            if (resource.endsWith("pom.properties")) {
               java.io.InputStream in = servletContext.getResourceAsStream(resource);
               Properties mProps = new Properties();
               try {
                  mProps.load(in);
               } catch (IOException e) {
                  getLogger().error("Unable to get application version from resource:" + resource + ".", e);
                  return "Unable to get application version";
               }
               applicationVersion = (String) mProps.get("version");
            }
         }

      }
      return applicationVersion;
   }

   @SuppressWarnings("unchecked")
   public static void getAllResourcesFromContextPath(ServletContext servletContext, Set<String> allResources, String startPath) {
      Set<String> newResources;
      newResources = servletContext.getResourcePaths(startPath);
      for (String resource : newResources) {
         if (resource.endsWith("/")) {
            getAllResourcesFromContextPath(servletContext, allResources, resource);
         }
         allResources.add(resource);
      }
   }

}