/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.stats;

import java.io.IOException;

import javax.naming.*;
import javax.servlet.jsp.PageContext;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.cmsc.util.EncodingUtil;
import com.finalist.cmsc.util.ServerUtil;

public class GoogleAnalyticsTag extends CmscTag {
   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(GoogleAnalyticsTag.class.getName());

   private final static String TYPE_BASIC = "basic"; // init and page counter code (default)
   private final static String TYPE_SCRIPT = "script"; // init script
   private final static String TYPE_PAGE_COUNTER = "pagecounter"; // page counter code
   private final static String TYPE_EVENT = "event"; // event code, category and action are required
   private static boolean isLiveProduction;

   private String accountParameter;
   private String categoryParameter;
   private String actionParameter;
   private String nodeNumberParameter;
   private String labelParameter;
   private String valueParameter;
   private String typeParameter = TYPE_BASIC;
   private boolean force; //testing purposes
   

   private static String contextAccount;
   static {
      InitialContext context;
      try {
         isLiveProduction = (ServerUtil.isProduction() && (ServerUtil.isLive() || ServerUtil.isSingle()));
         
         context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         contextAccount = (String) env.lookup("googleAnalytics/account");
      } catch (NamingException e) {
         log.info("No default Google Analytics account found in the context. Provide account information as attribute.");
      }
   }

   @Override
   public void doTag() throws IOException {
      /*
       * Find out where to get the Google Analytics account from, search order: 
       * 1) The "account"-parameter passed to the tag (only when available, live and production) 
       * 2) The "googleAnalytics/account" setting in the context XML (only when available, one of the two preferred methods) 
       * 3) The "googleanalytics.account" system property, from the system properties (only  when available, live and production)
       * 4) The googleanalyticsId field of the site (only when available, the other preferred method)
       */
      String account = null;
      
      String parameterAccount = PropertiesUtil.getProperty("googleanalytics.account");
      
      if (force) {
         if (ServerUtil.isProduction()) {
            log.error("Google Analytics tag: the 'force' attribute should NOT be used in production environments!");
         }
      }
      
      if (StringUtils.isNotBlank(accountParameter) && (isLiveProduction || force)) {
         account = accountParameter;
      } else if (StringUtils.isNotBlank(contextAccount)) {
         account = contextAccount;
      } else if (StringUtils.isNotBlank(parameterAccount) && (isLiveProduction || force)) {
         account = parameterAccount;
      } else if (isLiveProduction || force) {
         Site site = SiteManagement.getSiteFromPath(getPath());
         account = site.getGoogleanalyticsid();
      }

      // Include the Google Analytics code
      if (StringUtils.isNotBlank(account)) {
         
         PageContext ctx = (PageContext) getJspContext();
         final String domain = ctx.getRequest().getServerName();

         StringBuilder javascript = new StringBuilder();
         if (typeParameter.equals(TYPE_BASIC)) {
            appendPageCounter(javascript, account, domain);
            appendGAScript(javascript);
         }
         if (typeParameter.equals(TYPE_SCRIPT)) {
            appendGAScript(javascript);
         }
         if (typeParameter.equals(TYPE_PAGE_COUNTER)) {
            appendPageCounter(javascript, account, domain);
         }

         if (typeParameter.equals(TYPE_EVENT)) {
            appendTrackEvents(javascript);
         }
         
         ctx.getOut().write(javascript.toString());
      }
   }

   private void appendGAScript(StringBuilder javascript) {
      if (! typeParameter.equals(TYPE_BASIC)) {
         javascript.append("<script type=\"text/javascript\">\r\n");
      }
      javascript.append("try{\r\n" +
            " (function() {\r\n" + 
      		"    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\r\n" + 
      		"    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\r\n" + 
      		"    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\r\n" + 
      		"  })();\r\n" +
            "} catch(err) {}\r\n");
      javascript.append("</script>\r\n");
   }

   private void appendPageCounter(StringBuilder javascript, final String account, final String domain) {
      javascript.append("<script type=\"text/javascript\">\r\n");
      
      // Workaround for http://support.microsoft.com/kb/310676
      // Internet Explorer 6 does not set a cookie for two-letter domains like 12.nl
      
      javascript.append("var _gaq = _gaq || [];\r\n" +
                        "_gaq.push(['_setAccount', '").append(account).append("'],\r\n" +
                        "          ['_setDomainName', '").append(domain).append("'],\r\n" +
      		            "          ['_trackPageview']);\r\n");
      if (! typeParameter.equals(TYPE_BASIC)) {
         javascript.append("</script>\r\n");
      }
   }
   
   private void appendTrackEvents(StringBuilder javascript) {
      javascript.append("<script type=\"text/javascript\">");
      if (StringUtils.isNotBlank(nodeNumberParameter)) {
         actionParameter = getActionFromNodeNumber(nodeNumberParameter);
      }

      if (StringUtils.isBlank(categoryParameter)
            || StringUtils.isBlank(actionParameter)) {
         throw new IllegalArgumentException(
               "Both category and (action or nodeNumber) parameters are required when using type "
                     + TYPE_EVENT);
      }
      javascript.append("_gaq.push(['_trackEvent', '");
      javascript.append(escapeParameter(categoryParameter));
      javascript.append("', '");
      javascript.append(escapeParameter(actionParameter)+"'");
      if (StringUtils.isNotBlank(labelParameter)) {
         javascript.append(", '");
         javascript.append(escapeParameter(labelParameter) + "'");
         if (StringUtils.isNotBlank(valueParameter)) {
            javascript.append(", " + valueParameter);
         }
      }
      javascript.append("]);\r\n");
      javascript.append("</script>\r\n");
   }


   private String escapeParameter(String parameter) {
      return parameter.replace("'", "\\'");
   }

   private String getActionFromNodeNumber(String nodeNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
      Node node = cloud.getNode(nodeNumber);
      Node creationchannel = RepositoryUtil.getCreationChannel(node);

      String prefix = node.getNodeManager().getName();
      String path = "Unknown_Path";
      if (creationchannel != null) {
         String fullpath = creationchannel.getStringValue("path");
         path = StringUtils.removeStart(fullpath, "Repository/");
      }
      String title = EncodingUtil.convertNonAscii(node.getStringValue("title"));
      title = filterTitle(title);

      StringBuilder contentCounterName = new StringBuilder();
      contentCounterName.append(prefix);
      contentCounterName.append("/");
      contentCounterName.append(path);
      contentCounterName.append("/");
      contentCounterName.append(nodeNumber);
      contentCounterName.append("_");
      contentCounterName.append(title);
      return contentCounterName.toString();
   }

   private String filterTitle(String title) {
      // make sure the title will not break into different path nodes
      return title.replace('/', '_');
   }

   public void setAccount(String account) {
      this.accountParameter = account;
   }

   public void setType(String type) {
      if (type.equals(TYPE_BASIC) || type.equals(TYPE_EVENT)) {
         this.typeParameter = type;
      } else {
         throw new IllegalArgumentException(
               "type parameter should be empty, \"" + TYPE_BASIC
                     + "\", or \"" + TYPE_EVENT + "\"");
      }
   }

   public void setCategory(String categoryParameter) {
      this.categoryParameter = categoryParameter;
   }

   public void setNodeNumber(String nodeNumberParameter) {
      this.nodeNumberParameter = nodeNumberParameter;
   }

   public void setAction(String actionParameter) {
      this.actionParameter = actionParameter;
   }

   public void setLabel(String labelParameter) {
      this.labelParameter = labelParameter;
   }

   public void setValue(String valueParameter) {
      this.valueParameter = valueParameter;
   }
   
   public void setForce(boolean force) {
      this.force = force;
   }

   public boolean isForce() {
      return force;
   }
   
   /* For testing purposes only
   public static void main(String args[]) {
      GoogleAnalyticsTag gat = new GoogleAnalyticsTag();
      GoogleAnalyticsTag.contextAccount = "UA-17606XX-1";
      
      StringBuilder javascript = new StringBuilder();
      String domain = "www.cmscontainer.org";
      gat.categoryParameter ="content";
      gat.actionParameter = "action";
      
      gat.appendPageCounter(javascript,contextAccount,domain);
      gat.appendGAScript(javascript);
      
      gat.appendTrackEvents(javascript);
      
      System.out.println(javascript.toString());
   }
   */

}
