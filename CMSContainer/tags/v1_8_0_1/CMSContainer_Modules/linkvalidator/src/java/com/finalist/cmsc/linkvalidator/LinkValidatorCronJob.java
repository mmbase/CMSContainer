package com.finalist.cmsc.linkvalidator;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.mmbase.applications.crontab.AbstractCronJob;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.util.ServerUtil;
import com.finalist.util.http.HttpUtil;

public class LinkValidatorCronJob extends AbstractCronJob implements CronJob {

   private static final String URL_MANAGER = "urls";
   private static final String URL_FIELD = "url";
   private static final String VALID_FIELD = "valid";

   private static final int TIMEOUT = 15000;

   private static final Logger log = Logging.getLoggerInstance(LinkValidatorCronJob.class.getName());

   @Override
   public void run() {
      if (ServerUtil.isReadonly()) {
         return;
      }
      checkExternalLinks();
   }

   private void checkExternalLinks() {
      log.info("LinkValidation thread started");

      HttpClient cl = HttpUtil.getHttpClient(null, true, TIMEOUT, 0);
      LinkChecker checker = new LinkChecker(cl);

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

      NodeQuery urlQuery = cloud.getNodeManager(URL_MANAGER).createQuery();
      SearchUtil.addSortOrder(urlQuery, urlQuery.getNodeManager(), URL_FIELD, "UP");
      HugeNodeListIterator iterator = new HugeNodeListIterator(urlQuery);

      while (iterator.hasNext()) {
         Node linkNode = iterator.nextNode();
         String url = linkNode.getStringValue(URL_FIELD);
         boolean valid;
         if (url.startsWith("#") || url.startsWith("mailto:")) {
            valid = true;
         }
         else {
            valid = checker.isValid(url);
         }
         log.debug("Found url: [" + url + "] (" + valid + ")");
         if (linkNode.getBooleanValue(VALID_FIELD) != valid) {
            linkNode.setBooleanValue(VALID_FIELD, valid);
            linkNode.commit();
         }
      }
      log.info("LinkValidation thread done");
   }

}
