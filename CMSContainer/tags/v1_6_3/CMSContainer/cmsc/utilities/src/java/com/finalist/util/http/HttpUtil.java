package com.finalist.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.mmbase.PropertiesUtil;

public class HttpUtil {

   private static Log log = LogFactory.getLog(HttpUtil.class);
   
   public static String doGet(String url, Map<String, Object> parameterMap) throws PageNotFoundException {
      GetMethod method = new GetMethod(url);
      if (parameterMap != null) {
         StringBuffer queryString = createQueryString(parameterMap);
         method.setQueryString(queryString.toString());
      }
      
      return doRequest(url, method);
   }

   private static StringBuffer createQueryString(Map<String, Object> parameterMap) {
      StringBuffer queryString = new StringBuffer();
      for (Iterator<String> i = parameterMap.keySet().iterator(); i.hasNext();) {
         String key = i.next();
         Object objectValue = parameterMap.get(key);
         String value = null;
         if (objectValue instanceof String) {
            value = (String) objectValue;
         }
         if (objectValue instanceof String[]) {
            value = ((String[]) objectValue)[0];
         }

         try {
            String queryKey = URLEncoder.encode(key, "UTF-8");
            String queryValue = URLEncoder.encode(value, "UTF-8");

            queryString.append(queryKey);
            queryString.append("=");
            queryString.append(queryValue);
            if (i.hasNext()) {
               queryString.append("&");
            }
         }
         catch (UnsupportedEncodingException e) {
            log.debug("" + e.getMessage(), e);
         }
      }
      return queryString;
   }

   public static String doPost(String url, Map<String, Object> parameterMap) throws PageNotFoundException {
      PostMethod method = new PostMethod(url);
      if (parameterMap != null) {
         for (Iterator<String> i = parameterMap.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            Object objectValue = parameterMap.get(key);
            String value = null;
            if (objectValue instanceof String) {
               value = (String) objectValue;
            }
            if (objectValue instanceof String[]) {
               value = ((String[]) objectValue)[0];
            }
            method.addParameter(key, value);
         }
      }
      return doRequest(url, method);
   }

   public static String doRequest(String url, HttpMethod method)
         throws PageNotFoundException {
      String result;
      try {
         int response = getHttpClient().executeMethod(method);

         if (response == HttpStatus.SC_NOT_FOUND || response == HttpStatus.SC_FORBIDDEN) {
            throw new PageNotFoundException("Page does not exist: " + url);   
         }
         
         if (response != HttpStatus.SC_OK) {
            log.info(" HTTP response code: " + response);
         }
         result = method.getResponseBodyAsString();
      }
      catch (IOException e) {
         throw new PageNotFoundException("Could not read url " + url, e);
      }
      finally {
         method.releaseConnection();
      }

      if (result == null) {
         throw new PageNotFoundException("Could not read " + url);
      }

      if (result.contains("<title>Error ")) {
         throw new PageNotFoundException("Page does not exist: " + url);
      }
      if (result.contains("<title>The page cannot be found</title>")) {
         throw new PageNotFoundException("Page does not exist: " + url);
      }
      if (result.contains("<title>Directory Listing Denied</title>")) {
         throw new PageNotFoundException("Page does not exist: " + url);
      }

      return result;
   }
   
   private static HttpClient getHttpClient() {
      return getHttpClient("Jakarta Commons-HttpClient/3.0 (CMS Container)", true, 0, 10);
   }
   
   public static HttpClient getHttpClient(String userAgent, boolean useProxy, int timeout, int retry) {
      if (log.isDebugEnabled()) {
         log.debug("A new HttpClient instance is needed ...");
      }

      if (StringUtils.isEmpty(userAgent)) {
         // Some web servers don't allow the default user-agent sent by httpClient
         userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"; 
      }
      
      HttpClient cl = new HttpClient(new MultiThreadedHttpConnectionManager());
      cl.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);

      if (timeout > 0) {
         cl.getParams().setParameter(HttpConnectionManagerParams.SO_TIMEOUT, timeout);
         cl.getParams().setParameter(HttpConnectionManagerParams.CONNECTION_TIMEOUT, timeout);
      }
      
      if (retry > 1) {
         // Retry 10 times, even when request was already fully sent
         DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(retry, true);
         cl.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryhandler);
      }
      
      HostConfiguration hc = new EasySSLHostConfiguration();

      HttpState state = new HttpState();
      cl.setHostConfiguration(hc);
      cl.setState(state);
      if (useProxy) {
         addProxy(cl);
      }

      if (log.isDebugEnabled()) {
         log.debug("New HttpClient instance created.");
      }
      return cl;
   }

   public static void addProxy(HttpClient cl) {
      HostConfiguration hc = cl.getHostConfiguration();
      HttpState state = cl.getState();
      
      String proxyHost = getProxyHost();
      if (StringUtils.isNotEmpty(proxyHost)) {
         int proxyPort = getProxyPort();
         hc.setProxy(proxyHost, proxyPort);

         if (log.isDebugEnabled()) {
            log.debug("Proxy Host:" + proxyHost);
            log.debug("Proxy Port:" + proxyPort);
         }

         String proxyUser = getProxyUser();
         String proxyPassword = getProxyPassword();
         if (StringUtils.isNotEmpty(proxyUser) && proxyPassword != null) {
            if (log.isDebugEnabled()) {
               log.debug("Proxy User:" + proxyUser);
            }

            Credentials credentials;

            String proxyNtlmHost = getProxyNtlmHost();
            if (StringUtils.isNotEmpty(proxyNtlmHost)) {
               credentials = new NTCredentials(proxyUser, 
                     proxyPassword, proxyNtlmHost, getProxyNtlmDomain());
            }
            else {
               credentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
            }

            state.setProxyCredentials(AuthScope.ANY, credentials);
         }
      }
      else {
         if (log.isDebugEnabled()) {
            log.debug("Not using a proxy");
         }
      }
   }

   public static  String getProxyHost() {
      return getProxySetting("http.proxyHost");
   }

   public static  int getProxyPort() {
      String value = getProxySetting("http.proxyPort");
      if (StringUtils.isNotEmpty(value)) {
         return Integer.parseInt(value);
      }
      return -1;
   }

   public static  String getProxyUser() {
      return getProxySetting("http.proxyUser");
   }

   public static  String getProxyPassword() {
      return getProxySetting("http.proxyPassword");
   }

   public static  String getProxyNtlmHost() {
      return getProxySetting("http.auth.ntlm.host");
   }

   public static  String getProxyNtlmDomain() {
      return getProxySetting("http.auth.ntlm.domain");
   }

   public static  String getProxySetting(String setting) {
      String value = PropertiesUtil.getProperty(setting);
      if (StringUtils.isEmpty(value)) {
         value = System.getProperty(setting);
      }
      return value;
   }

}
