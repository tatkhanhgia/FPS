/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.ImageFieldAttribute;
import java.util.Base64;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingImageField_Temp {

    //<editor-fold defaultstate="collapsed" desc="Processing Initial Form Field">
    /**
     * Processing all Initial Form Field in Payload
     *
     * @param packageId
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
    public static InternalResponse processInitialField(
            long packageId,
            User user,
            List<Document> documents,
            ImageFieldAttribute processRequest,
            String transactionId
    ) throws Exception {
        //Nhwos thêm bước kiểm tra field.getValue dạng String
        //Nhớ thêm bước trả message lỗi đối với đoạn Convert        

        //<editor-fold defaultstate="collapsed" desc="Get all data of the field">
        Document document_ = null;
        long documentIdOriginal = 0;
        InternalResponse response = new InternalResponse();
        for (int i = documents.size() - 1; i >= 0; i--) {
            if (documents.get(i).getRevision() == 1) {
                documentIdOriginal = documents.get(i).getId();
                response = ConnectorField_Internal.getField(
                        documents.get(i).getId(),
                        processRequest.getFieldName(),
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

        if (!fieldData.getType().getParentType().equals(FieldTypeName.INITIAL.getParentName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into ImageField">
        ImageFieldAttribute initField = null;
        try {
            InternalResponse temp = convertExtendIntoImageField(
                    user,
                    fieldData,
                    processRequest);

            if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return temp;
            }

            initField = (ImageFieldAttribute) temp.getData();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check page Initial is valid?">
        if(document_.getDocumentPages() < fieldData.getPage()){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT
            ).setUser(user);
        }
        //</editor-fold>
        
        //Processing
        response = ProcessingFactory.createType(ProcessingFactory.TypeProcess.IMAGE).process(
                user,
                document_,
                documents.size(),
                fieldData.getDocumentFieldId(),
                initField,
                fieldData,
                transactionId,
                documentIdOriginal //flow 2 add this param
        );

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
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into Image">
    private static InternalResponse convertExtendIntoImageField(
            User user,
            ExtendedFieldAttribute fieldData,
            ImageFieldAttribute processRequest) throws Exception {
        //Read details
        ImageFieldAttribute imageField = new ObjectMapper().readValue(fieldData.getDetailValue(), ImageFieldAttribute.class);
        imageField = (ImageFieldAttribute) fieldData.clone(imageField, fieldData.getDimension());

        //Read Basic
        imageField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        imageField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Check if image is UUID ? => get From FMS">
        if (processRequest.getImage() != null) {
            imageField.setImage(processRequest.getImage());
        } else {
            try {
                if (imageField.getImage().length() <= 32) {
                    InternalResponse response = FMS.downloadDocumentFromFMS(imageField.getImage(), "");

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                                0,
                                0).setMessage(
                                new ResponseMessageController().writeStringField(
                                        "error",
                                        "Cannot get Image in Initial from FMS!").build()
                        );
                    }

                    byte[] image_ = (byte[]) response.getData();
                    imageField.setImage(Base64.getEncoder().encodeToString(image_));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                        0,
                        0).setMessage(
                        new ResponseMessageController().writeStringField(
                                "error",
                                "Cannot get Image in Initial from FMS!").build()
                );
            }
        }
        //</editor-fold>
        
        if (processRequest.isApplyToAll()) {
            imageField.setApplyToAll(true);
        } else if (!Utils.isNullOrEmpty(processRequest.getPages())) {
            imageField.setPages(processRequest.getPages());
        }

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, imageField);
    }
    //</editor-fold>
}
