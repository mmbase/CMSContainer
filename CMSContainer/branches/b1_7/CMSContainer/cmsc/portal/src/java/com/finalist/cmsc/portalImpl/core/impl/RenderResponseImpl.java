package com.finalist.cmsc.portalImpl.core.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.om.window.PortletWindow;


public class RenderResponseImpl extends org.apache.pluto.core.impl.RenderResponseImpl {

   public RenderResponseImpl(PortletWindow portletWindow, HttpServletRequest servletRequest,
         HttpServletResponse servletResponse, boolean containerSupportsBuffering) {
      super(portletWindow, servletRequest, servletResponse, containerSupportsBuffering);
   }

   @Override
   public void setContentType(String type) {
      if (!isIncluded()) {
         super.setContentType(type);
      }
   }
   
}
