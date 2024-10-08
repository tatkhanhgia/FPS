/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import vn.mobileid.id.FPS.controller.document.summary.micro.GetDocument;
import fps_core.enumration.FieldTypeName;
import fps_core.objects.child.AttachmentFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.ProcessingFactory;
import vn.mobileid.id.FPS.controller.enterprise.summary.micro.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.controller.field.summary.module.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessingAttachmentField {

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
        List<InternalResponse.InternalData> listOfErrorField = new ArrayList<>();
        InternalResponse responseFinal = new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                ""
        );

        for (ProcessingRequest.ProcessingFormFillRequest field : fields) {
            InternalResponse.InternalData errorField = new InternalResponse.InternalData();
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

            if (!fieldData.getType().getParentType().equals(FieldTypeName.ATTACHMENT.getParentName())) {
                errorField.setValue(
                        String.valueOf(A_FPSConstant.CODE_FIELD)
                        + String.valueOf(A_FPSConstant.SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION)
                );
                listOfErrorField.add(errorField);
                continue;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Convert ExtendField into File Field">
            AttachmentFieldAttribute fileField = null;
            try {
                InternalResponse convert = convertExtendIntoFileField(
                        user,
                        fieldData,
                        field);

                if (!convert.isValid()) {
                    if (convert.getCode() == 0 || convert.getCodeDescription() == 0) {
                        errorField.setValue(convert.getMessage());
                    } else {
                        errorField.setValue(
                                String.valueOf(convert.getCode()) + String.valueOf(convert.getCodeDescription()));
                    }
                    listOfErrorField.add(errorField);
                    if (response.getException() != null) {
                        responseFinal.setException(response.getException());
                    }
                    continue;
                }
                fileField = (AttachmentFieldAttribute) convert.getData();

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
            response = new ProcessingFactory().createType(ProcessingFactory.TypeProcess.ATTACHMENT).processField(
                    user,
                    document_,
                    documents.size(),
                    fieldData.getDocumentFieldId(),
                    fileField,
                    transactionId,
                    0L);

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

        InternalResponse.InternalData data = new InternalResponse.InternalData();
        data.setValue(listOfErrorField);
        response.setInternalData(data);
        return response;
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Convert ExtendedField into Attachment Field">
    private static InternalResponse convertExtendIntoFileField(
            User user,
            ExtendedFieldAttribute fieldData,
            ProcessingRequest.ProcessingFormFillRequest processField) throws Exception {
        //Read details
        AttachmentFieldAttribute imageField = MyServices.getJsonService().readValue(fieldData.getDetailValue(), AttachmentFieldAttribute.class);
        imageField = (AttachmentFieldAttribute) fieldData.clone(imageField, fieldData.getDimension());

        imageField.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
        imageField.setProcessOn(dateFormat.format(Date.from(Instant.now())));

        //<editor-fold defaultstate="collapsed" desc="Check value is String?">
        if (processField != null && !Utils.isNullOrEmpty(processField.getValue())) {
            if (!(processField.getValue() instanceof String) && Utils.isNullOrEmpty(imageField.getFile())) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD,
                        A_FPSConstant.SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT
                );
            }
        } else {
            if(Utils.isNullOrEmpty(imageField.getFile())){
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_ATTACHMENT,
                        A_FPSConstant.SUBCODE_MISSING_FILE_DATA_OF_ATTACHMENT
                );
            }
        }
        //</editor-fold>

        if (!Utils.isNullOrEmpty(processField.getFileName())) {
            String[] split = processField.getFileName().split("\\.");
            String extension = split[split.length - 1];
            imageField.setFileExtension(extension);
            imageField.setFileName(processField.getFileName());
        }

        if (!Utils.isNullOrEmpty((String) processField.getValue())) {
            imageField.setFile((String) processField.getValue());
        }

        //<editor-fold defaultstate="collapsed" desc="Check extension">
        if (Utils.isNullOrEmpty(imageField.getFileExtension())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD_ATTACHMENT,
                    A_FPSConstant.SUBCODE_MISSING_EXTENSION
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download File from FMS if need">
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
