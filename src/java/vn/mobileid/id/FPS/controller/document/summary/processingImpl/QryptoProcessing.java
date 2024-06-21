/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.DocumentType;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.FileManagement;
import fps_core.objects.core.Signature;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.services.others.qryptoService.object.Configuration;
import vn.mobileid.id.FPS.services.others.qryptoService.object.FileDataDetails;
import vn.mobileid.id.FPS.services.others.qryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.services.others.qryptoService.object.Item_IDPicture4Label;
import vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType;
import static vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType.Binary;
import static vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType.File;
import static vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType.ID_Picture_with_4_labels;
import vn.mobileid.id.FPS.services.others.qryptoService.object.QRSchema;
import vn.mobileid.id.FPS.services.others.qryptoService.process.CreateQRSchema;
import vn.mobileid.id.FPS.services.others.qryptoService.process.QryptoService;
import vn.mobileid.id.FPS.services.others.qryptoService.response.DownloadFileTokenResponse;
import vn.mobileid.id.FPS.services.others.qryptoService.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.FPS.controller.document.summary.micro.UploadDocument;
import vn.mobileid.id.FPS.controller.field.summary.micro.UpdateField;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.exception.InvalidFormatOfItems;
import vn.mobileid.id.FPS.exception.LoginException;
import vn.mobileid.id.FPS.exception.QryptoException;
import vn.mobileid.id.FPS.object.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.utils.Utils;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummary;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.qryptoService.process.ReplaceSigningTime;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;

/**
 *
 * @author GiaTK
 * Core of the QryptoProcessing
 */
class QryptoProcessing extends IVersion implements IDocumentProcessing {

    public QryptoProcessing(Version version) {
        super(version);
    }

    public QryptoProcessing() {
        super(IVersion.Version.V1);
    }

    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2] + 1;
        QryptoFieldAttribute field = (QryptoFieldAttribute) objects[3];
        List<ItemDetails> items = (List<ItemDetails>) objects[4];
        long documentFieldId = (long) objects[5];
        String transactionId = (String) objects[6];
        byte[] file;

        ExecutorService executors = Executors.newFixedThreadPool(3);

        CompletionService<Object> taskCompletion = new ExecutorCompletionService<>(executors);
        InternalResponse errorResponse = null;

        //<editor-fold defaultstate="collapsed" desc="Check status of Document">
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download document from FMS">
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update 2024-05-27: Add the items in Fill API into QryptoFieldAttribute if items in Field is null">
        if (Utils.isNullOrEmpty(field.getItems())) {
            field.setItems(items);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update 2024-05-27: Add logic read Signature in PDF and replace SigningTime @FirstSigner and @SecondSigner">
        List<Signature> signatures = null;

        TaskV2 getSignature = new TaskV2(new Object[]{file}, transactionId) {
            @Override
            public List<Signature> call() {
                try {
                    List<Signature> signatures = DocumentUtils_itext7.verifyDocument_i7((byte[]) this.get()[0]);
                    return signatures;
                } catch (Exception e) {
                    return null;
                }
            }
        };
        try {
            signatures = MyServices.getThreadManagement(1).executeTask(getSignature);
        } catch (Exception ex) {
            LogHandler.error(QryptoProcessing.class,
                    transactionId,
                    "Cannot get List Signature in file PDF! Not replace QryptoAnnotation");
        }
        //</editor-fold>

        //Update 2024-06-20: Add logic createConfig + Schema based on IVersion
        //<editor-fold defaultstate="collapsed" desc="Create Config + Schema">
        FileDataDetails fileDataDetail = new FileDataDetails();
        fileDataDetail.setValue(file);
        fileDataDetail.setFile_field("fileprocessingservice");

        ItemDetails file_ = new ItemDetails();
        file_.setField("FilePDF");
        file_.setType(5);
        file_.setFile_format("application/pdf");
        file_.setFile_name("PDFStamping.pdf");
        file_.setFile_field("fileprocessingservice");
        file_.setValue("none");

        items.add(file_);

        QRSchema.QR_META_DATA positionQR = new QRSchema.QR_META_DATA();
        positionQR.setxCoordinator(Math.round(field.getDimension().getX()));
        positionQR.setyCoordinator(Math.round(field.getDimension().getY()));
        positionQR.setIsTransparent(field.IsTransparent());
        positionQR.setQrDimension(Math.round(field.getDimension().getWidth()));
        positionQR.setPageNumber(Arrays.asList(field.getPage()));

        //=> If version = V2 , do not append the QRMetadata into Schema
        CreateQRSchema createQRSchema = new CreateQRSchema(
                user,
                signatures,
                getVersion().equals(IVersion.Version.V1));

        Configuration config = createQRSchema.createConfiguration(
                field,
                user,
                items.size() * 120,
                transactionId);

        InternalResponse createSchema = createQRSchema(
                user,
                createQRSchema,
                fileDataDetail,
                items,
                positionQR,
                transactionId);

        if (!createSchema.isValid()) {
            return createSchema;
        }
        QRSchema schema = (QRSchema) createSchema.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Run first Thread to summary the field (make Image/File turn from Base64 into UUID FMS)">
        taskCompletion.submit(new TaskV2(
                new Object[]{
                    field
                },
                transactionId
        ) {
            @Override
            public Object call() {
                try {
                    if (!Utils.isNullOrEmpty(field.getItems())) {
                        for (ItemDetails detail : field.getItems()) {
                            String file = null;
                            Item_IDPicture4Label.IDPicture4Label tempp = null;
                            switch (ItemsType.getItemsType(detail.getType())) {
                                case Binary:
                                case File: {
                                    file = (String) detail.getValue();
                                    break;
                                }
                                case ID_Picture_with_4_labels: {
                                    String temp_ = MyServices.getJsonService().writeValueAsString(detail.getValue());
                                    tempp = MyServices.getJsonService().readValue(temp_, Item_IDPicture4Label.IDPicture4Label.class);
                                    file = tempp.getBase64();
                                    break;
                                }
                                default: {
                                }
                            }
                            if (file != null) {
                                //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
                                if (file.length()
                                        > PolicyConfiguration.getInstant()
                                                .getSystemConfig()
                                                .getAttributes()
                                                .get(0)
                                                .getMaximumFile()) {
                                    try {
                                        InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.uploadToFMS(
                                                org.bouncycastle.util.encoders.Base64.decode(file),
                                                "png",
                                                transactionId);
                                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                            String uuid = (String) response.getData();
                                            if (tempp != null) {
                                                tempp.setBase64(uuid);
                                                detail.setValue(tempp);
                                            } else {
                                                detail.setValue(uuid);
                                            }
                                        } else {
                                        }
                                    } catch (Exception ex) {
                                        System.err.println("Cannot upload image from QR to FMS!. Using default");
                                    }
                                }
                                //</editor-fold>
                            }
                        }
                    }
                    return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LogHandler.error(FieldSummary.class, transactionId, ex);
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_QR_Qrypto,
                            A_FPSConstant.SUBCODE_INVALID_TYPE_OF_ITEM
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Call Qrypto">
        IssueQryptoWithFileAttachResponse QRdata = null;
        try {
            try {
                QRdata = QryptoService
                        .getInstance(1)
                        .generateQR(
                                MyServices.getJsonService().writeValueAsString(schema),
                                schema.getHeader(),
                                schema.getFormat(),
                                config,
                                transactionId);
            } catch (LoginException ex) {
                QryptoService.getInstance(1).login();
                QRdata = QryptoService
                        .getInstance(1)
                        .generateQR(schema, config, "tran");
            }
        } catch (QryptoException ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ResponseMessageController.errorMessageAdvanced(
                            "Error while calling to Qrypto",
                            ex.getMessage())
            ).setUser(user);
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                    A_FPSConstant.SUBCODE_CANNOT_CREATE_QR
            ).setUser(user).setException(ex);
        } catch (Throwable ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                    A_FPSConstant.SUBCODE_CANNOT_CREATE_QR
            ).setUser(user);
        }
        if (QRdata == null || Utils.isNullOrEmpty(QRdata.getFileTokenList())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                    A_FPSConstant.SUBCODE_FILE_TOKEN_NOT_FOUND
            );
        }
        //</editor-fold>

        //Update 2024-06-21: Add Logic process PDF based on Version
        //<editor-fold defaultstate="collapsed" desc="Processing file PDF">
        InternalResponse generateFinalPDF = processingFilePDF(QRdata, file, field);
        if(!generateFinalPDF.isValid()){
            return generateFinalPDF;
        }
        byte[] filePDFFinal = (byte[]) generateFinalPDF.getData();
        //</editor-fold>

        //Run thread after process
        //<editor-fold defaultstate="collapsed" desc="Create new Document + upload to FMS">
        taskCompletion.submit(new TaskV2(
                new Object[]{
                    filePDFFinal,
                    document.getPackageId(),
                    revision,
                    user},
                transactionId
        ) {
            @Override
            public Object call() {
                byte[] finalPDF = (byte[]) this.get()[0];
                long documentPackage = (Long) this.get()[1];
                int revision = (Integer) this.get()[2];
                User user = (User) this.get()[3];
                try {
                    //<editor-fold defaultstate="collapsed" desc="Upload to FMS">
                    InternalResponse response = FMS.uploadToFMS(finalPDF, DocumentType.PDF.getType(), transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                    //</editor-fold>

                    String uuid = (String) response.getData();

                    //<editor-fold defaultstate="collapsed" desc="Analysis File">
                    FileManagement fileManagement = DocumentUtils_itext7.analysisPDF_i7(finalPDF);
                    //</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="Create new Document">
                    response = UploadDocument.uploadDocument(
                            documentPackage,
                            revision,
                            fileManagement,
                            DocumentStatus.UPLOADED,
                            "none",
                            "none",
                            uuid,
                            "",
                            "hmac",
                            user.getEmail(),
                            transactionId);

                    return response;
                    //</editor-fold>
                } catch (Exception ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            A_FPSConstant.CODE_DOCUMENT,
                            A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_CREATE_NEW_REVISION_OF_DOCUMENT
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        //Update 2024 - 05 - 27: Add logic replace QryptoAnntation
        //<editor-fold defaultstate="collapsed" desc="Update Image of QR and processMultipleField Status of Field">
        taskCompletion.submit(new TaskV2(
                new Object[]{
                    field,
                    QRdata.getQryptoBase64(),
                    documentFieldId,
                    QRdata.getQryptoBase45(),
                    signatures
                },
                transactionId
        ) {
            @Override
            public Object call() {
                try {
                    QryptoFieldAttribute field = (QryptoFieldAttribute) this.get()[0];

                    long documentFieldId = (long) this.get()[2];
                    List<Signature> signatures = (List<Signature>) this.get()[4];

                    field.setProcessStatus(ProcessStatus.PROCESSED.getName());
                    field.setProcessBy(user.getEmail());
                    field.setProcessOn(Utils.getTimestamp());

                    InternalResponse response = UpdateField.updateValueOfField(
                            documentFieldId,
                            user,
                            MyServices.getJsonService().writeValueAsString(field),
                            this.getTransactionId());

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_DOCUMENT,
                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                        );
                    }

                    String qryptoBase45 = (String) this.get()[3];
                    field.setQryptoBase45(qryptoBase45);

                    String imageQR = (String) this.get()[1];

                    //<editor-fold defaultstate="collapsed" desc="Upload to FMS">
                    response = FMS.uploadToFMS(imageQR.getBytes(), DocumentType.PNG.getType(), transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                    //</editor-fold>

                    String uuid = (String) response.getData();
                    field.setImageQR(uuid);

                    //<editor-fold defaultstate="collapsed" desc="Update 2024-05-27: Add logic read Signature in PDF and replace SigningTime @FirstSigner and @SecondSigner">
                    String json = MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    )
                            .writeValueAsString(field);
                    ReplaceSigningTime replaceSigningTime = new ReplaceSigningTime(
                            user, signatures
                    );
                    String temp = replaceSigningTime.replaceSigningTimeReturnString(json);
                    //</editor-fold>

                    response = FieldSummaryInternal.updateFieldDetail(
                            documentFieldId,
                            user,
                            temp,
                            "hmac",
                            transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_DOCUMENT,
                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                        );
                    }
                    return response;
                } catch (Exception ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            A_FPSConstant.CODE_DOCUMENT,
                            A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                    ).setException(ex);
                }
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Take from Completion service">
        for (int i = 0; i < 3; i++) {
            try {
                Future<Object> result = taskCompletion.take();
                InternalResponse resultDetail = (InternalResponse) result.get();
                if (!resultDetail.isValid()) {
                    executors.shutdownNow();
                    errorResponse = resultDetail;
                }
            } catch (Exception ex) {
                errorResponse = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                        A_FPSConstant.CODE_FIELD_INITIAL,
                        A_FPSConstant.SUBCODE_ERROR_WHILE_PROCESSING_MULTI_THREAD
                ).setException(ex);
            }
        }
        //</editor-fold>

        if (errorResponse != null) {
            return errorResponse;
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    //==========================INTERNAL METHOD=================================
    //<editor-fold defaultstate="collapsed" desc="Create QRSchema">
    /**
     * Create QRSchema to call Qrypto
     *
     * @param user
     * @param createQRSchema
     * @param fileDataDetail
     * @param items
     * @param positionQR
     * @param transactionId
     * @return InternalResponse with QRSchema as an Object
     */
    private InternalResponse createQRSchema(
            User user,
            CreateQRSchema createQRSchema,
            FileDataDetails fileDataDetail,
            List<ItemDetails> items,
            QRSchema.QR_META_DATA positionQR,
            String transactionId
    ) {

        QRSchema schema = null;
        try {
            schema = createQRSchema.createQRSchema(
                    Arrays.asList(fileDataDetail),
                    items,
                    positionQR,
                    transactionId);
            return new InternalResponse().setData(schema);
        } catch (InvalidFormatOfItems ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                    A_FPSConstant.SUBCODE_INVALID_FORMAT_OF_ITEM
            ).setUser(user);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Processing file PDF based on Version">
    /**
     * Process Qrypto based on Version
     * @param QRdata
     * @param filePDF
     * @param field
     * @return InternalResponse with byte[] as an Object
     */
    private InternalResponse processingFilePDF(
            IssueQryptoWithFileAttachResponse QRdata,
            byte[] filePDF,
            QryptoFieldAttribute field
    ) {
        try {
            switch (getVersion()) {
                case V1: {
                    //<editor-fold defaultstate="collapsed" desc="Download PDF from Qrypto">
                    byte[] filePDFFinal = null;
                    try {
                        DownloadFileTokenResponse qryptoResponse = QryptoService.getInstance(1).downloadFileToken(
                                QRdata.getFileTokenList().get(QRdata.getFileTokenList().size() - 1));
                        if (qryptoResponse.getContent().isEmpty()) {
                            return new InternalResponse(
                                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                                    A_FPSConstant.SUBCODE_CANNOT_DOWNLOAD_FILE_FROM_QRYPTO
                            );
                        }
                        filePDFFinal = Base64.getDecoder().decode(qryptoResponse.getContent());
                        return new InternalResponse().setData(filePDFFinal);
                    } catch (Exception ex) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                A_FPSConstant.CODE_FIELD_QR_Qrypto,
                                A_FPSConstant.SUBCODE_CANNOT_DOWNLOAD_FILE_FROM_QRYPTO
                        ).setException(ex);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                A_FPSConstant.CODE_FIELD_QR_Qrypto,
                                A_FPSConstant.SUBCODE_CANNOT_DOWNLOAD_FILE_FROM_QRYPTO
                        );
                    }
                    //</editor-fold>
                }
                case V2: {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    try {
                        byte[] imageQR = Base64.getDecoder().decode(QRdata.getQryptoBase64());
                        byte[] finalFilePDF = DocumentUtils_itext7.sign(
                                filePDF,
                                imageQR,
                                field);
                        return new InternalResponse().setData(finalFilePDF);
                    } catch (Exception ex) {
                        return CreateInternalResponse.createErrorInternalResponse(
                                A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                A_FPSConstant.CODE_FIELD_QR_Qrypto,
                                A_FPSConstant.SUBCODE_CANNOT_SIGN_QRYPTO);
                    }
                    //</editor-fold>
                }
                default:
                    throw new AssertionError();
            }
        } catch (Exception e) {
        }
        return CreateInternalResponse.createErrorInternalResponse(
                A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                A_FPSConstant.CODE_FIELD_QR_Qrypto,
                A_FPSConstant.SUBCODE_CANNOT_SIGN_QRYPTO);
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
        Signature sig = new Signature();
        sig.setSigningTime(new Date(System.currentTimeMillis()));

    }
}
