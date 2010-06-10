package com.finalist.cmsc.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;

public class ConfirmAction extends Action{

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

      String authId = httpServletRequest.getParameter("s");
      String pageNumber = httpServletRequest.getParameter("pn");
      String name = httpServletRequest.getParameter("nm");
      String returnUrl = "";
      String target = "failure";
      if (StringUtils.isBlank(pageNumber)) {
         throw new NullPointerException("The page number is null");
      }
      
      // Note: Kristian and Nico
      // Do we also need to replace the HOSTNAME? Is the page always on the same site? See LinkTag.java
      
      returnUrl = SiteManagement.getPath(Integer.parseInt(pageNumber), !ServerUtil.useServerName());
      returnUrl += "/_rp_".concat(name).concat("_").concat("active").concat("/1_");
      if (authId != null) {
         PersonService personService = (PersonService)ApplicationContextFactory.getBean("personService");
         Long authenticationId =Long.parseLong(authId);
         if(authenticationId > 0) {
            Person person = personService.getPersonByAuthenticationId(authenticationId);
            if(person != null) {
               if (person.getActive().equalsIgnoreCase(RegisterStatus.ACTIVE.getName())) {
                  target = "actived";
               }
               else {
                  person.setActive(RegisterStatus.ACTIVE.getName());
                  personService.updatePerson(person);
                  target = "success";
               }
            }
         }
      }
      returnUrl += target;
      returnUrl = HttpUtil.getWebappUri(httpServletRequest) + returnUrl;
      
      httpServletResponse.sendRedirect(httpServletResponse.encodeRedirectURL(returnUrl));
      return null;
   }
}
