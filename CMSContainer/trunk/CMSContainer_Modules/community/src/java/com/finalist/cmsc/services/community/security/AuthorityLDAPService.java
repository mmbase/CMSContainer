package com.finalist.cmsc.services.community.security;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.services.AbstractLDAPService;

public class AuthorityLDAPService  extends AbstractLDAPService implements AuthorityService {

   private static final Logger log = Logger.getLogger(AuthorityLDAPService.class);
   
   public static final String GROUPS_BASE_DN = "ou=Groups,ou=idstore,dc=nai,dc=nl"; 
   public static final String GROUP_CLASS_NAME = "groupOfUniqueNames"; 
   
   public List<Authority> getAllAuthorities(PagingStatusHolder holder) {
      AndFilter filter = new AndFilter();
      filter.and(new EqualsFilter("objectClass", GROUP_CLASS_NAME));
      List<Authority> groups= (List<Authority>)getLdapTemplate().search(GROUPS_BASE_DN, filter.encode(), new GroupContextMapper());
      return groups;
   }
   
   public Set<String> getAuthorityNamesForUser(String userId) {
      AndFilter filter = new AndFilter();
      filter.and(new EqualsFilter("objectClass", GROUP_CLASS_NAME));
      // If you remove this second condition you would get all groups
      filter.and(new EqualsFilter("uniqueMember", "cn="+userId+",ou=Relations,ou=idstore,dc=nai,dc=nl"));
      List<Authority> groups= (List<Authority>)getLdapTemplate().search(GROUPS_BASE_DN, filter.encode(), new GroupContextMapper());
      Set<String> groupNames = new HashSet<String>();
      for (Authority authority : groups) {
         groupNames.add(authority.getName());
      }
      return groupNames;
   }
   
   public boolean authorityExists(String authorityName) {
      // TODO Auto-generated method stub
      return false;
   }

   public int countAllAuthorities() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int countAssociatedAuthorities(String name) {
      // TODO Auto-generated method stub
      return 0;
   }

   public Authority createAuthority(String parentName, String authorityName) {
      // TODO Auto-generated method stub
      return null;
   }

   public void deleteAuthority(String authorityName) {
      // TODO Auto-generated method stub
      
   }

   public List<Authority> findAssociatedAuthorityByName(String name,
         PagingStatusHolder holder) {
      // TODO Auto-generated method stub
      return null;
   }

   public Authority findAuthorityById(Long authorityId) {
      // TODO Auto-generated method stub
      return null;
   }

   @SuppressWarnings("unchecked")
   public Authority findAuthorityByName(String authorityName) {
      AndFilter filter = new AndFilter();
      filter.and(new EqualsFilter("objectClass", GROUP_CLASS_NAME));
      filter.and(new EqualsFilter("cn", authorityName));
      List<Authority> groups= (List<Authority>)getLdapTemplate().search(GROUPS_BASE_DN, filter.encode(), new GroupContextMapper());
      if(groups.size() > 0) {
    	  return groups.get(0);
      }
      else {
    	  return null;
      }
   }

   public List<Authority> getAssociatedAuthorities(Map map,
         PagingStatusHolder holder) {
      // TODO Auto-generated method stub
      return null;
   }

   public int getAssociatedAuthoritiesNum(Map map, PagingStatusHolder holder) {
      // TODO Auto-generated method stub
      return 0;
   }

   public Set<String> getAuthorityNames() {
      AndFilter filter = new AndFilter();
      filter.and(new EqualsFilter("objectClass", GROUP_CLASS_NAME));
      List<Authority> groups= (List<Authority>)getLdapTemplate().search(GROUPS_BASE_DN, filter.encode(), new GroupContextMapper());
      Set<String> groupNames = new HashSet<String>();
      for (Authority authority : groups) {
         groupNames.add(authority.getName());
      }
      return groupNames;
   }


   
   private class GroupContextMapper extends AbstractContextMapper  {

      public Object doMapFromContext(DirContextOperations context) {
          String groupName = context.getStringAttribute("cn");
          Authority group = new Authority();
          group.setName(groupName);
          return group;
      }
  }

}
