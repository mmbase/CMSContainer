package com.finalist.cmsc.services.community.person;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.AbstractLDAPService;
import com.finalist.cmsc.services.community.domain.PersonExportImportVO;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.Authority;

/**
 * @author Kevin
 */
public class PersonLDAPService  extends AbstractLDAPService implements PersonService {

   public static final String RELATION_BASE_DN = "ou=Relations,ou=idstore,dc=nai,dc=nl"; 
   public static final String RELATION_CLASS_NAME = "naiIDStorePerson";
   
   private AuthenticationService authenticationService;
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
         Long authenticationId, String active, Date registerDate) {
      // TODO Auto-generated method stub
      return null;
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
      // TODO Auto-generated method stub
      return null;
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
 

}
