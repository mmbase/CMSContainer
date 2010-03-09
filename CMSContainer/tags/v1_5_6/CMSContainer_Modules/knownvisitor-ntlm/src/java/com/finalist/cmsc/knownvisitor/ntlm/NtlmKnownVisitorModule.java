package com.finalist.cmsc.knownvisitor.ntlm;

import java.util.Hashtable;

import javax.naming.*;
import javax.naming.directory.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.knownvisitor.KnownVisitorModule;
import com.finalist.cmsc.knownvisitor.Visitor;
import com.finalist.cmsc.mmbase.PropertiesUtil;

public class NtlmKnownVisitorModule extends KnownVisitorModule {
   static Log log = LogFactory.getLog(NtlmKnownVisitorModule.class);

   public static final String SESSION_ATTRIBUTE = "knownVisitor";

   public static final String PROPERTY_ENABLED = "knownvisitor-ntlm.enabled";
   public static final String PROPERTY_DOMAIN_CONTROLLER = "knownvisitor-ntlm.domaincontroller";
   public static final String PROPERTY_DOMAIN = "knownvisitor-ntlm.domain";
   public static final String PROPERTY_IPEXCEPTIONS = "knownvisitor-ntlm.ipexceptions";
   public static final String PROPERTY_BASIC_AUTH = "knownvisitor-ntlm.basic-authentication";

   public static final String PROPERTY_FIELD_EMAIL = "knownvisitor-ntlm.field.email";
   public static final String PROPERTY_FIELD_REALNAME = "knownvisitor-ntlm.field.realname";
   public static final String PROPERTY_FIELD_USERNAME = "knownvisitor-ntlm.field.username";
   public static final String PROPERTY_LOGONNAME = "knownvisitor-ntlm.logonname";
   public static final String PROPERTY_LOGONPASSWORD = "knownvisitor-ntlm.logonpassword";
   public static final String PROPERTY_SEARCHDN = "knownvisitor-ntlm.searchDN";

   


   public NtlmKnownVisitorModule() {
      // nothing
   }


   @Override
   public Visitor getVisitor(HttpServletRequest request) {
      HttpSession ssn = request.getSession(false);
      return (ssn != null ? (Visitor) ssn.getAttribute(NtlmKnownVisitorModule.SESSION_ATTRIBUTE) : null);
   }

   public void setVisitor(HttpServletRequest request, Visitor visitor) {
      request.getSession().setAttribute(SESSION_ATTRIBUTE, visitor);
   }
   

   @Override
   public void init() {
      KnownVisitorModule.setInstance(this);
   }

   public void readLdapInfo(Visitor v) {
      NtlmVisitor visitor = (NtlmVisitor) v;
      
      DirContext ctx;
      String query = "(" + getProperty(PROPERTY_FIELD_USERNAME) + "=" + visitor.getIdentifier() + ")";
      String searchDN = getProperty(PROPERTY_SEARCHDN);
      String server = getProperty(PROPERTY_DOMAIN_CONTROLLER);

      // Assemble a hash with data to use when connecting...
      Hashtable<String, String> env = new Hashtable<String, String>();
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      env.put(Context.SECURITY_PRINCIPAL, "cn=" + getProperty(PROPERTY_LOGONNAME) + "," + searchDN);
      env.put(Context.SECURITY_CREDENTIALS, getProperty(PROPERTY_LOGONPASSWORD));

      // Make a directory context by connecting with the above details.
      try {
         ctx = new InitialDirContext(env);
         NamingEnumeration<SearchResult> answer = ctx.search("ldap://" + server + "/" + searchDN, query, null);
         if (answer.hasMoreElements()) {
            SearchResult result = answer.next();
            Attribute values = result.getAttributes().get(getProperty(PROPERTY_FIELD_REALNAME));
            if (values.size() > 0) {
               visitor.setDisplayName((String) values.get(0));
            }

            values = result.getAttributes().get(getProperty(PROPERTY_FIELD_EMAIL));
            if (values.size() > 0) {
               visitor.setEmail((String) values.get(0));
            }
         }
      }
      catch (NamingException e) {
         log.error("problem reading user from LDAP: ", e);
         visitor.setDisplayName("LDAP failed");
      }
   }


   private String getProperty(String key) {
      return PropertiesUtil.getProperty(key);
   }

}