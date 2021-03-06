package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.EmailSender;

public class RegisterPortlet extends AbstractLoginPortlet {

   public static final String ACEGI_SECURITY_FORM_EMAIL_KEY = "email";
   public static final String ACEGI_SECURITY_FORM_FIRSTNAME_KEY = "firstName";
   public static final String ACEGI_SECURITY_FORM_INFIX_KEY = "infix";
   public static final String ACEGI_SECURITY_FORM_LASTNAME_KEY = "lastName";
   public static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "passwordText";
   public static final String ACEGI_SECURITY_FORM_PASSWORDCONF_KEY = "passwordConfirmation";
   public static final String ACEGI_SECURITY_FORM_TERMS = "agreedToTerms";
   public static final String ACEGI_SECURITY_DEFAULT = "defaultmessages";
   public static final String AUTHENTICATION_ID_KEY = "authenticationId";
   public static final String GROUPNAME = "groupName";

   public static final String ALLGROUPNAMES = "allGroupNames";
   public static final String NOGROUP = "nogroup";
   protected static final String ERRORMESSAGES = "errormessages";

   public static final String USE_TERMS = "useterms";
   public static final String TERMS_PAGE = "termsPage";

   private static final Log log = LogFactory.getLog(RegisterPortlet.class);

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      PortletPreferences preferences = req.getPreferences();
      setAttribute(req, GROUPNAME, preferences.getValue(GROUPNAME, ""));
      setAttribute(req, ALLGROUPNAMES, getAllGroupNames());
      setAttribute(req, USE_TERMS, preferences.getValue(USE_TERMS, ""));

      String pageid = preferences.getValue(TERMS_PAGE, null);
      if (StringUtils.isNotEmpty(pageid)) {
         String pagepath = SiteManagement.getPath(Integer.valueOf(pageid), true);

         if (pagepath != null) {
            setAttribute(req, "pagepath", pagepath);
         }
      }

      super.doEditDefaults(req, res);
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      setPortletParameter(portletId, GROUPNAME, request.getParameter(GROUPNAME));
      setPortletParameter(portletId, USE_TERMS, request.getParameter(USE_TERMS));
      setPortletNodeParameter(portletId, TERMS_PAGE, request.getParameter(TERMS_PAGE));

      super.processEditDefaults(request, response);
   }

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {

      Map<String, String> errorMessages = new HashMap<String, String>();
      PortletPreferences preferences = request.getPreferences();

      String emailTo = request.getParameter(ACEGI_SECURITY_FORM_EMAIL_KEY).trim();
      String firstName = request.getParameter(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
      String infix = request.getParameter(ACEGI_SECURITY_FORM_INFIX_KEY);
      String lastName = request.getParameter(ACEGI_SECURITY_FORM_LASTNAME_KEY);
      String passwordText = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);

      Long authId = null;

      if (StringUtils.isBlank(emailTo)) {
         errorMessages.put(ACEGI_SECURITY_FORM_EMAIL_KEY, "register.email.empty");
      } else if (!EmailSender.isEmailAddress(emailTo)) {
         errorMessages.put(ACEGI_SECURITY_FORM_EMAIL_KEY, "register.email.match");
      }

      validateInputFields(request, errorMessages, preferences);

      if (errorMessages.isEmpty()) { //Only continue with Community checks, when there are no other errors yet.
      
         AuthenticationService authenticationService = (AuthenticationService) ApplicationContextFactory.getBean("authenticationService");
         PersonService personHibernateService = (PersonService) ApplicationContextFactory.getBean("personService");
         Long authenticationId = authenticationService.getAuthenticationIdForUserId(emailTo);
   
         if (authenticationId == null) {
            // TODO check if password is mandatory
            //if (passwordText == null) passwordText="";
            Authentication authentication = authenticationService.createAuthentication(emailTo, passwordText);
            if (authentication.getId() != null) {
               authId = authentication.getId();
   
               //If the names are not needed in the form, they can be emptied and stored.
               if (firstName == null) firstName="";
               if (infix == null) infix="";
               if (lastName == null) lastName = "";
               
               Person person = personHibernateService.createPerson(firstName, infix, lastName, authId, RegisterStatus.UNCONFIRMED.getName(), new Date());
               person.setEmail(emailTo);
               personHibernateService.updatePerson(person);
   
               String groupName = preferences.getValue(GROUPNAME, null);
               if (null != groupName && !NOGROUP.equals(groupName)) {
                  authenticationService.addAuthorityToUserByAuthenticationId(authId.toString(), groupName);
               }
   
               String emailSubject = preferences.getValue(EMAIL_SUBJECT, "Your account details associated with the given email address.\n");
               String emailText = preferences.getValue(EMAIL_TEXT, null);
               String emailFrom = preferences.getValue(EMAIL_FROMEMAIL, null);
               String nameFrom = preferences.getValue(EMAIL_FROMNAME, "Senders Name"); //Add default name
   
               emailText = getEmailBody(emailText, request, authentication, person);
               try {
                  if (!EmailSender.isEmailAddress(emailFrom)) {
                     errorMessages.put(ACEGI_SECURITY_DEFAULT, "Email address '" + emailFrom + "' set in the edit_defaults properties is not available or working!");
                  } else {
                     EmailSender.sendEmail(emailFrom, nameFrom, emailTo, emailSubject, emailText, emailTo, "text/plain;charset=utf-8");
                  }
               } catch (AddressException e) {
                  log.error("Email address failed", e);
                  errorMessages.put(ACEGI_SECURITY_DEFAULT, "One of the email addresses failed '" + emailFrom + "' or '" + emailTo + "'!"); 
               } catch (MessagingException e) {
                  log.error("Email MessagingException failed", e);
                  errorMessages.put(ACEGI_SECURITY_DEFAULT, "Sending email failed, from '" + emailFrom + "' to '" + emailTo + "'!");
               }
               if (errorMessages.isEmpty()) {
                  response.setRenderParameter(ACEGI_SECURITY_FORM_EMAIL_KEY, emailTo);
               } else {
                  //Email could not be sent, but the user is created..should remove user & authentication
                  personHibernateService.deletePersonByAuthenticationId(authId);
                  authenticationService.deleteAuthentication(authId);
               }

            } else {
               log.error("adding of authenticationId in database failed");
               errorMessages.put(ACEGI_SECURITY_DEFAULT, "adding of authenticationId in database failed");
            }
         } else {
            if (!isExistingUserAllowed()) {
               errorMessages.put(ACEGI_SECURITY_DEFAULT, "register.user.exists");
            } else {
               authId = authenticationId;
               response.setRenderParameter("active", "subscribed");
               request.getPortletSession().setAttribute("active", "subscribed");
            }
         }
      }

      if (errorMessages.size() > 0) {
         request.getPortletSession().setAttribute(ERRORMESSAGES, errorMessages);
         request.getPortletSession().setAttribute(ACEGI_SECURITY_FORM_EMAIL_KEY, emailTo);
         request.getPortletSession().setAttribute(ACEGI_SECURITY_FORM_FIRSTNAME_KEY, firstName);
         request.getPortletSession().setAttribute(ACEGI_SECURITY_FORM_INFIX_KEY, infix);
         request.getPortletSession().setAttribute(ACEGI_SECURITY_FORM_LASTNAME_KEY, lastName);
      } else {
          request.setAttribute(AUTHENTICATION_ID_KEY, authId);
      }
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      String screenId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      PortletSession portletSession = request.getPortletSession();

      request.setAttribute("page", screenId);
      String template;
      
      Map<String, String> errorMessages = (HashMap<String, String>) portletSession.getAttribute(ERRORMESSAGES);
      
      String email = request.getParameter("email");
      String active = request.getParameter("active");
      
      if (StringUtils.isNotEmpty(active)) {
         request.setAttribute("active", active);
         template = getTemplate("register_success");
      } else {
         if (StringUtils.isNotEmpty(email)) {
            template = getTemplate("register_success");
         } else {
            template = getTemplate("register");
         }
      }

      if (errorMessages != null && errorMessages.size() > 0) {
         String emailTo = (String) portletSession.getAttribute(ACEGI_SECURITY_FORM_EMAIL_KEY);
         String firstName = (String) portletSession.getAttribute(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
         String infix = (String) portletSession.getAttribute(ACEGI_SECURITY_FORM_INFIX_KEY);
         String lastName = (String) portletSession.getAttribute(ACEGI_SECURITY_FORM_LASTNAME_KEY);
         portletSession.removeAttribute(ERRORMESSAGES);
         portletSession.removeAttribute(ACEGI_SECURITY_FORM_EMAIL_KEY);
         portletSession.removeAttribute(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
         portletSession.removeAttribute(ACEGI_SECURITY_FORM_INFIX_KEY);
         portletSession.removeAttribute(ACEGI_SECURITY_FORM_LASTNAME_KEY);
         request.setAttribute(ERRORMESSAGES, errorMessages);
         request.setAttribute(ACEGI_SECURITY_FORM_EMAIL_KEY, emailTo);
         request.setAttribute(ACEGI_SECURITY_FORM_FIRSTNAME_KEY, firstName);
         request.setAttribute(ACEGI_SECURITY_FORM_INFIX_KEY, infix);
         request.setAttribute(ACEGI_SECURITY_FORM_LASTNAME_KEY, lastName);
      }

      doInclude("view", template, request, response);
   }

   protected void validateInputFields(ActionRequest request, Map<String, String> errorMessages, PortletPreferences preferences) {
       String firstName = request.getParameter(ACEGI_SECURITY_FORM_FIRSTNAME_KEY);
       String lastName = request.getParameter(ACEGI_SECURITY_FORM_LASTNAME_KEY);
       String passwordText = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);
       String passwordConfirmation = request.getParameter(ACEGI_SECURITY_FORM_PASSWORDCONF_KEY);

      if (StringUtils.isBlank(firstName)) {
         errorMessages.put(ACEGI_SECURITY_FORM_FIRSTNAME_KEY, "register.firstname.empty");
      }
      if (StringUtils.isBlank(lastName)) {
         errorMessages.put(ACEGI_SECURITY_FORM_LASTNAME_KEY, "register.lastname.empty");
      }

      if (StringUtils.isEmpty(passwordText)) {
         errorMessages.put(ACEGI_SECURITY_FORM_PASSWORD_KEY, "register.password.empty");
      }
      if (!passwordText.equals(passwordConfirmation)) {
         errorMessages.put(ACEGI_SECURITY_FORM_PASSWORD_KEY, "register.passwords.not_equal");
      }

      String terms = request.getParameter(ACEGI_SECURITY_FORM_TERMS);
      if (preferences.getValue(USE_TERMS, "").equalsIgnoreCase("yes") && StringUtils.isBlank(terms)) {
         errorMessages.put(ACEGI_SECURITY_FORM_TERMS, "register.terms.agree");
      }
   }

   protected Set<String> getAllGroupNames() {
      AuthorityService authorityService = (AuthorityService) ApplicationContextFactory.getBean("authorityService");
      return new TreeSet<String>(authorityService.getAuthorityNames());
   }
   
   /**
    * Returns jsp template for given key. Can be overridden with a specific view.
    * 
    * Currently these keys are used:
    * <ul>
    * <li>register_success</li>
    * <li>register</li>
    * </ul> 
    * @param key
    * @return
    */
   protected String getTemplate(String key) {
       if ("register_success".equals(key)) {
           return "login/register_success.jsp";
       }
       if ("register".equals(key)) {
           return "login/register.jsp";
       }
       throw new IllegalArgumentException("Unknown key: " + key);
   }
   
   /* 
    * For newsletter registration it is okay to have an existing user
    * for ordinary registration it is not okay.
    */
   protected boolean isExistingUserAllowed() {
       return false;
   }
   
}
