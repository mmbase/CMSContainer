/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.portlets.gallery2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.portlets.ContentChannelPortlet;
import com.finalist.cmsc.services.contentrepository.ContentRepository;


/**
 * Portlet to edit images of a specific content channel
 * 
 * @author Marco Fang
 */
public class Gallery2Portlet extends ContentChannelPortlet {

   /**
    * Configuration constants.
    */
   protected static final String WIDTH = "width";
   protected static final String COLUMN = "column";

   /**
    * Configuration default constants.
    */
   public static final String WIDTH_ATTR_DEFAULT = "110px";


   @Override
   protected void saveParameters(ActionRequest request, String portletId) {
      setPortletParameter(portletId, CONTENTELEMENT, request.getParameter(CONTENTELEMENT));
      setPortletParameter(portletId, WIDTH, request.getParameter(WIDTH));
      setPortletParameter(portletId, COLUMN, request.getParameter(COLUMN));

      super.saveParameters(request, portletId);
   }


   @Override
   protected void setEditResponse(ActionRequest request, ActionResponse response, Map<String, Node> nodesMap)
         throws PortletModeException {
      response.setPortletMode(PortletMode.VIEW);
   }


   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      addExtraAttributes(req);
      super.doView(req, res);
   }


   @Override
   protected void doEdit(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      req.setAttribute("portletMode", "view");
      super.doView(req, res);
   }


   protected void addExtraAttributes(RenderRequest req) {
      PortletPreferences preferences = req.getPreferences();
      req.setAttribute(CONTENTELEMENT, preferences.getValue(CONTENTELEMENT, null));
      req.setAttribute(WIDTH, preferences.getValue(WIDTH, WIDTH_ATTR_DEFAULT));
      req.setAttribute(COLUMN, preferences.getValue(COLUMN, null));
   }



   @Override
   protected int countContentElements(RenderRequest req, List<String> assettypes, String channel, int offset,
         String orderby, String direction, String archive, int elementsPerPage, int year, int month, int day,
         boolean useLifecycleBool, int maxDays) {
      int totalItems = ContentRepository.countAssetElements(channel, assettypes, orderby, direction,
            useLifecycleBool, archive, offset, elementsPerPage, year, month, day, maxDays);
      return totalItems;
   }

}
