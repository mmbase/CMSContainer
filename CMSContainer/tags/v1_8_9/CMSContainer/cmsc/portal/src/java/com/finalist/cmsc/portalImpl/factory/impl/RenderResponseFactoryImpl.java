package com.finalist.cmsc.portalImpl.factory.impl;

import javax.portlet.RenderResponse;

import org.apache.pluto.factory.RenderResponseFactory;
import org.apache.pluto.om.window.PortletWindow;

import com.finalist.cmsc.portalImpl.core.impl.RenderResponseImpl;

/**
 * this factory creates a RenderResponse which works for jsp and jspx files
 */
public class RenderResponseFactoryImpl implements RenderResponseFactory {

   public void init(javax.servlet.ServletConfig config, java.util.Map properties) throws Exception {
   }

   public RenderResponse getRenderResponse(PortletWindow portletWindow,
         javax.servlet.http.HttpServletRequest servletRequest,
         javax.servlet.http.HttpServletResponse servletResponse, boolean containerSupportsBuffering) {
      RenderResponse renderResponse = new RenderResponseImpl(portletWindow, servletRequest,
            servletResponse, containerSupportsBuffering);
      return renderResponse;
   }

   public void destroy() throws Exception {
   }
}
