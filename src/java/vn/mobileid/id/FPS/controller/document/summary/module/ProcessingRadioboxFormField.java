/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import vn.mobileid.id.FPS.controller.document.summary.micro.GetDocument;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.RadioBoxFieldAttributeV2;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.child.RadioFieldAttribute;
import fps_core.objects.core.BasicFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.ProcessingFactory;
import vn.mobileid.id.FPS.controller.field.summary.module.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingRadioboxFormField extends IVersion {

    public ProcessingRadioboxFormField(Version version) {
        super(version);
    }

    public ProcessingRadioboxFormField() {
        super(IVersion.Version.V1);
    }

    //<editor-fold defaultstate="collapsed" desc="Processing Radiobox Form Field">
    /**
     * Processing all RadioBox in Payload
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
    public InternalResponse processRadioField(
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
                    response = FieldSummaryInternal.getField(
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

            if (!fieldData.getType().getParentType().equals(FieldTypeName.RADIOBOX.getParentName()) &&
                    !fieldData.getType().getParentType().equals(FieldTypeName.RADIOBOXV2.getParentName())) {
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

            //<editor-fold defaultstate="collapsed" desc="Check page is valid?">
            if (document_.getDocumentPages() < fieldData.getPage()) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD) + String.valueOf(A_FPSConstant.SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into RadiobField">
            BasicFieldAttribute radioField = null;
            try {
                if (field.getValue() != null && !(field.getValue() instanceof Boolean)) {
                    field.setValue(null);
                    LogHandler.getInstance().info(
                            ProcessingCheckboxFormField.class,
                            transactionId,
                            "The value is not a boolean => Using default");
                }
                radioField = convertExtendIntoRadioField(user, fieldData, field.getValue() == null ? null : (boolean) field.getValue());
            } catch (Exception ex) {
                errorField.setValue(Utils.summaryException(ex));
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //Processing
            response = new ProcessingFactory().createType(ProcessingFactory.TypeProcess.RADIO, getVersion()).processField(
                    user,
                    document_,
                    documents.size(),
                    fieldData.getDocumentFieldId(),
                    radioField,
                    fieldData,
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
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into RadioField">
    private BasicFieldAttribute convertExtendIntoRadioField(
            User user,
            ExtendedFieldAttribute fieldData,
            Boolean value) throws Exception {
        //Read details
        BasicFieldAttribute radioBox = null;
        if (this.getVersion().equals(Version.V1)) {
            radioBox = new RadioFieldAttribute();
            radioBox = MyServices.getJsonService().readValue(fieldData.getDetailValue(), RadioFieldAttribute.class);
        } else {
            radioBox = new RadioBoxFieldAttributeV2();
            radioBox = MyServices.getJsonService().readValue(fieldData.getDetailValue(), RadioBoxFieldAttributeV2.class);
        }
        radioBox = (BasicFieldAttribute) fieldData.clone(radioBox, fieldData.getDimension());

        radioBox.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        radioBox.setProcessOn(dateFormat.format(Date.from(Instant.now())));
//        if (value != null) {
//            checkboxField.setChecked(value);
//        }

        return radioBox;
    }
    //</editor-fold>

}
