package com.finalist.cmsc.services;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

public abstract class AbstractLDAPService {

   private LdapTemplate ldapTemplate;

   /**
    * Get an instantiated ldap template
    * @return The LDAP Template
    */
   public LdapTemplate getLdapTemplate() {
       return ldapTemplate;
   }

   /**
    * (Spring) setter
    * @param ldapTemplate The template for the 
    */
   public void setLdapTemplate(LdapTemplate ldapTemplate) {
       this.ldapTemplate = ldapTemplate;
   }

   /**
    * Utility method.
    * Search for a specific object in LDAP. If no objects are found, null is returned.
    * If multiple objects are found, an IncorrectResultSizeDataAccessException is thrown
    * @param baseDN the DN to use as the base of the search. 
    * @param filter the search filter.
    * @param mapper The mapper to use for the search.
    * @return A found object, or null if there are no results.
    * @throws IncorrectResultSizeDataAccessException if more than one result was found.
    */
   @SuppressWarnings("unchecked")
   public Object searchObject(String baseDN, String filter, ContextMapper mapper) {
       List results = getLdapTemplate().search(baseDN, filter, mapper);
       if (results == null || results.size() == 0) {
           return null;
       } else if (results.size() > 1) {
           String message = MessageFormat.format("Found too many results for query {0} {1}", baseDN, filter);
           throw new IncorrectResultSizeDataAccessException(message, 1, results.size());
       } else {
           return results.get(0);
       }
   }
}
