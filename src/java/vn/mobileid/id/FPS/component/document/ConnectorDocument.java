/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.DocumentType;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FileManagement;
import fps_core.objects.core.QRFieldAttribute;
import fps_core.objects.core.Signature;
import fps_core.objects.core.SignatureFieldAttribute;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.component.field.AddField;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.component.field.GetField;
import vn.mobileid.id.FPS.component.field.UpdateField;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import fps_core.enumration.FieldTypeName;
import fps_core.mixin.InitialsFieldAttributeMixIn;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.SyncForDokobit;
import vn.mobileid.id.FPS.object.TemporalObject;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.DocumentSerializer;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.systemManagement.Resources;
import vn.mobileid.id.utils.ManagementTemporal;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.utils.Utils;
import fps_core.objects.core.InitialsFieldAttribute;
import fps_core.objects.core.TextFieldAttribute;
import vn.mobileid.id.FPS.component.document.process.interfaces.IVersion;
import vn.mobileid.id.FPS.object.ProcessFileField;
import vn.mobileid.id.FPS.object.ProcessInitialField;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/**
 *
 * @author GiaTK
 */
public class ConnectorDocument {

    // <editor-fold defaultstate="collapsed" desc="Upload Documents">
    public static InternalResponse uploadDocument(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        //Get Header
        byte[] fileData = Utils.getBinaryStream(request);
        String fileName = Utils.getRequestHeader(request, "x-file-name");
        if (fileName == null) {
            fileName = "Document" + "_" + Utils.generateUUID();
        }
        String temp = Utils.getRequestHeader(request, "x-convert-document");
        Boolean isConvert = Boolean.valueOf(temp == null ? "false" : temp);
        if (fileData == null || fileData.length == 0) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_MISSING_FILE_DATA
            );
            response.setUser(user);
            return response;
        }

        //Create Package to handle
        response = UploadDocument.createPackage(
                fileName,
                user.getAid(),
                user.getAzp(),
                "hmac",
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        long packageId = (long) response.getData();

        //Pool Upload + Analysis
//        ExecutorService executor = Executors.newFixedThreadPool(2);
        ThreadManagement threadPool = MyServices.getThreadManagement();
        Future<?> upload = threadPool.submitTask(new TaskV2(new Object[]{fileData}, transactionId) {
            @Override
            public Object call() {
                InternalResponse res = new InternalResponse();
                try {
                    res = FMS.uploadToFMS(
                            fileData,
                            DocumentType.PDF.getType(),
                            transactionId);
                } catch (Exception ex) {
                    res.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                    res.setException(ex);
                    res.setCode(A_FPSConstant.CODE_FMS);
                    res.setCodeDescription(A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
                }
                return res;
            }
        });
        Future<?> analysis = threadPool.submitTask(new TaskV2(new Object[]{fileData}, transactionId) {
            @Override
            public Object call() {
                try {
                    return DocumentUtils_itext7.analysisPDF_i7(fileData);
                } catch (Exception ex) {
                    return null;
                }
            }
        });

        response = (InternalResponse) upload.get();
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        FileManagement file = (FileManagement) analysis.get();
        if (file == null) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_CANNOT_ANALYSIS_FILE
            );
            response.setUser(user);
            return response;
        }

        file.setName(fileName);
        String uuid = (String) response.getData();

        //Upload file to DB
        response = UploadDocument.uploadDocument(
                packageId,
                1,
                file,
                DocumentStatus.UPLOADED,
                "none",
                "none",
                uuid,
                "Uploaded to FMS",
                "hmac",
                user.getAzp(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        response.setUser(user);
        response.setStatus(A_FPSConstant.HTTP_CODE_CREATED);
        response.setMessage(new ResponseMessageController()
                .writeNumberField("document_id", packageId)
                .build());
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Upload Documents Base64">
    public static InternalResponse uploadDocumentBase64(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        //Get Header
        String fileName = Utils.getRequestHeader(request, "x-file-name");
        if (fileName == null) {
            fileName = "Document" + "_" + Utils.generateUUID();
        }
        String temp = Utils.getRequestHeader(request, "x-convert-document");
        Boolean isConvert = Boolean.valueOf(temp == null ? "false" : temp);

        //Check
        if (Utils.isNullOrEmpty(payload)) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            );
            response.setUser(user);
            return response;
        }
        String fileData_base64 = null;
        byte[] fileData = null;
        try {
            fileData_base64 = Utils.getFromJson("file_data", payload);
            fileData = Base64.getDecoder().decode(fileData_base64);
        } catch (IllegalArgumentException e) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_BASE64_IS_INVALID_SCHEME
            );
            response.setUser(user);
            return response;
        } catch (Exception ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
            response.setUser(user);
            return response;
        }

        //Create Package to handle
        response = UploadDocument.createPackage(
                fileName,
                user.getAid(),
                user.getAzp(),
                "hmac",
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        long packageId = (long) response.getData();

        //Pool Upload + Analysis
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> upload = executor.submit(new TaskV2(new Object[]{fileData}, transactionId) {
            @Override
            public Object call() {
                byte[] fileData = (byte[]) this.get()[0];
                InternalResponse res = new InternalResponse();
                try {
                    res = FMS.uploadToFMS(
                            fileData,
                            DocumentType.PDF.getType(),
                            transactionId);
                } catch (Exception ex) {
                    res.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                    res.setException(ex);
                }
                return res;
            }
        });
        Future<?> analysis = executor.submit(new TaskV2(new Object[]{fileData}, transactionId) {
            @Override
            public Object call() {
                try {
                    byte[] fileData = (byte[]) this.get()[0];
                    return DocumentUtils_itext7.analysisPDF_i7(fileData);
                } catch (Exception ex) {
                    return null;
                }
            }
        });

        response = (InternalResponse) upload.get();
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        FileManagement file = (FileManagement) analysis.get();
        if (file == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_CANNOT_ANALYSIS_FILE
            );
        }

        file.setName(fileName);
        String uuid = (String) response.getData();

        //Upload file to DB
        response = UploadDocument.uploadDocument(
                packageId,
                1,
                file,
                DocumentStatus.UPLOADED,
                "none",
                "none",
                uuid,
                "Uploaded to FMS",
                "hmac",
                user.getAzp(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        response.setUser(user);
        response.setStatus(A_FPSConstant.HTTP_CODE_CREATED);
        response.setMessage(new ResponseMessageController()
                .writeNumberField("document_id", packageId)
                .build());
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Download Documents">
    public static InternalResponse downloadDocument(
            HttpServletRequest request,
            long packageId,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        //Get Headers
        String temp = Utils.getRequestHeader(request, "x-original-file");
        Boolean isOriginal = Boolean.valueOf(temp == null ? "false" : temp);

        //Get documents
        response = GetDocument.getDocuments(
                packageId,
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        List<Document> listDoc = (List) response.getData();
        String uuid = null;
        for (Document doc : listDoc) {
            if (isOriginal && doc.getRevision() == 1) {
                uuid = doc.getUuid();
            }
            if (!isOriginal && (doc.getRevision() == listDoc.size())) {
                uuid = doc.getUuid();
            }
        }
        System.out.println("\n===Download Document from FMS===");
        System.out.println("\tUUID:" + uuid);
        response = FMS.downloadDocumentFromFMS(
                uuid,
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                response.getData()
        );
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Download Documents Base64">
    public static InternalResponse downloadDocumentBase64(
            HttpServletRequest request,
            long packageId,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        String temp = Utils.getRequestHeader(request, "x-original-file");
        Boolean isOriginal = Boolean.valueOf(temp == null ? "false" : temp);

        //Get documents
        response = GetDocument.getDocuments(
                packageId,
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        List<Document> listDoc = (List) response.getData();
        String uuid = null;
        for (Document doc : listDoc) {
            if (isOriginal && doc.getRevision() == 1) {
                uuid = doc.getUuid();
            }
            if (!isOriginal && (doc.getRevision() == listDoc.size())) {
                uuid = doc.getUuid();
            }
        }
        response = FMS.downloadDocumentFromFMS(
                uuid,
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController()
                        .writeStringField(
                                "file_data",
                                Base64.getEncoder().encodeToString((byte[]) response.getData()))
                        .build()
        );
        response.setUser(user);

        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Documents">
    public static InternalResponse getDocuments(
            HttpServletRequest request,
            long packageId,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        response = GetDocument.getDocuments(packageId, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        response.setUser(user);

        //Get Documents and serializer it
        List<Document> listDoc = (List<Document>) response.getData();
        DocumentSerializer serializer = new DocumentSerializer(listDoc, packageId);
        response.setMessage(MyServices.getJsonService().writeValueAsString(serializer));
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Sign Document">
    public static InternalResponse signDocument(
            HttpServletRequest request,
            String payload,
            long packageId,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            );
            response.setUser(user);
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check and parse Payload">
        response = CheckPayloadRequest.checkSignRequest(payload, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        ProcessingRequest processRequest = (ProcessingRequest) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Documents">
        response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }

        List<Document> documents = (List<Document>) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get SignatureField with the name is in payload of request">
        Document document_ = null;
        for (Document document : documents) {
            if (document.getRevision() == 1) {
                response = ConnectorField_Internal.getField(
                        document.getId(),
                        processRequest.getFieldName(),
                        transactionId);
            }
            if (document.getRevision() == documents.size()) {
                document_ = document;
            }
        }
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check field is processed yet? and is type signature?">
        if (!Utils.isNullOrEmpty(fieldData.getFieldValue())) {
            SignatureFieldAttribute temp = MyServices.getJsonService().readValue(fieldData.getFieldValue(), SignatureFieldAttribute.class);
            if (temp.getProcessStatus().equalsIgnoreCase(ProcessStatus.PROCESSED.getName())) {
                response = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS
                );
                response.setUser(user);
                return response;
            }
        }
        if (!fieldData.getType().getTypeName().equals("SIGNATURE")) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION
            );
            response.setUser(user);
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check Hash of Signature field is existed in Temporal Table?">
        InternalResponse response2 = ManagementTemporal.getTemporal(
                String.valueOf(document_.getId()),
                processRequest.getHashValue(),
                transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_DOCUMENT_IS_ALREADY_CHANGES_GETHASH_AGAIN
            );
            response.setUser(user);
            return response;
        }

        TemporalObject temporalObject = (TemporalObject) response2.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Mapping ExtendFieldAttribute into SignatureFieldAttribute">
        SignatureFieldAttribute signatureField = new SignatureFieldAttribute();
        signatureField.setDimension(fieldData.getDimension());
        signatureField.setFieldName(fieldData.getFieldName());
        signatureField.setPage(fieldData.getPage());
        signatureField.setVisibleEnabled(fieldData.getVisibleEnabled());
        signatureField.setVerification(MyServices.getJsonService(new ObjectMapper().setAnnotationIntrospector(
                new IgnoreIngeritedIntrospector())).readValue(payload, Signature.class));
        signatureField.setLevelOfAssurance(fieldData.getLevelOfAssurance());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Fill Signature">
        response = new ProcessingFactory().createType_Module(ProcessingFactory.TypeProcess.SIGNATURE).fillFormField(
                user,
                processRequest.isSkipVerification(),
                document_,
                documents.size(),
                fieldData.getDocumentFieldId(),
                signatureField,
                transactionId,
                temporalObject.getData());

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Thread to update Process status of the QR Field Attribute if existed">
        ExecutorService executors = Executors.newFixedThreadPool(2);
        if (response.getInternalData() != null) {
            Future<?> future = executors.submit(
                    new TaskV2(
                            new Object[]{response.getInternalData().getValue()},
                            transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        ExtendedFieldAttribute qrField_ex = (ExtendedFieldAttribute) this.get()[0];
                        QRFieldAttribute qr = MyServices.getJsonService().readValue(qrField_ex.getDetailValue(), QRFieldAttribute.class);
                        qr = (QRFieldAttribute) qrField_ex.clone(qr, qrField_ex.getDimension());
                        qr.setProcessStatus(ProcessStatus.PROCESSED.getName());
                        response = UpdateField.updateValueOfField(
                                qrField_ex.getDocumentFieldId(),
                                user,
                                MyServices.getJsonService().writeValueAsString(qr),
                                transactionId);
                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            response.setUser(user);
                            return response;
                        }
                    } catch (Exception ex) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                        response.setCode(A_FPSConstant.CODE_DOCUMENT);
                        response.setCodeDescription(A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD);
                        response.setException(ex);
                    }
                    return response;
                }
            });
            if (((InternalResponse) future.get()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return ((InternalResponse) future.get()).setUser(user);
            }
        }

        //Delete all data in temporal which name is "document_.getId()"
        Future<?> future2 = executors.submit(
                new TaskV2(
                        new Object[]{String.valueOf(document_.getId())}, transactionId) {
            @Override
            public Object call() {
                try {
                    String temp = (String) this.get()[0];
                    return ManagementTemporal.removeTemporal(temp, transactionId);
                } catch (Exception ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            "Test"
                    );
                }
            }
        }
        );
        executors.shutdown();

        if (((InternalResponse) future2.get()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return ((InternalResponse) future2.get()).setUser(user);
        }
        //</editor-fold>

        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
            response = ProcessModuleForEnterprise.getInstance(user).processResponse(user.getScope(), packageId, transactionId);
            return response.setUser(user);
        }
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Create Form Field">
    /**
     * Create a new form field in file
     *
     * @param request
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse createFormField(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        //Check payload
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }

        //Parse payload
        ProcessingRequest processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessingRequest.class);
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            ).setException(ex).setUser(user);
        }

        return new ProcessingTextFormField().processMultipleTextField(
                Utils.getIdFromURL(request.getRequestURI()),
                user,
                processRequest.getText(),
                transactionId)
                .setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Document Image">
    public static InternalResponse getDocumentImage(
            HttpServletRequest request,
            long packageId,
            int page,
            String transactionId
    ) throws Exception {
        //Process header
        String temp = Utils.getRequestHeader(request, "x-original-file");
        String isItext_ = Utils.getRequestHeader(request, "x-itext-enabled");
        String imageType = Utils.getRequestHeader(request, "x-image-type");
        Boolean isOriginal = Boolean.valueOf(temp == null ? "false" : temp);
        Boolean isItext = Boolean.valueOf(isItext_ == null ? "false" : isItext_);

        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        //Get Document Image
        response = GetDocument.getDocumentImage(
                packageId,
                page,
                isItext,
                imageType,
                transactionId);

        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Document Image Base64">
    public static InternalResponse getDocumentImageBase64(
            HttpServletRequest request,
            long packageId,
            int page,
            String transactionId
    ) throws Exception {
        //Process header
        String temp = Utils.getRequestHeader(request, "x-original-file");
        String isItext_ = Utils.getRequestHeader(request, "x-itext-enabled");
        String imageType = Utils.getRequestHeader(request, "x-image-type");
        Boolean isOriginal = Boolean.valueOf(temp == null ? "false" : temp);
        Boolean isItext = Boolean.valueOf(isItext_ == null ? "false" : isItext_);

        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        //Get Document Image
        response = GetDocument.getDocumentImage(
                packageId,
                page,
                isItext,
                imageType,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        byte[] temp_ = (byte[]) response.getData();
        String format = (String) response.getHeaders().get("x-image-type");

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController()
                        .writeStringField(
                                "file_data",
                                Base64.getEncoder().encodeToString(temp_))
                        .writeStringField(
                                "image_format",
                                format)
                        .build());
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fill Form Field">
    public static InternalResponse fillFormField_V1(
            HttpServletRequest request,
            String payload,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse Payload">
        ProcessingRequest processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessingRequest.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            ).setException(ex).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Text Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getText())) {
            response = new ProcessingTextFormField(new TextFieldAttribute()).processMultipleTextField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getText(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process DateTime Field">
        if (!Utils.isNullOrEmpty(processRequest.getDateTimes())) {
            response = new ProcessingDateTimeField().processMultipleTextField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getDateTimes(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Checkbox Form Field V1">
        if (!Utils.isNullOrEmpty(processRequest.getCheckbox())) {
            response = new ProcessingCheckboxFormField(IVersion.Version.V1).processCheckboxField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getCheckbox(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Checkbox Form Field - Version2 nhen">
        if (!Utils.isNullOrEmpty(processRequest.getCheckboxV2())) {
            response = new ProcessingCheckboxFormField(IVersion.Version.V2).processCheckboxField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getCheckboxV2(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Radio Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getRadio())) {
            response = ProcessingRadioboxFormField.processRadioField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getRadio(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Stamp Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getStamp())) {
            response = ProcessingFileField.processMultipleFileFormField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getStamp(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Camera Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getCameras())) {
            response = ProcessingCameraField.processMultipleCameraField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getCameras(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Attachment Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getAttachment())) {
            response = ProcessingAttachmentField.processMultipleFileFormField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getAttachment(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Hyperlink Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getHyperlinks())) {
            response = ProcessingHyperLinkField.processMultipleHyperLinkField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getHyperlinks(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process ComboBox Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getCombos())) {
            response = new ProcessingComboBoxField().processMultipleTextField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getCombos(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Toggle Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getToggles())) {
            response = new ProcessingToggleField().processMultipleTextField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getToggles(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process NumericStepper Form Field">
        if (!Utils.isNullOrEmpty(processRequest.getNumericSteppers())) {
            response = new ProcessingNumericStepper().processMultipleTextField(
                    Utils.getIdFromURL(request.getRequestURI()),
                    user,
                    processRequest.getNumericSteppers(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }
        //</editor-fold>

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Document Verification">
    public static InternalResponse getDocumentVerification(
            HttpServletRequest request,
            long packageId,
            String transactionId
    ) throws Exception {
        //Get Documents and verify
        InternalResponse response = getDocuments(request, packageId, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        List<Document> documents = (List<Document>) response.getData();
        Document document = null;
        for (Document document_ : documents) {
            if (document_.getRevision() == documents.size()) {
                document = document_;
            }
        }

        //Verify all signature fields in PDF
        response = FMS.downloadDocumentFromFMS(document.getUuid(), transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        byte[] pdf = (byte[]) response.getData();

        //<editor-fold defaultstate="collapsed" desc="Get SignatureField with the name is in payload of request">
        List<ExtendedFieldAttribute> fields = new ArrayList<>();
        for (Document temp : documents) {
            response = GetField.getFieldsData(
                    temp.getId(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            fields.addAll((List<ExtendedFieldAttribute>) response.getData());
        }

        //</editor-fold>
        List<Signature> listSignature1 = DocumentUtils_itext7.verifyDocument_i7(pdf);

        if (listSignature1 == null || listSignature1.isEmpty()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_CANNOT_VERIFY_THIS_DOCUMENT
            );
        }

        //Change field_name in List<Sig> into right field_name
        try {
            for (Signature signature : listSignature1) {
                for (ExtendedFieldAttribute field : fields) {
                    try {
                        SignatureFieldAttribute temp = MyServices.getJsonService().readValue(field.getDetailValue(), SignatureFieldAttribute.class);
                        if (temp.getVerification().getSignatureId().equals(signature.getFieldName())
                                || temp.getVerification().getSignatureId().equals(signature.getSignatureId())) {
                            signature.setFieldName(field.getFieldName());
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("DocumentPackage:" + packageId + " does not containts field but in file PDF is have it!! Warning");
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                MyServices.getJsonService().writeValueAsString(listSignature1)
        );
    }
    // </editor-fold>

//    // <editor-fold defaultstate="collapsed" desc="Get Hash of Signature Field">
//    public static InternalResponse getHashOfSignatureField(
//            HttpServletRequest request,
//            String payload,
//            long packageId,
//            String transactionId
//    ) throws Exception {
//        //<editor-fold defaultstate="collapsed" desc="Verify">
//        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
//        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response;
//        }
//        User user = (User) response.getData();
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Check input and get header datas">
//        if (Utils.isNullOrEmpty(payload)) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                    A_FPSConstant.CODE_FAIL,
//                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
//            ).setUser(user);
//        }
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Check detail in payload and parse into ProcessRequest Object">
//        response = CheckPayloadRequest.checkHashRequest(payload, transactionId);
//        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response.setUser(user);
//        }
//        ProcessingRequest processRequest = (ProcessingRequest) response.getData();
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Get Documents">
//        response = GetDocument.getDocuments(
//                packageId,
//                transactionId);
//
//        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response.setUser(user);
//        }
//
//        List<Document> documents = (List<Document>) response.getData();
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Get Signature Field based on the name that in payload of the request">
//        Document document_ = null;
//        long documentIdOriginal = 0;
//        for (Document document : documents) {
//            if (document.getRevision() == 1) {
//                response = ConnectorField_Internal.getField(
//                        document.getId(),
//                        processRequest.getFieldName(),
//                        transactionId);
//                documentIdOriginal = document.getId();
//            }
//            if (document.getRevision() == documents.size()) {
//                document_ = document;
//            }
//        }
//        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response.setUser(user);
//        }
//        ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Check field is processed yet? and is type signature">
//        if (!fieldData.getType().getParentType().equals(FieldTypeName.SIGNATURE.getParentName())) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                    A_FPSConstant.CODE_FIELD,
//                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION
//            ).setUser(user);
//        }
//
//        InternalResponse response_1 = CheckFieldProcessedYet.checkProcessed(fieldData.getFieldValue());
//        if (response_1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response_1.setUser(user);
//        }
//
//        //</editor-fold>
//        
//        //<editor-fold defaultstate="collapsed" desc="Check Hash of Signature field is existed in Temporal Table?">
//        if (!Utils.isNullOrEmpty(fieldData.getHash())) {
//            InternalResponse response2 = ManagementTemporal.getTemporal(
//                    String.valueOf(document_.getId()),
//                    fieldData.getHash(),
//                    transactionId);
//            if (response2.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                ManagementTemporal.removeTemporal(
//                        String.valueOf(document_.getId()),
//                        transactionId);
////                return new InternalResponse(
////                        A_FPSConstant.HTTP_CODE_SUCCESS,
////                        new ResponseMessageController().writeStringField("hash_value", fieldData.getHash()).build()
////                ).setUser(user);
//            }
//        }
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Mapping into SignatureFieldAttribute">
//        SignatureFieldAttribute signatureField = new SignatureFieldAttribute();
//        signatureField = MyServices.getJsonService().readValue(fieldData.getDetailValue(), SignatureFieldAttribute.class);
//        signatureField = (SignatureFieldAttribute) fieldData.clone(signatureField, fieldData.getDimension());
//
//        signatureField.setHandSignatureImage(processRequest.getHandSignatureImage());
//        signatureField.setLevelOfAssurance(fieldData.getLevelOfAssurance());
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
//        signatureField.setProcessOn(dateFormat.format(Date.from(Instant.now())));
//        signatureField.setProcessBy(user.getAzp());
//
//        if(signatureField.getVerification() == null){
//            signatureField.setVerification(new Signature());
//        }
//        processRequest.convert(signatureField);
//        
//        if (!Utils.isNullOrEmpty(signatureField.getHandSignatureImage())) {
//            signatureField.getVerification().setImageEnabled(true);
//        }
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Processing - Create Form Field">
//        response = ProcessingFactory.createType_Module(ProcessingFactory.TypeProcess.SIGNATURE).createFormField(new Object[]{
//            user,
//            document_,
//            documentIdOriginal,
//            signatureField,
//            signatureField.getLevelOfAssurance() != null ? signatureField.getLevelOfAssurance().contains("ESEAL") : false,
//            transactionId
//        });
//        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response.setUser(user);
//        }
//
//        ExtendedFieldAttribute qrField_ex = null;
//        QRFieldAttribute qrField = null;
//        if (response.getInternalData() != null) {
//            qrField_ex = (ExtendedFieldAttribute) response.getInternalData().getValue();
//            qrField = MyServices.getJsonService().readValue(qrField_ex.getDetailValue(), QRFieldAttribute.class);
//            qrField = (QRFieldAttribute) qrField_ex.clone(qrField, qrField_ex.getDimension());
//            qrField.setProcessStatus(ProcessStatus.PROCESSED.getName());
//        }
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Update hash of the Field">
//        String hash = (String) ((Object[]) response.getData())[1];
//
//        InternalResponse response2
//                = UpdateField.updateHashOfField(
//                        fieldData.getDocumentFieldId(),
//                        user,
//                        hash,
//                        transactionId);
//        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response2.setUser(user);
//        }
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Set Signature name into SignatureFieldAttribute">
//        signatureField.getVerification().setSignatureId((String) ((Object[]) response.getData())[3]);
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Update field details (Value in DB will relative to the input of client)">
//        signatureField.setProcessStatus(ProcessStatus.IN_PROCESS.getName());
//        response2
//                = UpdateField.updateValueOfField(
//                        fieldData.getDocumentFieldId(),
//                        user,
//                        MyServices.getJsonService().writeValueAsString(signatureField),
//                        transactionId);
//        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            response2.setUser(user);
//            return response2;
//        }
//
//        response2
//                = UpdateField.updateFieldDetails(
//                        fieldData.getDocumentFieldId(),
//                        user,
//                        signatureField,
//                        "hmac",
//                        transactionId);
//        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            response2.setUser(user);
//            return response2;
//        }
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="Create Thread Pool to handle 3 thread. Upload TemporaData ; Update Status of Document and update Value of QR Field if existed">
//        ExecutorService executors = Executors.newFixedThreadPool(3);
//
//        //Upload temporal data
//        Future<?> uploadTemporalData = executors.submit(new TaskV2(new Object[]{
//            (byte[]) ((Object[]) response.getData())[2],
//            hash,
//            document_.getId()}, transactionId) {
//            @Override
//            public Object call() {
//                InternalResponse response = new InternalResponse();
//                try {
//                    byte[] temporal = (byte[]) this.get()[0];
//                    response = ManagementTemporal.addTemporal(
//                            String.valueOf(this.get()[2]),
//                            (String) this.get()[1],
//                            TemporalObject.Type.TEMPORAL_DATA.getId(),
//                            temporal,
//                            transactionId);
//                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                        response.setUser(user);
//                        return response;
//                    }
//                } catch (Exception ex) {
//                    LogHandler.error(ConnectorDocument.class, transactionId, ex);
//                    response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
//                    response.setException(ex);
//                    response.setMessage(ResponseMessageController
//                            .errorMessageAdvanced(
//                                    "Error while calling in Future",
//                                    "Future 'upload Temporal Data' is made an exception"));
//                }
//                return response;
//            }
//        });
//
//        //Update status of Document
//        Future<?> updateStatusOfDocument = executors.submit(new TaskV2(new Object[]{
//            document_.getId(),
//            user}, transactionId) {
//            @Override
//            public Object call() {
//                InternalResponse response = new InternalResponse();
//                try {
//                    response = UpdateDocument.updateStatusOfDocument(
//                            (long) this.get()[0],
//                            (User) this.get()[1],
//                            DocumentStatus.PROCESSING,
//                            transactionId);
//                } catch (Exception ex) {
//                    LogHandler.error(ConnectorDocument.class, transactionId, ex);
//                    response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
//                    response.setException(ex);
//                    response.setMessage(ResponseMessageController
//                            .errorMessageAdvanced(
//                                    "Error while calling in Future",
//                                    "Future 'upload Temporal Data' is made an exception"));
//                }
//                return response;
//            }
//        });
//
//        //<editor-fold defaultstate="collapsed" desc="Update value of the QRField if existed">
//        if (qrField != null) {
//            Future<?> uploadValueOfQR = executors.submit(new TaskV2(new Object[]{
//                qrField_ex,
//                qrField}, transactionId) {
//                @Override
//                public Object call() {
//                    ExtendedFieldAttribute qrField_ex = (ExtendedFieldAttribute) this.get()[0];
//                    QRFieldAttribute qrField = (QRFieldAttribute) this.get()[1];
//                    InternalResponse response = new InternalResponse();
//                    try {
//                        response
//                                = UpdateField.updateValueOfField(
//                                        qrField_ex.getDocumentFieldId(),
//                                        user,
//                                        MyServices.getJsonService().writeValueAsString(qrField),
//                                        transactionId);
//                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                            response.setUser(user);
//                            return response;
//                        }
//                    } catch (Exception ex) {
//                        LogHandler.error(ConnectorDocument.class, transactionId, ex);
//                        response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
//                        response.setException(ex);
//                        response.setMessage(ResponseMessageController
//                                .errorMessageAdvanced(
//                                        "Error while calling in Future",
//                                        "Future 'upload Temporal Data' is made an exception"));
//                    }
//                    return response;
//                }
//            });
//            if (((InternalResponse) uploadValueOfQR.get()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                return ((InternalResponse) uploadValueOfQR.get()).setUser(user);
//            }
//        }
//        executors.shutdown();
//        //</editor-fold>
//
//        InternalResponse response1 = (InternalResponse) uploadTemporalData.get();
//        response2 = (InternalResponse) updateStatusOfDocument.get();
//        if (response1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response1;
//        }
//        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//            return response2;
//        }
//        //</editor-fold>
//
//        return new InternalResponse(
//                A_FPSConstant.HTTP_CODE_SUCCESS,
//                new ResponseMessageController().writeStringField("hash_value", hash).build()
//        );
//    }
//    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Synchronized new DocumentID with UUID">
    public static InternalResponse synchronizedUUID(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Headers data and payload data">
        String fileName = Utils.getRequestHeader(request, "x-file-name");
        if (fileName == null) {
            fileName = "Document" + "_" + Utils.generateUUID();
        }
        String preserve_ = Utils.getRequestHeader(request, "x-preserve-document");
        boolean preserve = false;
        if (preserve_ != null) {
            preserve = Boolean.parseBoolean(preserve_);
        }
        System.out.println("Preserve:" + preserve);
        SyncForDokobit object = null;
        try {
            object = MyServices.getJsonService().readValue(payload, SyncForDokobit.class);
        } catch (Exception ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
            response.setException(ex).setUser(user);
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check input is satisfied">
        if (Utils.isNullOrEmpty(object.getUuid())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_MISSING_UUID
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download Document From FMS">
        response = FMS.downloadDocumentFromFMS(object.getUuid(), transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        byte[] data = (byte[]) response.getData();
        FileManagement file = DocumentUtils_itext7.analysisPDF_i7((byte[]) data);

        if (file == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_CANNOT_ANALYSIS_FILE
            ).setUser(user);
        }
        file.setName(fileName);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Child function => Create new Package to hover all data">
        if (object.getDocument_id() == 0) {
            response = child_CreateNew(user, fileName, file, preserve, data, object, transactionId);
            response.setUser(user);
            return response;
        } //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Child function => Apppend more data into Workflow">
        else {
            response = child_Append(user, fileName, file, preserve, data, object, transactionId);
            response.setUser(user);
            return response;
        }
        //</editor-fold>
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Pre check the Document">
    public static InternalResponse pre(
            HttpServletRequest request,
            String contentType,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get data from input">
        byte[] fileData = null;
        if (contentType.contains("application/json")) {
            try {
                fileData = Base64.getDecoder().decode(
                        Utils.getFromJson("file_data",
                                Utils.getPayload(request)));
            } catch (Exception ex) {
                response = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FAIL,
                        A_FPSConstant.SUBCODE_MISSING_FILE_DATA
                );
                response.setException(ex).setUser(user);
                return response;
            }
        } else if (contentType.contains("application/octet-stream")) {
            fileData = Utils.getBinaryStream(request);
        } else {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNSUPPORTED_MEDIA_TYPE,
                    ""
            ).setUser(user);
        }
        if (fileData == null || fileData.length == 0) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_MISSING_FILE_DATA
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Pre Check Document">
        response = PreCheckDocument.preCheckDocument(fileData);
        String message = "";
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            message = new ResponseMessageController()
                    .writeNumberField("document_status", 1)
                    .writeStringField("document_status_name", "This document is empty")
                    .build();
        } else {
            message = new ResponseMessageController()
                    .writeNumberField("document_status", 0)
                    .writeStringField("document_status_name", "This document exists an FormField")
                    .build();
        }
        //</editor-fold>

        response.setStatus(A_FPSConstant.HTTP_CODE_SUCCESS);
        response.setMessage(message);
        return response.setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fill Initials Version1">
    public static InternalResponse fillInitialField_V1(
            HttpServletRequest request,
            long packageId,
            String payload,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse Payload">
        InitialsFieldAttribute processRequest = null;
        try {
            processRequest = MyServices.getJsonService(
                    new ObjectMapper().addMixIn(InitialsFieldAttribute.class, InitialsFieldAttributeMixIn.class)
            )
                    .readValue(payload, InitialsFieldAttribute.class);
//            response = CheckPayloadRequest.checkAddInitialField(processRequest, transactionId);
//            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                response.setUser(user);
//                return response;
//            }

            response = CheckPayloadRequest.checkFillInitialField(processRequest, transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                response.setUser(user);
                return response;
            }
        } catch (JsonProcessingException ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
            response.setException(ex).setUser(user);
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Initial Form Field">
        response = ProcessingInitialFormField.genVersion(IVersion.Version.V1).processInitialField(
                packageId,
                user,
                processRequest,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        //</editor-fold>

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fill Initials Version2">
    public static InternalResponse fillInitialField_V2(
            HttpServletRequest request,
            long packageId,
            String payload,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse Payload">
        ProcessInitialField processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessInitialField.class);

            response = CheckPayloadRequest.checkFillInitialField(processRequest, transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                response.setUser(user);
                return response;
            }
        } catch (JsonProcessingException ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
            response.setException(ex).setUser(user);
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process Initial Form Field">
        response = ProcessingInitialFormField.genVersion(IVersion.Version.V3).processInitialField_V2(
                packageId,
                user,
                processRequest,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        //</editor-fold>

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fill File Field Version2">
    public static InternalResponse fillFileField_V2(
            HttpServletRequest request,
            long packageId,
            String payload,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse Payload">
        ProcessFileField processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessFileField.class);

            response = CheckPayloadRequest.checkFillField(processRequest, transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                response.setUser(user);
                return response;
            }
        } catch (JsonProcessingException ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
            response.setException(ex).setUser(user);
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process File Form Field">
        response = ProcessingFileField.processMultipleFileFormField(
                packageId,
                user,
                processRequest,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response.setUser(user);
            return response;
        }
        //</editor-fold>

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Recall Document">
    public static InternalResponse recallDocument(
            HttpServletRequest request,
            long packageId,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Documents">
        response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        List<Document> documents = (List<Document>) response.getData();
        Document document_ = null;
        for (Document document : documents) {
            if (document.getRevision() == documents.size()) {
                document_ = document;
            }
        }
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        //</editor-fold>

        return null;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fill QR Qrypto Field">
    public static InternalResponse createQRQrypto(
            HttpServletRequest request,
            long packageId,
            String payload,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse Payload">
        ProcessingRequest processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessingRequest.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            ).setException(ex).setUser(user);
        }

        if (Utils.isNullOrEmpty(processRequest.getFieldName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process QR Qrypto Field">
        if (!Utils.isNullOrEmpty(processRequest.getItem())) {
            //<editor-fold defaultstate="collapsed" desc="Flow 1 => Gen Qrypto from payload">
            response = ProcessingQRQryptoField.processQRQryptoField(
                    packageId,
                    processRequest.getFieldName(),
                    user,
                    processRequest.getItem(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response.setUser(user);
            }
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="Version 2 - Generate from old items in field">
            response = ProcessingQRQryptoField.processQRQryptoFieldV2(
                    packageId,
                    processRequest.getFieldName(),
                    user,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response.setUser(user);
            }
            //</editor-fold>
        }
        //</editor-fold>

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
        response.setUser(user);
        return response;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Hash of Signature Field">
    public static InternalResponse getHashOfSignatureField(
            HttpServletRequest request,
            String payload,
            long packageId,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check input and get header datas">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check detail in payload and parse into ProcessRequest Object">
        response = CheckPayloadRequest.checkHashRequest(payload, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        ProcessingRequest processRequest = (ProcessingRequest) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Documents">
        response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        List<Document> documents = (List<Document>) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Signature Field based on the name that in payload of the request">
        Document document_ = null;
        long documentIdOriginal = 0;
        for (Document document : documents) {
            if (document.getRevision() == 1) {
                response = ConnectorField_Internal.getField(
                        document.getId(),
                        processRequest.getFieldName(),
                        transactionId);
                documentIdOriginal = document.getId();
            }
            if (document.getRevision() == documents.size()) {
                document_ = document;
            }
        }
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check field is processed yet? and is type signature">
        if (!fieldData.getType().getParentType().equals(FieldTypeName.SIGNATURE.getParentName())
                && !fieldData.getType().getParentType().equals(FieldTypeName.INPERSON.getParentName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION
            ).setUser(user);
        }

        InternalResponse response_1 = CheckFieldProcessedYet.checkProcessed(fieldData.getFieldValue());
        if (response_1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_1.setUser(user);
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Check Hash of Signature field is existed in Temporal Table?">
        if (!Utils.isNullOrEmpty(fieldData.getHash())) {
            InternalResponse response2 = ManagementTemporal.getTemporal(
                    String.valueOf(document_.getId()),
                    fieldData.getHash(),
                    transactionId);
            if (response2.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                ManagementTemporal.removeTemporal(
                        String.valueOf(document_.getId()),
                        transactionId);
//                return new InternalResponse(
//                        A_FPSConstant.HTTP_CODE_SUCCESS,
//                        new ResponseMessageController().writeStringField("hash_value", fieldData.getHash()).build()
//                ).setUser(user);
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check page Sig is valid?">
        if (document_.getDocumentPages() < fieldData.getPage()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT
            ).setUser(user);
        }
        //</editor-fold>

        response = ProcessingSignatureField.processSignatureFieldV2(
                packageId,
                user,
                document_,
                documentIdOriginal,
                fieldData,
                processRequest,
                transactionId);

        response.setUser(user);
        return response;
    }
    // </editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Child function Synchonized - create New Document Id ">
    private static InternalResponse child_CreateNew(
            User user,
            String fileName,
            FileManagement file,
            boolean preserve,
            byte[] data,
            SyncForDokobit objects,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Create package to handle">
        InternalResponse response = UploadDocument.createPackage(
                fileName,
                user.getAid(),
                user.getAzp(),
                "hmac",
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        long packageId = (long) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Upload file to DB">
        response = UploadDocument.uploadDocument_v2(
                packageId,
                user.getIci(),
                1,
                file,
                DocumentStatus.UPLOADED,
                "none",
                "none",
                objects.getUuid(),
                "Uploaded to FMS",
                "hmac",
                user.getAzp(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        long documentId = (long) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Preserve Form Field">
        if (preserve) {
            InternalResponse child = PreserveFormField.preserve(documentId, user, data, transactionId);
            if (child.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return child;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add Field + Field Detail(if need)">
        if (objects.getFields() != null) {
            for (BasicFieldAttribute field : objects.getFields()) {
                try {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.SIGNATURE.getParentName()));
                    InternalResponse test
                            = AddField.addField(
                                    documentId,
                                    field,
                                    "hmac",
                                    user.getAzp(),
                                    transactionId);

                    if (test.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        test.setUser(user);
                        return test;
                    }

                    int fieldId = (int) test.getData();

                    test = AddField.addDetailField(
                            fieldId,
                            Resources.getFieldTypes()
                                    .get(FieldTypeName.SIGNATURE.getParentName()).getTypeId(),
                            field,
                            "hmac",
                            user.getAzp(),
                            transactionId);
                    if (test.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        test.setUser(user);
                        return test;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        //</editor-fold>

        response.setStatus(A_FPSConstant.HTTP_CODE_CREATED);
        response.setMessage(new ResponseMessageController()
                .writeNumberField("document_id", packageId)
                .build());
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Child function Synchronized - Append more data into Workflow">
    private static InternalResponse child_Append(
            User user,
            String fileName,
            FileManagement file,
            boolean preserve,
            byte[] data,
            SyncForDokobit objects,
            String transactionId) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Check how many document in that package">
        InternalResponse response = GetDocument.getDocuments(
                objects.getDocument_id(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        List<Document> listDoc = (List) response.getData();
        //</editor-fold> 

        //<editor-fold defaultstate="collapsed" desc="Upload file to DB">
        response = UploadDocument.uploadDocument_v2(
                objects.getDocument_id(),
                user.getIci(),
                listDoc == null ? 1 : listDoc.size() + 1,
                file,
                DocumentStatus.UPLOADED,
                "none",
                "none",
                objects.getUuid(),
                Configuration.getInstance().getUrlFMS(),
                "hmac",
                user.getAzp(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        long documentId = 0;
        for (Document document : listDoc) {
            if (document.getRevision() == 1) {
                documentId = document.getId();
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Preserve Form Field">
        if (preserve) {
            InternalResponse child = PreserveFormField.preserve(documentId, user, data, transactionId);
            if (child.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return child;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add Field + Field Detail(if need)">
        if (objects.getFields() != null) {
            for (SignatureFieldAttribute field : objects.getFields()) {
                try {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.SIGNATURE.getParentName()));
                    InternalResponse test = AddField.addField(
                            documentId,
                            field,
                            "hmac",
                            user.getAzp(),
                            transactionId);

                    if (test.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        continue;
                    }

                    int fieldId = (int) test.getData();

                    test = AddField.addDetailField(
                            fieldId,
                            Resources.getFieldTypes()
                                    .get(FieldTypeName.SIGNATURE.getParentName()).getTypeId(),
                            field,
                            "hmac",
                            user.getAzp(),
                            transactionId);
                    if (test.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        test.setUser(user);
                        return test;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        //</editor-fold>

        response.setStatus(A_FPSConstant.HTTP_CODE_CREATED);
        response.setMessage(new ResponseMessageController()
                .writeNumberField("document_id", objects.getDocument_id())
                .build());
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Child function fill form field">
    private static InternalResponse distributeFillFromField() {
        return null;
    }
    //</editor-fold>

}
