/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.core.InitialsFieldAttribute;
import java.util.Base64;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import fps_core.enumration.FieldTypeName;
import fps_core.utils.LogHandler;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.ProcessFileField;
import vn.mobileid.id.FPS.object.ProcessInitialField;
import vn.mobileid.id.FPS.object.ProcessingRequest;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.Resources;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CheckPayloadRequest {

    //<editor-fold defaultstate="collapsed" desc="Check Sign Request">
    /**
     * Check sign request
     * <p>
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse checkSignRequest(
            String payload,
            String transactionId
    ) throws Exception {
        //Parse payload
        ProcessingRequest processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessingRequest.class);
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
    /**
     * Check the hash request
     * <p>
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse checkHashRequest(
            String payload,
            String transactionId
    ) throws Exception {
        //Parse payload
        ProcessingRequest processRequest = null;
        try {
            processRequest = MyServices.getJsonService().readValue(payload, ProcessingRequest.class);
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
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) 
     * Check the mandatory variables in field.
     *<p>
     * @param object
     * @param transactionId
     * @return
     */
    public static InternalResponse checkBasicField(BasicFieldAttribute object, String transactionId) {
        LogHandler.HierarchicalLog log = new LogHandler.HierarchicalLog("Check basic field");
        
//        log.addStartHeading1("Start checking field name");
        if (object.getFieldName() == null) {
            log.addEndHeading1("Checked field name fail");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            ).setHierarchicalLog(log);
        }
        log.addEndHeading1("Checked field name successfully");
        
//        log.addStartHeading1("Start checking page");
        if (object.getPage() <= 0) {
            log.addEndHeading1("Checked page fail");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_PAGE
            ).setHierarchicalLog(log);
        }
        log.addEndHeading1("Checked page successfully");
        
//        log.addStartHeading1("Start checking dimension");
        if (object.getDimension() == null) {
            log.addEndHeading1("Checked dimension fail");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_DIMENSION
            ).setHierarchicalLog(log);
        }
        log.addEndHeading1("Checked dimension successfully");
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        ).setHierarchicalLog(log);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CheckBasicField when update field">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) 
     * Check the mandatory variables in field.
     *<p>
     * @param object
     * @param transactionId
     * @return
     */
    public static InternalResponse checkBasicFieldWhenUpdateField(BasicFieldAttribute object, String transactionId) {
        LogHandler.HierarchicalLog log = new LogHandler.HierarchicalLog("Check basic field");
        
//        log.addStartHeading1("Start checking field name");
        if (object.getFieldName() == null) {
            log.addEndHeading1("Checked field name fail");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            ).setHierarchicalLog(log);
        }
        log.addEndHeading1("Checked field name successfully");        
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        ).setHierarchicalLog(log);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CheckBasicField with T type">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) 
     * Check the mandatory variables in field.
     *<p>
     * @param object
     * @param transactionId
     * @return
     */
    public static <T> InternalResponse checkBasicFieldT(BasicFieldAttribute<T> object, String transactionId) {
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
    
    //<editor-fold defaultstate="collapsed" desc="Check Field type based on String">
    /**
     * Dùng để kiểm tra dữ liệu xem là Field nhập vào có tồn tại parentType và
     * có đúng dạng đúng với trong hệ thống 
     * Use for check the textField of input is valid "textField". Check if it existed in DB
     *
     * @param field
     * @return
     */
    public static boolean checkField(String type, FieldTypeName parentType) {
        boolean check1 = Resources.getFieldTypes().containsKey(type);
        boolean check2 = false;
        if (check1) {
            check2 = Resources.getFieldTypes().get(type).getParentType().equals(parentType.getParentName());
        }
        return check2;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Field type based on Object">
    /**
     * Dùng để kiểm tra dữ liệu xem là Field nhập vào có tồn tại parentType đúng
     * với trong hệ thống Use for check the parentType of input "textField".
     * Check if it existed in DB
     *
     * @param field
     * @return
     */
    public static boolean checkField(BasicFieldAttribute field, FieldTypeName parentType) {
        boolean check1 = Resources.getFieldTypes().containsKey(field.getTypeName());
        boolean check2 = false;
        if (check1) {
            check2 = Resources.getFieldTypes().get(field.getTypeName()).getParentType().equals(parentType.getParentName());
        }
        return check2;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Add Initial Field">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) 
     * Check the mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public static InternalResponse checkAddInitialField(InitialsFieldAttribute object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }

        if (object.getPage() <= 0 && !object.isApplyToAll()) {
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

    //<editor-fold defaultstate="collapsed" desc="Check Initial Field based on InitialFieldAttribute">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...) 
     * Check the mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public static InternalResponse checkFillInitialField(InitialsFieldAttribute object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }

//        if (object.getPage() <= 0 && Utils.isNullOrEmpty(object.getPages()) && !object.isApplyToAll()) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD,
//                            A_FPSConstant.SUBCODE_MISSING_PAGE
//            );
//        }
//        if (Utils.isNullOrEmpty(object.getImage())) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD_INITIAL,
//                            A_FPSConstant.SUBCODE_MISSING_OR_EMPTY_IMAGE_OF_INITIAL
//            );
//        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Initial Field based on ProcessInitialField">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...)
     * Check the mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public static InternalResponse checkFillInitialField(ProcessInitialField object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }

//        if (object.getPage() <= 0 && Utils.isNullOrEmpty(object.getPages()) && !object.isApplyToAll()) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD,
//                            A_FPSConstant.SUBCODE_MISSING_PAGE
//            );
//        }
//        if (Utils.isNullOrEmpty(object.getImage())) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD_INITIAL,
//                            A_FPSConstant.SUBCODE_MISSING_OR_EMPTY_IMAGE_OF_INITIAL
//            );
//        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check File Field based on ProcessFileField">
    /**
     * Kiểm tra dữ liệu bắt buộc phải có của field (name, ...)
     * Check the mandatory variables in field.
     *
     * @param object
     * @param transactionId
     * @return
     */
    public static InternalResponse checkFillField(ProcessFileField object, String transactionId) {
        if (object.getFieldName() == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }

//        if (object.getPage() <= 0 && Utils.isNullOrEmpty(object.getPages()) && !object.isApplyToAll()) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD,
//                            A_FPSConstant.SUBCODE_MISSING_PAGE
//            );
//        }
//        if (Utils.isNullOrEmpty(object.getImage())) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD_INITIAL,
//                            A_FPSConstant.SUBCODE_MISSING_OR_EMPTY_IMAGE_OF_INITIAL
//            );
//        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                object
        );
    }
    //</editor-fold>
}
