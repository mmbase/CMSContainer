/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation.tree;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.security.Rank;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;


public class SiteTreeItemRenderer implements NavigationTreeItemRenderer {

    protected static final String FEATURE_WORKFLOW = "workflowitem";

    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
       UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
       boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);
       
       String name = parentNode.getStringValue(SiteUtil.TITLE_FIELD);
       String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );

       String id = String.valueOf(parentNode.getNumber());
       TreeElement element = renderer.createElement(parentNode, role, name, fragment, secure);

       if (SecurityUtil.isEditor(role)) {

          if (SecurityUtil.isWebmaster(role)) {
             element.addOption(renderer.createTreeOption("edit_defaults.png", 
                         "site.site.edit", "SiteEdit.do?number=" + id));
          }

          renderer.addParentOptions(element, id);

          if (SecurityUtil.isChiefEditor(role)
                && ((model.getChildCount(parentNode) == 0) || SecurityUtil.isWebmaster(role))) {
             element.addOption(renderer.createTreeOption("delete.png", "site.site.remove", 
                         "SiteDelete.do?number=" + id));
          }

          if (NavigationUtil.getChildCount(parentNode) >= 2) {
             element.addOption(renderer.createTreeOption("reorder.png", "site.page.reorder", 
                         "reorder.jsp?parent=" + id));
          }

          if (SecurityUtil.isChiefEditor(role)) {
             element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", 
                         "javascript:paste('" + id + "');"));
          }

          if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
             element.addOption(renderer.createTreeOption("publish.png", "site.page.publish",
                   "../workflow/publish.jsp?number=" + id));
             element.addOption(renderer.createTreeOption("masspublish.png", "site.page.masspublish",
                   "../workflow/masspublish.jsp?number=" + id));
          }
       }
       Cloud cloud = parentNode.getCloud();
       if (cloud.getUser().getRank() == Rank.ADMIN) {
      	 int pos = cloud.getNode(id).getIntValue("pos");
      	 if(!firstSite(cloud, pos)) {
	          element.addOption(renderer.createTreeOption("up.png", "site.page.sorting.up",
	                "javascript:siteSortUp('"+id+"');"));
      	 }
      	 if(!lastSite(cloud, pos)) {
	          element.addOption(renderer.createTreeOption("down.png", "site.page.sorting.down",
	                "javascript:siteSortDown('"+id+"');"));
      	 }
       }
       element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
             "../usermanagement/pagerights.jsp?number=" + id));
       
       return element;
    }

	private boolean firstSite(Cloud cloud, int pos) {
		NodeList list = cloud.getNodeManager(SiteUtil.SITE).getList("pos < "+pos, null, null);
		return (list.size() == 0);
	}

	private boolean lastSite(Cloud cloud, int pos) {
		NodeList list = cloud.getNodeManager(SiteUtil.SITE).getList("pos > "+pos, null, null);
		return (list.size() == 0);
	}

	public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
		// has no parent
	}

	public boolean showChildren(Node parentNode) {
		return true;
	}
    
}
