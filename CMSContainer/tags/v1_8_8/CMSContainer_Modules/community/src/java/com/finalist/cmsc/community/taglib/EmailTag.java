package com.finalist.cmsc.community.taglib;

import com.finalist.cmsc.services.community.person.Person;

public class EmailTag extends AbstractCommunityTag {

   @Override
   protected String getValue(Person person) {
      return person.getEmail();
   }
   
}
