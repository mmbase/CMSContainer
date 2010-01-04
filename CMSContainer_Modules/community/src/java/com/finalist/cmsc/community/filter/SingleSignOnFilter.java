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
               AuthenticationService authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");
               PersonService personLDAPService = (PersonService)ApplicationContextFactory.getBean("personLDAPService");
               AuthenticationService authenticationLDAPService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationLDAPService");
               PersonService personService = (PersonService)ApplicationContextFactory.getBean("personService");
               
               AuthorityService aus = (AuthorityService) ApplicationContextFactory.getBean("authorityService");
               AuthorityService ldapAus = (AuthorityService) ApplicationContextFactory.getBean("authorityLDAPService");
               UsernamePasswordAuthenticationToken authRequest = null;
               Authentication tempAuthentication = authenticationLDAPService.findAuthentication(userId);
               Person person = personService.getPersonByUserId(tempAuthentication.getUserId());
               Person newPerson= personLDAPService.getPersonByUserId(userId);
    
               if (person != null) {
                  //update the information of person
                  if (!person.equals(newPerson)) {
                     person.setFirstName(newPerson.getFirstName());
                     person.setLastName(newPerson.getLastName());
                     person.setInfix(newPerson.getInfix());                    
                  }
                  if (person.getActive() != newPerson.getActive()) {
                     person.setActive(newPerson.getActive());
                  }
                  personService.updatePerson(person);
               }
               else {
                  Authentication newAuthentication = authenticationService.createAuthentication(tempAuthentication);
                  personService.createPerson(newPerson.getFirstName(), "", newPerson.getLastName(), newAuthentication.getId(), RegisterStatus.ACTIVE.getName(), new Date());
                    
               }
               Set < String > authrityNames = aus.getAuthorityNames();
               Set < String > authritiesInLdap = ldapAus.getAuthorityNamesForUser(userId);
               Set < String > authritiesInDB =aus.getAuthorityNamesForUser(userId);
               for (String authority : authritiesInLdap) {
                  if (!authrityNames.contains(authority)) {
                     aus.createAuthority(null, authority);
                     authenticationService.addAuthorityToUser(userId, authority);
                  }
                  else {                        
                     if (!authritiesInDB.contains(authority)) {
                        authenticationService.addAuthorityToUser(userId, authority);
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
