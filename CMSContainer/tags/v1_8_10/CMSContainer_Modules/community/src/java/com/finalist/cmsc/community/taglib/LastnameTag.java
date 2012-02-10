package com.finalist.cmsc.community.taglib;

import com.finalist.cmsc.services.community.person.Person;

public class LastnameTag extends AbstractCommunityTag  {

   @Override
   protected String getValue(Person person) {
      return person.getLastName();
   }
}
