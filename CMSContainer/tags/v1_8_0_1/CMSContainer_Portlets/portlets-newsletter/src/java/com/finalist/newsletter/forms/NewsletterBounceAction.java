package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.domain.NewsletterBounce;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.util.NewsletterBounceUtil;

public class NewsletterBounceAction extends DispatchAction {

   public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      int pageSize = 12;
      int offset = 0;
      if (StringUtils.isNotEmpty(PropertiesUtil.getProperty("repository.search.results.per.page"))) {
         pageSize = Integer.parseInt(PropertiesUtil.getProperty("repository.search.results.per.page"));
      }

      String from = request.getParameter("from");
      if(from != null) {
         request.getSession().setAttribute("bounce_page_from",from);
      }
      String[] check_items = request.getParameterValues("chk_items");
      
      if(check_items != null && check_items.length > 0) {
         String type = request.getParameter("type");
         if ("bounce".equalsIgnoreCase(type)) {
            for (String check_item : check_items) {
               //delete bounce
               NewsletterBounceUtil.deleteBounce(check_item);
            }
         }
         else if ("member".equalsIgnoreCase(type)) {
            for (String check_item : check_items) {
               //delete member
               String authId = NewsletterBounceUtil.deleteMember(check_item);
               CommunityModuleAdapter.deleteSubscriber(authId);
               
            }
         }
      }
      
      String strOffset = request.getParameter("offset");
      String direction = request.getParameter("direction");
      String order = request.getParameter("order");
      String newsletterId = request.getParameter("newsletterId");
      if (StringUtils.isNotEmpty(strOffset)) {
         offset = Integer.parseInt(strOffset);
      }
      if(StringUtils.isNotEmpty(newsletterId) && "all".equalsIgnoreCase(newsletterId)) {
         newsletterId = null;
      }
      List<NewsletterBounce> bounces = NewsletterBounceUtil.getBounceRecords(offset * pageSize, pageSize, order,
            direction,newsletterId);
      int count = NewsletterBounceUtil.getTotalCount(newsletterId);
      request.setAttribute("resultList", bounces);
      request.setAttribute("resultCount", count);
      request.setAttribute("offset", offset);
      request.setAttribute("direction", direction);
      request.setAttribute("order", order);
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }

   public ActionForward getItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      String number = request.getParameter("objectnumber");
      NewsletterBounce bounce = NewsletterBounceUtil.getNewsletterBounce(Integer.parseInt(number));
      request.setAttribute("bounce", bounce);
      return mapping.findForward("info");
   }
}
