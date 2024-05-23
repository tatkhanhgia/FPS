/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import fps_core.enumration.FieldTypeName;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.core.TextFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.ProcessingRequest.ProcessingFormFillRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 * @param <T>
 */
public class ProcessingTextFormField<T extends TextFieldAttribute>{
    private T type;

    public ProcessingTextFormField(T type) {
        this.type = type;
    }

    public ProcessingTextFormField() {
        this.type = (T) new TextFieldAttribute();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Processing Text Form Field">
    /**
     * Processing all TextField in Payload
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
    public InternalResponse processMultipleTextField(
            long packageId,
            User user,
            List<ProcessingRequest.ProcessingFormFillRequest> fields,
            String transactionId
    ) throws Exception {
        List<InternalData> listOfErrorField = new ArrayList<>();

        for (ProcessingRequest.ProcessingFormFillRequest field : fields) {
            InternalData errorField = new InternalData();
            errorField.setName(field.getFieldName());

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

            if (!fieldData.getType().getParentType().equals(getFieldTypeName().getParentName())) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into TextField">
            TextFieldAttribute textField = null;
            try {
                InternalResponse temp = convert(user, fieldData, field);
                if(!temp.isValid()){
                    return temp;
                }
                textField = (TextFieldAttribute)temp.getData();
            } catch (Exception ex) {
                LogHandler.error(ProcessingTextFormField.class, transactionId, ex);
                errorField.setValue(Utils.summaryException(ex));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check page TextFormField is valid?">
            if (document_.getDocumentPages() < fieldData.getPage()) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD) + String.valueOf(A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>
            
            //Processing 
            response = new ProcessingFactory(type).createType(ProcessingFactory.TypeProcess.TEXTFIELD).processField(
                    user,
                    document_,
                    documents.size(),
                    fieldData.getDocumentFieldId(),
                    textField,
                    fieldData,
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
                ProcessModuleForEnterprise.getInstance(user).getStatusCodeFillFormField(listOfErrorField),
                ""
        );

        InternalData data = new InternalData();
        data.setValue(listOfErrorField);
        response.setInternalData(data);
        return response;
    }
    //</editor-fold>

    //===============================Version 1 =================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into TextField">
    private static TextFieldAttribute convertExtendIntoTextField(
            User user,
            ExtendedFieldAttribute fieldData,
            String value) throws Exception {
        //Read details
        TextFieldAttribute textField = MyServices.getJsonService().readValue(fieldData.getDetailValue(), TextFieldAttribute.class);
        textField = (TextFieldAttribute) fieldData.clone(textField, fieldData.getDimension());

        if (value != null) {
            textField.setValue(value);
        }

        textField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        textField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        return textField;
    }
    //</editor-fold>

    //===============================Version 2 =================================
    public FieldTypeName getFieldTypeName(){
        return FieldTypeName.TEXTBOX;
    }
    
    public InternalResponse convert(
            User user,
            ExtendedFieldAttribute fieldData, 
            ProcessingFormFillRequest processField) throws Exception{
        //Read details
        T textField = (T) MyServices.getJsonService().readValue(fieldData.getDetailValue(), type.getClass());
        textField = (T) fieldData.clone(textField, fieldData.getDimension());

        if (processField != null && processField.getValue() != null) {
            if(!(processField.getValue() instanceof String) && Utils.isNullOrEmpty(textField.getValue())){
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                );
            }
            textField.setValue((String)processField.getValue());
        } else {
            if(Utils.isNullOrEmpty(textField.getValue())){
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                );
            }
        }

        textField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        textField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, textField);
    }
}
