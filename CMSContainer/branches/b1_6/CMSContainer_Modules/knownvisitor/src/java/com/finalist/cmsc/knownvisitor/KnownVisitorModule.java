package com.finalist.cmsc.knownvisitor;

import java.util.Hashtable;
import java.util.List;

import javax.naming.*;
import javax.naming.directory.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.module.Module;

import com.finalist.cmsc.mmbase.PropertiesUtil;

public abstract class KnownVisitorModule extends Module {

   private static final Log log = LogFactory.getLog(KnownVisitorModule.class);

   private static final String SESSION_ATTRIBUTE = "knownVisitor";
   
   private static final String PROPERTY_ENABLED = "knownvisitor.enabled";
   private static final String PROPERTY_IPEXCEPTIONS = "knownvisitor.ipexceptions";
   private static final String PROPERTY_BASIC_AUTH = "knownvisitor.basic-authentication";

   private static final String PROPERTY_LDAP_SERVER = "knownvisitor.ldapserver";
   private static final String PROPERTY_REALM = "knownvisitor.realm";
   private static final String PROPERTY_LOGONNAME = "knownvisitor.logonname";
   private static final String PROPERTY_LOGONPASSWORD = "knownvisitor.logonpassword";
   private static final String PROPERTY_BASEDN = "knownvisitor.baseDN";

   private static final String PROPERTY_FIELD_EMAIL = "knownvisitor.field.email";
   private static final String PROPERTY_FIELD_REALNAME = "knownvisitor.field.realname";
   private static final String PROPERTY_FIELD_USERNAME = "knownvisitor.field.username";

   
   private static KnownVisitorModule instance;

   public static KnownVisitorModule getInstance() {
      return instance;
   }

   public static void setInstance(KnownVisitorModule module) {
      instance = module;
   }
   

   public Visitor getVisitor(HttpServletRequest request) {
      HttpSession ssn = request.getSession(false);
      return (ssn != null ? (Visitor) ssn.getAttribute(SESSION_ATTRIBUTE) : null);
   }

   public void setVisitor(HttpServletRequest request, Visitor visitor) {
      request.getSession().setAttribute(SESSION_ATTRIBUTE, visitor);
   }

   public void readLdapInfo(Visitor visitor) {
      // Assemble a hash with data to use when connecting...
      String ldapPrincipal = getLdapPrincipal();
      String ldapPassword = getLdapPassword();


      // Make a directory context by connecting with the above details.
      try {
         DirContext ctx = ldapLogin(ldapPrincipal, ldapPassword);

         String baseDN = getLdapBaseDN();
         String query = "(" + getLdapUsernameField() + "=" + visitor.getIdentifier() + ")";
         
         NamingEnumeration<SearchResult> answer = ctx.search(baseDN, query, null);
         if (answer.hasMoreElements()) {
            SearchResult result = answer.next();
            Attribute values = result.getAttributes().get(getLdapRealnameField());
            if (values.size() > 0) {
               visitor.setDisplayName((String) values.get(0));
            }

            values = result.getAttributes().get(getLdapEmailField());
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

   public DirContext ldapLogin(String ldapPrincipal, String ldapPassword) throws NamingException {
      String server = getLdapServer();
      if (server.indexOf("://") < 0) {
         // no protocol, fallback to ldap
         server = "ldap://" + server;
      }
      
      Hashtable<String, String> env = new Hashtable<String, String>();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      env.put(Context.PROVIDER_URL, server);

      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      env.put(Context.SECURITY_PRINCIPAL, ldapPrincipal);
      env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
      
      DirContext ctx = new InitialDirContext(env);
      return ctx;
   }

   public boolean offerBasic(HttpServletRequest req) {
      boolean offerBasic = req.isSecure();
      if (!offerBasic) {
         String basic = getBasicAuth();
         if (StringUtils.isBlank(basic) || "secure".equalsIgnoreCase(basic)) {
            return false;
         }
         else {
            // basic authentication is not forced to be on secured urls and the current request is not secured
            // This does not mean that the url arrived at the webserver was not secured. The webserver could
            // proxy the request without using the secured flag.
            return true;
         }
      }
      return offerBasic;
   }

   public String getBasicAuth() {
      return PropertiesUtil.getProperty(PROPERTY_BASIC_AUTH);
   }

   public boolean isEnabled() {
      return Boolean.parseBoolean(PropertiesUtil.getProperty(PROPERTY_ENABLED));
   }

   public List<String> getIpExceptions() {
       return PropertiesUtil.getPropertyAsList(PROPERTY_IPEXCEPTIONS);
   }

   public String getProperty(String key) {
      return PropertiesUtil.getProperty(key);
   }

   public String getLdapPassword() {
      return getProperty(PROPERTY_LOGONPASSWORD);
   }

   public String getLdapPrincipal() {
      return getProperty(PROPERTY_LOGONNAME);
   }

   public String getLdapServer() {
      return getProperty(PROPERTY_LDAP_SERVER);
   }

   public String getLdapBaseDN() {
      return getProperty(PROPERTY_BASEDN);
   }

   public String getLdapUsernameField() {
      return getProperty(PROPERTY_FIELD_USERNAME);
   }

   public String getLdapEmailField() {
      return getProperty(PROPERTY_FIELD_EMAIL);
   }

   public String getLdapRealnameField() {
      return getProperty(PROPERTY_FIELD_REALNAME);
   }

   public String getRealm() {
      return getProperty(PROPERTY_REALM);
   }
}
