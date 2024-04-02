/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.DocumentType;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.FileManagement;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.QryptoService.object.FileDataDetails;
import vn.mobileid.id.FPS.QryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.QryptoService.process.CreateQRSchema;
import vn.mobileid.id.FPS.QryptoService.process.QryptoService;
import vn.mobileid.id.FPS.QryptoService.response.DownloadFileTokenResponse;
import vn.mobileid.id.FPS.QryptoService.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.FPS.component.document.UploadDocument;
import vn.mobileid.id.FPS.component.field.UpdateField;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.exception.InvalidFormatOfItems;
import vn.mobileid.id.FPS.exception.LoginException;
import vn.mobileid.id.FPS.exception.QryptoException;
import vn.mobileid.id.FPS.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.utils.TaskV2;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.FPS.component.document.process.interfaces.IDocumentProcessing;

/**
 *
 * @author GiaTK
 */
class QryptoProcessing implements IDocumentProcessing {

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

        //<editor-fold defaultstate="collapsed" desc="Create Config + Schema">
        FileDataDetails temp = new FileDataDetails();
        temp.setValue(file);
        temp.setFile_field("fileprocessingservice");

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

        Configuration config = CreateQRSchema.createConfiguration(
                field,
                user,
                items.size()*120,
                transactionId);
        QRSchema schema = null;
        try {
            schema = CreateQRSchema.createQRSchema(
                    Arrays.asList(temp),
                    items,
                    positionQR,
                    transactionId);
        } catch (InvalidFormatOfItems ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                    A_FPSConstant.SUBCODE_INVALID_FORMAT_OF_ITEM
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Call Qrypto">
        IssueQryptoWithFileAttachResponse QRdata = null;
        try {
            try {
                QRdata = QryptoService
                        .getInstance(1)
                        .generateQR(schema, config, "tran");
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

        ExecutorService executors = Executors.newFixedThreadPool(2);
        CompletionService<Object> taskCompletion = new ExecutorCompletionService<>(executors);
        InternalResponse errorResponse = null;
        
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

        //<editor-fold defaultstate="collapsed" desc="Update Image of QR and processMultipleField Status of Field">
        taskCompletion.submit(new TaskV2(
                new Object[]{
                    field,
                    QRdata.getQryptoBase64(),
                    documentFieldId
                },
                transactionId
        ) {
            @Override
            public Object call() {
                try {
                    QryptoFieldAttribute field = (QryptoFieldAttribute) this.get()[0];
                    String imageQR = (String)this.get()[1];
                    long documentFieldId = (long)this.get()[2];
                    field.setImageQR(imageQR);
                    field.setProcessStatus(ProcessStatus.PROCESSED.getName());
                    field.setProcessBy(user.getEmail());
                    field.setProcessOn(Utils.getTimestamp());
                    
                    return UpdateField.updateValueOfField(
                            documentFieldId,
                            user,
                            new ObjectMapper().writeValueAsString(field),
                            this.getTransactionId());
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
        for(int i=0; i<2; i++){
            try{
                Future<Object> result = taskCompletion.take();
                InternalResponse resultDetail = (InternalResponse) result.get();
                if(resultDetail.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
                    executors.shutdownNow();
                    errorResponse = resultDetail;
                }
            }catch(Exception ex){
                errorResponse = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                        A_FPSConstant.CODE_FIELD_INITIAL,
                        A_FPSConstant.SUBCODE_ERROR_WHILE_PROCESSING_MULTI_THREAD
                ).setException(ex);
            }
        }
        //</editor-fold>
        
        if(errorResponse != null){
            return errorResponse;
        }
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

}
