package com.finalist.cmsc.services.community.domain;

public class GroupForShowVO {
   private String groupName;
   private String users;
   private int userAmount;
   private String groupId;

   public String getGroupId() {
      return groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public String getGroupName() {
      return groupName;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   public String getUsers() {
      return users;
   }

   public void setUsers(String users) {
      this.users = users;
   }
   
   public int getUserAmount() {
      return userAmount;
   }
   
   public void setUserAmount(int userAmount) {
      this.userAmount = userAmount;
   }
}
