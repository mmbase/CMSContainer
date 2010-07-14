package com.finalist.cmsc.community.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.User;
import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.Authority;
import com.finalist.cmsc.services.community.security.AuthorityService;

public class SingleSignOnFilter implements Filter{

   private PersonService personLDAPService;
   private AuthenticationService authenticationLDAPService;
   
   private PersonService personService;
   private AuthorityService authorityService;               
   private AuthenticationService authenticationService;
   
   public void init(FilterConfig arg0) throws ServletException {
      personLDAPService = (PersonService)ApplicationContextFactory.getBean("personLDAPService");
      authenticationLDAPService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationLDAPService");
      
      personService = (PersonService)ApplicationContextFactory.getBean("personService");
      authorityService = (AuthorityService) ApplicationContextFactory.getBean("authorityService");               
      authenticationService = (AuthenticationService)ApplicationContextFactory.getBean("authenticationService");
   }
   
   public void doFilter(ServletRequest sRequest, ServletResponse sResponse,
         FilterChain filterChain) throws IOException, ServletException {
   
      if (sRequest instanceof HttpServletRequest) {
         HttpServletRequest request = (HttpServletRequest)sRequest;
         org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         String requestURI = request.getRequestURI().toString();
         
         if(!requestURI.toLowerCase().endsWith("css") &&
            !requestURI.toLowerCase().endsWith("js") &&
            !requestURI.toLowerCase().endsWith("png") &&
            !requestURI.toLowerCase().endsWith("gif") &&
            !requestURI.toLowerCase().endsWith("jpg") &&
            !requestURI.toLowerCase().endsWith("jpeg") &&
            !requestURI.toLowerCase().endsWith("ico")) {
            
            String idStoreId = request.getRemoteUser();
            
            if (StringUtils.isNotEmpty(idStoreId)) {
               Authentication ldapAuthentication = authenticationLDAPService.findAuthentication(idStoreId);
               addPerson(idStoreId, ldapAuthentication);
               addAuthority(ldapAuthentication);
               
               Set < Authority > authorities = ldapAuthentication.getAuthorities();
               List < GrantedAuthority > grantedAuthorities = new ArrayList < GrantedAuthority >();
               for (Authority authority : authorities) {
                  grantedAuthorities.add(new GrantedAuthorityImpl(authority.getName()));
               }
               GrantedAuthority[] grantedAuthorityArray = grantedAuthorities.toArray(new GrantedAuthority[grantedAuthorities
                     .size()]);
               
               User user = new User(ldapAuthentication.getUserId(), ldapAuthentication.getPassword(), ldapAuthentication.isEnabled(), true,
                     true, true, grantedAuthorityArray);
               UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, ldapAuthentication.getPassword(),grantedAuthorityArray);
               authRequest.setDetails(user);
               SecurityContextHolder.getContext().setAuthentication(authRequest);
            }
        }
      }
      filterChain.doFilter(sRequest, sResponse);
   }

   public void destroy() {
      
   }
   
   private void addPerson(String idStoreId, Authentication ldapAuthentication) {
      
      Person personLDAP= personLDAPService.getPersonByUserId(idStoreId);
      Authentication authentication = authenticationService.findAuthentication(ldapAuthentication.getUserId());
      if (authentication != null) {
         Person person = personService.getPersonByUserId(ldapAuthentication.getUserId());
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
         Person newPerson = personService.createPerson(personLDAP.getFirstName()==null?"":personLDAP.getFirstName(), "", personLDAP.getLastName()==null?"":personLDAP.getLastName(), newAuthentication.getId(), RegisterStatus.ACTIVE.getName(), new Date());
         newPerson.setEmail(personLDAP.getEmail());
         personService.updatePerson(newPerson);
      }
   }

   private void addAuthority(Authentication ldapAuthentication) {
      Set < String > allDBAuthorities = authorityService.getAuthorityNames();
      Set < String > authoritiesDB =authorityService.getAuthorityNamesForUser(ldapAuthentication.getUserId());
      
      for (Authority authority : ldapAuthentication.getAuthorities()) {
         if (!allDBAuthorities.contains(authority.getName())) {
            authorityService.createAuthority(null, authority.getName());
            authenticationService.addAuthorityToUser(ldapAuthentication.getUserId(), authority.getName());
         }
         else {                        
            if (!authoritiesDB.contains(authority.getName())) {
               authenticationService.addAuthorityToUser(ldapAuthentication.getUserId(), authority.getName());
            }
         }
      }
   }


}
