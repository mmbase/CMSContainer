package com.finalist.cmsc.community.taglib;


public class UseSSOTag extends AbstractSSOTag  {
   
   @Override
   public String getValue() {
      return getEnvironment("useSSO");
   }
}
