/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.excel2menu;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.util.UploadUtil;
import com.finalist.cmsc.util.UploadUtil.BinaryData;

public class MenuImportRequest {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(MenuImportRequest.class.getName());


   public void process(Cloud cloud, HttpServletRequest request) {
      try {
         List<UploadUtil.BinaryData> binaries = UploadUtil.uploadFiles(request, 4 * 1000 * 1000);

         String properties = request.getParameter("properties");
         Properties params = new Properties();
         params.load(new ByteArrayInputStream(properties.getBytes()));

         ExcelConfig config = new ExcelConfig(params);
         Excel2Menu t = new Excel2Menu(cloud, config);

         for (BinaryData binary : binaries) {
            t.convert(binary.getInputStream());
         }
      }
      catch (IOException e) {
         log.warn("" + e.getMessage(), e);
      }
   }

}
