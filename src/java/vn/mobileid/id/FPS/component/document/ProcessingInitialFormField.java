/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.ExtendedFieldAttribute;
import fps_core.objects.InitialsFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import java.util.ArrayList;
import java.util.Base64;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessInitialField;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingInitialFormField {

    //<editor-fold defaultstate="collapsed" desc="Processing Initial Form Field">
    /**
     * Processing all Initial Form Field in Payload 
     * Flow: Have one field need to be append (If want to process more Field, that value will be provide
     * inside the field) 
     * => Get Doc Revision, ExtendedFieldAttribute and then check some case 
     * => Parse into InitialFieldAttribute 
     * => Then call sub method to process the field Initial (If have flag process all, the sub
     * method will do it)
     *<p>
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
            InitialsFieldAttribute processRequest,
            String transactionId
    ) throws Exception {
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

        //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into InitialField">
        InitialsFieldAttribute initField = null;
        try {
            InternalResponse temp = convertExtendIntoInitialField(
                    user,
                    fieldData,
                    processRequest);

            if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return temp;
            }

            initField = (InitialsFieldAttribute) temp.getData();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check page Initial is valid?">
        if (document_.getDocumentPages() < fieldData.getPage()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT
            ).setUser(user);
        }
        //</editor-fold>

        //Processing
        response = ProcessingFactory.createType(ProcessingFactory.TypeProcess.INITIALS).processMultipleField(
                user,
                document_,
                documents.size(),
                fieldData.getDocumentFieldId(),
                initField,
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

    //<editor-fold defaultstate="collapsed" desc="Processing Mutiple Initial Form Field">
    /**
     * Processing all Initial Form Field in Payload 
     * Flow: already have all ProcessField need to be append value
     * For each ProcessField
     *   => Get from DB and check some case(valid) then parse into InitialFieldAttribute
     *   => Call sub method to process that InitialField
     * <p>
     * @param documents
     * @param user
     * @param fields
     * @param transactionId
     * @return InternalResponse If the InternalResponse.getStatus() !=
     * HTTP.Success => That InternalResponse have an InternalData satisfied
     * format InternalData(null,List<InternalData>) - All fields that have an
     * error while processed
     * @throws Exception
     */
    public static InternalResponse processMultipleInitial_V2(
            List<Document> documents,
            User user,
            ProcessInitialField fields,
            String transactionId
    ) throws Exception {
        List<InternalResponse.InternalData> listOfErrorField = new ArrayList<>();
        InternalResponse responseFinal = new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                ""
        );

        fields.getInitialFieldNames().add(fields.getFieldName());

        //<editor-fold defaultstate="collapsed" desc="Get Original Document and last Document">
        Document document_ = null;
        long documentIDOriginal = 0;
        for (int i = documents.size() - 1; i >= 0; i--) {
            if (documents.get(i).getRevision() == 1) {
                documentIDOriginal = documents.get(i).getId();
            }
            if (documents.get(i).getRevision() == documents.size()) {
                document_ = documents.get(i);
            }
        }
        //</editor-fold>

        for (String field : fields.getInitialFieldNames()) {
            InternalResponse.InternalData errorField = new InternalResponse.InternalData();
            errorField.setName(field);

            //<editor-fold defaultstate="collapsed" desc="Check value is String?">
            if (fields.getValue() != null) {
                if (!(fields.getValue() instanceof String)) {
                    errorField.setValue(
                            String.valueOf(A_FPSConstant.CODE_FIELD_IMAGE)
                            + String.valueOf(A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT)
                    );
                    listOfErrorField.add(errorField);
                    continue;
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Field with the input name">
            InternalResponse response = ConnectorField_Internal.getField(
                    documentIDOriginal,
                    field,
                    transactionId);

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

            if (!fieldData.getType().getParentType().equals(FieldTypeName.INITIAL.getParentName())
                    && !fieldData.getType().getParentType().equals(FieldTypeName.CAMERA.getParentName())) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into ImageField">
            InitialsFieldAttribute initialField = null;
            try {
                InternalResponse convertResponse = convertExtendIntoInitialField(
                        user,
                        fieldData,
                        null
                );

                if (convertResponse.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    return convertResponse;
                }

                initialField = (InitialsFieldAttribute) convertResponse.getData();

                if (fields.getValue() != null) {
                    initialField.setImage(fields.getValue());
                }
            } catch (Exception ex) {
                errorField.setValue(Utils.summaryException(ex));
                if (response.getException() != null) {
                    responseFinal.setException(response.getException());
                }
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check ImageField is have image data ?">
            if (Utils.isNullOrEmpty(initialField.getImage())) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD_IMAGE)
                        + String.valueOf(A_FPSConstant.SUBCODE_MISSING_IMAGE)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check page Image Field is valid?">
            if (document_.getDocumentPages() < fieldData.getPage()) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD) + String.valueOf(A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //Processing
            response = ProcessingFactory.createType(ProcessingFactory.TypeProcess.IMAGE).processField(
                    user,
                    document_,
                    fieldData.getDocumentFieldId(),
                    initialField,
                    transactionId,
                    documents.size());

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                if (response.getCode() == 0 || response.getCodeDescription() == 0) {
                    errorField.setValue(response.getMessage());
                } else {
                    errorField.setValue(
                            String.valueOf(response.getCode()) + String.valueOf(response.getCodeDescription()));
                }
                listOfErrorField.add(errorField);
                if (response.getException() != null) {
                    responseFinal.setException(response.getException());
                }
                continue;
            }
        }

        if (listOfErrorField.isEmpty()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }

        InternalResponse.InternalData data = new InternalResponse.InternalData();
        data.setValue(listOfErrorField);
        responseFinal.setInternalData(data);
        return responseFinal;
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into Initial Field">
    private static InternalResponse convertExtendIntoInitialField(
            User user,
            ExtendedFieldAttribute fieldData,
            InitialsFieldAttribute processRequest) throws Exception {
        //Read details
        InitialsFieldAttribute initialField = new ObjectMapper().readValue(fieldData.getDetailValue(), InitialsFieldAttribute.class);
        initialField = (InitialsFieldAttribute) fieldData.clone(initialField, fieldData.getDimension());

        //Read Basic
        initialField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        initialField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        if (processRequest != null) {
            //<editor-fold defaultstate="collapsed" desc="Check if image is UUID ? => get From FMS">
            if (processRequest.getImage() != null) {
                initialField.setImage(processRequest.getImage());
            } else {
                try {
                    if (initialField.getImage().length() <= 32) {
                        InternalResponse response = FMS.downloadDocumentFromFMS(initialField.getImage(), "");

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
                        initialField.setImage(Base64.getEncoder().encodeToString(image_));
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
                initialField.setApplyToAll(true);
            } else if (!Utils.isNullOrEmpty(processRequest.getPages())) {
                initialField.setPages(processRequest.getPages());
            }
        }

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, initialField);
    }
    //</editor-fold>
}
