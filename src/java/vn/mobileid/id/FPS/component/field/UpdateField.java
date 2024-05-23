/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.RotateDegree;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateField {

    //<editor-fold defaultstate="collapsed" desc="updateValueOfField">
    /**
     * Cập nhật lại giá trị của field (tức giá trị người dùng truyền lên) sau
     * khi đã xử lý field xong
     *
     * @param documentFieldId
     * @param user
     * @param dataAfterProcessing
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse updateValueOfField(
            long documentFieldId,
            User user,
            String dataAfterProcessing,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().updateValuesOfField(
                documentFieldId,
                user,
                dataAfterProcessing,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }        
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Update Hash of the Field">
    /**
     * Cập nhật lại giá trị hash của field (tức giá trị người dùng truyền lên) 
     *
     * @param documentFieldId
     * @param user
     * @param dataAfterProcessing
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse updateHashOfField(
            long documentFieldId,
            User user,
            String hash,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().updateHashOfField(
                documentFieldId,
                user,
                hash,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Update the Field Detail">
    public static InternalResponse updateFieldDetails(
            long documentFieldId,
            User user,
            Object value,
            String hmac,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = null;
        if (value instanceof String) {
            response = DatabaseFactory.getDatabaseImpl_field().updateFieldDetails(
                    documentFieldId,
                    0,
                    (String)value,
                    hmac,
                    user.getAzp(),
                    transactionId);
        } else {
            response = DatabaseFactory.getDatabaseImpl_field().updateFieldDetails(
                    documentFieldId,
                    0,
                    MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    ).writeValueAsString(value),
                    hmac,
                    user.getAzp(),
                    transactionId);
        }
        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update the Field">
    /**
     * Cập nhật thông tin của Field
     * Update the new data of the Field
     * @param documentFieldId
     * @param type
     * @param name
     * @param value
     * @param hash
     * @param pageNumber
     * @param width
     * @param height
     * @param top
     * @param left
     * @param visible
     * @param hmac
     * @param modifiedBy
     * @param transactionId
     * @param args
     * @throws Exception 
     */
    public static InternalResponse updateField(
            long documentFieldId,
            int type,
            String name, 
            String value,
            String hash, 
            int pageNumber, 
            float width, 
            float height, 
            float top,
            float left,
            RotateDegree rotate,
            boolean visible,
            String hmac,
            String modifiedBy,
            String transactionId
    )throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().updateField(
                documentFieldId,
                type, 
                name, 
                value, 
                hash, 
                pageNumber, 
                width,
                height, 
                top, 
                left,
                rotate,
                visible,
                hmac, 
                modifiedBy, 
                transactionId);
        if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL, 
                            response.getStatus()
            );
        }
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception {
        //Update the Field
        InternalResponse response = UpdateField.updateField(
                21, 
                0,
                null,
                "value", 
                null,
                0, 
                2000, 
                0, 
                0, 
                0, 
                null,
                true,
                "hmac",
                "GIATK",
                "transactionId");
        System.out.println("Status:"+response.getStatus());
    }
}
