package com.finalist.cmsc.linkvalidator;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class LinkChecker {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(LinkChecker.class.getName());

   /** The maximum number of redirections for a link. */
   private static final int MAX_NB_REDIRECT = 10;

   /** Use the get method to test pages. */
   private static final String GET_METHOD = "get";

   /** Use the head method to test pages. */
   private static final String HEAD_METHOD = "head";

   private HttpClient cl;

   public LinkChecker(HttpClient cl) {
      this.cl = cl;
   }

   public boolean isValid(String url) {
      // LCM-54: L1 uses urls with embedded spaces (works in IE but not in
      // Commons HTTP). Completely URLEncoding does not work either (slashes
      // should not be encoded for example), so just replaced all the spaces
      // with a plus sign.

      boolean valid = false;
      url = url.trim();

      try {
         HttpMethod httpget = checkLink(url, 0, GET_METHOD);
         
         int responseCode = httpget.getStatusCode();
         valid = responseCode == HttpStatus.SC_OK;
         
         if (!valid) {
            log.debug("Got responsecode (" + responseCode + ")for url : " + url);
         }
      }
      catch (HttpException ex) {
         log.debug("Found an invalid url : " + url, ex);
      }
      catch (IllegalArgumentException ex) {
         log.debug("Found an invalid url : " + url, ex);
      }
      catch (IOException ex) {
         log.debug("Got a IOException for url : " + url, ex);
      }
      catch (Throwable t) {
         log.debug("Got an unexpected Throwable for url : " + url, t);
      }
      return valid;
   }
   
   /**
    * Checks the given link.
    * 
    * @param link the link to check.
    * @param nbRedirect the number of current redirects.
    * @return HttpMethod
    * @throws IOException if something goes wrong.
    */
   public HttpMethod checkLink(String link, int nbRedirect, String method) throws IOException {
      if (nbRedirect > MAX_NB_REDIRECT) {
         throw new HttpException("Maximum number of redirections (" + MAX_NB_REDIRECT
               + ") exceeded");
      }

      try {
         URI escapedAlreadyUrl = new URI(link, true);
         link = escapedAlreadyUrl.toString();
      }
      catch (URIException e) {
         URI escapeUrl = new URI(link, false);
         link = escapeUrl.toString();
      }
      
      HttpMethod hm;

      if (HEAD_METHOD.equalsIgnoreCase(method)) {
         hm = new HeadMethod(link);
      }
      else {
         if (GET_METHOD.equalsIgnoreCase(method)) {
            hm = new GetMethod(link);
         }
         else {
            log.error("Unsupported method: " + method + ", using 'get'.");
            hm = new GetMethod(link);
         }
      }

      try {
         hm.setFollowRedirects(false);

         URL url = new URL(link);

         cl.getHostConfiguration().setHost(url.getHost(), url.getPort(), url.getProtocol());

         cl.executeMethod(hm);

         StatusLine sl = hm.getStatusLine();

         if (sl == null) {
            log.error("Unknown error validating link : " + link);
            return null;
         }

         if (hm.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
               || hm.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
               || hm.getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
            Header locationHeader = hm.getResponseHeader("location");

            if (locationHeader == null) {
               log.error("Site sent redirect, but did not set Location header");
               return hm;
            }

            String newLink = locationHeader.getValue();

            // Be careful to absolute/relative links
            if (!newLink.startsWith("http://") && !newLink.startsWith("https://")) {
               if (newLink.startsWith("/")) {
                  URL oldUrl = new URL(link);

                  newLink = oldUrl.getProtocol() + "://" + oldUrl.getHost()
                        + (oldUrl.getPort() > 0 ? ":" + oldUrl.getPort() : "") + newLink;
               }
               else {
                  newLink = link + newLink;
               }
            }
            
            HttpMethod oldHm = hm;

            if (log.isDebugEnabled()) {
               log.debug("[" + link + "] is redirected to [" + newLink + "]");
            }

            oldHm.releaseConnection();

            hm = checkLink(newLink, nbRedirect + 1, method);
         }
      }
      finally {
         hm.releaseConnection();
      }

      return hm;
   }

}
