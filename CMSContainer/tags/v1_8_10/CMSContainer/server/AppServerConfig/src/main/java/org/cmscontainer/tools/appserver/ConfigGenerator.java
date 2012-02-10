package org.cmscontainer.tools.appserver;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import com.finalist.cmsc.util.XsltUtil;


public class ConfigGenerator {

   public static void main(String[] args) throws MalformedURLException, TransformerException, IOException {
      ConfigGenerator generator = new ConfigGenerator();
      
      HashMap<String, Object> xslParams = new HashMap<String, Object>();
      
      File xml = new File(args[0]);
      
      String jettyEnvXml = generator.transformXml("jetty/env.xsl", xml, xslParams);
      System.out.println(jettyEnvXml);
   }

   protected String transformXml(String xsl, Object xml, HashMap<String, Object> xslParams)
         throws TransformerException, IOException, MalformedURLException {

      InputStream xslSrc = this.getClass().getResourceAsStream(xsl);
      XsltUtil xsltUtil = new XsltUtil(xml, xslSrc, null);
      return xsltUtil.transformToString(xslParams);
   }
}
