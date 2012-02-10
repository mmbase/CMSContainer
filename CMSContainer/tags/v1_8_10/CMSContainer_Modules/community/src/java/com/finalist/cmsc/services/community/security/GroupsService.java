/**
 * 
 */
package com.finalist.cmsc.services.community.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.finalist.cmsc.services.community.ApplicationContextFactory;


/**
 * @author Billy
 *
 */
public class GroupsService {
   
   private static final Logger log = Logger.getLogger(GroupsService.class);
   
   public List<String> syncronizeGroupsFromIDstore(){
      List<String> results = new ArrayList<String>();
      AuthorityService authorityLDAPService = getAuthorityLDAPService();
      if (authorityLDAPService == null) {
         return results;
      }
      Set <String> list = authorityLDAPService.getAuthorityNames();
      Iterator<String> iter = list.iterator();
      while(iter.hasNext()){
         String authorityName = iter.next();
         AuthorityService authorityHibernateService = getAuthorityHibernateService();
         if(! authorityHibernateService.authorityExists(authorityName)){
            authorityHibernateService.createAuthority("", authorityName);
            results.add(authorityName);
         }
      }
      return results;
   }
   
   private AuthorityLDAPService getAuthorityLDAPService() {
      AuthorityLDAPService authLDAPService;
      try {
         authLDAPService = (AuthorityLDAPService)ApplicationContextFactory.getBean("authorityLDAPService");
      }
      catch (NoSuchBeanDefinitionException e) {
         log.info("No LDAP service defined and used.");
         authLDAPService = null;
      }
      return authLDAPService;
   }

   private AuthorityService getAuthorityHibernateService() {
      return (AuthorityService)ApplicationContextFactory.getBean("authorityService");
   }

}
