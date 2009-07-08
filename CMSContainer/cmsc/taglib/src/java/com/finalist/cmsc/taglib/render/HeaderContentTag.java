/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.headerresource.*;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;

/**
 * meta types which stil can be added: 
 * "generator" http-equiv="generator" content="" /> 
 * "rating" http-equiv="rating" content="general" /> 
 * "distribution" http-equiv="distribution" content="global" /> 
 * "robots" http-equiv="robots" content="all" /> 
 * "revisit-after" http-equiv="revisit-after" content="1 week" />
 * "country" http-equiv="country" content="netherlands" />
 * 
 * @author freek
 */
public class HeaderContentTag extends CmscTag {

   private boolean dublin = false;


   public void setDublin(String dublin) {
      this.dublin = Boolean.valueOf(dublin);
   }


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();

      String path = getPath();
      Site site = SiteManagement.getSiteFromPath(path);
      NavigationItem item = SiteManagement.getNavigationItemFromPath(path);
      if (site != null) {
         String siteLanguage = site.getLanguage();

         List<MetaHeaderResource> metaResources = new ArrayList<MetaHeaderResource>();
         Set<LinkHeaderResource> linkResources = new HashSet<LinkHeaderResource>();
         Set<ScriptHeaderResource> scriptResources = new HashSet<ScriptHeaderResource>();

         if (item != null) {
            String description = item.getDescription();
            if (StringUtils.isBlank(description)) {
               description = item.getTitle();
            }
            addMeta(metaResources, false, "description", description, siteLanguage, null);
         }
         addMeta(metaResources, false, "author", site.getCreator(), siteLanguage, null);
         addMeta(metaResources, false, "copyright", site.getRights(), siteLanguage, null);
         addMeta(metaResources, false, "language", siteLanguage, null, "language");
         addMeta(metaResources, false, "generator", "CMS Container", null, null);


         if (dublin) {
            addMeta(metaResources, true, "format", "text/html");
            addMeta(metaResources, true, "type", "Collection");
            addMeta(metaResources, true, "language", siteLanguage);
            if (item != null) {
                addMeta(metaResources, true, "title", item.getTitle());
                addMeta(metaResources, true, "description", item.getDescription());
            }
            addMeta(metaResources, true, "creator", site.getCreator());
            addMeta(metaResources, true, "publisher", site.getPublisher());
            addMeta(metaResources, true, "rights", site.getRights());
            addMeta(metaResources, true, "source", site.getSource());
         }
         
         if (!site.equals(item)) {
            addPortletHeaders(metaResources, linkResources, scriptResources);
         }
         
         String header = buildResponseHeader(metaResources, linkResources, scriptResources);
         ctx.getOut().print(header);
      }
   }

   private void addMeta(List<MetaHeaderResource> metaResources, boolean dublin, String name, String content, String lang, String httpEquiv){
      if (StringUtils.isNotBlank(content)) {
         metaResources.add(new MetaHeaderResource(dublin, name, content, lang, httpEquiv));
      }
   }
   
   private void addMeta(List<MetaHeaderResource> metaResources, boolean dublin, String name, String content){
      if (StringUtils.isNotBlank(content)) {
         metaResources.add(new MetaHeaderResource(dublin, name, content));
      }
   }

   private void addPortletHeaders(List<MetaHeaderResource> metaResources,
         Set<LinkHeaderResource> linkResources, Set<ScriptHeaderResource> scriptResources) {

      ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
      if (container != null) {
         Iterator<PortletFragment> portlets = container.getAllPortlets().iterator();
         while (portlets.hasNext()) {
            PortletFragment pf = portlets.next();
            Collection<HeaderResource> portletResources = pf.getHeaderResources();
            if (portletResources != null) {
               for (HeaderResource resource : portletResources) {
                  if (resource instanceof MetaHeaderResource) {
                     MetaHeaderResource m = (MetaHeaderResource) resource;
                     if (m.isDublin()) {
                        if (dublin) {
                           mergeMeta(metaResources, m);
                        }
                     }
                     else {
                        mergeMeta(metaResources, m);
                     }
                  }
                  if (resource instanceof LinkHeaderResource) {
                     linkResources.add((LinkHeaderResource) resource);
                  }
                  if (resource instanceof ScriptHeaderResource) {
                     scriptResources.add((ScriptHeaderResource) resource);
                  }
               }
            }
         }
      }
   }

   private void mergeMeta(List<MetaHeaderResource> metaResources, MetaHeaderResource m) {
      int index = metaResources.indexOf(m);
      if (index >= 0) {
         metaResources.set(index, m);
      }
      else {
         metaResources.add(m);
      }
   }

   private String buildResponseHeader(List<MetaHeaderResource> metaResources,
         Set<LinkHeaderResource> linkResources, Set<ScriptHeaderResource> scriptResources) {
      StringBuffer header = new StringBuffer();

      for (MetaHeaderResource resource : metaResources) {
         if (!resource.isDublin()) {
            resource.render(header);
            header.append("\n");
         }
      }
      
      if (dublin) {
         LinkHeaderResource dublinSchema = new LinkHeaderResource("schema.DC", "http://dublincore.org/documents/dces/", null);
         dublinSchema.render(header);
         for (MetaHeaderResource resource : metaResources) {
            if (resource.isDublin()) {
               resource.render(header);
               header.append("\n");
            }
         }
      }

      for (LinkHeaderResource resource : linkResources) {
         resource.render(header);
         header.append("\n");
      }
      
      for (ScriptHeaderResource resource : scriptResources) {
         resource.render(header);
         header.append("\n");
      }
      
      return header.toString();
   }
}
