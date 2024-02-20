/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

import fps_core.objects.QRFieldAttribute;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.QryptoService.object.Configuration;
import vn.mobileid.id.FPS.QryptoService.object.FileDataDetails;
import vn.mobileid.id.FPS.QryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.QryptoService.object.QRSchema;
import vn.mobileid.id.FPS.QryptoService.process.CreateQRSchema;
import vn.mobileid.id.FPS.QryptoService.process.QryptoService;
import vn.mobileid.id.FPS.QryptoService.response.DownloadFileTokenResponse;
import vn.mobileid.id.FPS.QryptoService.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.exception.InvalidFormatOfItems;
import vn.mobileid.id.FPS.exception.LoginException;
import vn.mobileid.id.FPS.exception.QryptoException;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class QryptoProcessing implements DocumentProcessing {

    @Override
    public InternalResponse process(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        long documentFieldId = (long) objects[2];
        QRFieldAttribute field = (QRFieldAttribute) objects[3];
        List<ItemDetails> items = (List<ItemDetails>) objects[4];
        String transactionId = (String) objects[5];
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
        temp.setFile_field("FileStamping.pdf");

        ItemDetails file_ = new ItemDetails();
        file_.setField("FilePDF");
        file_.setType(5);
        file_.setFile_format("application/pdf");
        file_.setFile_name("PDFStamping.pdf");
        file_.setFile_field("FileStamping.pdf");
        file_.setValue("none");

        items.add(file_);

        QRSchema.QR_META_DATA positionQR = new QRSchema.QR_META_DATA();
        positionQR.setxCoordinator(Math.round(field.getDimension().getX()));
        positionQR.setyCoordinator(Math.round(field.getDimension().getY()));
        positionQR.setIsTransparent(field.IsTransparent());
        positionQR.setQrDimension(Math.round(field.getDimension().getWidth()));
        positionQR.setPageNumber(Arrays.asList(field.getPage()));

        Configuration config = CreateQRSchema.createConfiguration(field, user, transactionId);
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
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                filePDFFinal
        );
    }

}
