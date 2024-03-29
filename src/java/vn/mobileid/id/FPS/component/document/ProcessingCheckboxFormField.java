/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.CheckBoxFieldAttribute;
import fps_core.objects.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.field.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingCheckboxFormField {

    //<editor-fold defaultstate="collapsed" desc="Processing Checkbox Form Field">
    /**
     * Processing all CheckboxField in Payload
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
    public static InternalResponse processCheckboxField(
            long packageId,
            User user,
            List<ProcessingRequest.ProcessingFormFillRequest> fields,
            String transactionId
    ) throws Exception {
        //Nhwos thêm bước kiểm tra field.getValue dạng String
        //Nhớ thêm bước trả message lỗi đối với đoạn Convert
        List<InternalData> listOfErrorField = new ArrayList<>();

        for (ProcessingRequest.ProcessingFormFillRequest field : fields) {
            System.out.println("Field:"+field.getFieldName());
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
                errorField.setValue(response.getMessage());
                listOfErrorField.add(errorField);
                continue;
            }

            ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();

            if (!Utils.isNullOrEmpty(fieldData.getFieldValue())) {
                errorField.setValue(ResponseMessageController.getErrorMessageAdvanced(
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS,
                        "",
                        "en",
                        transactionId));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check data in ExtendedField is sastified">
            if (CheckFieldProcessedYet.checkProcessed(fieldData.getFieldValue()).getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD) +
                        String.valueOf(A_FPSConstant.SUBCODE_FIELD_ALREADY_PROCESS)
                        );
                listOfErrorField.add(errorField);
                continue;
            }
            
            if (!fieldData.getType().getParentType().equals("TEXTBOX")) {
                errorField.setValue(ResponseMessageController.getErrorMessageAdvanced(
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION,
                        "",
                        "en",
                        transactionId));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into TextField">
            CheckBoxFieldAttribute checkboxField = null;
            try {
                checkboxField = convertExtend_into_CheckboxField(user, fieldData, (String) field.getValue());
            } catch (Exception ex) {
                errorField.setValue(Utils.summaryException(ex));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //Processing
            response = ProcessingFactory.createType_Module(ProcessingFactory.TypeProcess.CHECKBOX).createFormField(
                    user,
                    document_,
                    documents.size(),
                    fieldData.getDocumentFieldId(),
                    checkboxField,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                errorField.setValue(response.getMessage());
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

        InternalData data = new InternalData();
        data.setValue(listOfErrorField);
        response.setInternalData(data);
        return response;
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into CheckboxField">
    private static CheckBoxFieldAttribute convertExtend_into_CheckboxField(
            User user,
            ExtendedFieldAttribute fieldData,
            String value) throws Exception {
        //Read details
        CheckBoxFieldAttribute checkboxField = new ObjectMapper().readValue(fieldData.getDetailValue(), CheckBoxFieldAttribute.class);
        //Read Basic
        checkboxField.setFieldName(fieldData.getFieldName());
        checkboxField.setPage(fieldData.getPage());
        checkboxField.setDimension(fieldData.getDimension());
        checkboxField.setVisibleEnabled(fieldData.getVisibleEnabled());
        checkboxField.setRequired(fieldData.getRequired());
        checkboxField.setType(fieldData.getType());
        checkboxField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        checkboxField.setProcessOn(dateFormat.format(Date.from(Instant.now())));
        checkboxField.setValue(value);

        return checkboxField;
    }
    //</editor-fold>
}
