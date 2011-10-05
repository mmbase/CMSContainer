package com.finalist.cmsc.forms.validation.basic;


public class PhoneNumberValidator extends RegexValidator {

   private static final String MSISDN_REGEXP = "^[+]{0,1}[()\\d -]*$";

   public PhoneNumberValidator() {
      super("validator.phone.message", MSISDN_REGEXP);
   }

}
