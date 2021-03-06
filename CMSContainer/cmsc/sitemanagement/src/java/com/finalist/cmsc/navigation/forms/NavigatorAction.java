/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.security.Rank;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.struts.*;
import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;

public class NavigatorAction extends TreeAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
   		HttpServletResponse response, Cloud cloud) throws Exception {
   	
   	String doParam = request.getParameter("do");
   	if(doParam != null && doParam.equals("sortSite") && cloud.getUser().getRank() == Rank.ADMIN) {
   		doSiteSort(cloud, request.getParameter("id1"), request.getParameter("id2"));
   	}

      JstlUtil.setResourceBundle(request, "cmsc-site"); 
      return super.execute(mapping, form, request, response, cloud);
   }


   private void doSiteSort(Cloud cloud, String id1, String id2) {
   	Node node1 = cloud.getNode(id1); 
		int pos1 = node1.getIntValue("pos");
   	Node node2 = cloud.getNode(id2); 
		int pos2 = node2.getIntValue("pos");
		node1.setIntValue("pos", pos2);
		node1.commit();
		node2.setIntValue("pos", pos1);
		node2.commit();
	}


	@Override
   protected Node getRootNode(Cloud cloud) {
      return null;
   }


   @Override
   protected TreeInfo getTreeInfo(Cloud cloud) {
      TreeInfo info = NavigationUtil.getNavigationInfo(cloud);
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

      NavigationTreeModel model = new NavigationTreeModel(node);
      NavigationAjaxRenderer chr = new NavigationAjaxRenderer(request, response, "content");
      AjaxTree t = new AjaxTree(model, chr, info);
      t.setImgBaseUrl("../gfx/icons/");
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
            for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
               Node child = iter.next();
               strings.add(child.getStringValue(TreeUtil.PATH_FIELD));
            }
         }
      }
      return strings;
   }

}
