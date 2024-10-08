/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.AppearanceDesign;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.FieldTypeName;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext5;
import fps_core.module.DocumentUtils_itext7;
import fps_core.module.DocumentUtils_rssp_i5;
import fps_core.module.DocumentUtils_rssp_i7;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FileManagement;
import fps_core.objects.core.FileFieldAttribute;
import fps_core.objects.core.QRFieldAttribute;
import fps_core.objects.core.Signature;
import fps_core.objects.core.SignatureFieldAttribute;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FPS.controller.fms.FMS;
import static vn.mobileid.id.FPS.controller.document.summary.module.CheckStatusOfDocument.checkStatusOfDocument;
import vn.mobileid.id.FPS.controller.document.summary.micro.UploadDocument;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.controller.field.summary.micro.GetField;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.Crypto;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.utils.Utils;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IModuleProcessing;
import vn.mobileid.id.FPS.controller.field.summary.module.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/**
 *
 * @author GiaTK
 */
class SignatureProcessing implements IDocumentProcessing, IModuleProcessing {

    private static boolean stampQRCode = true; //If not, add QRCode into PDF with tranditional way

    //<editor-fold defaultstate="collapsed" desc="Process (Use for the flow append immediately)">
    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        //Convert data
        User user = null;
        boolean isVerify = false;
        Document document = null;
        int revision = 0;
        long documentFieldId = 0;
        SignatureFieldAttribute field = null;
        String transactionId = null;
        try {
            user = (User) objects[0];
            isVerify = (boolean) objects[1];
            document = (Document) objects[2];
            revision = (int) objects[3];
            documentFieldId = (long) objects[4];
            field = (SignatureFieldAttribute) objects[5];
            transactionId = (String) objects[6];
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }

        //Variable
        byte[] file = null;
        //Check status document
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }

        //Get Data
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(), transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();

        //Append data into field 
        try {
            //Analys file 
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);

            Future<?> analysis = executor.submit(new TaskV2(new Object[]{file}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });

            //Append signature
            byte[] signFile = DocumentUtils_rssp_i5.appendSignature(
                    file,
                    field,
                    transactionId);

            //Upload to FMS
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{signFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    byte[] signFile = (byte[]) this.get()[0];
                    String transactionId = this.getTransactionId();
                    try {
                        //Update new Document in FMS
                        return FMS.uploadToFMS(signFile, "pdf", transactionId);

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

            FileManagement fileManagement = (FileManagement) analysis.get();
            if (fileManagement == null) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_CANNOT_ANNALYSIS_THE_DOCUMENT
                );
            }
            fileManagement.setName(document.getName() + "(" + (revision + 1) + ")");
            fileManagement.setSize(signFile.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(signFile, fileManagement.getAlgorithm().getName())));

            response = (InternalResponse) uploadFMS.get();
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //Update new Document to DB  
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Signed signature Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            //Update field after processing
            ObjectMapper ob = new ObjectMapper();
            ob.setAnnotationIntrospector(new IgnoreIngeritedIntrospector());
            ob.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
            response = FieldSummaryInternal.updateValueOfField(
                    documentFieldId,
                    user,
                    MyServices.getJsonService(ob).writeValueAsString(field),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }

            //Get signature value and write into DB - Update Field Details
            String signatureValue = DocumentUtils_itext5.getSignatureValue_i5(signFile, field.getFieldName());
            SignatureFieldAttribute fieldNew = new SignatureFieldAttribute();
            Signature component = new Signature();
            component.setSignatureValue(signatureValue);
            fieldNew.setVerification(component);

            response = FieldSummaryInternal.updateFieldDetail(
                    documentFieldId,
                    user,
                    fieldNew,
                    uuid,
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                );
            }

            //Return data verify to client
            if (!isVerify) {

            }
            response = new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, document);
            response.setUser(user);
            return response;

        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append signature value into file\"}"
            ).setException(ex);
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Create Form Field"> 
    /**
     * Tạo Form Field và trả về dạng Objects
     *
     * @param objects: thứ tự truyền lần lượt là User, Document, revision,
     * SignatureFieldAttribute, transactionId
     * @return Obj[0]: binaryData của file đã tạo khung dạng byte[] Obj[1]:
     * chuỗi hash của file. Sử dụng để ký dạng String Obj[2]: temporalData dạng
     * byte[] User: thông tin của user
     * @InternalData: thông tin ExtendFieldAttribute loaij QR nếu tồn tại - Data
     * of the ExtendFieldAttribute type QR if that field is existed in
     * DocumentPackage
     *
     * History: Update change addImage of QRCode into stampV2 (2024-08-23)
     * @throws Exception
     */
    @Override
    public InternalResponse createFormField(Object... objects) throws Exception {
        //Convert data
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        long documentIdOriginal = (long) objects[2];
        SignatureFieldAttribute field = (SignatureFieldAttribute) objects[3];
        String appearance = (String) objects[4];
        String transactionId = (String) objects[5];
        byte[] file;

        //<editor-fold defaultstate="collapsed" desc="Check Status">
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }
        InternalResponse response = checkStatusOfDocument(document, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get data from FMS">
        response = FMS.downloadDocumentFromFMS(document.getUuid(), transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>

        try {
            InternalData internalData = null;
            //<editor-fold defaultstate="collapsed" desc="Create QR and Append into file first">
            response = GetField.getFieldsData(
                    documentIdOriginal,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            for (ExtendedFieldAttribute temp : (Iterable<? extends ExtendedFieldAttribute>) response.getData()) {
                InternalResponse response_1 = CheckFieldProcessedYet.checkProcessed(temp.getFieldValue());
                if (!response_1.isValid()) {
                    continue;
                }
                if (temp.getType().getParentType().equals(FieldTypeName.QR.getParentName())) {
                    QRFieldAttribute qr = MyServices.getJsonService().readValue(temp.getDetailValue(), QRFieldAttribute.class);
                    qr = (QRFieldAttribute) temp.clone(qr, temp.getDimension());

                    byte[] imageQR = null;
                    //<editor-fold defaultstate="collapsed" desc="Download Image from FMS">
                    if (qr.getImageQR().length() <= 32) {
                        response = FMS.downloadDocumentFromFMS(qr.getImageQR(), "");

                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            return new InternalResponse(
                                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                    0,
                                    0).setMessage(
                                    new ResponseMessageController().writeStringField(
                                            "error",
                                            "Cannot get Image in QRCode from FMS!").build()
                            );
                        }
                        imageQR = (byte[]) response.getData();
                    } else {
                        imageQR = Base64.getDecoder().decode(qr.getImageQR());
                    }
                    //</editor-fold>

                    try {
                        if (stampQRCode) {
                            FileFieldAttribute qrField = new FileFieldAttribute();
                            qrField.setFieldName(qr.getFieldName());
                            qrField.setDimension(qr.getDimension());
                            qrField.setRotate(qr.getRotate());
                            qrField.setPage(qr.getPage());
                            qrField.setFile(qr.getImageQR());
                            file = DocumentUtils_itext7.stampV2(file, qrField, transactionId);
                        } else {
                            file = DocumentUtils_itext7.addImage(
                                    file,
                                    imageQR,
                                    qr.getPage(),
                                    (float) qr.getDimension().getX(),
                                    (float) qr.getDimension().getY(),
                                    (float) qr.getDimension().getWidth(),
                                    (float) qr.getDimension().getWidth());
                        }

                        internalData = new InternalData();
                        internalData.setName("qr");
                        internalData.setValue(temp);
                    } catch (Exception ex) {
                        response = new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_QR,
                                A_FPSConstant.SUBCODE_CANNOT_GENERATE_QR
                        );
                        response.setException(ex);
                        return response;
                    }
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Create form Signature">
            Object[] objs = null;
            if (!Utils.isNullOrEmpty(appearance)) {
                objs = DocumentUtils_rssp_i7.createEsealFormSignatureV2(
                        field.getFieldName(),
                        file,
                        field,
                        PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat(),
                        AppearanceDesign.valueOf(appearance),
                        transactionId);
            } else {
                objs = DocumentUtils_rssp_i7.createFormSignature(
                        field.getFieldName(),
                        file,
                        field,
                        PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat(),
                        transactionId);
            }
            //</editor-fold>

            response = new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, objs);
            response.setUser(user);

            return response.setInternalData(internalData);

        } catch (Exception ex) {
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_SIGNATURE,
                    A_FPSConstant.SUBCODE_CANNOT_GENERATE_HASH_OF_THIS_SIGNATURE_FIELD
            );
            response.setException(ex);
            response.setUser(user);
            return response;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fill Form Field"> 
    /**
     * Fill dữ liệu (signature value) vào file Temporal trong DB
     *
     * @param objects
     * @return dữ liệu
     * @InternalData: thông tin ExtendFieldAttribute loaij QR nếu tồn tại - Data
     * of the ExtendFieldAttribute type QR if that field is existed in
     * DocumentPackage
     * @throws Exception
     */
    @Override
    public InternalResponse fillFormField(Object... objects) throws Exception {
        //Convert data
        User user = null;
        boolean skip_verification = false;
        Document document = null;
        int revision = 0;
        long documentFieldId = 0;
        SignatureFieldAttribute field = null;
        String transactionId = null;
        byte[] file = null;
        try {
            user = (User) objects[0];
            skip_verification = (boolean) objects[1];
            document = (Document) objects[2];
            revision = (int) objects[3];
            documentFieldId = (long) objects[4];
            field = (SignatureFieldAttribute) objects[5];
            transactionId = (String) objects[6];
            file = (byte[]) objects[7];
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }

        //Check status document
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }

        //Append data into field 
        try {
            //Analys file 
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);

            Future<?> analysis = executor.submit(new TaskV2(new Object[]{file}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });

            //Append signature
            byte[] signFile = DocumentUtils_rssp_i7.appendSignatureValue(
                    file,
                    field,
                    transactionId);

            //Upload to FMS
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{signFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    byte[] signFile = (byte[]) this.get()[0];
                    String transactionId = this.getTransactionId();
                    try {
                        //Update new Document in FMS
                        return FMS.uploadToFMS(signFile,
                                "pdf",
                                transactionId);

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

            FileManagement fileManagement = (FileManagement) analysis.get();
            if (fileManagement == null) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_CANNOT_ANNALYSIS_THE_DOCUMENT
                );
            }
            fileManagement.setName(document.getName() + "(" + (revision + 1) + ")");
            fileManagement.setSize(signFile.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(signFile, fileManagement.getAlgorithm().getName())));

            InternalResponse response = (InternalResponse) uploadFMS.get();
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //Update new Document to DB  
            response = UploadDocument.uploadDocument_v2(
                    document.getPackageId(),
                    0,
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Signed signature Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            //Return data verify to client
            if (!skip_verification) {
                List<Signature> signatures = DocumentUtils_itext7.verifyDocument_i7(signFile);
                for (Signature signature : signatures) {
                    if (signature.getFieldName().equals(field.getFieldName())
                            || (!Utils.isNullOrEmpty(field.getVerification().getSignatureId())
                            && field.getVerification().getSignatureId().equals(signature.getSignatureId()))) {
                        Signature temp = signature;
                        temp.setCertificateChain(field.getVerification().getCertificateChain());
                        temp.setSignatureValue(field.getVerification().getSignatureValue());
                        field.setVerification(temp);
                        break;
                    }
                }
            }

            //<editor-fold defaultstate="collapsed" desc="Get All fields and get signature + qr from that array">
            InternalData internalData = null;
            ExtendedFieldAttribute signatureField = null;
            response = GetField.getFieldsData(
                    document.getId(),
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            for (ExtendedFieldAttribute temp : (List<ExtendedFieldAttribute>) response.getData()) {
                if (temp.getType().getParentType().equals(FieldTypeName.QR.getParentName())) {
                    internalData = new InternalData();
                    internalData.setName("qr");
                    internalData.setValue(temp);
                }
                if (temp.getType().getParentType().equals(FieldTypeName.SIGNATURE.getParentName())) {
                    signatureField = temp;
                }
            }
            //</editor-fold>

            //Update field after processing
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());

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

            document.setUuid(uuid);
            response = new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, document);
            response.setUser(user);
            response.setInternalData(internalData);
            return response;

        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_SIGNATURE,
                    A_FPSConstant.SUBCODE_CANNOT_PROCESS_THIS_DOCUMENT
            ).setException(ex);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Delete Form Field"> 
    @Override
    public InternalResponse deleteFormField(Object... objects) throws Exception {
        //Convert data
        User user = (User) objects[0];
        byte[] file = (byte[]) objects[1];
        String fieldName = (String) objects[2];
        String transactionId = (String) objects[3];

        InternalResponse response = new InternalResponse();
        try {
            //Delete form
            byte[] objs = DocumentUtils_itext7.deleteFormField_i7(
                    file,
                    fieldName,
                    transactionId);

            response = new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, objs);
            response.setUser(user);
            return response;

        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot delete signature form\"}"
            ).setException(ex);
        }
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Replace Form Field">
    @Override
    public InternalResponse replaceFormField(Object... objects) throws Exception {
        return null;
    }
    //</editor-fold>

    //=====================INTERNAL METHOD======================================
}
