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

import org.acegisecurity.context.SecurityContextHolder;
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

   private PersonService personLDAPService;
   private AuthenticationService authenticationLDAPService;
   private AuthorityService authorityLDAPService;
   
   private PersonService personService;
   private AuthorityService authorityService;               
   private AuthenticationService authenticationService;
   
   public void init(FilterConfig arg0) throws ServletException {
      personLDAPService = (PersonService)ApplicationContextFactory.getBean("personLDAPService");
      authenticationLDAPService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationLDAPService");
      authorityLDAPService = (AuthorityService) ApplicationContextFactory.getBean("authorityLDAPService");
      
      personService = (PersonService)ApplicationContextFactory.getBean("personService");
      authorityService = (AuthorityService) ApplicationContextFactory.getBean("authorityService");               
      authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");
   }
   
   public void doFilter(ServletRequest sRequest, ServletResponse sResponse,
         FilterChain filterChain) throws IOException, ServletException {
   
      if (sRequest instanceof HttpServletRequest) {
         HttpServletRequest request = (HttpServletRequest)sRequest;
         org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         if(authentication == null) {   
            String idStoreId = request.getRemoteUser();
            
            if (StringUtils.isNotEmpty(idStoreId)) {
               Authentication ldapAuthentication = authenticationLDAPService.findAuthentication(idStoreId);
               addPerson(idStoreId, ldapAuthentication);
               addAuthority(ldapAuthentication);
               
               UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(ldapAuthentication.getUserId(), ldapAuthentication.getPassword());
               SecurityContextHolder.getContext().setAuthentication(authRequest);
            }
         }
      }
      filterChain.doFilter(sRequest, sResponse);
   }

   public void destroy() {
      
   }
   
   private void addPerson(String idStoreId, Authentication ldapAuthentication) {
      Person person = personService.getPersonByUserId(ldapAuthentication.getUserId());
      Person personLDAP= personLDAPService.getPersonByUserId(idStoreId);
   
      if (person != null) {
         //update the information of person
         person.setFirstName(personLDAP.getFirstName());
         person.setLastName(personLDAP.getLastName());
         person.setInfix(personLDAP.getInfix());                    
         person.setActive(personLDAP.getActive());
         person.setEmail(personLDAP.getEmail());
         personService.updatePerson(person);
      }
      else {
         Authentication newAuthentication = authenticationService.createAuthentication(ldapAuthentication);
         Person newPerson = personService.createPerson(personLDAP.getFirstName(), "", personLDAP.getLastName(), newAuthentication.getId(), RegisterStatus.ACTIVE.getName(), new Date());
         newPerson.setEmail(personLDAP.getEmail());
         personService.updatePerson(newPerson);
      }
   }

   private void addAuthority(Authentication ldapAuthentication) {
      Set < String > allDBAuthorities = authorityService.getAuthorityNames();
      Set < String > authoritiesLDAP = authorityLDAPService.getAuthorityNamesForUser(ldapAuthentication.getUserId());
      Set < String > authoritiesDB =authorityService.getAuthorityNamesForUser(ldapAuthentication.getUserId());
      for (String authority : authoritiesLDAP) {
         if (!allDBAuthorities.contains(authority)) {
            authorityService.createAuthority(null, authority);
            authenticationService.addAuthorityToUser(ldapAuthentication.getUserId(), authority);
         }
         else {                        
            if (!authoritiesDB.contains(authority)) {
               authenticationService.addAuthorityToUser(ldapAuthentication.getUserId(), authority);
            }
         }
      }
   }


}
