package com.finalist.cmsc.community.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;

public class SingleSignOnFilter implements Filter{

   private static final String SESSION_KEY_SUBJECT = "session_key_subject";
   
   public void destroy() {
      
   }

   public void doFilter(ServletRequest sRequest, ServletResponse sResponse,
         FilterChain filterChain) throws IOException, ServletException {
     // org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (sRequest instanceof HttpServletRequest) {
         HttpServletRequest request = (HttpServletRequest)sRequest;
         org.acegisecurity.Authentication authentication = (org.acegisecurity.Authentication)request.getSession().getAttribute(SESSION_KEY_SUBJECT);
      
         if(authentication == null) {   
            String userId = request.getRemoteUser();
            
            if (StringUtils.isNotEmpty(userId)) {
              
               PersonService personLDAPService = (PersonService)ApplicationContextFactory.getBean("personLDAPService");
               AuthenticationService authenticationLDAPService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationLDAPService");
               AuthorityService authorityLDAPService = (AuthorityService) ApplicationContextFactory.getBean("authorityLDAPService");
               
               PersonService personService = (PersonService)ApplicationContextFactory.getBean("personService");
               AuthorityService authorityService = (AuthorityService) ApplicationContextFactory.getBean("authorityService");               
               AuthenticationService authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");

               UsernamePasswordAuthenticationToken authRequest = null;
               Authentication tempAuthentication = authenticationLDAPService.findAuthentication(userId);
               Person person = personService.getPersonByUserId(tempAuthentication.getUserId());
               Person PersonLDAP= personLDAPService.getPersonByUserId(userId);
    
               if (person != null) {
                  //update the information of person
                  if (!person.equals(PersonLDAP)) {
                     person.setFirstName(PersonLDAP.getFirstName());
                     person.setLastName(PersonLDAP.getLastName());
                     person.setInfix(PersonLDAP.getInfix());                    
                  }
                  if (person.getActive() != PersonLDAP.getActive()) {
                     person.setActive(PersonLDAP.getActive());
                  }
                  personService.updatePerson(person);
               }
               else {
                  Authentication newAuthentication = authenticationService.createAuthentication(tempAuthentication);
                  personService.createPerson(PersonLDAP.getFirstName(), "", PersonLDAP.getLastName(), newAuthentication.getId(), RegisterStatus.ACTIVE.getName(), new Date());
                    
               }
               Set < String > authrityNames = authorityService.getAuthorityNames();
               Set < String > authritiesInLdap = authorityLDAPService.getAuthorityNamesForUser(tempAuthentication.getUserId());
               Set < String > authritiesInDB =authorityService.getAuthorityNamesForUser(tempAuthentication.getUserId());
               for (String authority : authritiesInLdap) {
                  if (!authrityNames.contains(authority)) {
                     authorityService.createAuthority(null, authority);
                     authenticationService.addAuthorityToUser(tempAuthentication.getUserId(), authority);
                  }
                  else {                        
                     if (!authritiesInDB.contains(authority)) {
                        authenticationService.addAuthorityToUser(tempAuthentication.getUserId(), authority);
                     }
                  }
               }
               
               authRequest = new UsernamePasswordAuthenticationToken(tempAuthentication.getUserId(), tempAuthentication.getPassword());
  
                 // authRequest = new UsernamePasswordAuthenticationToken("sander@sanderbos.com", "abcd123");
               HttpSession session = request.getSession(true);
               session.setAttribute(SESSION_KEY_SUBJECT, authRequest);
            }
         }
         else {
           // SecurityContextHolder.getContext().setAuthentication(authentication);
         }
      }
      filterChain.doFilter(sRequest, sResponse);
   }

   public void init(FilterConfig arg0) throws ServletException {
      
   }

}
