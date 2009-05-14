package com.finalist.cmsc.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.module.Module;


public class ContentElementModule extends Module {
   
   static Log log = LogFactory.getLog(ContentElementModule.class);
   @Override
   public void init() {
       new ContentElementEventListener();
   }
}
