/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.ExtendedFieldAttribute;
import fps_core.objects.QRFieldAttribute;
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
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;

/**
 *
 * @author GiaTK
 */
public class ProcessingQRQryptoField {

    //<editor-fold defaultstate="collapsed" desc="Processing QR Qrypto Form Field">
    /**
     * Processing QR Qrypto Form Field in Payload
     *
     * @param packageId
     * @param fieldName
     * @param user
     * @param documents
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
        QRFieldAttribute QRField = null;
        try {
            QRField = convertExtend_into_QRField(
                    user,
                    fieldData
                    );
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //Processing
        response = ProcessingFactory.createType(ProcessingFactory.TypeProcess.QRYPTO).process(
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
    
    //==========================================================================
    
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into TextField">
    private static QRFieldAttribute convertExtend_into_QRField(
            User user,
            ExtendedFieldAttribute fieldData) throws Exception {
        //Read details
        QRFieldAttribute QRField = new ObjectMapper().readValue(fieldData.getDetailValue(), QRFieldAttribute.class);

        //Read Basic
        QRField.setFieldName(fieldData.getFieldName());
        QRField.setPage(fieldData.getPage());
        QRField.setDimension(fieldData.getDimension());
        QRField.setVisibleEnabled(fieldData.getVisibleEnabled());
        QRField.setRequired(fieldData.getRequired());
        QRField.setType(fieldData.getType());
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

        return QRField;
    }
    //</editor-fold>
}
