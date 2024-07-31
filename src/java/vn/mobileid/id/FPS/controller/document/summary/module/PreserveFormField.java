/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import fps.readqr.enumeration.InputType;
import fps.readqr.objects.ImageQRCode;
import fps_core.enumration.FieldTypeName;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.core.SignatureFieldAttribute;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import vn.mobileid.id.FPS.controller.field.summary.micro.AddField;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.controller.field.summary.micro.DeleteField;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.Resources;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class PreserveFormField {

    //<editor-fold defaultstate="collapsed" desc="Preserve Form Field">
    /**
     * Preserve all signature form field and qrypto field in file PDF
     *
     * @param documentId
     * @param user
     * @param data
     * @param transactionId
     * @return InternalResponse
     * @throws Exception
     */
    public static InternalResponse preserve(
            long documentId,
            User user,
            byte[] data,
            String transactionId) throws Exception {
        List<BasicFieldAttribute> listFields = new ArrayList<>();
        List<SignatureFieldAttribute> lists = DocumentUtils_itext7.getAllSignatures(data);
        List<ImageQRCode> codes = MyServices.getQRDetectionService(InputType.PDF).scanDocument(data);
//        if (lists == null || lists.isEmpty()) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_SUCCESS, ""
//            );
//        }

        if (!Utils.isNullOrEmpty(lists)) {
            //<editor-fold defaultstate="collapsed" desc="2024-07-23: Add logic preserve Qrypto field">
            for (int i = 0; i < lists.size(); i++) {
                SignatureFieldAttribute signature = lists.get(i);
                if (signature.getFieldName().contains("QRYPTO") || signature.getFieldName().contains("qrypto")) {
                    signature.setLevelOfAssurance(null);
                    signature.setProcessStatus(ProcessStatus.PROCESSED.getName());
                    signature.setType(Resources.getFieldTypes().get(FieldTypeName.QRYPTO.getParentName()));

                    listFields.add(signature);
                    lists.remove(i);
                }
            }
            //</editor-fold>

            for (SignatureFieldAttribute signature : lists) {
                signature.setType(Resources.getFieldTypes().get(FieldTypeName.SIGNATURE.getParentName()));
                signature.setProcessStatus(ProcessStatus.PROCESSED.getName());
                listFields.add(signature);
            }
        }

        //<editor-fold defaultstate="collapsed" desc="2024-07-29: Add logic preserve other annotation(Qrypto stamp,...)">
        List<BasicFieldAttribute> temp = DocumentUtils_itext7.getAllAnnotation(data, null);
        if (!Utils.isNullOrEmpty(temp)) {
            for (BasicFieldAttribute field : temp) {
                if (field.getFieldName() == null) {
                    field.setFieldName("unknown_field");
                }
                
                String fieldName = field.getFieldName().toLowerCase();
                if (fieldName.contains("qrypto")) {
                    QryptoFieldAttribute qryptoField = (QryptoFieldAttribute) field;
                    qryptoField.setType(Resources.getFieldTypes().get(FieldTypeName.QRYPTO.getParentName()));
                    qryptoField.setProcessStatus(ProcessStatus.PROCESSED.getName());

                    //<editor-fold defaultstate="collapsed" desc="Read from list QRCode Detect from Document">
                    for (ImageQRCode qrCode : codes) {
                        if (qrCode.getAnnotationName() != null) {
                            if (qrCode.getAnnotationName().equals(qryptoField.getFieldName())) {
                                qryptoField.setQryptoBase45(qrCode.getQrCodeText());
                            }
                        }
                    }
                    //</editor-fold>
                }
            }
            listFields.addAll(temp);
        }
        //</editor-fold>

        for (BasicFieldAttribute field : listFields) {
            addField(
                    documentId,
                    field,
                    user,
                    FieldTypeName.valueOf(field.getType().getParentType()),
                    transactionId);
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, ""
        );
    }
    //</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Add/Delete Field">
    private static <T extends BasicFieldAttribute> InternalResponse addField(
            long documentId,
            T field,
            User user,
            FieldTypeName fieldType,
            String transactionId
    ) throws Exception {
        //Get field if existed in DB
        InternalResponse response = new InternalResponse();
        response = FieldSummaryInternal.getField(
                documentId,
                field.getFieldName(),
                transactionId);

        if (response.isValid()) {
            try {
                DeleteField.deleteField(
                        documentId,
                        field.getFieldName(),
                        transactionId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        response = AddField.addField(
                documentId,
                field,
                "hmac",
                user.getEmail(),
                transactionId);
        if (!response.isValid()) {
            return new InternalResponse();
        }
        int fieldId = (int) response.getData();
        response = AddField.addDetailField(
                fieldId,
                Resources.getFieldTypes().get(fieldType.getParentName()).getTypeId(),
                field,
                "hmac",
                user.getEmail(),
                transactionId);
        if (!response.isValid()) {
            return response;
        }

        response = FieldSummaryInternal.updateValueOfField(
                fieldId,
                user,
                MyServices.getJsonService().writeValueAsString(field),
                transactionId);
        if (!response.isValid()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
            );
        }
        return new InternalResponse();
    }
    //</editor-fold>

    public static void main(String[] args) throws IOException {
        List<SignatureFieldAttribute> signatures = DocumentUtils_itext7.getAllSignatures(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.pdf")));
        for (SignatureFieldAttribute signature : signatures) {
            System.out.println("Name:" + signature.getFieldName());
            System.out.println("Dimension:" + signature.getDimension().getX());
            System.out.println("Dimension:" + signature.getDimension().getY());
            System.out.println("Dimension:" + signature.getDimension().getWidth());
            System.out.println("Dimension:" + signature.getDimension().getHeight());
        }
    }
}
