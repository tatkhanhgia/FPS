/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.BasicFieldAttribute;
import fps_core.objects.InitialsFieldAttribute;
import java.util.Base64;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CheckPayloadRequest {

    //<editor-fold defaultstate="collapsed" desc="Check Sign Request">
    public static InternalResponse checkSignRequest(
            String payload,
            String transactionId
    ) throws Exception {
        //Parse payload
        ProcessingRequest processRequest = null;
        try {
            processRequest = new ObjectMapper().readValue(payload, ProcessingRequest.class);
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
        }
        if (Utils.isNullOrEmpty(processRequest.getFieldName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }
        if (processRequest.getSignatureValue() == null || processRequest.getSignatureValue().isEmpty()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_SIGNATURE,
                            A_FPSConstant.SUBCODE_MISSING_SIGNATURE_VALUE
            );
        }
        if (Utils.isNullOrEmpty(processRequest.getCertificateChain())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_SIGNATURE,
                            A_FPSConstant.SUBCODE_MISSING_OR_EMTYPE_CERTIFICATES_CHAIN
            );
        }
        InternalResponse response = new InternalResponse();
        response.setStatus(A_FPSConstant.HTTP_CODE_SUCCESS);
        response.setData(processRequest);
        return response;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Check Hash Request">
    public static InternalResponse checkHashRequest(
            String payload,
            String transactionId
    ) throws Exception {
        //Parse payload
        ProcessingRequest processRequest = null;
        try {
            processRequest = new ObjectMapper().readValue(payload, ProcessingRequest.class);
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
            );
        }
        if (Utils.isNullOrEmpty(processRequest.getFieldName())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }
        if (Utils.isNullOrEmpty(processRequest.getCertificateChain())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_SIGNATURE,
                            A_FPSConstant.SUBCODE_MISSING_OR_EMTYPE_CERTIFICATES_CHAIN
            );
        }
        if (Utils.isNullOrEmpty(processRequest.getSignatureAlgorithm())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_SIGNATURE,
                            A_FPSConstant.SUBCODE_MISSING_SIGNATURE_ALGORITHM
            );
        }
        if (Utils.isNullOrEmpty(processRequest.getSignedHash())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_SIGNATURE,
                            A_FPSConstant.SUBCODE_MISSING_SIGNED_HASH
            );
        }
        try {
            if (processRequest.getHandSignatureImage() != null) {
                byte[] temp = Base64.getDecoder().decode(processRequest.getHandSignatureImage());
                System.out.println("Có Image");
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_SIGNATURE,
                            A_FPSConstant.SUBCODE_THE_HAND_IMAGE_SIGNATURE_CANNOT_PARSE
            );
        }

        InternalResponse response = new InternalResponse();
        response.setStatus(A_FPSConstant.HTTP_CODE_SUCCESS);
        response.setData(processRequest);
        return response;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CheckBasicField">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) Check the
     * mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public  static InternalResponse checkBasicField(BasicFieldAttribute object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }
        if (object.getPage() <= 0) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_PAGE
            );
        }
        if (object.getDimension() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_DIMENSION
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Field based on String">
    /**
     * Dùng để kiểm tra dữ liệu xem là Field nhập vào có tồn tại parentType và
     * có đúng dạng đúng với trong hệ thống Use for check the textField of input
     * is valid "textField". Check if it existed in DB
     *
     * @param field
     * @return
     */
    public  static boolean checkField(String type, FieldTypeName parentType) {
        boolean check1 = Resources.getFieldTypes().containsKey(type);
        boolean check2 = false;
        if (check1) {
            check2 = Resources.getFieldTypes().get(type).getParentType().equals(parentType.getParentName());
        }
        return check2;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Field based on Object">
    /**
     * Dùng để kiểm tra dữ liệu xem là Field nhập vào có tồn tại parentType đúng
     * với trong hệ thống Use for check the parentType of input "textField".
     * Check if it existed in DB
     *
     * @param field
     * @return
     */
    public  static boolean checkField(BasicFieldAttribute field, FieldTypeName parentType) {
        boolean check1 = Resources.getFieldTypes().containsKey(field.getTypeName());
        boolean check2 = false;
        if (check1) {
            check2 = Resources.getFieldTypes().get(field.getTypeName()).getParentType().equals(parentType.getParentName());
        }
        return check2;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Check Initial Field">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) Check the
     * mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public  static InternalResponse checkAddInitialField(InitialsFieldAttribute object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }
        
        if (object.getPage() <= 0 && Utils.isNullOrEmpty(object.getPages()) && !object.isApplyToAll()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_PAGE
            );
        }
//        if (Utils.isNullOrEmpty(object.getImage())) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                    ResponseMessageController.getErrorMessageAdvanced(
//                            A_FPSConstant.CODE_FIELD_INITIAL,
//                            A_FPSConstant.SUBCODE_MISSING_OR_EMPTY_IMAGE_OF_INITIAL,
//                            "en",
//                            transactionId)
//            );
//        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Initial Field">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) Check the
     * mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public  static InternalResponse checkFillInitialField(InitialsFieldAttribute object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }
        
        if (object.getPage() <= 0 && Utils.isNullOrEmpty(object.getPages()) && !object.isApplyToAll()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD,
                            A_FPSConstant.SUBCODE_MISSING_PAGE
            );
        }
        if (Utils.isNullOrEmpty(object.getImage())) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_INITIAL,
                            A_FPSConstant.SUBCODE_MISSING_OR_EMPTY_IMAGE_OF_INITIAL
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        );
    }
    //</editor-fold>
}

