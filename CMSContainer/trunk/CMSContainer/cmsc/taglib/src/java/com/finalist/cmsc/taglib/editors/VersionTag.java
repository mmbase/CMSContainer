package com.finalist.cmsc.taglib.editors;

import java.io.IOException;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.util.version.VersionUtil;

public class VersionTag extends SimpleTagSupport {

   private static final String TYPE_APPLICATION = "application";
   private static final String TYPE_CMSC = "cmsc";
   private static final String TYPE_MMBASE = "mmbase";
   private static final String TYPE_LIBS = "libs";
   
   private static final String UNKNOWN = "unknown/mixed";

   private String type = TYPE_APPLICATION;
   private String var;


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();
      Object version = null;

      if (type.equalsIgnoreCase(TYPE_APPLICATION)) {
         version = VersionUtil.getApplicationVersion(ctx.getServletContext());
      }
      else if (type.equalsIgnoreCase(TYPE_CMSC)) {
         version = VersionUtil.getCmscVersion(ctx.getServletContext());
      }
      else if (type.equalsIgnoreCase(TYPE_MMBASE)) {
         version = VersionUtil.getMmbaseVersion(ctx.getServletContext());
      }
      else if (type.equalsIgnoreCase(TYPE_LIBS)) {
         version = VersionUtil.getLibVersions(ctx.getServletContext());
      }
      else {
         throw new IllegalArgumentException("No type not found, see tld for list of types.");
      }
      if(version == null) {
      	version = UNKNOWN;
      }
      if (var == null) {
         ctx.getOut().write(version.toString());
      }
      else {
         ctx.setAttribute(var, version);
      }
   }


   public void setType(String type) {
      this.type = type;
   }


   public void setVar(String var) {
      this.var = var;
   }
}
