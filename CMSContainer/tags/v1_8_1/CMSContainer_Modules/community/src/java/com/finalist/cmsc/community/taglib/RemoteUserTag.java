package com.finalist.cmsc.community.taglib;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

public class RemoteUserTag extends AbstractCommunityTag  {

   @Override
   protected String getValue(Person person) {

      AuthenticationService as = getAuthenticationService();
      Authentication authentication = as.getAuthenticationById(person.getAuthenticationId());
      return authentication.getUserId();
   }

}
