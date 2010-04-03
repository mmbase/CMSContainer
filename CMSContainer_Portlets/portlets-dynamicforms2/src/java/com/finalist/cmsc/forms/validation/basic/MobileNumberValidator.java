package com.finalist.cmsc.forms.validation.basic;


public class MobileNumberValidator extends RegexValidator {

   private static final String MSISDN_REGEXP = "^06-?\\d\\d\\d\\d\\d\\d\\d\\d$";

   public MobileNumberValidator() {
      super("validator.mobile.message", MSISDN_REGEXP);
   }

}
