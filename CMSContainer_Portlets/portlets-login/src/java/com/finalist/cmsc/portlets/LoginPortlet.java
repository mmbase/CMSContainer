/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.login.PasswordGenerator;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.Community;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.util.EmailSender;

/**
 * Login portlet
 *
 * @author Remco Bos
 */
public class LoginPortlet extends AbstractLoginPortlet {
   
   public static final String ERRORMESSAGE = "errormessage";

   protected static final String ACTION_PARAMETER = "action";

   protected static final String ACEGI_SECURITY_FORM_USERNAME_KEY = "j_username";
   protected static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "j_password";
   protected static final String EMAIL_TEMPLATE_DIR = "/WEB-INF/templates/view/login/forgotpassword.txt";
   
   protected static final String SEND_PASSWORD = "send_password";

   protected static final Log log = LogFactory.getLog(LoginPortlet.class);
   
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
   PortletException {
      super.DEFAULT_EMAIL_CONFIRM_TEMPLATE = EMAIL_TEMPLATE_DIR;
      super.doEditDefaults(req, res);
   }
   
   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String action = request.getParameter(ACTION_PARAMETER);
      PortletPreferences preferences = request.getPreferences();
      String username = request.getParameter(ACEGI_SECURITY_FORM_USERNAME_KEY);
      String password = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);
      String remoteUser =  request.getRemoteUser();
      if ("login".equalsIgnoreCase(action)) {

         String send_password =  request.getParameter(SEND_PASSWORD);
         
         if (StringUtils.isEmpty(send_password)) { 

            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
               if (StringUtils.isBlank(username)) {
                  response.setRenderParameter(ERRORMESSAGE, "register.email.empty");
               } 
               else {
                  response.setRenderParameter(ERRORMESSAGE, "register.password.empty");
               }
               return;
            }

            if (StringUtils.isNotEmpty(remoteUser)) {
               request.getPortletSession().setAttribute("username", remoteUser, PortletSession.APPLICATION_SCOPE);
               String pageid = preferences.getValue(PAGE, null);
               if (StringUtils.isNotEmpty(pageid)) {
                  Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
                  String redirectUrl = NavigationUtil.getNavigationItemUrl((HttpServletRequest)request, (HttpServletResponse)response, cloud.getNode(Integer.valueOf(pageid)));
                  response.sendRedirect(redirectUrl);
               }
            } else {
               PersonService personHibernateService = (PersonService) ApplicationContextFactory.getBean("personService");
               Person person = personHibernateService.getPersonByUserId(username);
               
               if (person != null && RegisterStatus.UNCONFIRMED.getName().equalsIgnoreCase(person.getActive())) {
                  response.setRenderParameter(ERRORMESSAGE, "view.account.unconfirmed");
               }
               else if (person != null && RegisterStatus.BLOCKED.getName().equalsIgnoreCase(person.getActive())) {
                  response.setRenderParameter(ERRORMESSAGE, "view.account.blocked");
               } else {
                  log.info(String.format("Login failed for user %s", username));
                  response.setRenderParameter(ERRORMESSAGE, "login.failed");
               }
            }
         }
         else {
            response.setRenderParameter(SEND_PASSWORD, "send");
         }
      } else if ("logout".equals(action)) {
         request.getPortletSession().removeAttribute("username", PortletSession.APPLICATION_SCOPE);
         Community.logout();
         
      } else if ("send_password".equals(action)) {
         //TODO  send password
         String email =  request.getParameter("username");
         String sendMessage = "view.account.success";
         AuthenticationService authenticationService = (AuthenticationService) ApplicationContextFactory
         .getBean("authenticationService");
         PersonService personHibernateService = (PersonService) ApplicationContextFactory.getBean("personService");

         if (authenticationService.authenticationExists(email)) {
            Person person = personHibernateService.getPersonByUserId(email);
            Authentication authentication = authenticationService.findAuthentication(email);
           if(RegisterStatus.ACTIVE.getName().equalsIgnoreCase(person.getActive()) || RegisterStatus.UNCONFIRMED.getName().equalsIgnoreCase(person.getActive()))
           {
              //todo reset password and send mail 
              PasswordGenerator generator = new PasswordGenerator();
              String newPassword = null;
              try {
                 newPassword = generator.generate(PasswordGenerator.PRINTABLE_CHARACTERS, 7);
                 authenticationService.updateAuthentication(email, authentication.getPassword(), newPassword);
                 authentication.setPassword(newPassword);
                 String emailSubject = preferences.getValue(EMAIL_SUBJECT,"Your account details associated with the given email address.\n");
                 String emailText = preferences.getValue(EMAIL_TEXT, null);
                 String emailFrom = preferences.getValue(EMAIL_FROMEMAIL, null);
                 String nameFrom = preferences.getValue(EMAIL_FROMNAME, null);
                 emailText = getEmailBody(emailText,request, authentication, person);
                 if (StringUtils.isNotBlank(emailFrom) && !EmailSender.isEmailAddress(emailFrom)) {
                    throw new AddressException("Email address '" + emailFrom + "' is not available");
                 }
                 EmailSender.sendEmail(emailFrom, nameFrom, email, emailSubject, emailText,
                             email, "text/plain;charset=utf-8");
                 sendMessage = "view.account.success";
              } 
              catch (AddressException e) {
                 log.error("Email address incorrect",e);
              } 
              catch (MessagingException e) {
                 log.error("Email MessagingException",e);
              }
              catch (Exception e) {
                 log.error(e);
              } 
           }
           else if (RegisterStatus.BLOCKED.getName().equalsIgnoreCase(person.getActive())){
              sendMessage = "view.account.blocked"; 
           }
         } 
         else {
            //log.info("add authenticationId failed");
            sendMessage = "view.account.notexist"; 
         }
         response.setRenderParameter(SEND_PASSWORD, sendMessage);
      } 
      else {
         // Unknown
         log.error(String.format("Unknown action '%s'", action));
      }
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      
      String template;
      String error = request.getParameter(ERRORMESSAGE);
      String send_password = request.getParameter(SEND_PASSWORD);

      if (StringUtils.isNotBlank(error)) {
         request.setAttribute(ERRORMESSAGE, error);
      }//Community.isAuthenticated()
      if (StringUtils.isNotEmpty(request.getRemoteUser())) {
         template = "login/logout.jsp";
      } else {
         template = "login/login.jsp";
      }
      if (StringUtils.isNotBlank(send_password)) {
         setAttribute(request, "sendMessage", send_password);
         template = "login/send_password.jsp";
      }
      doInclude("view", template, request, response);
   }
    @Override
   protected String getEmailBody(String emailText, ActionRequest request, Authentication authentication, Person person) {
      super.DEFAULT_EMAIL_CONFIRM_TEMPLATE = EMAIL_TEMPLATE_DIR;
      return formatConfirmationText(emailText,authentication,person,null);
   }

}
