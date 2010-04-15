/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.portlets.googlemap;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.JspPortlet;

public class GoogleMapPortlet extends JspPortlet {

   /**
    * Configuration constants.
    */
   public static final String GOOGLE_KEY_PROPERTY = "google.key";
   public static final String KEY = "key";
   public static final String ADDRESS = "address";
   public static final String INFO = "info";
   public static final String HEIGHT = "height";
   public static final String WIDTH = "width";

   public static final String HEIGHT_DIRECTIONS = "heightDirections";
   public static final String WIDTH_DIRECTIONS = "widthDirections";

   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      request.setAttribute(ADDRESS, preferences.getValue(ADDRESS, null));
      request.setAttribute(INFO, preferences.getValue(INFO, null));
      request.setAttribute(HEIGHT, convertToPixels(preferences.getValue(HEIGHT, null)));
      request.setAttribute(WIDTH, convertToPixels(preferences.getValue(WIDTH, null)));

      request.setAttribute(HEIGHT_DIRECTIONS, convertToPixels(preferences.getValue(HEIGHT_DIRECTIONS, null)));
      request.setAttribute(WIDTH_DIRECTIONS, convertToPixels(preferences.getValue(WIDTH_DIRECTIONS, null)));
      
      String key = preferences.getValue(KEY, PropertiesUtil.getProperty(GOOGLE_KEY_PROPERTY));
      request.setAttribute(KEY, key);
      
      super.doView(request, response);
   }

   protected String convertToPixels(String value) {
      if (value == null) return null;
      return (!value.endsWith("px")) ? (value + "px") : value;
   }

}