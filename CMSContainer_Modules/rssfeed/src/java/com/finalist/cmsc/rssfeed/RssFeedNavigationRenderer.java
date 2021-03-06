package com.finalist.cmsc.rssfeed;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.rssfeed.beans.om.RssFeed;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.*;

public class RssFeedNavigationRenderer implements NavigationItemRenderer {

   private Log log = LogFactory.getLog(RssFeedNavigationRenderer.class);

   /**
    * The date format defined in RFC 822 and used in RSS feeds.
    * See e.g. http://www.faqs.org/rfcs/rfc822.html
    */
   private final static SimpleDateFormat formatRFC822Date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

   public String getContentType() {
      return "application/rss+xml";
   }

   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig) {

      if (item instanceof RssFeed) {
         RssFeed rssFeed = (RssFeed) item;

         String rssLink = rssFeed.getLink();
         if (StringUtils.isEmpty(rssLink)) {
             rssLink = getSiteUrl(request, rssFeed);
         }         
         
         Document doc = XmlUtil.createDocument();
         Element rss = XmlUtil.createRoot(doc, "rss");
         XmlUtil.createAttribute(rss, "version", "2.0");
         Element channel = XmlUtil.createChild(rss, "channel");
         XmlUtil.createChildText(channel, "title", rssFeed.getTitle());         
         XmlUtil.createChildText(channel, "link", rssLink);
         XmlUtil.createChildText(channel, "language", rssFeed.getLanguage());
         XmlUtil.createChildText(channel, "description", rssFeed.getDescription());
         XmlUtil.createChildText(channel, "copyright", rssFeed.getCopyright());
         XmlUtil.createChildText(channel, "managingEditor", rssFeed.getEmail_managing_editor());
         XmlUtil.createChildText(channel, "webMaster", rssFeed.getEmail_webmaster());
         XmlUtil.createChildText(channel, "generator", "CMS Container RssFeed");
         XmlUtil.createChildText(channel, "docs", "http://www.rssboard.org/rss-specification");

         List<String> contentTypesList = rssFeed.getContenttypes();
         int channelNumber = rssFeed.getChannel();

         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node contentNode = cloud.getNode(channelNumber);

         int maxAgeInDays = rssFeed.getMax_age_in_days();

         boolean useLifecycle = true;
         int maxNumber = rssFeed.getMaximum();
         if (maxNumber <= 0) {
            maxNumber = -1;
         }

         Date lastChange = buildItemsPerChannel(request, channel, contentTypesList, cloud, contentNode, maxAgeInDays, useLifecycle, maxNumber, null, true);

         if (lastChange != null) {
            XmlUtil.createChildText(channel, "lastBuildDate", formatRFC822Date.format(lastChange));
         }

         try {
            response.getWriter().write(XmlUtil.serializeDocument(doc));
         } catch (IOException e) {
            log.error(e);
         }
      } else {
         throw new IllegalArgumentException("Got a wrong type in the RssFeedNavigationRenderer (only wants RssFeed), was" + item.getClass());
      }
   }

   private Date buildItemsPerChannel(HttpServletRequest request, Element channel, List<String> contentTypesList, Cloud cloud, Node contentChannel, int maxAgeInDays, boolean useLifecycle, int maxNumber, Date lastChange, boolean first) {
      NodeQuery query = RepositoryUtil.createLinkedContentQuery(contentChannel, contentTypesList, ContentElementUtil.PUBLISHDATE_FIELD, "down", useLifecycle, null, 0, maxNumber, -1, -1, -1, null);
      // Add constraint: max age in days
      if (maxAgeInDays > 0) {
         SearchUtil.addDayConstraint(query, cloud.getNodeManager(RepositoryUtil.CONTENTELEMENT), ContentElementUtil.PUBLISHDATE_FIELD, "-" + maxAgeInDays);
      }
      NodeList results = query.getNodeManager().getList(query);
      for (NodeIterator ni = results.nodeIterator(); ni.hasNext();) {
         Node resultNode = ni.nextNode();
         Element itemE = XmlUtil.createChild(channel, "item");
         XmlUtil.createChildText(itemE, "title", resultNode.getStringValue("title"));

         String uniqueUrl = makeAbsolute(getContentUrl(resultNode), request);
         XmlUtil.createChildText(itemE, "link", uniqueUrl);

         String description = null;
         if (resultNode.getNodeManager().hasField("intro")) {
            description = resultNode.getStringValue("intro");
         }
         if ((description == null || description.length() == 0) && resultNode.getNodeManager().hasField("body")) {
            description = resultNode.getStringValue("body");
            if (description.indexOf("<br/>") != -1) {
               description = description.substring(0, description.indexOf("<br/>"));
            }
         }
         if (description != null) {
            description = description.replaceAll("<.*?>", "");
         }
         XmlUtil.createChildText(itemE, "description", description);
         XmlUtil.createChildText(itemE, "pubDate", formatRFC822Date.format(resultNode.getDateValue("publishdate")));
         XmlUtil.createChildText(itemE, "guid", uniqueUrl);

         if (first) {
            NodeList images = resultNode.getRelatedNodes("images", "imagerel", "destination");
            if (images.size() > 0) {
               Node image = ResourcesUtil.getImageNode(images.getNode(0), 160, 120); // height by width
               String imageUrl = image.getFunctionValue("servletpath", null).toString();

               Element imageE = XmlUtil.createChild(channel, "image");
               XmlUtil.createChildText(imageE, "url", imageUrl);
               XmlUtil.createChild(imageE, "title");
               XmlUtil.createChildText(imageE, "link", uniqueUrl);
            }
         }

         Date change = resultNode.getDateValue("lastmodifieddate");
         if (lastChange == null || change.getTime() > lastChange.getTime()) {
            lastChange = change;
         }
      }
      return lastChange;
   }

   private String getSiteUrl(HttpServletRequest request, RssFeed rss) {
      if (ServerUtil.useServerName()) {
         return getServerDocRoot(request);
      } else {
         String site = SiteManagement.getSite(rss);
         return getServerDocRoot(request) + site;
      }
   }

   private String getServerDocRoot(HttpServletRequest request) {
      return HttpUtil.getWebappUri(request);
   }

   private String getContentUrl(Node node) {
      return ResourcesUtil.getServletPathWithAssociation("content", "/content/*", node.getStringValue("number"), node.getStringValue("title"));
   }

   private String makeAbsolute(String url, HttpServletRequest request) {
      String webapp = HttpUtil.getServerDocRoot(request);
      if (url.startsWith("/")) {
         url = webapp + url.substring(1);
      } else {
         url = webapp + url;
      }
      return url;
   }
}
