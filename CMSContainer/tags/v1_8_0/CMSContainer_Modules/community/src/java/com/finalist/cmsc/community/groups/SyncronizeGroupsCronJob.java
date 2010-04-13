/**
 * 
 */
package com.finalist.cmsc.community.groups;

import org.mmbase.applications.crontab.AbstractCronJob;
import org.mmbase.applications.crontab.CronJob;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.security.GroupsService;


/**
 * @author Billy
 *
 */
public class SyncronizeGroupsCronJob extends AbstractCronJob implements CronJob {

   @Override
   public void run() {
      getGroupsService().syncronizeGroupsFromIDstore();
   }
   
   public GroupsService getGroupsService() {
      return (GroupsService)ApplicationContextFactory.getBean("groupsService");
   }
}
