package com.finalist.cmsc.services.community.security;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
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
   
   public static final String GROUPS_BASE_DN = "ou=Groups,ou=idstore,dc=nai,dc=nl"; 
   public static final String GROUP_CLASS_NAME = "groupOfUniqueNames"; 

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
   
   public void addAuthorityToUser(String userId, String authorityName) {
//	   Authority authority = authorityLDAPService.findAuthorityByName(authorityName);
	   AndFilter filter = new AndFilter();
	   filter.and(new EqualsFilter("objectClass", GROUP_CLASS_NAME));
	   filter.and(new EqualsFilter("cn", authorityName));

	   Attributes groupAttributes = (Attributes) searchObject(GROUPS_BASE_DN, filter.encode(), getGroupMapper());

	   if(groupAttributes == null) {
		   log.warn("New group, create it!");
	       groupAttributes = new BasicAttributes();
	       BasicAttribute groupClassAttribute = new BasicAttribute("objectClass");
	       groupClassAttribute.add(GROUP_CLASS_NAME);
	       groupAttributes.put(groupClassAttribute);
	       groupAttributes.put("cn", authorityName);
	       groupAttributes.put("description", "NAi Group created by AuthenticationLDAPService");
	       groupAttributes.put("uniqueMember", "cn="+userId);

	       DistinguishedName newItemDN = new DistinguishedName(GROUPS_BASE_DN);
	       newItemDN.add("cn", authorityName);
	       getLdapTemplate().bind(newItemDN, null, groupAttributes);
	   }
	   else {
		   log.warn("Old group, joining it!");
	      groupAttributes.get("uniqueMember").add("cn="+userId);
	      DistinguishedName newItemDN = new DistinguishedName(GROUPS_BASE_DN);
	      newItemDN.add("cn", authorityName);
	      getLdapTemplate().rebind(newItemDN, null, groupAttributes);
	   }
	   log.warn("Done adding to group!");
   }
   
   
   
   private ContextMapper getGroupMapper() {
	   return new GroupContextMapper();
   }

   private class GroupContextMapper extends AbstractContextMapper  {
      public Object doMapFromContext(DirContextOperations context) {
    	  return context.getAttributes();
      }
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

   public void removeAuthorityFromUser(String userId, String authorityName) {
	   log.warn("This method is not implemented");
//	   Authentication authentication = findAuthentication(userId);
//	   Authority authority = authorityLDAPService.findAuthorityByName(authorityName);
//	   if(authority != null) {
//		   authentication.removeAuthority(authority);
//	   }
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
