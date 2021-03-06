/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.select.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationInfo;
import com.finalist.cmsc.navigation.NavigationTreeModel;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.navigation.select.SelectRenderer;
import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;
import com.finalist.tree.ajax.SelectAjaxRenderer;

public class SelectorAction extends com.finalist.cmsc.struts.SelectorAction {

   private boolean strictPageOnly;

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      String action = request.getParameter("action");
      if (StringUtils.isEmpty(action)) {
         NavigationInfo info = new NavigationInfo(NavigationUtil.getNavigationInfo(cloud));
         cloud.setProperty("Selector" + NavigationInfo.class.getName(), info);
      }

      if (mapping instanceof SelectorPageActionMapping) {
         SelectorPageActionMapping selectoMapping = (SelectorPageActionMapping) mapping;
         strictPageOnly = selectoMapping.isStrictPageOnly();
      }

      JstlUtil.setResourceBundle(request, "cmsc-site");
      return super.execute(mapping, form, request, response, cloud);
   }


   @Override
   protected String getChannelId(HttpServletRequest request, Cloud cloud) {
      String path = request.getParameter("path");
      if (StringUtils.isNotEmpty(path)) {
         Node parentNode = NavigationUtil.getPageFromPath(cloud, path);
         if (parentNode != null) {
            return String.valueOf(parentNode.getNumber());
         }
      }
      return super.getChannelId(request, cloud);
   }


   @Override
   protected Node getRootNode(Cloud cloud) {
      return null;
   }


   @Override
   protected TreeInfo getTreeInfo(Cloud cloud) {
      TreeInfo info = (TreeInfo) cloud.getProperty("Selector" + NavigationInfo.class.getName());
      return info;
   }


   @Override
   protected List<Node> getOpenChannels(Node channelNode) {
      if (PagesUtil.isPageType(channelNode)) {
         return NavigationUtil.getPathToRoot(channelNode);
      }
      return null;
   }


   @Override
   protected AjaxTree getTree(HttpServletRequest request, HttpServletResponse response, Cloud cloud, TreeInfo info,
         String persistentid) {
      Node node = cloud.getNode(persistentid);
      if (!SiteUtil.isSite(node)) {
         node = NavigationUtil.getSiteFromPath(cloud, node.getStringValue(TreeUtil.PATH_FIELD));
      }

      NavigationTreeModel model = new NavigationTreeModel(node, strictPageOnly);
      SelectAjaxRenderer chr = new SelectRenderer(response, getLinkPattern(), getTarget());
      AjaxTree t = new AjaxTree(model, chr, info);
      t.setImgBaseUrl("../../gfx/icons/");
      return t;
   }


   @Override
   protected List<String> getChildren(Cloud cloud, String path) {
      List<String> strings = new ArrayList<String>();
      if (StringUtils.isEmpty(path)) {
         NodeList sites = SiteUtil.getSites(cloud);
         for (Iterator<Node> iter = sites.iterator(); iter.hasNext();) {
            Node child = iter.next();
            strings.add(child.getStringValue(TreeUtil.PATH_FIELD));
         }
      }
      else {
         Node parentNode = NavigationUtil.getPageFromPath(cloud, path);
         if (parentNode != null) {
            NodeList children = NavigationUtil.getChildren(parentNode);
            if (strictPageOnly) {
               children = NavigationUtil.getStrictPageOrderedChildren(parentNode);
            }
            else {
               children = NavigationUtil.getChildren(parentNode);
            }

            for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
               Node child = iter.next();
               strings.add(child.getStringValue(TreeUtil.PATH_FIELD));
            }
         }
      }
      return strings;
   }

}
