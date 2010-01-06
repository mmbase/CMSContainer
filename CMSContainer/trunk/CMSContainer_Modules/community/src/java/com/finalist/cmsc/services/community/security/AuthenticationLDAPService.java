package com.finalist.cmsc.services.community.security;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.AbstractLDAPService;

/**
 * 
 * @author Kevin
 */
public class AuthenticationLDAPService extends AbstractLDAPService implements AuthenticationService {

   private static final Logger log = Logger.getLogger(AuthenticationLDAPService.class);
   
   public static final String RELATION_BASE_DN = "ou=Relations,ou=idstore,dc=nai,dc=nl"; 
   public static final String RELATION_CLASS_NAME = "naiIDStorePerson";
   private AuthorityService authorityLDAPService;
   
   @Required
   public void setAuthorityLDAPService(AuthorityService authorityLDAPService) {
      this.authorityLDAPService = authorityLDAPService;
   }
 
   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Authentication findAuthentication(String userId) {
      return getNaiIDStorePersonByProperty("nai-idStoreId",userId);
   }   
   
   private Authentication getNaiIDStorePersonByProperty(String propertyName, String propertyValue) {
      AndFilter filter = new AndFilter();
      filter.and(new EqualsFilter("objectClass", RELATION_CLASS_NAME));
      filter.and(new EqualsFilter(propertyName, propertyValue));

      Authentication authentication = (Authentication) searchObject(RELATION_BASE_DN, filter.encode(), getContextMapper());
      if (authentication != null) {
          // Also retrieve/ set the groups of the relation
          for (String groupName: authorityLDAPService.getAuthorityNamesForUser(authentication.getUserId())) {
             Authority authority = new Authority();
             authority.setName(groupName);
             authentication.addAuthority(authority);
          }
      }
      return authentication;
  }   
  
   protected ContextMapper getContextMapper() {
      return new AuthenticationContextMapper();
   }
   
   /**
    * Authentication context (attribute) mapper, that resolves LDAP results into Authentication domain objects.
    * @author Kevin
    *
    */
   private class AuthenticationContextMapper extends AbstractContextMapper {
      public Object doMapFromContext(DirContextOperations context) {

      Authentication authentication = new Authentication();
      authentication.setUserId(context.getStringAttribute("cn"));
      authentication.setPassword(getPasswordAttribute(context,"userPassword"));
      return authentication;
      }
   }
   /**
    * Retrieve the password attribute value as a string
    * @param context The LDAP result to process
    * @param attributeName The name of the attribute to retrieve
    * @return The attribute value, -1 (!) in case the attribute is not present
    */
   private String getPasswordAttribute(DirContextOperations context, String attributeName) {
       String passwordString = null;
       Object value = context.getObjectAttribute(attributeName);
       if (value != null) {
           if (value instanceof byte[]) {
               byte[] passwordBytes = (byte[]) value;
               try {
                   passwordString = new String(passwordBytes, "US-ASCII");
               } catch (UnsupportedEncodingException uee) {
                   log.error("This should never happen, US-ASCII is always supported", uee);
               }
           } else {
               log.error("Unexpected password type: " + value.getClass().getName());
           }
       }
       return passwordString;
   }   
   public void addAuthorityToUser(String userId, String authority) {
      // TODO Auto-generated method stub
      
   }

   public void addAuthorityToUserByAuthenticationId(String authId,
         String groupName) {
      // TODO Auto-generated method stub
      
   }

   public boolean authenticate(String userId, String password) {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean authenticationExists(String userId) {
      // TODO Auto-generated method stub
      return false;
   }

   public Authentication createAuthentication(String userId, String password) {
      // TODO Auto-generated method stub
      return null;
   }

   public Authentication createAuthentication(Authentication authentication) {
      // TODO Auto-generated method stub
      return null;
   }

   public void deleteAuthentication(Long id) {
      // TODO Auto-generated method stub
      
   }

   public List<Authentication> findAuthentications() {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Authentication> findAuthenticationsForAuthority(String name) {
      // TODO Auto-generated method stub
      return null;
   }

   public Authentication getAuthenticationById(Long authenticationId) {
      // TODO Auto-generated method stub
      return null;
   }

   public Long getAuthenticationIdForUserId(String userId) {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isAuthenticationEnabled(String userId) {
      // TODO Auto-generated method stub
      return false;
   }

   public void removeAuthenticationFromAuthority(String authId, String groupName) {
      // TODO Auto-generated method stub
      
   }

   public void removeAuthorityFromUser(String userId, String authority) {
      // TODO Auto-generated method stub
      
   }

   public void setAuthenticationEnabled(String userId, boolean enabled) {
      // TODO Auto-generated method stub
      
   }

   public void updateAuthentication(String userId, String oldPassword,
         String newPassword) {
      // TODO Auto-generated method stub
      
   }

   public void updateAuthenticationPassword(String userId, String newPassword) {
      // TODO Auto-generated method stub
      
   }
}
