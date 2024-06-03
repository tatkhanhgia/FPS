/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import vn.mobileid.id.FPS.controller.document.summary.micro.GetDocument;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.core.InitialsFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.ProcessingFactory;
import vn.mobileid.id.FPS.controller.field.summary.module.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import java.util.ArrayList;
import java.util.Base64;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessInitialField;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingInitialFormField extends IVersion{

    ProcessingInitialFormField(Version version) {
        super(version);
    }
    
    public static ProcessingInitialFormField genVersion(Version version){
        return new ProcessingInitialFormField(version);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Processing Initial Form Field">
    /**
     * Processing all Initial Form Field in Payload
     * Flow: Have one field need to be append (If want to process more Field, that value will be provide
     * inside the field)
     * => Get Doc Revision, ExtendedFieldAttribute and then check some case
     * => Parse into InitialFieldAttribute
     * => Then call sub method to process the field Initial (If have flag process all, the sub
     * method will do it)
     * <p>
     * @param packageId
     * @param user
     * @param processRequest
     * @param transactionId
     * @return InternalResponse If the InternalResponse.getStatus() !=
     * HTTP.Success => That InternalResponse have an InternalData satisfied
     * format InternalData(null,List<InternalData>) - All fields that have an
     * error while processed
     * @throws Exception
     */
    public InternalResponse processInitialField(
            long packageId,
            User user,
            InitialsFieldAttribute processRequest,
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
        long documentIdOriginal = 0;
        response = new InternalResponse();
        for (int i = documents.size() - 1; i >= 0; i--) {
            if (documents.get(i).getRevision() == 1) {
                documentIdOriginal = documents.get(i).getId();
                response = FieldSummaryInternal.getField(
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
                    processRequest,
                    null);

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
        response = new ProcessingFactory()   
                .createType(ProcessingFactory.TypeProcess.INITIALS, 
                        getVersion())
                .processField(
                user, //user
                document_, //Document
                documents.size(), //Revision
                fieldData.getDocumentFieldId(), //fieldID
                initField, //InitField
                transactionId, //trans
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
     * => Get from DB and check some case(valid) then parse into InitialFieldAttribute
     * => Call sub method to process that InitialField
     * <p>
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
    public  InternalResponse processInitialField_V2(
            long packageId,
            User user,
            ProcessInitialField fields,
            String transactionId
    ) throws Exception {
        List<InternalResponse.InternalData> listOfErrorField = new ArrayList<>();
        InternalResponse responseFinal = new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                ""
        );

        if (fields.getInitialFieldNames() == null) {
            fields.setInitialFieldNames(new ArrayList<>());
        }
        
        if(!fields.getInitialFieldNames().contains(fields.getFieldName())){
            fields.getInitialFieldNames().add(fields.getFieldName());
        }

        for (String field : fields.getInitialFieldNames()) {
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
                            String.valueOf(A_FPSConstant.CODE_FIELD)
                            + String.valueOf(A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT)
                    );
                    listOfErrorField.add(errorField);
                    continue;
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Field with the input name">
            response = FieldSummaryInternal.getField(
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
                        null,
                        fields
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
                    ProcessingFactory.TypeProcess.INITIALS, 
                    getVersion())
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

    //============================METHOD INTERNAL===============================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into Initial Field">
    private static InternalResponse convertExtendIntoInitialField(
            User user,
            ExtendedFieldAttribute fieldData,
            InitialsFieldAttribute processRequest,
            ProcessInitialField processRequestV2) throws Exception {
        //Read details
        InitialsFieldAttribute initialField = MyServices.getJsonService().readValue(fieldData.getDetailValue(), InitialsFieldAttribute.class);
        initialField = (InitialsFieldAttribute) fieldData.clone(initialField, fieldData.getDimension());

        //Read Basic
        initialField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        initialField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        if (processRequest != null) {
            if (processRequest.getImage() != null) {
                initialField.setImage(processRequest.getImage());
            }
            if (processRequest.isApplyToAll()) {
                initialField.setApplyToAll(true);
            } 
        } else 
            try {
            if (processRequestV2 != null && !Utils.isNullOrEmpty(processRequestV2.getValue())) {
                initialField.setImage(processRequestV2.getValue());
            }
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

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, initialField);
    }
    //</editor-fold>
}
