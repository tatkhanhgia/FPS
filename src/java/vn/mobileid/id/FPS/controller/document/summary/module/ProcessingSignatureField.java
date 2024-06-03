/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import vn.mobileid.id.FPS.controller.document.summary.micro.UpdateDocument;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.FieldTypeName;
import fps_core.enumration.ProcessStatus;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.core.QRFieldAttribute;
import fps_core.objects.core.Signature;
import fps_core.objects.core.SignatureFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Future;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.ProcessingFactory;
import vn.mobileid.id.FPS.controller.field.summary.module.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.controller.field.summary.micro.UpdateField;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.controller.document.summary.DocumentSummary;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.TemporalObject;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.ManagementTemporal;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingSignatureField {

    //<editor-fold defaultstate="collapsed" desc="Processing Signature Form Field">
    /**
     * Processing Signature in Payload
     *
     * @param packageId
     * @param user
     * @param document_
     * @param documentIdOriginal
     * @param fieldData
     * @param transactionId
     * @param processRequest
     * @return InternalResponse
     * @throws Exception
     */
    public static InternalResponse processSignatureField(
            long packageId,
            User user,
            Document document_,
            long documentIdOriginal,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest processRequest,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Check field is processed yet? and is type signature">
        if (!fieldData.getType().getParentType().equals(FieldTypeName.SIGNATURE.getParentName())) {
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

        //<editor-fold defaultstate="collapsed" desc="Mapping into SignatureFieldAttribute">
        SignatureFieldAttribute signatureField = convertExtendIntoSignatureField(user, fieldData, processRequest);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Processing - Create Form Field">
        InternalResponse response = new ProcessingFactory().createType_Module(ProcessingFactory.TypeProcess.SIGNATURE).createFormField(new Object[]{
            user,
            document_,
            documentIdOriginal,
            signatureField,
            signatureField.getLevelOfAssurance() != null ? signatureField.getLevelOfAssurance().contains("ESEAL") : false,
            transactionId
        });
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        ExtendedFieldAttribute qrField_ex = null;
        QRFieldAttribute qrField = null;
        if (response.getInternalData() != null) {
            qrField_ex = (ExtendedFieldAttribute) response.getInternalData().getValue();
            qrField = MyServices.getJsonService().readValue(qrField_ex.getDetailValue(), QRFieldAttribute.class);
            qrField = (QRFieldAttribute) qrField_ex.clone(qrField, qrField_ex.getDimension());
            qrField.setProcessStatus(ProcessStatus.PROCESSED.getName());
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update hash of the Field">
        String hash = (String) ((Object[]) response.getData())[1];

        InternalResponse response2
                = UpdateField.updateHashOfField(
                        fieldData.getDocumentFieldId(),
                        user,
                        hash,
                        transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response2.setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Set Signature name into SignatureFieldAttribute">
        signatureField.getVerification().setSignatureId((String) ((Object[]) response.getData())[3]);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update field details (Value in DB will relative to the input of client)">
        signatureField.setProcessStatus(ProcessStatus.IN_PROCESS.getName());
        response2
                = UpdateField.updateValueOfField(
                        fieldData.getDocumentFieldId(),
                        user,
                        MyServices.getJsonService().writeValueAsString(signatureField),
                        transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response2.setUser(user);
            return response2;
        }

        response2
                = UpdateField.updateFieldDetails(
                        fieldData.getDocumentFieldId(),
                        user,
                        signatureField,
                        "hmac",
                        transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response2.setUser(user);
            return response2;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Thread Pool to handle 3 thread. Upload TemporaData ; Update Status of Document and update Value of QR Field if existed">
//        ExecutorService executors = Executors.newFixedThreadPool(3);
        ThreadManagement threadPool = MyServices.getThreadManagement(3);

        //Upload temporal data
        Future<?> uploadTemporalData = threadPool.submit(new TaskV2(new Object[]{
            (byte[]) ((Object[]) response.getData())[2],
            hash,
            document_.getId()}, transactionId) {
            @Override
            public Object call() {
                InternalResponse response = new InternalResponse();
                try {
                    byte[] temporal = (byte[]) this.get()[0];
                    response = ManagementTemporal.addTemporal(
                            String.valueOf(this.get()[2]),
                            (String) this.get()[1],
                            TemporalObject.Type.TEMPORAL_DATA.getId(),
                            temporal,
                            transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        response.setUser(user);
                        return response;
                    }
                } catch (Exception ex) {
                    LogHandler.error(DocumentSummary.class, transactionId, ex);
                    response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                    response.setException(ex);
                    response.setMessage(ResponseMessageController
                            .errorMessageAdvanced(
                                    "Error while calling in Future",
                                    "Future 'upload Temporal Data' is made an exception"));
                }
                return response;
            }
        });

        //Update status of Document
        Future<?> updateStatusOfDocument = threadPool.submit(new TaskV2(new Object[]{
            document_.getId(),
            user}, transactionId) {
            @Override
            public Object call() {
                InternalResponse response = new InternalResponse();
                try {
                    response = UpdateDocument.updateStatusOfDocument(
                            (long) this.get()[0],
                            (User) this.get()[1],
                            DocumentStatus.PROCESSING,
                            transactionId);
                } catch (Exception ex) {
                    LogHandler.error(DocumentSummary.class, transactionId, ex);
                    response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                    response.setException(ex);
                    response.setMessage(ResponseMessageController
                            .errorMessageAdvanced(
                                    "Error while calling in Future",
                                    "Future 'upload Temporal Data' is made an exception"));
                }
                return response;
            }
        });

        //<editor-fold defaultstate="collapsed" desc="Update value of the QRField if existed">
        if (qrField != null) {
            Future<?> uploadValueOfQR = threadPool.submit(new TaskV2(new Object[]{
                qrField_ex,
                qrField}, transactionId) {
                @Override
                public Object call() {
                    ExtendedFieldAttribute qrField_ex = (ExtendedFieldAttribute) this.get()[0];
                    QRFieldAttribute qrField = (QRFieldAttribute) this.get()[1];
                    InternalResponse response = new InternalResponse();
                    try {
                        response
                                = UpdateField.updateValueOfField(
                                        qrField_ex.getDocumentFieldId(),
                                        user,
                                        MyServices.getJsonService().writeValueAsString(qrField),
                                        transactionId);
                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            response.setUser(user);
                            return response;
                        }
                    } catch (Exception ex) {
                        LogHandler.error(DocumentSummary.class, transactionId, ex);
                        response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                        response.setException(ex);
                        response.setMessage(ResponseMessageController
                                .errorMessageAdvanced(
                                        "Error while calling in Future",
                                        "Future 'upload Temporal Data' is made an exception"));
                    }
                    return response;
                }
            });
            if (((InternalResponse) uploadValueOfQR.get()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return ((InternalResponse) uploadValueOfQR.get()).setUser(user);
            }
        }
        threadPool.shutdown();
        //</editor-fold>

        InternalResponse response1 = (InternalResponse) uploadTemporalData.get();
        response2 = (InternalResponse) updateStatusOfDocument.get();
        if (response1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response1;
        }
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response2;
        }
        //</editor-fold>
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController().writeStringField("hash_value", hash).build()
        );
    }
    //</editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="Processing Signature Form Field Version 2">
    /**
     * Processing Signature in Payload Vesion2 - Delete some checker that doesn't need it
     *
     * @param packageId
     * @param user
     * @param document_
     * @param documentIdOriginal
     * @param fieldData
     * @param transactionId
     * @param processRequest
     * @return InternalResponse
     * @throws Exception
     */
    public static InternalResponse processSignatureFieldV2(
            long packageId,
            User user,
            Document document_,
            long documentIdOriginal,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest processRequest,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Mapping into SignatureFieldAttribute">
        SignatureFieldAttribute signatureField = convertExtendIntoSignatureField(user, fieldData, processRequest);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Processing - Create Form Field">
        InternalResponse response = new ProcessingFactory().createType_Module(ProcessingFactory.TypeProcess.SIGNATURE).createFormField(new Object[]{
            user,
            document_,
            documentIdOriginal,
            signatureField,
            signatureField.getLevelOfAssurance() != null ? signatureField.getLevelOfAssurance().contains("ESEAL") : false,
            transactionId
        });
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        ExtendedFieldAttribute qrField_ex = null;
        QRFieldAttribute qrField = null;
        if (response.getInternalData() != null) {
            qrField_ex = (ExtendedFieldAttribute) response.getInternalData().getValue();
            qrField = MyServices.getJsonService().readValue(qrField_ex.getDetailValue(), QRFieldAttribute.class);
            qrField = (QRFieldAttribute) qrField_ex.clone(qrField, qrField_ex.getDimension());
            qrField.setProcessStatus(ProcessStatus.PROCESSED.getName());
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update hash of the Field">
        String hash = (String) ((Object[]) response.getData())[1];

        InternalResponse response2
                = UpdateField.updateHashOfField(
                        fieldData.getDocumentFieldId(),
                        user,
                        hash,
                        transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response2.setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Set Signature name into SignatureFieldAttribute">
        signatureField.getVerification().setSignatureId((String) ((Object[]) response.getData())[3]);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update field details (Value in DB will relative to the input of client)">
        signatureField.setProcessStatus(ProcessStatus.IN_PROCESS.getName());
        response2
                = UpdateField.updateValueOfField(
                        fieldData.getDocumentFieldId(),
                        user,
                        MyServices.getJsonService().writeValueAsString(signatureField),
                        transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response2.setUser(user);
            return response2;
        }

        response2
                = UpdateField.updateFieldDetails(
                        fieldData.getDocumentFieldId(),
                        user,
                        signatureField,
                        "hmac",
                        transactionId);
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            response2.setUser(user);
            return response2;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Thread Pool to handle 3 thread. Upload TemporaData ; Update Status of Document and update Value of QR Field if existed">
//        ExecutorService executors = Executors.newFixedThreadPool(3);
        ThreadManagement threadPool = MyServices.getThreadManagement(3);
        //Upload temporal data
        Future<?> uploadTemporalData = threadPool.submit(new TaskV2(new Object[]{
            (byte[]) ((Object[]) response.getData())[2],
            hash,
            document_.getId()}, transactionId) {
            @Override
            public Object call() {
                InternalResponse response = new InternalResponse();
                try {
                    byte[] temporal = (byte[]) this.get()[0];
                    response = ManagementTemporal.addTemporal(
                            String.valueOf(this.get()[2]),
                            (String) this.get()[1],
                            TemporalObject.Type.TEMPORAL_DATA.getId(),
                            temporal,
                            transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        response.setUser(user);
                        return response;
                    }
                } catch (Exception ex) {
                    LogHandler.error(DocumentSummary.class, transactionId, ex);
                    response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                    response.setException(ex);
                    response.setMessage(ResponseMessageController
                            .errorMessageAdvanced(
                                    "Error while calling in Future",
                                    "Future 'upload Temporal Data' is made an exception"));
                }
                return response;
            }
        });

        //Update status of Document
        Future<?> updateStatusOfDocument = threadPool.submit(new TaskV2(new Object[]{
            document_.getId(),
            user}, transactionId) {
            @Override
            public Object call() {
                InternalResponse response = new InternalResponse();
                try {
                    response = UpdateDocument.updateStatusOfDocument(
                            (long) this.get()[0],
                            (User) this.get()[1],
                            DocumentStatus.PROCESSING,
                            transactionId);
                } catch (Exception ex) {
                    LogHandler.error(DocumentSummary.class, transactionId, ex);
                    response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                    response.setException(ex);
                    response.setMessage(ResponseMessageController
                            .errorMessageAdvanced(
                                    "Error while calling in Future",
                                    "Future 'upload Temporal Data' is made an exception"));
                }
                return response;
            }
        });

        //<editor-fold defaultstate="collapsed" desc="Update value of the QRField if existed">
        if (qrField != null) {
            Future<?> uploadValueOfQR = threadPool.submit(new TaskV2(new Object[]{
                qrField_ex,
                qrField}, transactionId) {
                @Override
                public Object call() {
                    ExtendedFieldAttribute qrField_ex = (ExtendedFieldAttribute) this.get()[0];
                    QRFieldAttribute qrField = (QRFieldAttribute) this.get()[1];
                    InternalResponse response = new InternalResponse();
                    try {
                        response
                                = UpdateField.updateValueOfField(
                                        qrField_ex.getDocumentFieldId(),
                                        user,
                                        MyServices.getJsonService().writeValueAsString(qrField),
                                        transactionId);
                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            response.setUser(user);
                            return response;
                        }
                    } catch (Exception ex) {
                        LogHandler.error(DocumentSummary.class, transactionId, ex);
                        response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
                        response.setException(ex);
                        response.setMessage(ResponseMessageController
                                .errorMessageAdvanced(
                                        "Error while calling in Future",
                                        "Future 'upload Temporal Data' is made an exception"));
                    }
                    return response;
                }
            });
            if (((InternalResponse) uploadValueOfQR.get()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return ((InternalResponse) uploadValueOfQR.get()).setUser(user);
            }
        }
        threadPool.shutdown();
        //</editor-fold>

        InternalResponse response1 = (InternalResponse) uploadTemporalData.get();
        response2 = (InternalResponse) updateStatusOfDocument.get();
        if (response1.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response1;
        }
        if (response2.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response2;
        }
        //</editor-fold>
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController().writeStringField("hash_value", hash).build()
        );
    }
    //</editor-fold>
    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into SignatureField">
    private static SignatureFieldAttribute convertExtendIntoSignatureField(
            User user,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest processRequest) throws Exception {
        //Read details
        SignatureFieldAttribute signatureField = new SignatureFieldAttribute();
        signatureField = MyServices.getJsonService().readValue(fieldData.getDetailValue(), SignatureFieldAttribute.class);
        signatureField = (SignatureFieldAttribute) fieldData.clone(signatureField, fieldData.getDimension());

        signatureField.setHandSignatureImage(processRequest.getHandSignatureImage());
        signatureField.setLevelOfAssurance(fieldData.getLevelOfAssurance());

        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        signatureField.setProcessOn(dateFormat.format(Date.from(Instant.now())));
        signatureField.setProcessBy(user.getAzp());

        if(signatureField.getVerification() == null){
            signatureField.setVerification(new Signature());
        }
        processRequest.convert(signatureField);
        
        if (!Utils.isNullOrEmpty(signatureField.getHandSignatureImage())) {
            signatureField.getVerification().setImageEnabled(true);
        }

        return signatureField;
    }
    //</editor-fold>
}
