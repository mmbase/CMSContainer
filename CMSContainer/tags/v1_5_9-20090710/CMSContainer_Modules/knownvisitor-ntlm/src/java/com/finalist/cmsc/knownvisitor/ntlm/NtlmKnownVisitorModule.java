package com.finalist.cmsc.knownvisitor.ntlm;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.knownvisitor.KnownVisitorModule;
import com.finalist.cmsc.mmbase.PropertiesUtil;

public class NtlmKnownVisitorModule extends KnownVisitorModule {
   static Log log = LogFactory.getLog(NtlmKnownVisitorModule.class);

   private static final String PROPERTY_ENABLED = "knownvisitor-ntlm.enabled";
   private static final String PROPERTY_IPEXCEPTIONS = "knownvisitor-ntlm.ipexceptions";

   private static final String PROPERTY_DOMAIN_CONTROLLER = "knownvisitor-ntlm.domaincontroller";
   private static final String PROPERTY_DOMAIN = "knownvisitor-ntlm.domain";

   private static final String PROPERTY_FIELD_EMAIL = "knownvisitor-ntlm.field.email";
   private static final String PROPERTY_FIELD_REALNAME = "knownvisitor-ntlm.field.realname";
   private static final String PROPERTY_FIELD_USERNAME = "knownvisitor-ntlm.field.username";
   private static final String PROPERTY_LOGONNAME = "knownvisitor-ntlm.logonname";
   private static final String PROPERTY_LOGONPASSWORD = "knownvisitor-ntlm.logonpassword";
   private static final String PROPERTY_SEARCHDN = "knownvisitor-ntlm.searchDN";

   private static final String PROPERTY_BASIC_AUTH = "knownvisitor-ntlm.basic-authentication";
   private static final String PROPERTY_NTLM_AUTH = "knownvisitor-ntlm.ntlm-authentication";

   public NtlmKnownVisitorModule() {
      // nothing
   }

   @Override
   public void init() {
      KnownVisitorModule.setInstance(this);
   }

   public String getDomainController() {
      return getProperty(PROPERTY_DOMAIN_CONTROLLER);
   }

   public String getDomain() {
      return getProperty(PROPERTY_DOMAIN);
   }
   
   public boolean isEnabled() {
      String property = PropertiesUtil.getProperty(PROPERTY_ENABLED);
      if (property != null) {
         return Boolean.parseBoolean(property);
      }
      return super.isEnabled();
   }
   
   public boolean offerNtlm(HttpServletRequest req) {
      String property = PropertiesUtil.getProperty(PROPERTY_NTLM_AUTH);
      if (StringUtils.isNotBlank(property)) {
         return Boolean.parseBoolean(property);
      }
      return false;
   }

   
   public List<String> getIpExceptions() {
      List<String> property = PropertiesUtil.getPropertyAsList(PROPERTY_IPEXCEPTIONS);
      if (property != null && !property.isEmpty()) {
         return property;
      }
      return super.getIpExceptions();
   }
   
   @Override
   public String getBasicAuth() {
      return PropertiesUtil.getProperty(PROPERTY_BASIC_AUTH);
   }
   
   @Override
   public String getLdapServer() {
      String ldapServer = super.getLdapServer();
      if (StringUtils.isNotBlank(ldapServer)) {
         return ldapServer;
      }
      return getDomainController();
   }
   
   public String getLdapPassword() {
      String property = getProperty(PROPERTY_LOGONPASSWORD);
      if (StringUtils.isNotBlank(property)) {
         return property;
      }
      return super.getLdapPassword();
   }

   public String getLdapPrincipal() {
      String property = getProperty(PROPERTY_LOGONNAME);
      if (StringUtils.isNotBlank(property)) {
         return "cn=" + property + "," + getLdapBaseDN();
      }
      return super.getLdapPrincipal();
   }

   public String getLdapBaseDN() {
      String property = getProperty(PROPERTY_SEARCHDN);
      if (StringUtils.isNotBlank(property)) {
         return property;
      }
      return super.getLdapBaseDN();
   }

   public String getLdapUsernameField() {
      String property = getProperty(PROPERTY_FIELD_USERNAME);
      if (StringUtils.isNotBlank(property)) {
         return property;
      }
      return super.getLdapUsernameField();
   }

   public String getLdapEmailField() {
      String property = getProperty(PROPERTY_FIELD_EMAIL);
      if (StringUtils.isNotBlank(property)) {
         return property;
      }
      return super.getLdapEmailField();
   }

   public String getLdapRealnameField() {
      String property = getProperty(PROPERTY_FIELD_REALNAME);
      if (StringUtils.isNotBlank(property)) {
         return property;
      }
      return super.getLdapRealnameField();
   }
   
}
