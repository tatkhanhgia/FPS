/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.QryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import java.util.Base64;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.QryptoService.object.Item_IDPicture4Label.IDPicture4Label;
import vn.mobileid.id.FPS.QryptoService.object.ItemsType;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 * Using for processing the QR Qrypto Field:
 * + Checked value is satisfied
 * + Get Document (original, lastest)
 * + Get ExtendFieldAttribute and parse it into QryptoFieldAttribute
 * + Call submethod QryptoProcessing
 */
public class ProcessingQRQryptoField {

    //<editor-fold defaultstate="collapsed" desc="Processing QR Qrypto Form Field">
    /**
     * Processing QR Qrypto Form Field in Payload
     *
     * @param packageId
     * @param fieldName
     * @param user
     * @param processRequest
     * @param transactionId
     * @return InternalResponse If the InternalResponse.getStatus() !=
     * HTTP.Success => That InternalResponse have an InternalData satisfied
     * format InternalData(null,List<InternalData>) - All fields that have an
     * error while processed
     * @throws Exception
     */
    public static InternalResponse processQRQryptoField(
            long packageId,
            String fieldName,
            User user,
            List<ItemDetails> processRequest,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Get Documents">
        InternalResponse response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        List<Document> documents = (List<Document>) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get all data of the field">
        Document document_ = null;
        response = new InternalResponse();
        for (int i = documents.size() - 1; i >= 0; i--) {
            if (documents.get(i).getRevision() == 1) {
                response = ConnectorField_Internal.getField(
                        documents.get(i).getId(),
                        fieldName,
                        transactionId);
            }
            if (documents.get(i).getRevision() == documents.size()) {
                document_ = documents.get(i);
            }
        }

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check data in ExtendedField is sastified">
        InternalResponse response_ = CheckFieldProcessedYet.checkProcessed(fieldData.getFieldValue());
        if (response_.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        if (!fieldData.getType().getParentType().equals(FieldTypeName.QRYPTO.getParentName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into QRField">
        QryptoFieldAttribute QRField = null;
        try {
            InternalResponse res = convertExtendIntoQryptoField(
                    user,
                    fieldData,
                    true
            );

            if (!res.isValid()) {
                return res;
            }

            QRField = (QryptoFieldAttribute) res.getData();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //Processing
        response = new ProcessingFactory().createType(ProcessingFactory.TypeProcess.QRYPTO).processField(
                user,
                document_,
                documents.size(),
                QRField,
                processRequest,
                fieldData.getDocumentFieldId(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );

        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Processing QR Qrypto Form Field based on old items in Field">
    /**
     * Processing QR Qrypto Form Field in Payload
     *
     * @param packageId
     * @param fieldName
     * @param user
     * @param transactionId
     * @return InternalResponse If the InternalResponse.getStatus() !=
     * HTTP.Success => That InternalResponse have an InternalData satisfied
     * format InternalData(null,List<InternalData>) - All fields that have an
     * error while processed
     * @throws Exception
     */
    public static InternalResponse processQRQryptoFieldV2(
            long packageId,
            String fieldName,
            User user,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Get Documents">
        InternalResponse response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        List<Document> documents = (List<Document>) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get all data of the field">
        Document document_ = null;
        response = new InternalResponse();
        for (int i = documents.size() - 1; i >= 0; i--) {
            if (documents.get(i).getRevision() == 1) {
                response = ConnectorField_Internal.getField(
                        documents.get(i).getId(),
                        fieldName,
                        transactionId);
            }
            if (documents.get(i).getRevision() == documents.size()) {
                document_ = documents.get(i);
            }
        }

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check data in ExtendedField is sastified">
        InternalResponse response_ = CheckFieldProcessedYet.checkProcessed(fieldData.getFieldValue());
        if (response_.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_;
        }

        if (!fieldData.getType().getParentType().equals(FieldTypeName.QRYPTO.getParentName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into QRField">
        QryptoFieldAttribute QRField = null;
        try {
            InternalResponse res = convertExtendIntoQryptoField(
                    user,
                    fieldData,
                    false
            );

            if (res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return res;
            }

            QRField = (QryptoFieldAttribute) res.getData();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check page Sig is valid?">
        if (document_.getDocumentPages() < fieldData.getPage()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT
            );
        }
        //</editor-fold>

        //Processing
        response = new ProcessingFactory().createType(ProcessingFactory.TypeProcess.QRYPTO).processField(
                user,
                document_,
                documents.size(),
                QRField,
                QRField.getItems(),
                fieldData.getDocumentFieldId(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );

        return response;
    }

    //</editor-fold>
    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into Qrypto Field">
    /**
     * Converts an ExtendedFieldAttribute into a QryptoFieldAttribute.
     * <p>
     * Chuyển đổi một ExtendedFieldAttribute thành QryptoFieldAttribute.
     *
     * @param user The user performing the conversion.
     * Người dùng thực hiện chuyển đổi.
     * @param fieldData The ExtendedFieldAttribute to convert.
     * ExtendedFieldAttribute cần chuyển đổi.
     * @return An InternalResponse containing the converted QryptoFieldAttribute on success, or an error response otherwise.
     * Một InternalResponse chứa QryptoFieldAttribute đã chuyển đổi nếu thành công, hoặc một phản hồi lỗi nếu không.
     * @throws Exception If an error occurs during conversion.
     * Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    private static InternalResponse convertExtendIntoQryptoField(
            User user,
            ExtendedFieldAttribute fieldData,
            boolean ignoreMissingItem) throws Exception {
        //Read details
        QryptoFieldAttribute QRField = MyServices.getJsonService().readValue(fieldData.getDetailValue(), QryptoFieldAttribute.class);
        QRField = (QryptoFieldAttribute) fieldData.clone(QRField, fieldData.getDimension());

        //Read Basic
        QRField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
        QRField.setProcessOn(dateFormat.format(Date.from(Instant.now())));
        QRField.setPage(fieldData.getPage());

        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS">
        if (!Utils.isNullOrEmpty(QRField.getItems())) {
            for (ItemDetails item : QRField.getItems()) {
                if (item.getType() == ItemsType.ID_Picture_with_4_labels.getId()) {
                    String temp_ = MyServices.getJsonService().writeValueAsString(item.getValue());
                    IDPicture4Label tempp = MyServices.getJsonService().readValue(temp_, IDPicture4Label.class);
                    if (tempp != null
                            //                            && tempp.getIdPicture() != null
                            && tempp.getBase64() != null
                            && tempp.getBase64().length() <= 32) {
                        try {
                            InternalResponse response = FMS.downloadDocumentFromFMS(tempp.getBase64(), "");

                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                return new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                        A_FPSConstant.CODE_FMS,
                                        A_FPSConstant.SUBCODE_ERROR_WHILE_DOWNLOAD_FMS);
                            }

                            byte[] image_ = (byte[]) response.getData();
                            tempp.setBase64(Base64.getEncoder().encodeToString(image_));
                            item.setValue(tempp);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return new InternalResponse(
                                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                    A_FPSConstant.CODE_FMS,
                                    A_FPSConstant.SUBCODE_ERROR_WHILE_DOWNLOAD_FMS);
                        }

                    }
                }
                if (item.getType() == ItemsType.Binary.getId() || item.getType() == ItemsType.File.getId()) {
                    try {
                        if (((String) item.getValue()).length() <= 32) {
                            InternalResponse response = FMS.downloadDocumentFromFMS((String) item.getValue(), "");

                            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                                return new InternalResponse(
                                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                        0,
                                        0).setMessage(
                                        new ResponseMessageController().writeStringField(
                                                "error",
                                                "Cannot get Image in Qrypto from FMS!").build()
                                );
                            }

                            byte[] image_ = (byte[]) response.getData();
                            item.setValue(Base64.getEncoder().encodeToString(image_));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                0,
                                0).setMessage(
                                new ResponseMessageController().writeStringField(
                                        "error",
                                        "Cannot get Image in Qrypto  from FMS!").build()
                        );
                    }
                }
            }
        }
        //</editor-fold>

        if (Utils.isNullOrEmpty(QRField.getItems()) && !ignoreMissingItem) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_QR_Qrypto,
                    A_FPSConstant.SUBCODE_MISSING_ITEMS);
        }

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, QRField);
    }
    //</editor-fold>
}
