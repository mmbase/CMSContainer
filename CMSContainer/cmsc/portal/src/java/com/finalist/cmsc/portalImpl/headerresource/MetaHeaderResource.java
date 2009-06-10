package com.finalist.cmsc.portalImpl.headerresource;

import com.finalist.cmsc.util.XmlUtil;

public class MetaHeaderResource implements HeaderResource {

   /**
    * <meta name="name" content="content" lang="lang" http-equiv="httpEquiv"/>
    */

   private String name;
   private String content;
   private String lang;
   private String httpEquiv;
   private boolean dublin;


   public MetaHeaderResource(boolean dublin, String name, String content, String lang, String httpEquiv) {
      this.dublin = dublin;
      this.name = name;
      this.content = content;
      this.lang = lang;
      this.httpEquiv = httpEquiv;
   }


   public MetaHeaderResource(boolean dublin, String name, String content) {
      this(dublin, name, content, null, null);
   }


   public boolean isDublin() {
      return dublin;
   }

   public void render(StringBuffer buffer) {
      if (content != null && content.length() > 0) {
         buffer.append("<meta name=\"");
         if (isDublin()) {
            buffer.append("DC.");
         }
         buffer.append(name);
         buffer.append("\" content=\"");
         String contentStr = "";
         contentStr = content.replaceAll("\"", "");
         contentStr = XmlUtil.xmlEscape(contentStr);
         buffer.append(contentStr);
         buffer.append("\"");
         if (lang != null) {
            buffer.append(" lang=\"");
            buffer.append(lang);
            buffer.append("\"");
         }
         if (httpEquiv != null) {
            buffer.append(" http-equiv=\"");
            buffer.append(httpEquiv);
            buffer.append("\"");
         }
         buffer.append("/>");
      }
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (dublin ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }


   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      MetaHeaderResource other = (MetaHeaderResource) obj;
      if (dublin != other.dublin) return false;
      if (name == null) {
         if (other.name != null) return false;
      }
      else
         if (!name.equals(other.name)) return false;
      return true;
   }
   
   @Override
   public String toString() {
      return "meta_" + (isDublin() ? "DC." : ".") + name;
   }
}
