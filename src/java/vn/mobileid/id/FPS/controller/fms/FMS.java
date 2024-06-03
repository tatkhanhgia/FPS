/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.fms;

import fmsclient.FMSClient;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 *  
 */
public class FMS {
    //<editor-fold defaultstate="collapsed" desc="Upload to FMS - Process Servlet Request">
    public static InternalResponse upload(
            HttpServletRequest request,
            String transactionId) throws Exception{
        //<editor-fold defaultstate="collapsed" desc="Get Headers and document Data">
        byte[] fileData = Utils.getBinaryStream(request);
        String type = Utils.getRequestHeader(request, "x-mime-type");
        if (fileData == null || fileData.length == 0) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_MISSING_FILE_DATA
            );
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>
        
        response =  uploadToFMS(fileData,type,transactionId);
        if(response.getStatus()==A_FPSConstant.HTTP_CODE_SUCCESS){
            String uuid = (String)response.getData();
            response.setMessage(
                    new ResponseMessageController()
                            .writeStringField("uuid", uuid)
                            .build());
        }
        return response;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Download from FMS - Process Servlet Request">
    public static InternalResponse download(
            HttpServletRequest request,
            String transactionId) throws Exception{
        //<editor-fold defaultstate="collapsed" desc="Get Headers and document Data">
        String uuid = request.getRequestURI().split("/")[request.getRequestURI().split("/").length-1];
        
        if(Utils.isNullOrEmpty(uuid)){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL, 
                            A_FPSConstant.SUBCODE_MISSING_UUID
            );
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>
        
        response =  downloadDocumentFromFMS(uuid, transactionId);
        
        return response;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Delete from FMS - Process Servlet Request">
    public static InternalResponse delete(
            HttpServletRequest request,
            String transactionId) throws Exception{
        //<editor-fold defaultstate="collapsed" desc="Get Headers and UUID">
        String uuid = request.getRequestURI().split("/")[request.getRequestURI().split("/").length-1];
        
        if(Utils.isNullOrEmpty(uuid)){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL, 
                            A_FPSConstant.SUBCODE_MISSING_UUID
            );
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>
        
        response =  deleteDocument(uuid, transactionId);
        
        return response;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Upload to FMS">
    /**
     * Tải file lên hệ thống FMS - Upload the file into FMS
     *
     * @param data
     * @param type
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse uploadToFMS(
            byte[] data,
            String type,
            String transactionId
    ) throws Exception {
        FMSClient client = new FMSClient();
        if(Configuration.getInstance().IsGTK_Dev()){
            client.setGTK_Dev();
        }
        client.setURL(Configuration.getInstance().getUrlFMS());
        client.setData(data);
        client.setFormat(type);
        try {
            client.uploadFile();
        } catch (Exception ex) {
            LogHandler.error(
                    FMS.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
        }
        if (client.getHttpCode() != 200) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_FMS_REJECT_UPLOAD);
        }
        String uuid = client.getUUID();
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                (Object) uuid
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Download Document From FMS">
    /**
     * Tải file từ FMS về - Download the file from the FMS
     * @param uuid
     * @param transactionId
     * @return byte[]
     * @throws Exception 
     */
    public  static InternalResponse downloadDocumentFromFMS(
            String uuid,
            String transactionId
    )throws Exception{
        FMSClient client = new FMSClient();
        client.setUUID(uuid);
        if(Configuration.getInstance().IsGTK_Dev()){
            client.setGTK_Dev();
        }
        try{
            client.downloadFile();
        } catch(Exception ex){
            LogHandler.error(
                    FMS.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_ERROR_WHILE_DOWNLOAD_FMS);
        }
        if(client.getHttpCode() != 200){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_FMS_REJECT_DOWNLOAD);
        }
        byte[] data = client.getData();
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                data
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Delete Document">
    /**
     * Tải file từ FMS về - Download the file from the FMS
     * @param uuid
     * @param transactionId
     * @return byte[]
     * @throws Exception 
     */
    public  static InternalResponse deleteDocument(
            String uuid,
            String transactionId
    )throws Exception{
        FMSClient client = new FMSClient();
        client.setUUID(uuid);
        try{
            client.deleteFile();
        } catch(Exception ex){
            LogHandler.error(
                    FMS.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_ERROR_WHILE_DOWNLOAD_FMS);
        }
        if(client.getHttpCode() != 200){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_FMS_REJECT_DOWNLOAD);
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
}
