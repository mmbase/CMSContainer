package com.finalist.cmsc.forms.validation.basic;


public class PostCodeValidator extends RegexValidator {

   private static final String POSTCODE_REGEXP = "^[123456789]\\d{3}\\s*[ABCDEGHJKLMNPRSTVWXZabcdeghjklmnprstvwxz]{2}$";

   public PostCodeValidator() {
      super("validator.postcode.message", POSTCODE_REGEXP);
   }
   
}
