package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

/**
 * using for deleting newsletter subscriber from newsletter
 *
 * @author Lisa
 */
public class NewsletterSubscriberDeleteAction extends MMBaseFormlessAction {

   private NewsletterSubscriptionServices service;
   
   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return refreshing newsletter subscriber list
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String authId = request.getParameter("authid");
      String newsletterId = request.getParameter("newsletterId");

      if (StringUtils.isNotBlank(authId)) {
         Node newsletterNode = cloud.getNode(Integer.parseInt(newsletterId));
         List<Node> subscriptions = newsletterNode.getRelatedNodes(cloud.getNodeManager("subscriptionrecord"));

         for (Node subscription : subscriptions) {
            String subscriberId = subscription.getStringValue("subscriber");

            if (subscriberId.equals(authId)) {
               service = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
               service.modifyStauts(Integer.parseInt(authId), Integer.parseInt(newsletterId), "INACTIVE");
            }
         }
      }
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }
}