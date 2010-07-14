package com.finalist.cmsc.repository;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class has been override because the getLastModified() from the version in MMbase fails 
 * because it does not know how to handle icache nodes
 * 
 * @author freek
 */
public class ImageServlet extends org.mmbase.servlet.ImageServlet {

   private static final Object ICACHE_NODEMANAGER = "icaches";

	private static Logger log;

   private String lastModifiedField = null;

   /**
    * Also look at icache nodes
    * 
    * {@inheritDoc}
    **/
	@Override
   protected long getLastModified(HttpServletRequest req) {
       if (lastModifiedField == null) return -1;
       try {
           QueryParts query = readQuery(req, null);
           Node node = getServedNode(query, getNode(query));
           if (node != null && node.getNodeManager().hasField(lastModifiedField)) {
               return node.getDateValue(lastModifiedField).getTime();
           } else if (node != null && node.getNodeManager().getName().equals(ICACHE_NODEMANAGER)) {
         	   // get the image node
         	  int imageNumber = node.getIntValue("id");
         	   // return the last modified from the image node
         	   return node.getCloud().getNode(imageNumber).getDateValue(lastModifiedField).getTime();
           } else {
               return -1;
           }
       } catch (IOException ieo) {
           return -1;
       }
   }


   /**
    * This method has been override to get the lastmodified parameter available here in the override image servlet
    * {@inheritDoc}
    */
	@Override
   public void init() throws ServletException {
       super.init();
       lastModifiedField = getInitParameter("lastmodifiedfield");
       if ("".equals(lastModifiedField)) lastModifiedField = null;
       log = Logging.getLoggerInstance(ImageServlet.class);
       if (lastModifiedField != null) {
           log.service("Field '" + lastModifiedField + "' will be used to calculate lastModified");
       }
   }
}
