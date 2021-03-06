package com.finalist.cmsc.favorites.taglib;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.favorites.util.FavoritesUtil;

public class AddTag extends SimpleTagSupport {

   private String user;
   private String name;
   private String url;


   public void doTag() {
      FavoritesUtil.addFavorite(user, name, url);
   }


   public void setName(String name) {
      this.name = name;
   }


   public void setUrl(String url) {
      this.url = url;
   }


   public void setUser(String user) {
      this.user = user;
   }
}
