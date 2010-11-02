package com.finalist.cmsc.services.community;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


public class JndiPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

   private static final Log log = LogFactory.getLog(JndiPlaceholderConfigurer.class);

   private Context env;

   public JndiPlaceholderConfigurer() throws Exception {
      this("java:comp/env");
   }
   
   public JndiPlaceholderConfigurer(String base) throws Exception {
      Context context = new InitialContext();
      env = (Context) context.lookup(base);
   }

   protected String resolvePlaceholder(String placeholder, Properties props) {
      String value = null;
      try {
         Map<?, ?> environment = this.env.getEnvironment();
         if (environment != null) {
            value = (String) environment.get(placeholder);
         }
         if (value == null) {
            value = (String) env.lookup(placeholder);
         }
      }
      catch (NamingException e) {
         log.error(e);
      }
      return value;
   }
}
