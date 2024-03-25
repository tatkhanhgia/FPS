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
import vn.mobileid.id.FPS.QryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.ImageFieldAttribute;
import fps_core.objects.TextFieldAttribute;
import java.util.ArrayList;
import vn.mobileid.id.FPS.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingImageField {

    //<editor-fold defaultstate="collapsed" desc="Processing Image Form Field">
    /**
     * Processing Image Form Field in Payload
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
    public static InternalResponse processImageField(
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
            return response;
        }

        if (!fieldData.getType().getParentType().equals(FieldTypeName.IMAGE.getParentName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into ImageField">
        ImageFieldAttribute imageField = null;
        try {
            imageField = convertExtendIntoImageField(
                    user,
                    fieldData
            );
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //Processing
        response = ProcessingFactory.createType(ProcessingFactory.TypeProcess.IMAGE).process(
                user,
                document_,
                fieldData.getDocumentFieldId(),
                imageField,
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

    //<editor-fold defaultstate="collapsed" desc="Processing Image Form Field">
    /**
     * Processing all ImageField in Payload
     *
     * @param packageId
     * @param user
     * @param fields
     * @param transactionId
     * @return InternalResponse If the InternalResponse.getStatus() !=
     * HTTP.Success => That InternalResponse have an InternalData satisfied
     * format InternalData(null,List<InternalData>) - All fields that have an
     * error while processed
     * @throws Exception
     */
    public static InternalResponse processImageFields(
            long packageId,
            User user,
            List<ProcessingRequest.ProcessingFormFillRequest> fields,
            String transactionId
    ) throws Exception {
        List<InternalResponse.InternalData> listOfErrorField = new ArrayList<>();

        for (ProcessingRequest.ProcessingFormFillRequest field : fields) {
            InternalResponse.InternalData errorField = new InternalResponse.InternalData();
            errorField.setName(field.getFieldName());

            //<editor-fold defaultstate="collapsed" desc="Check value is String?">
            if (field.getValue() != null) {
                if (!(field.getValue() instanceof String)) {
                    errorField.setValue(
                            String.valueOf(A_FPSConstant.CODE_FIELD_IMAGE)
                            + String.valueOf(A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT)
                    );
                    listOfErrorField.add(errorField);
                    continue;
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Documents">
            InternalResponse response = GetDocument.getDocuments(
                    packageId,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            List<Document> documents = (List<Document>) response.getData();
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get all data of the field">
            Document document_ = null;
            for (int i = documents.size() - 1; i >= 0; i--) {
                if (documents.get(i).getRevision() == 1) {
                    response = ConnectorField_Internal.getField(
                            documents.get(i).getId(),
                            field.getFieldName(),
                            transactionId);
                }
                if (documents.get(i).getRevision() == documents.size()) {
                    document_ = documents.get(i);
                }
            }

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                errorField.setValue(String.valueOf(response.getCode()) + String.valueOf(response.getCodeDescription()));
                listOfErrorField.add(errorField);
                continue;
            }

            ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();

            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Check data in ExtendedField is sastified">
            if (CheckFieldProcessedYet.checkProcessed(fieldData.getFieldValue()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS)
                );
                listOfErrorField.add(errorField);
                continue;
            }

            if (!fieldData.getType().getParentType().equals("IMAGE")) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into TextField">
            ImageFieldAttribute imageField = null;
            try {
                imageField = convertExtendIntoImageField(
                        user,
                        fieldData);
                if (field.getValue() != null) {
                    imageField.setImage((String) field.getValue());
                }
            } catch (Exception ex) {
                errorField.setValue(Utils.summaryException(ex));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //Processing
            response = ProcessingFactory.createType(ProcessingFactory.TypeProcess.IMAGE).process(
                    user,
                    document_,
                    fieldData.getDocumentFieldId(),
                    imageField,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                errorField.setValue(
                        String.valueOf(response.getCode()) + String.valueOf(response.getCodeDescription()));
                listOfErrorField.add(errorField);
                continue;
            }
        }

        if (listOfErrorField.isEmpty()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }
        InternalResponse response = new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                ""
        );

        InternalResponse.InternalData data = new InternalResponse.InternalData();
        data.setValue(listOfErrorField);
        response.setInternalData(data);
        return response;
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into ImageField">
    private static ImageFieldAttribute convertExtendIntoImageField(
            User user,
            ExtendedFieldAttribute fieldData) throws Exception {
        //Read details
        ImageFieldAttribute imageField = new ObjectMapper().readValue(fieldData.getDetailValue(), ImageFieldAttribute.class);
        imageField = (ImageFieldAttribute) fieldData.clone(imageField, fieldData.getDimension());

        imageField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
        imageField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        return imageField;
    }
    //</editor-fold>
}
