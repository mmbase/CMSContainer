package com.finalist.cmsc.versioning.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningService;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.versioning.Diff_Match_Patch;

public class DiffAction extends MMBaseFormlessAction {

   private static final String ARCHIVENUMBER = "archivenumber";
   private static final String OBJECTNUMBER = "objectnumber";

   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {

      String objectNumber = getParameter(request, OBJECTNUMBER);
      String archiveNumber = getParameter(request, ARCHIVENUMBER);
      Diff_Match_Patch diff = new Diff_Match_Patch();
      String elementData = Versioning.toXml(cloud.getNode(objectNumber));
      Node versionNode = cloud.getNode(archiveNumber);
      String versionData = versionNode.getStringValue(VersioningService.NODE_DATA);
      request.setAttribute("diffs", diff.diff_prettyHtml(diff.diff_main(elementData, versionData)));
      return mapping.findForward("success");
   }

}
