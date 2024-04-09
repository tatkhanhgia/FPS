/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.core.FileFieldAttribute;
import java.util.ArrayList;
import java.util.Base64;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.ProcessFileField;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingFileField {

    //<editor-fold defaultstate="collapsed" desc="Processing File Form Field">
    /**
     * Processing Image Form Field in Payload
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
    public static InternalResponse processFileFormField(
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

        if (!fieldData.getType().getParentType().equals(FieldTypeName.INITIAL.getParentName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into ImageField">
        FileFieldAttribute imageField = null;
        try {
            InternalResponse convertResponse = convertExtendIntoFileField(
                    user,
                    fieldData
            );

            if (convertResponse.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return convertResponse;
            }

            imageField = (FileFieldAttribute) convertResponse.getData();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        //</editor-fold>

        //Processing
        response = new ProcessingFactory().createType(ProcessingFactory.TypeProcess.IMAGE).processField(
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

    //<editor-fold defaultstate="collapsed" desc="Processing Multiple File Form Field Version2">
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
    public static InternalResponse processMultipleFileFormField(
            long packageId,
            User user,
            ProcessFileField fields,
            String transactionId
    ) throws Exception {
        List<InternalResponse.InternalData> listOfErrorField = new ArrayList<>();
        InternalResponse responseFinal = new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                ""
        );

        if (fields.getFields() == null) {
            fields.setFields(new ArrayList<>());
        }

        if (!fields.getFields().contains(fields.getFieldName())) {
            fields.getFields().add(fields.getFieldName());
        }

        for (String field : fields.getFields()) {
            InternalResponse.InternalData errorField = new InternalResponse.InternalData();
            errorField.setName(field);

            //<editor-fold defaultstate="collapsed" desc="Get Documents">
            InternalResponse response = GetDocument.getDocuments(
                    packageId,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response.setUser(user);
            }

            List<Document> documents = (List<Document>) response.getData();
            //</editor-fold>

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

            //<editor-fold defaultstate="collapsed" desc="Check value is String?">
            if (fields.getValue() != null) {
                if (!(fields.getValue() instanceof String)) {
                    errorField.setValue(
                            String.valueOf(A_FPSConstant.CODE_FIELD_STAMP)
                            + String.valueOf(A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT)
                    );
                    listOfErrorField.add(errorField);
                    continue;
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Field with the input name">
            response = ConnectorField_Internal.getField(
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

            if (!fieldData.getType().getParentType().equals(FieldTypeName.STAMP.getParentName())) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into ImageField">
            FileFieldAttribute initialField = null;
            try {
                InternalResponse convertResponse = convertExtendIntoFileField(
                        user,
                        fieldData
                );

                if (convertResponse.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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

                initialField = (FileFieldAttribute) convertResponse.getData();

                if (fields.getValue() != null) {
                    initialField.setFile(fields.getValue());
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
            if (Utils.isNullOrEmpty(initialField.getFile())) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD_STAMP)
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
            response = new ProcessingFactory().createType(
                    ProcessingFactory.TypeProcess.IMAGE)
                    .processField(
                            user,
                            document_,
                            documents.size(),
                            fieldData.getDocumentFieldId(),
                            initialField,
                            transactionId,
                            documentIDOriginal);

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

    //<editor-fold defaultstate="collapsed" desc="Processing Multiple File Form Field Version1">
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
    public static InternalResponse processMultipleFileFormField(
            long packageId,
            User user,
            List<ProcessingRequest.ProcessingFormFillRequest> fields,
            String transactionId
    ) throws Exception {
        List<InternalData> listOfErrorField = new ArrayList<>();
        InternalResponse responseFinal = new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                ""
        );

        for (ProcessingRequest.ProcessingFormFillRequest field : fields) {
            InternalData errorField = new InternalData();
            errorField.setName(field.getFieldName());

            //<editor-fold defaultstate="collapsed" desc="Check value is String?">
            if (field.getValue() != null) {
                if (!(field.getValue() instanceof String)) {
                    errorField.setValue(
                            String.valueOf(A_FPSConstant.CODE_FIELD_STAMP)
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

            if (!fieldData.getType().getParentType().equals("TEXTBOX")) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into File Field">
            FileFieldAttribute fileField = null;
            try {
                InternalResponse convert = convertExtendIntoFileField(user, fieldData);

                if (!convert.isValid()) {
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
                
                fileField = (FileFieldAttribute)convert.getData();

            } catch (Exception ex) {
                errorField.setValue(Utils.summaryException(ex));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check page FileField is valid?">
            if (document_.getDocumentPages() < fieldData.getPage()) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD) + String.valueOf(A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //Processing
            response = new ProcessingFactory().createType(ProcessingFactory.TypeProcess.IMAGE).processField(
                    user,
                    document_,
                    documents.size(),
                    fieldData.getDocumentFieldId(),
                    fileField,
                    fieldData,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                errorField.setValue(
                        String.valueOf(response.getCode()) + String.valueOf(response.getCodeDescription()));
                listOfErrorField.add(errorField);
            }
        }

        if (listOfErrorField.isEmpty()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }
        InternalResponse response = new InternalResponse(
                ProcessModuleForEnterprise.getInstance(user).getStatusCodeFillFormField(listOfErrorField),
                ""
        );

        InternalData data = new InternalData();
        data.setValue(listOfErrorField);
        response.setInternalData(data);
        return response;
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into FileField">
    private static InternalResponse convertExtendIntoFileField(
            User user,
            ExtendedFieldAttribute fieldData) throws Exception {
        //Read details
        FileFieldAttribute imageField = new ObjectMapper().readValue(fieldData.getDetailValue(), FileFieldAttribute.class);
        imageField = (FileFieldAttribute) fieldData.clone(imageField, fieldData.getDimension());

        imageField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
        imageField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS if need">
        if (!Utils.isNullOrEmpty(imageField.getFile()) && imageField.getFile().length() <= 32) {
            try {
                InternalResponse response = FMS.downloadDocumentFromFMS(imageField.getFile(), "");

                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_ERROR_WHILE_DOWNLOAD_FMS);
                }

                byte[] image_ = (byte[]) response.getData();
                imageField.setFile(Base64.getEncoder().encodeToString(image_));
            } catch (Exception ex) {
                ex.printStackTrace();
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                            A_FPSConstant.CODE_FMS,
                            A_FPSConstant.SUBCODE_ERROR_WHILE_DOWNLOAD_FMS);
            }
        }
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                imageField);
    }
    //</editor-fold>

}
