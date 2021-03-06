/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.util.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;
import org.mmbase.module.tools.MMAdmin;
import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.mmbase.TreePathCache;
import com.finalist.cmsc.services.Properties;
import com.finalist.cmsc.services.security.LoginSession;
import com.finalist.cmsc.util.ServerUtil;

/**
 * MMBase specific PortalLayoutService implementation, in this case MMBase
 * manages the Screens/Layout and Portlets
 * 
 * @author Wouter Heijke
 */
public class SiteManagementServiceMMBaseImpl extends SiteManagementService {
   private static Log log = LogFactory.getLog(SiteManagementServiceMMBaseImpl.class);

   private CloudProvider cloudProvider;

   @Override
   public void init(ServletConfig config, Properties aProperties) throws Exception {
      this.cloudProvider = CloudProviderFactory.getCloudProvider();
      log.info("SiteManagementService STARTED");

      waitForMMBase();
   }


   private void waitForMMBase() {
      MMAdmin mmadmin = (MMAdmin) Module.getModule("mmadmin", true);

      try {
         while (!mmadmin.getState()) {
            // not started, sleep some time
            Thread.sleep(1000L);
         }
      }
      catch (InterruptedException e) {
         log.debug(e.getMessage(), e);
      }
   }


   @Override
   public boolean isNavigation(String path) {
      log.debug("isNavigation:'" + path + "'");
      if (ServerUtil.isStaging()) {
          NavigationItem item = SiteModelManager.getInstance().getNavigationItem(path);
          return showNavigation(item);
      }
      else {
          // live has a faster check
          return SiteModelManager.getInstance().hasNavigationItem(path);
      }
   }


    private boolean showNavigation(NavigationItem item) {
        if (item != null) {
              if (isValidNavigation(item)) {
                  return true;
              }
              else {
                  return isUserCloud();
              }
          }
          return false;
    }

    private void removeInvalidNavigationsFromList(List<? extends NavigationItem> children) {
        if (ServerUtil.isStaging()) {
             for (Iterator<? extends NavigationItem> iterator = children.iterator(); iterator.hasNext();) {
                NavigationItem child = iterator.next();
                if (!showNavigation(child)) {
                   iterator.remove();
                }
             }
         }
    }
    
   private boolean isValidNavigation(NavigationItem item) {
       if (item.isUse_expirydate()) {
           Date now = new Date();
           return now.after(item.getPublishdate()) && now.before(item.getExpiredate());
       }
       return true;
   }


   @Override
   public LoginSession getLoginSession(HttpServletRequest request) {
      Cloud cloud = null;

      LoginSession ls = new LoginSession();
      ls.setAuthenticated(false);

      if (request != null) {
         cloud = CloudUtil.getCloudFromSession(request);
      }
      else {
         log.error("No request");
      }

      if (cloud != null) {
         UserContext u = cloud.getUser();
         // log.debug("UserContext='" + u.getIdentifier() + "'");

         Rank r = u.getRank();
         if (r.getInt() >= Rank.BASICUSER_INT) {
            ls.setAuthenticated(true);
         }
         else {
            ls.setAuthenticated(false);
         }
      }
      else {
         log.debug("No cloud found");
      }

      return ls;
   }

   @Override
   public List<Site> getSites() {
       List<Site> sites = SiteModelManager.getInstance().getSites(); 
       removeInvalidNavigationsFromList(sites);
       return sites;
   }

   public <E extends NavigationItem> List<E> getNavigationItems(NavigationItem parent, Class<E> childClazz) {
       if (parent != null) {
          List<E> children = SiteModelManager.getInstance().getChildren(parent, childClazz);
          removeInvalidNavigationsFromList(children);
          return children;
       }
       return new ArrayList<E>();
    }

   @Override
   public NavigationItem getNavigationItem(int channel) {
      NavigationItem navigationItem = SiteModelManager.getInstance().getNavigationItem(channel);
      if (showNavigation(navigationItem)) {
          return navigationItem;
      }
      return null;
   }


   @Override
   public List<Stylesheet> getStylesheetForPageByPath(String path, boolean override) {
      List<Page> pagesToRoot = getPagesFromPath(path);// get all pages to root
      List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();

      if (override) {
         // loop through pages
         // if override only take the sheets of the last page
         for (int count = pagesToRoot.size()-1; count >= 0; count--) {
            Page page = pagesToRoot.get(count);
            stylesheets = getStylesheetForPage(page);

            if (!stylesheets.isEmpty()) {
               return stylesheets;
            }
         }
      }
      else {
         stylesheets = getStylesheetForPage(pagesToRoot.get(0));

         // loop through pages
			for (int count = 1; count < pagesToRoot.size(); count++) {
			   Page page = pagesToRoot.get(count);
			   List<Stylesheet> pageSheets = getStylesheetForPage(page);
			   if (!pageSheets.isEmpty()) {
			      removeOverwriteableStylesheets(stylesheets);
			      stylesheets.addAll(pageSheets);
			   }
			}
		}
     
      return stylesheets;
   }

	private void removeOverwriteableStylesheets(List<Stylesheet> stylesheets) {
      for (Iterator<Stylesheet> iterator = stylesheets.iterator(); iterator.hasNext();) {
         Stylesheet stylesheet = iterator.next();
         if (stylesheet.isOverwriteable()) {
            iterator.remove();
         }
      }
   }

   private List<Stylesheet> getStylesheetForPage(Page page) {
	   List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();
      List<Integer> stylesheetNumbers = page.getStylesheet();
      for (int j = 0; j < stylesheetNumbers.size(); j++) {
         Integer stylesheetNumber = stylesheetNumbers.get(j);
         Stylesheet stylesheet = SiteModelManager.getInstance().getStylesheet(stylesheetNumber.intValue());
         stylesheets.add(stylesheet);
      }
      return stylesheets;
   }

   @Override
   public NavigationItem getNavigationItemFromPath(String path) {
       NavigationItem navigationItem = SiteModelManager.getInstance().getNavigationItem(path);
       if (showNavigation(navigationItem)) {
           return navigationItem;
       }
       return null;
   }


   @Override
   public Site getSiteFromPath(String path) {
       Site navigationItem = SiteModelManager.getInstance().getSite(path);
       if (showNavigation(navigationItem)) {
           return navigationItem;
       }
       return null;
   }


   @Override
   public <E extends NavigationItem> List<E> getListFromPath(String path, Class<E> clazz) {
      List<E> itemsForPath = SiteModelManager.getInstance().getItemsForPath(path, clazz);
      if (ServerUtil.isStaging()) {
          for (E child : itemsForPath) {
             if (!showNavigation(child)) {
                itemsForPath.clear();
                break;
             }
          }
      }

      return itemsForPath;
   }


   @Override
   public String getPath(NavigationItem item, boolean includeRoot) {
      return SiteModelManager.getInstance().getPath(item, includeRoot);
   }


   @Override
   public String getPath(int itemId, boolean includeRoot) {
      NavigationItem item = SiteModelManager.getInstance().getNavigationItem(itemId);
      if (item == null) {
         return null;
      }
      else {
         return SiteModelManager.getInstance().getPath(item, includeRoot);
      }
   }


   @Override
   public List<View> getViews(String screenId, String layoutId) {
      return SiteModelManager.getInstance().getViews(screenId, layoutId);
   }


   @Override
   public List<View> getViews(String definitionId) {
      return SiteModelManager.getInstance().getViews(definitionId);
   }


   @Override
   public List<View> getViews(PortletDefinition definition) {
      return SiteModelManager.getInstance().getViews(definition);
   }


   @Override
   public List<PortletDefinition> getSingletonPortlets(String screenId, String layoutId) {
      List<PortletDefinition> defs = SiteModelManager.getInstance().getSingletonPortlets(screenId, layoutId);
      removeDefinitionsBasedOnRank(defs);
      return defs;
   }


   @Override
   public List<PortletDefinition> getPortletDefintions(String screenId, String layoutId) {
      List<PortletDefinition> defs = SiteModelManager.getInstance().getPortletDefintions(screenId, layoutId);
      removeDefinitionsBasedOnRank(defs);
      return defs;
   }


   private void removeDefinitionsBasedOnRank(List<PortletDefinition> defs) {
      Cloud cloud = getUserCloud();
      Rank rank = cloud.getUser().getRank();
      for (Iterator<PortletDefinition> iter = defs.iterator(); iter.hasNext();) {
         PortletDefinition definition = iter.next();
         if (definition.getRank() > rank.getInt()) {
            iter.remove();
         }
      }
   }

   private boolean isUserCloud() {
       Cloud cloud = CloudUtil.getCloudFromThread();
       return cloud != null;
   }

   private Cloud getUserCloud() {
      Cloud cloud = CloudUtil.getCloudFromThread();
      if (cloud == null) {
         log.warn("User cloud not found in thread; make sure that the user cloud is bound");
         cloud = cloudProvider.getAdminCloud();
      }
      return cloud;
   }


   @Override
   public List<String> getContentTypes(String portletId) {
      return SiteModelManager.getInstance().getContentTypes(portletId);
   }


   @Override
   public Set<String> getPagePositions(String pageId) {
      return SiteModelManager.getInstance().getPagePositions(Integer.valueOf(pageId));
   }


   @Override
   public List<Integer> getPageImagesForPath(String name, String path) {
      List<Page> pagesToRoot = getPagesFromPath(path);// get all pages to root

      for (int count = pagesToRoot.size() - 1; count >= 0; count--) {
         Page page = pagesToRoot.get(count);
         List<Integer> image = page.getPageImage(name);
         if (image != null) {
            return image;
         }
      }
      return null;
   }


   @Override
   public Layout getLayout(int layout) {
      return SiteModelManager.getInstance().getLayout(layout);
   }


   @Override
   public Portlet getPortlet(int portletId) {
      return SiteModelManager.getInstance().getPortlet(portletId);
   }


   @Override
   public PortletDefinition getPortletDefinition(int definition) {
      return SiteModelManager.getInstance().getPortletDefinition(definition);
   }


   @Override
   public View getView(int view) {
      return SiteModelManager.getInstance().getView(view);
   }


   @Override
   public String getSite(NavigationItem item) {
      return SiteModelManager.getInstance().getSite(item);
   }


   @Override
   public void resetSiteCache() {
      SiteModelManager.getInstance().resetSiteCache();
      TreePathCache.clearAllCaches();
   }
}
