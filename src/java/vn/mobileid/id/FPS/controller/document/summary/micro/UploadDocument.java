/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.micro;

import fps_core.enumration.DocumentStatus;
import fps_core.objects.FileManagement;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class UploadDocument {

    /**
     * Tạo một package mới - Create a new package
     *
     * @param name
     * @param enterpriseId
     * @param enterpriseName
     * @param hmac
     * @param transactionId
     * @return
     * @throws Exception
     */
    public  static InternalResponse createPackage(
            String name,
            int enterpriseId,
            String enterpriseName,
            String hmac,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_document().createDocumentPackage(
                name,
                enterpriseId,
                enterpriseName,
                hmac,
                transactionId);
        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {

            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    response.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                (long) response.getObject()
        );
    }

    //<editor-fold defaultstate="collapsed" desc="Upload file into DB">
    /**
     * Tải file lên hệ thống DB của FPS - Upload the file into DB of FPS
     *
     * @param documentPackage
     * @param revision
     * @param file
     * @param status
     * @param url
     * @param content
     * @param uuid
     * @param dmsProperty
     * @param hmac
     * @param createdBy
     * @param transactionId
     * @return InternalResponse with documentId (long) as an Object
     * @throws Exception
     */
    public  static InternalResponse uploadDocument(
            long documentPackage,
            int revision,
            FileManagement file,
            DocumentStatus status,
            String url,
            String content,
            String uuid,
            String dmsProperty,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_document().uploadDocument(
                documentPackage,
                0,
                revision,
                file.getName(),
                file.getSize(),
                url,
                file.getDocumentType(),
                status,
                file.getRotate(),
                file.getPage(),
                file.getHeight(),
                file.getWidth(),
                file.getDocumentCustom(),
                file.getDigest(),
                content,
                uuid,
                Configuration.getInstance().getUrlFMS(),
                hmac,
                createdBy,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        ).setData(response.getObject());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Upload file into DB with ClientID">
    /**
     * Tải file lên hệ thống DB của FPS - Upload the file into DB of FPS
     *
     * @param documentPackage
     * @param identityOfClientId
     * @param revision
     * @param file
     * @param status
     * @param url
     * @param content
     * @param uuid
     * @param dmsProperty
     * @param hmac
     * @param createdBy
     * @param transactionId
     * @return long DocumentId
     * @throws Exception
     */
    public  static InternalResponse uploadDocument_v2(
            long documentPackage,
            long identityOfClientId,
            int revision,
            FileManagement file,
            DocumentStatus status,
            String url,
            String content,
            String uuid,
            String dmsProperty,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_document().uploadDocument(
                documentPackage,
                identityOfClientId,
                revision,
                file.getName(),
                file.getSize(),
                url,
                file.getDocumentType(),
                status,
                file.getRotate(),
                file.getPage(),
                file.getHeight(),
                file.getWidth(),
                file.getDocumentCustom(),
                file.getDigest(),
                content,
                uuid,
                Configuration.getInstance().getUrlFMS(),
                hmac,
                createdBy,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
        //Test function createPackage
//        InternalResponse res = UploadDocument.createPackage(
//                "Document1",
//                3,
//                "Mobile-Id",
//                "hmac",
//                "transactionId");
//        if (res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            System.out.println("Message:" + res.getMessage());
//        } else {
//            System.out.println("Id:" + res.getData());
//        }

        //Test function Upload file to FMS
//        InternalResponse res = UploadDocument.uploadToFMS(
//                Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\3signature10MB.pdf")),
//                "pdf",
//                "test transaction");
//        if (res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            System.out.println("Message:" + res.getMessage());
//        } else {
//            System.out.println("Id:" + res.getData());
//        }
        //Test function upload file to DB
//        InternalResponse res = UploadDocument.uploadDocument_v2(
//                0,
//                1,
//                "document Name1",
//                0,
//                "Url",
//                DocumentType.PDF,
//                "digest",
//                "content",
//                "uuid",
//                "FMS",
//                "hmac",
//                "GiaTK",
//                "transactionId");
//        
//        if(res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Message:"+res.getMessage());
//        } else {
//            System.out.println("Status:"+res.getStatus());
//        }
    }

}
