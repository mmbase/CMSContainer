package com.finalist.cmsc.services.community.person;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.AbstractLDAPService;
import com.finalist.cmsc.services.community.domain.PersonExportImportVO;
import com.finalist.cmsc.services.community.security.Authority;

/**
 * @author Kevin
 */
public class PersonLDAPService  extends AbstractLDAPService implements PersonService {

   public static final String RELATION_BASE_DN = "ou=Relations,ou=idstore,dc=nai,dc=nl"; 
   public static final String RELATION_CLASS_NAME = "naiIDStorePerson";
   
   /**
    * {@inheritDoc}
    */
   @Transactional(readOnly = true)
   public Person getPersonByUserId(String userId) {
      if (StringUtils.isBlank(userId)) {
         throw new IllegalArgumentException("UserId is not filled in. ");
      }
     // Person person = findPersonByUserId(userId);
     return getNaiIDStorePersonByProperty("nai-idStoreId",userId);
   }
   
   /**
    * Find a relation based on a unique property. of the relation
    * @param propertyName The name of the unique LDAP property
    * @param propertyValue The value of the unique LDAP property
    * @return The found relation, or null in case the relation could not be found.
    */
   private Person getNaiIDStorePersonByProperty(String propertyName, String propertyValue) {
       AndFilter filter = new AndFilter();
       filter.and(new EqualsFilter("objectClass", RELATION_CLASS_NAME));
       filter.and(new EqualsFilter(propertyName, propertyValue));

       Person person = (Person) searchObject(RELATION_BASE_DN, filter.encode(), getContextMapper());
//       if (relation != null) {
//           // Also retrieve/ set the groups of the relation
//           for (String groupName: groupService.getRelationGroupsFromLdap(relation.getIdStoreId())) {
//               relation.getGroups().add(new Group(groupName));
//           }
//       }
       return person;
   }

   protected ContextMapper getContextMapper() {
      return new PersonContextMapper();
   }   
  
   private class PersonContextMapper extends AbstractContextMapper {

      public Object doMapFromContext(DirContextOperations context) {
         Person person = new Person();
         person.setFirstName(getStringAttribute(context, "nai-firstName"));
         person.setInfix(getStringAttribute(context, "nai-nameInfix"));
         person.setLastName(getStringAttribute(context, "nai-lastName"));
         person.setEmail(getStringAttribute(context, "nai-email"));
         if ("yes".equals(getStringAttribute(context, "nai-active"))) {
            person.setActive(RegisterStatus.ACTIVE.getName());
         }
         else {
            person.setActive(RegisterStatus.UNCONFIRMED.getName());
         }
         return person;
      }
   }
      
   private String getStringAttribute(DirContextOperations context, String attributeName) {
      return context.getStringAttribute(attributeName);
  }
   
   public void addRelationRecord(String level, PersonExportImportVO importPerson) {
      // TODO Auto-generated method stub
      
   }

   public void batchClean() {
      // TODO Auto-generated method stub
      
   }

   public void changeStateByAuthenticationId(Long authenticationId,
         String active) {
      // TODO Auto-generated method stub
      
   }

   public int countAllPersons() {
      // TODO Auto-generated method stub
      return 0;
   }

   public void creatRelationRecord(PersonExportImportVO xperson) {
      // TODO Auto-generated method stub
      
   }

   public Person createPerson(String firstName, String infix, String lastName,
         Object authenticationId, String active, Date registerDate) {
	   String email = (String)authenticationId;
	   
      // Create a new person and store it
      Person person = new Person();
      person.setFirstName(firstName); 
      person.setInfix(infix);
      person.setLastName(lastName);
      person.setEmail(email); // used to find account
      person.setActive(active);
      person.setRegisterDate(registerDate);

	   String storeId = generateNewIdStoreId(email);
      person.setId(new Long(storeId.substring(storeId.lastIndexOf("@")+1)));

	   BasicAttributes attributes = new BasicAttributes();
	   
       BasicAttribute relationClassAttribute = new BasicAttribute("objectClass");
       relationClassAttribute.add(RELATION_CLASS_NAME);
       attributes.put(relationClassAttribute);

	   attributes.put(new BasicAttribute("nai-idStoreId", storeId));
	   if(firstName != null) {
		   attributes.put(new BasicAttribute("nai-firstname", firstName));
	   }
	   if(infix != null) {
		   attributes.put(new BasicAttribute("nai-nameInfix", infix));
	   }
	   attributes.put(new BasicAttribute("nai-lastName", lastName));
	   attributes.put(new BasicAttribute("nai-email", email));
       if ("yes".equals(active)) {
    	   attributes.put(new BasicAttribute("nai-active", RegisterStatus.ACTIVE.getName()));
        }
        else {
     	   attributes.put(new BasicAttribute("nai-active", RegisterStatus.UNCONFIRMED.getName()));
        }
       attributes.put("nai-synchronisationStatus", "new");

       attributes.put("cn", storeId);
       attributes.put("sn", "Unused");

       
       DistinguishedName newItemDN = new DistinguishedName(RELATION_BASE_DN);
       newItemDN.add("cn", storeId);
	   
	  getLdapTemplate().bind(newItemDN, null, attributes);

      return person;
   }

   /**
    * Generate a unique ID-store id, by looking in the LDAP database to ensure it is unique 
    * @param emailAddress The email address to base the unique id on
    * @return The generated unique id
    * @throws ServiceException In case the emailAddress is already in use (currently) by a relation.
    */
   private synchronized String generateNewIdStoreId(String emailAddress) {
       // We never create a new relation for an email address that already exists
       if (getPersonByEmail(emailAddress) != null) {
           throw new RuntimeException("Relation with email address "+emailAddress+" already exists");
       }
       // Find an unused id
       int index = 1;
       while (true) {
           String candidateId = createUniqueId(emailAddress, index);
           if (getPersonByUserId(candidateId) == null) {
               // Use this id
               return candidateId;
           }
           // Try another new id
           index++;
       }
   }
   
   /**
    * String function to create a unique id with the following properties:
    *  - length < 64
    *  - Based on email address, but possibly not containing all email characters. The id
    *  itself is not an email address
    *  - Includes suffix (this is important to ensure id is unique as long as suffix is unique)
    * @param emailAddress The email address to use in the id
    * @param suffix The extra number that must be included in the unique id
    * @return The generated id
    */
   private String createUniqueId(String emailAddress, int suffix) {
       // Create a string based on the suffix, that will be the end of the id
       int maxIdLength = 64;
       String suffixString = "@" + String.valueOf(suffix);
       // Strip all non-Ascii Characters
       StringBuilder uniqueId = new StringBuilder();
       for (char emailAddressChar: emailAddress.toCharArray()) {
           if (uniqueId.length() + suffixString.length() >= maxIdLength) {
               // String will become too long
               break;
           }
           // Do not include non-Ascii characters
           if (String.valueOf(emailAddressChar).matches("[A-Za-z0-9\\.@]")) {
               uniqueId.append(emailAddressChar);
           }
       }
       return uniqueId.toString() + suffixString;
   }

   public boolean deletePersonByAuthenticationId(Long userId) {
      // TODO Auto-generated method stub
      return false;
   }

   public void deleteRelationRecord(Long id) {
      // TODO Auto-generated method stub
      
   }

   public List<Authority> getAllAuthorities() {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Person> getAllPeople() {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Person> getAllPersons() {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Person> getAssociatedPersons(Map conditions) {
      // TODO Auto-generated method stub
      return null;
   }

   public int getAssociatedPersonsNum(Map<String, String> map) {
      // TODO Auto-generated method stub
      return 0;
   }

   public List<Person> getLikePersons(Person example) {
      // TODO Auto-generated method stub
      return null;
   }

   public Person getPersonByAuthenticationId(Long authenticationId) {
      // TODO Auto-generated method stub
      return null;
   }

   public Person getPersonByEmail(String email) {
      if (StringUtils.isBlank(email)) {
          throw new IllegalArgumentException("UserId is not filled in. ");
       }
      // Person person = findPersonByUserId(userId);
      return getNaiIDStorePersonByProperty("nai-email",email);
   }

   public List<PersonExportImportVO> getPersonExportImportVO() {
      // TODO Auto-generated method stub
      return null;
   }

   public List<PersonExportImportVO> getPersonExportImportVO(String group) {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Person> getPersons(Person example) {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Person> getPersonsByAuthenticationIds(
         Set<Integer> authenticationIds, String name, String email) {
      // TODO Auto-generated method stub
      return null;
   }

   public List<Object[]> getSubscribersRelatedInfo(Set<Long> authenticationIds,
         String fullName, String userName, String email, boolean paging) {
      // TODO Auto-generated method stub
      return null;
   }

   public int getSubscribersRelatedInfoCount(Set<Long> authenticationIds,
         String fullName, String userName, String email, boolean paging) {
      // TODO Auto-generated method stub
      return 0;
   }

   public void updatePerson(Person person) {
      // TODO Auto-generated method stub
      
   }
   
   private class GenderContextMapper extends AbstractContextMapper {

      public Object doMapFromContext(DirContextOperations context) {
         return getStringAttribute(context, "nai-gender");
      }
   }

   public String getGenderByUserId(String userId) {
      AndFilter filter = new AndFilter();
      filter.and(new EqualsFilter("objectClass", RELATION_CLASS_NAME));
      filter.and(new EqualsFilter("cn", userId));
      List<String> groups = (List<String>)getLdapTemplate().search(RELATION_BASE_DN, filter.encode(), new GenderContextMapper());
      if(groups.size() >= 1){
         return groups.get(0);
      }
      return "unknown";
   }

   public void setGenderByUserId(String userId, String gender) {
       DistinguishedName itemDN = new DistinguishedName(RELATION_BASE_DN);
       itemDN.add("cn", userId);

	   BasicAttribute attr = new BasicAttribute("nai-gender", gender);
	   ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
	   getLdapTemplate().modifyAttributes(itemDN, new ModificationItem[] {item});
	   
	   attr = new BasicAttribute("nai-synchronisationStatus", "changed");
	   item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
	   getLdapTemplate().modifyAttributes(itemDN, new ModificationItem[] {item});

   }
}
