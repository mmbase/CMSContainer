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

import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.util.http.BulkUploadUtil;

public class AssetUploadAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm assetUploadForm = (AssetUploadForm) form;
      String parentchannel = assetUploadForm.getParentchannel();
      FormFile file = assetUploadForm.getFile();

      String big = "";
      String exsit = "";
      String exsitChannel = "";
      String exsitChannelId = "";
      String exsitAssetTitle = "";
      String isZip = "";
      List<String> notUploadedFiles = new ArrayList<String>();
      List<String> uploadedFiles = new ArrayList<String>();

      if (notEmptyFile(file)) {
         String assetType = AssetElementUtil.judgeAssetType(file.getFileName());
         NodeManager manager = cloud.getNodeManager(assetType);
         if (BulkUploadUtil.isZipFile(file.getContentType(), file.getFileName())) {
            isZip = "isZip";
            List<Integer> nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file, notUploadedFiles,
                  uploadedFiles);
            // to archive the upload asset
            if (nodes != null) {
               AssetElementUtil.addRelationsForNodes(nodes, cloud);
            }
         } else {
            if (BulkUploadUtil.maxFileSizeBiggerThan(file.getFileSize())) {
               if (AssetElementUtil.isNewFile(file, manager)) {
                  List<Integer> nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file, notUploadedFiles,
                        uploadedFiles);
                  // to archive the upload asset
                  if (nodes != null) {
                     AssetElementUtil.addRelationsForNodes(nodes, cloud);
                  }
               } else {
                  exsit = "exsit";
                  exsitChannel = AssetElementUtil.getPathForAsset(file, manager);
                  exsitChannelId = RepositoryUtil.getChannelFromPath(cloud, exsitChannel).getStringValue("number");
                  exsitAssetTitle = AssetElementUtil.getTitleFromExsitAsset(file, manager);
                  notUploadedFiles.add(file.getFileName());
               }
            } else {
               big = "big";
               notUploadedFiles.add(file.getFileName());
            }
         }
      }
      addToSession(request, "notUploadedFiles", notUploadedFiles);
      addToSession(request, "uploadedFiles", uploadedFiles);
      addToSession(request, "uploadingDone", "yes");

      String url = mapping.findForward(SUCCESS).getPath() + "?type=asset&direction=down" + "&parentchannel="
            + parentchannel + "&failed=" + notUploadedFiles.size() + "&uploaded=" + uploadedFiles.size() + "&isZip="
            + isZip + "&big=" + big + "&exsit=" + exsit + "&exsitChannel=" + exsitChannel + "&exsitChannelId="
            + exsitChannelId + "&exsitAssetTitle=" + exsitAssetTitle + "&maxAllowFileSize=" + BulkUploadUtil.getMaxAllowFileSize()/(1024*1024);

      return new ActionForward(url, true);
   }

   private boolean notEmptyFile(FormFile file) {
      return (file.getFileSize() != 0 && StringUtils.isNotEmpty(file.getFileName()));
   }

   private void addToSession(HttpServletRequest request, String name, Object value) {
      if (value != null) {
         request.getSession().setAttribute(name, value);
      }
   }
}
