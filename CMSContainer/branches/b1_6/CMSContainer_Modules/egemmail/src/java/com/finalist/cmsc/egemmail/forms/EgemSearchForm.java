package com.finalist.cmsc.egemmail.forms;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

@SuppressWarnings("serial")
public class EgemSearchForm extends ActionForm {

   public static final String SEARCH = "search";
   
   private String author;
   private String keywords;
   private String page;
   private String title;
   private boolean limitToLastWeekModified = true;
   private boolean limitToLastWeekCreated = true;
   private final Set<Integer> nodesOnScreen = new HashSet<Integer>();


   public String getAuthor() {
      return author;
   }


   public String getKeywords() {
      return keywords;
   }


   public Set<Integer> getNodesOnScreen() {
      return nodesOnScreen;
   }


   public String getPage() {
      return page;
   }


   public String getTitle() {
      return title;
   }


   public boolean isLimitToLastWeekModified() {
      return limitToLastWeekModified;
   }


   /*
    * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
    *      javax.servlet.http.HttpServletRequest)
    */
   @Override
   public void reset(ActionMapping mapping, HttpServletRequest request) {
      setLimitToLastWeekModified(false);
      setLimitToLastWeekCreated(false);
      nodesOnScreen.clear();

      super.reset(mapping, request);
   }


   public void setAuthor(String author) {
      this.author = author;
   }


   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }


   public void setLimitToLastWeekModified(boolean lastWeek) {
      this.limitToLastWeekModified = lastWeek;
   }


   public void setPage(String page) {
      this.page = page;
   }

   
   public void setTitle(String title) {
      this.title = title;
   }


   public void setLimitToLastWeekCreated(boolean limitToLastWeekCreated) {
      this.limitToLastWeekCreated = limitToLastWeekCreated;
   }


   public boolean isLimitToLastWeekCreated() {
      return limitToLastWeekCreated;
   }
}
