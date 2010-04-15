/**
 * 
 */
package com.finalist.cmsc.navigation.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.beans.om.Layout;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * @author Billy
 *
 */
public class LayoutAction extends MMBaseAction {
	
	private static final String LAYOUT = "layout";
	private static final String ORDERFIELD = "title";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			Cloud cloud) throws Exception {
		NodeList nodelist = SearchUtil.findOrderedNodeList(cloud, LAYOUT, ORDERFIELD);
		JSONArray jsonArray = new JSONArray();
		NodeIterator iter = nodelist.nodeIterator();
		while(iter.hasNext()){
			Node node = iter.nextNode();
			Layout layout = new Layout();
			layout.setId(node.getNumber());
			layout.setTitle(node.getStringValue("title"));
			JSONObject jsonObject = new JSONObject(layout);
			jsonArray.put(jsonObject);
		}
	    response.setCharacterEncoding("UTF-8");
		response.getWriter().print(jsonArray.toString());
		return null;
	}

}
