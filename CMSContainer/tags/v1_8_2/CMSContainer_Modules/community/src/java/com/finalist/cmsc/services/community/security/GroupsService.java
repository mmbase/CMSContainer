/**
 * 
 */
package com.finalist.cmsc.services.community.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.finalist.cmsc.services.community.ApplicationContextFactory;


/**
 * @author Billy
 *
 */
public class GroupsService {
   
   public List<String> syncronizeGroupsFromIDstore(){
      List<String> results = new ArrayList<String>();
      AuthorityService authorityLDAPService = getAuthorityLDAPService();
      Set < String > list = authorityLDAPService.getAuthorityNames();
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
      return (AuthorityLDAPService)ApplicationContextFactory.getBean("authorityLDAPService");
   }

   private AuthorityService getAuthorityHibernateService() {
      return (AuthorityService)ApplicationContextFactory.getBean("authorityService");
   }

}
