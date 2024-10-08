/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.FileManagement;
import fps_core.objects.core.FileFieldAttribute;
import java.util.Base64;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.document.summary.micro.UploadDocument;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Crypto;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/**
 *
 * @author GiaTK
 */
class FileProcessing extends IVersion implements IDocumentProcessing {

    public FileProcessing(Version version) {
        super(version);
    }

    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        switch(getVersion()){
            case V1:{
                return flow1(objects);
            }
            case V2:{
                return flow2(objects);
            }
        }
        return flow1(objects);
    }

    //==========================INTERNAL METHOD=================================
    //<editor-fold defaultstate="collapsed" desc="Flow 1 - Using Stamp version 1">
    public InternalResponse flow1(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        FileFieldAttribute field = (FileFieldAttribute) objects[4];
        String transactionId = (String) objects[5];
        long documentIDOriginal = (long) objects[6];
        byte[] file;

        //Check status document
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }

        //<editor-fold defaultstate="collapsed" desc="Download Document from FMS">
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>
        
        try {
            //<editor-fold defaultstate="collapsed" desc="Add FileField/Stamp into File">
            byte[] resultODF = DocumentUtils_itext7.stamp(
                    file,
                    field,
                    transactionId);
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Analysis file">
            ThreadManagement executor = MyServices.getThreadManagement(2);
            
            Future<?> analysis = executor.submit(new TaskV2(new Object[]{resultODF}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Upload to FMS">
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{resultODF}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        byte[] appendedFile = (byte[]) this.get()[0];
                        response = FMS.uploadToFMS(appendedFile, "pdf", transactionId);
                        return response;
                    } catch (Exception ex) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                        response.setCode(A_FPSConstant.CODE_FMS);
                        response.setCodeDescription(A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
                        response.setException(ex);
                    }
                    return response;
                }
            });

            executor.shutdown();
            //</editor-fold>

            FileManagement fileManagement = (FileManagement) analysis.get();
            if (fileManagement == null) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_CANNOT_ANNALYSIS_THE_DOCUMENT
                );
            }
            fileManagement.setSize(resultODF.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(resultODF, fileManagement.getAlgorithm().getName())));
            response = (InternalResponse) uploadFMS.get();

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //<editor-fold defaultstate="collapsed" desc="Update new Document Revision">
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended Image - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update Field after processing">
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());
            
            //<editor-fold defaultstate="collapsed" desc="Upload Image of Fill (field) into FMS and update new UUID into initField">
            try {
                InternalResponse callFMS = FMS.uploadToFMS(
                        Base64.getDecoder().decode(field.getFile()),
                        "png",
                        transactionId);
                if(callFMS.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
                    System.err.println("Cannot upload new Image of Initial into FMS! Using default");
                } else {
                    String uuid_ = (String)callFMS.getData();
                    field.setFile(uuid_);
                }
            } catch (Exception ex) {
                LogHandler.getInstance().error(FileProcessing.class, 
                        transactionId, 
                        "Cannot upload new Image of Initial into FMS! Using default");
            }

            //</editor-fold>
            
            response = FieldSummaryInternal.updateValueOfField(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(field),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Update Field Detail">
            response = FieldSummaryInternal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    )
                            .writeValueAsString(field),
                    "HMAC",
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                );
            }
            //</editor-fold>
            
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    resultODF
            );
        } catch (Exception ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot add image into file\"}"
            );
            response.setException(ex);
            return response;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Flow 2 - Using Stamp version 2">
    public InternalResponse flow2(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        FileFieldAttribute field = (FileFieldAttribute) objects[4];
        String transactionId = (String) objects[5];
        long documentIDOriginal = (long) objects[6];
        byte[] file;

        //Check status document
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }

        //<editor-fold defaultstate="collapsed" desc="Download Document from FMS">
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>
        
        try {
            //<editor-fold defaultstate="collapsed" desc="Add FileField/Stamp into File">
            byte[] resultODF = DocumentUtils_itext7.stampV2(
                    file,
                    field,
                    transactionId);
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Analysis file">
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);

            Future<?> analysis = executor.submit(new TaskV2(new Object[]{resultODF}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Upload to FMS">
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{resultODF}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        byte[] appendedFile = (byte[]) this.get()[0];
                        response = FMS.uploadToFMS(appendedFile, "pdf", transactionId);
                        return response;
                    } catch (Exception ex) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                        response.setCode(A_FPSConstant.CODE_FMS);
                        response.setCodeDescription(A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
                        response.setException(ex);
                    }
                    return response;
                }
            });

            executor.shutdown();
            //</editor-fold>

            FileManagement fileManagement = (FileManagement) analysis.get();
            if (fileManagement == null) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_CANNOT_ANNALYSIS_THE_DOCUMENT
                );
            }
            fileManagement.setSize(resultODF.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(resultODF, fileManagement.getAlgorithm().getName())));
            response = (InternalResponse) uploadFMS.get();

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //<editor-fold defaultstate="collapsed" desc="Update new Document Revision">
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended Image - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update Field after processing">
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());
            
            //<editor-fold defaultstate="collapsed" desc="Upload Image of Fill (field) into FMS and update new UUID into initField">
            try {
                InternalResponse callFMS = FMS.uploadToFMS(
                        Base64.getDecoder().decode(field.getFile()),
                        "png",
                        transactionId);
                if(callFMS.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
                    System.err.println("Cannot upload new Image of Initial into FMS! Using default");
                } else {
                    String uuid_ = (String)callFMS.getData();
                    field.setFile(uuid_);
                }
            } catch (Exception ex) {
                LogHandler.getInstance().error(FileProcessing.class, 
                        transactionId, 
                        "Cannot upload new Image of Initial into FMS! Using default");
            }

            //</editor-fold>
            
            response = FieldSummaryInternal.updateValueOfField(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(field),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Update Field Detail">
            response = FieldSummaryInternal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    )
                            .writeValueAsString(field),
                    "HMAC",
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                );
            }
            //</editor-fold>
            
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    resultODF
            );
        } catch (Exception ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot add image into file\"}"
            );
            response.setException(ex);
            return response;
        }
    }
    //</editor-fold>
}
