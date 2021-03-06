/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.select.forms;

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
import com.finalist.cmsc.repository.RepositoryInfo;
import com.finalist.cmsc.repository.RepositoryTreeModel;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.repository.select.SelectRenderer;
import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;
import com.finalist.tree.ajax.SelectAjaxRenderer;

public class SelectorAction extends com.finalist.cmsc.struts.SelectorAction {

   private boolean contentChannelOnly;


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      String action = request.getParameter("action");
      String portletId = request.getParameter("portletId");
      String from = request.getParameter("onlycollection");
      if (StringUtils.isEmpty(action)) {
         RepositoryInfo info = new RepositoryInfo(RepositoryUtil.getRepositoryInfo(cloud));
         cloud.setProperty("Selector" + RepositoryInfo.class.getName(), info);
      }

      if (mapping instanceof SelectorChannelActionMapping) {
         SelectorChannelActionMapping selectoMapping = (SelectorChannelActionMapping) mapping;
         contentChannelOnly = selectoMapping.isContentChannelOnly();
      }

      addToRequest(request, "actionname", mapping.getPath());
      addToRequest(request, "portletId", portletId);
      addToRequest(request, "onlycollection", from);

      JstlUtil.setResourceBundle(request, "cmsc-repository");
      return super.execute(mapping, form, request, response, cloud);
   }

   protected String getLinkPattern(HttpServletRequest request) {
      String portletId = request.getParameter("portletId");
      if (StringUtils.isNotBlank(portletId)) {
         return super.getLinkPattern() + "&portletId=" + portletId;
      }
      return super.getLinkPattern();
   }

   @Override
   protected String getChannelId(HttpServletRequest request, Cloud cloud) {
      String path = request.getParameter("path");
      if (StringUtils.isNotEmpty(path)) {
         Node parentNode = RepositoryUtil.getChannelFromPath(cloud, path);
         if (parentNode != null) {
            return String.valueOf(parentNode.getNumber());
         }
      }
      return super.getChannelId(request, cloud);
   }


   @Override
   protected Node getRootNode(Cloud cloud) {
      return RepositoryUtil.getRootNode(cloud);
   }


   @Override
   protected TreeInfo getTreeInfo(Cloud cloud) {
      TreeInfo info = (TreeInfo) cloud.getProperty("Selector" + RepositoryInfo.class.getName());
      return info;
   }


   @Override
   protected List<Node> getOpenChannels(Node channelNode) {
      if (RepositoryUtil.isContentChannel(channelNode)) {
         return RepositoryUtil.getPathToRoot(channelNode);
      }
      return null;
   }


   @Override
   protected AjaxTree getTree(HttpServletRequest request, HttpServletResponse response, Cloud cloud, TreeInfo info,
         String persistentid) {
      RepositoryTreeModel model = new RepositoryTreeModel(cloud, contentChannelOnly);
      SelectAjaxRenderer chr = new SelectRenderer(response, getLinkPattern(request), getTarget());
      AjaxTree t = new AjaxTree(model, chr, info);
      t.setImgBaseUrl("../../gfx/icons/");
      return t;
   }


   @Override
   protected List<String> getChildren(Cloud cloud, String path) {
      List<String> strings = new ArrayList<String>();
      if (StringUtils.isEmpty(path)) {
         Node parentNode = RepositoryUtil.getRootNode(cloud);
         strings.add(parentNode.getStringValue(TreeUtil.PATH_FIELD));
      }
      else {
         Node parentNode = RepositoryUtil.getChannelFromPath(cloud, path);
         if (parentNode != null) {
            NodeList children;
            if (contentChannelOnly) {
               children = RepositoryUtil.getContentChannelOrderedChildren(parentNode);
            }
            else {
               children = RepositoryUtil.getChildren(parentNode);
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
