package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import com.finalist.util.http.BulkUploadUtil;

public class AssetUploadAction extends AbstractUploadAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm assetUploadForm = (AssetUploadForm) form;
      String parentchannel = assetUploadForm.getParentchannel();
      FormFile file = assetUploadForm.getFile();

      String url = "";
      String emptyFileName = "no";
      String exceed = "no";
      String emptyFile = "no";
      String uploadingDone = "yes";
      int fileSize = file.getFileSize();
      int failed = 0;
      int uploaded = 0;
      List<String> notUploadedFiles = new ArrayList<String>();
      List<String> uploadedFiles = new ArrayList<String>();

      if (!BulkUploadUtil.maxFileSizeBiggerThan(fileSize)
            &&!BulkUploadUtil.isZipFile(file.getContentType(), file.getFileName())) {
         exceed = "yes";
         uploadingDone = "no";
      } else if (fileSize == 0) {
         emptyFile = "yes";
         uploadingDone = "no";
      } else if (StringUtils.isEmpty(file.getFileName())) {
         emptyFileName = "yes";
         uploadingDone = "no";
      } else{
         String assetType = "";
         if (isImage(file.getFileName())) {
            assetType = "images";
         } else {
            assetType = "attachments";
         }

         NodeManager manager = cloud.getNodeManager(assetType);

         if (isNewFile(file, manager)) {
            List<Integer> nodes = null;
            nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file, notUploadedFiles, uploadedFiles);
            // to archive the upload asset
            if (nodes != null) {
               addRelationsForNodes(nodes, cloud);
               uploaded = nodes.size();
            }
         } else {
            notUploadedFiles.add(file.getFileName());
         }
      }
      failed = notUploadedFiles.size();
      uploaded = uploadedFiles.size();

      if (notUploadedFiles != null) {
         request.getSession().setAttribute("notUploadedFiles", notUploadedFiles);
      }
      if (uploadedFiles != null) {
         request.getSession().setAttribute("uploadedFiles", uploadedFiles);
      }

      url = mapping.findForward(SUCCESS).getPath() + "?type=asset&direction=down" + "&parentchannel=" + parentchannel
            + "&uploaded=" + uploaded + "&failed=" + failed + "&uploadingDone=" + uploadingDone + "&emptyFileName=" 
            + emptyFileName + "&exceed=" + exceed + "&emptyFile=" + emptyFile;

      return new ActionForward(url, true);
   }
}
