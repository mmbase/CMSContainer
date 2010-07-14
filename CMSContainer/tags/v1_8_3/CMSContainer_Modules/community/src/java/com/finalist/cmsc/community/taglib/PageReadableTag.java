/**
 * 
 */
package com.finalist.cmsc.community.taglib;

import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.acegisecurity.context.SecurityContextHolder;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.community.security.AuthorityService;


/**
 * @author Billy
 *
 */
public class PageReadableTag extends BodyTagSupport {
   
   private String pageId;
   
   
   
   @Override
   public int doStartTag() throws JspException {
      super.doStartTag();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node pageNode = cloud.getNode(getPageId());
      NodeList pagegroups = pageNode.getRelatedNodes("pagegroup");
      
      if(pagegroups.size() == 0) return EVAL_BODY_INCLUDE;;
      
      AuthorityService authorityService = (AuthorityService) ApplicationContextFactory.getBean("authorityService");   
      org.acegisecurity.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication == null) return SKIP_BODY;
      Set<String> authorityNames = authorityService.getAuthorityNamesForUser(authentication.getName());
      
      for(int i = 0; i < pagegroups.size(); i++){
         Node group = pagegroups.get(i);
         if(authorityNames.contains(group.getValue("name"))) 
            return EVAL_BODY_INCLUDE;
      }
      return SKIP_BODY;
   }

   public String getPageId() {
      return pageId;
   }

   public void setPageId(String pageId) {
      this.pageId = pageId;
   }

}
