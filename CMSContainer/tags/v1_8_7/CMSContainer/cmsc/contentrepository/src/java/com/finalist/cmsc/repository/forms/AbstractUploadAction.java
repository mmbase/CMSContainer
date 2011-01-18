package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public abstract class AbstractUploadAction extends MMBaseAction {

   private static final String ATTACHMENTS = "attachments";
   private static final String IMAGES = "images";
   private static final String ALL = "all";
   private static final String SITEASSETS = "siteassets";
   private static final String SESSION_CREATION = "creation";

   protected static final Log log = LogFactory.getLog(AbstractUploadAction.class);

   @Override
   public  ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      
      AssetUploadForm imageUploadForm = (AssetUploadForm) form;
      String parentchannel = imageUploadForm.getParentchannel();
      FormFile file = imageUploadForm.getFile();
      String strict = imageUploadForm.getStrict();

      String exist = "1";
      String exceed = "yes";
      int nodeId = 0;

      if (parentchannel.equalsIgnoreCase(SITEASSETS)) {
         parentchannel = RepositoryUtil.getRoot(cloud);
      } else if (parentchannel.equalsIgnoreCase(ALL) || StringUtils.isEmpty(parentchannel)) {
         parentchannel = (String) request.getSession().getAttribute(SESSION_CREATION);
      }
     List<String> notUploadedFiles = new ArrayList<String>();
      List<String> uploadedFiles = new ArrayList<String>();
      int fileSize = file.getFileSize();
      if (isValidFile(file)) {
         if (BulkUploadUtil.maxFileSizeBiggerThan(fileSize)) {
            NodeManager manager = cloud.getNodeManager(getManagerName());
            exceed = "no";
         if (IMAGES.equals(getManagerName())) {
            if (BulkUploadUtil.isImage(file.getFileName()) || BulkUploadUtil.isZipFile(file.getContentType(), file.getFileName())) {
                  if (AssetElementUtil.isNewFile(file, manager)) {
                     exist = "0";
                     List<Integer> nodes = null;
                     nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file,notUploadedFiles,
                        uploadedFiles);
                     // to archive the upload asset
                     if (nodes != null  && nodes.size() > 0 ) {
                        AssetElementUtil.addRelationsForNodes(nodes, cloud);
                        nodeId = nodes.get(0);
                     }
                  } 
                  else {
                     exist = "1";
                  }
            } 
            else {
               exist = "0";
               exceed = "no";
            }
         }
         else if (ATTACHMENTS.equals(getManagerName())) { 
            if (AssetElementUtil.isNewFile(file, manager)) {
               exist = "0";
               List<Integer> nodes = null;
               nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file,notUploadedFiles,
                     uploadedFiles);
               // to archive the upload asset
               if (nodes != null && nodes.size() > 0 ) {
                  AssetElementUtil.addRelationsForNodes(nodes, cloud);
                  nodeId = nodes.get(0);
               }
            } 
            else {
               exist = "1";
            }            
          }
        }
        else {
            exist = "0";
            }
      }
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&strict=" + strict + "&exist=" + exist
            + "&exceed=" + exceed + "&channelid=" + parentchannel + "&uploadedNodes=" + nodeId, true);
   }
   
   protected abstract String getManagerName() ;
   
   private boolean isValidFile(FormFile file) {
      return (BulkUploadUtil.maxFileSizeBiggerThan(file.getFileSize()) || BulkUploadUtil.isZipFile(file
            .getContentType(), file.getFileName()))
            && file.getFileSize() != 0 && StringUtils.isNotEmpty(file.getFileName());
   }
}
