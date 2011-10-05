package com.finalist.cmsc.forms.validation.basic;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class RegexValidator extends FieldValidator {

   private final static Log Log = LogFactory.getLog(RegexValidator.class);

   private String regexp;

   public RegexValidator(String datapattern) {
      this("validator.regex.message", datapattern);
   }
   
   public RegexValidator(String key, String datapattern) {
      super(key);
      if (datapattern == null) {
         Log.warn("No regular expression passed to validator!");
      }
      this.regexp = datapattern;
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public final boolean validate(ValueField field) {
      if (regexp == null || "".equals(regexp)) {
         Log.error("Regular expression for validation is empty");
         return false;
      }

      if (field.isRequired()) {
         if (StringUtils.isEmpty(field.getStringValue())) {
            return false;
         }
      }
      else {
         if (StringUtils.isEmpty(field.getStringValue())) {
            return true;
         }
      }

      if (Log.isDebugEnabled()) {
         Log.debug("Regular expression for validation: " + regexp);
         Log.debug("String for validation: " + field.getStringValue());
      }
      try {
         boolean result = field.getStringValue().matches(regexp);
         Log.debug("Validation result: " + result);
         return result;
      }
      catch (PatternSyntaxException e) {
         Log.error("Regularexpression is not valid: " + regexp, e);
      }
      return false;
   }

}
