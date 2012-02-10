package com.finalist.cmsc.directreaction.taglib;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mmbase.bridge.*;

import com.finalist.cmsc.directreaction.util.Reaction;
import com.finalist.cmsc.directreaction.util.ReactionUtil;

/**
 * The GetReactionTag will retrieve a single reaction node from the live/remote
 * database/cloud and then populate and return a Reaction object.
 *
 * @author jderuijter
 */
public class GetReactionTag extends SimpleTagSupport {

   private int number;
   private String var;


   @Override
   public void doTag() {
      Cloud remoteCloud = ReactionUtil.getRemoteCloud();
      Node node = remoteCloud.getNode(number);

      Reaction reaction = new Reaction(node.getIntValue("number"), node.getStringValue("title"), node
            .getStringValue("body"), node.getStringValue("name"), node.getStringValue("email"), node
            .getDateValue("creationdate"), this.getRelatedContentTitle());

      getJspContext().setAttribute(var, reaction);
   }


   public String getRelatedContentTitle() {
      String contentTitle = null;
      NodeList nodeList = ReactionUtil.getRemoteCloud().getList("" + number, "reaction,contentelement", "contentelement.title", null,
            null, null, null, true);
      
      //Only using one node, because the getList was using 1 number.
      if (!nodeList.isEmpty()) {
         Node node = nodeList.getNode(0);
         contentTitle = node.getStringValue("contentelement.title");
      }
      
      return contentTitle;
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setVar(String var) {
      this.var = var;
   }

}
