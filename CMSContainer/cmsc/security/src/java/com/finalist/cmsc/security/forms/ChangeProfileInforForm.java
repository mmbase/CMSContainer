package com.finalist.cmsc.security.forms;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;

import com.finalist.cmsc.struts.MMBaseAction;

/**
 * Form bean for the ChangePasswordForm page.
 * 
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class ChangeProfileInforForm extends ActionForm {

   private String username;
   private String password1;
   private String newpassword;
   private String confirmnewpassword;
   private String firstname;
   private String prefix;
   private String surname;

   private String email;
   private String company;
   private String department;
   private boolean emailSignal;

   @Override
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      if ((getPassword1() == null || getPassword1().trim().length() == 0)
            && (getNewpassword() == null || getNewpassword().trim().length() == 0)
            && (getNewpassword() == null || getNewpassword().trim().length() == 0)) {
      } else {
         if (getPassword1() == null || getPassword1().trim().length() == 0) {
            errors.add("password1", new ActionMessage("error.password.incorrect"));
         }
         if (getNewpassword() == null || getNewpassword().trim().length() < 5 || getNewpassword().trim().length() > 15) {
            errors.add("newpassword", new ActionMessage("error.password.invalid"));
         }
         if (getConfirmnewpassword() == null || getConfirmnewpassword().trim().length() < 5
               || getConfirmnewpassword().trim().length() > 15) {
            errors.add("confirmnewpassword", new ActionMessage("error.password.invalid"));
         }
         if (!getConfirmnewpassword().equals(getNewpassword())) {
            errors.add("newpassword", new ActionMessage("error.password.nomatch"));
         }
         if (errors.size() == 0) {
            if (getPassword1().equals(getNewpassword())) {
               errors.add("newpassword", new ActionMessage("error.newpassword.incorrect"));
            } else {
               try {
                  Cloud cloud = MMBaseAction.getCloudFromSession(request);
                  HashMap<String, String> user = new HashMap<String, String>();
                  user.put("username", cloud.getUser().getIdentifier());
                  user.put("password", password1);
                  ContextProvider.getCloudContext(ContextProvider.getDefaultCloudContextName()).getCloud("mmbase",
                        "name/password", user);
               } catch (java.lang.SecurityException se) {
                  errors.add("password1", new ActionMessage("error.password.incorrect"));
               }
            }

         }
      }
      if (getEmail() == null || getEmail().length() == 0) {
         errors.add("email", new ActionMessage("error.email.empty"));
      }
      return errors;
   }

   public String getPassword1() {
      return password1;
   }

   public void setPassword1(String password) {
      this.password1 = password;
   }

   public String getNewpassword() {
      return newpassword;
   }

   public void setNewpassword(String newpassword) {
      this.newpassword = newpassword;
   }

   public String getConfirmnewpassword() {
      return confirmnewpassword;
   }

   public void setConfirmnewpassword(String confirmnewpassword) {
      this.confirmnewpassword = confirmnewpassword;
   }

   public void setFirstname(String firstname) {
      this.firstname = firstname;
   }

   public String getFirstname() {
      return firstname;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String getPrefix() {
      return prefix;
   }

   public void setSurname(String surname) {
      this.surname = surname;
   }

   public String getSurname() {
      return surname;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getEmail() {
      return email;
   }

   public void setCompany(String company) {
      this.company = company;
   }

   public String getCompany() {
      return company;
   }

   public void setDepartment(String department) {
      this.department = department;
   }

   public String getDepartment() {
      return department;
   }

   public void setEmailSignal(boolean emailSignal) {
      this.emailSignal = emailSignal;
   }

   public boolean isEmailSignal() {
      return emailSignal;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getUsername() {
      return username;
   }
}