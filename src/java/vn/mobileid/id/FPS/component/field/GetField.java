/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.fieldAttribute.ExtendedFieldAttribute;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Broadcast;

/**
 *
 * @author GiaTK
 */ 
public class GetField extends Broadcast{
    /**
     * Get về toàn bộ giá trị của field + field detail dựa trên documentId và fieldName. Get all information both field and field detail in DB
     * @param documentId
     * @param transactionId
     * @return fieldData ở định dạng ExtendedFieldAttribute
     */
    protected static InternalResponse getFieldData(
            long documentId,
            String fieldName,
            String transactionId
    ) throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().getFieldData(
                documentId,
                fieldName,
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
                response.getObject()
        );
    }
    
    /**
     * Get về toàn bộ giá trị của field + field detail dựa trên documentId và documentFieldId. Get all information both field and field detail in DB
     * @param documentId
     * @param transactionId
     * @return fieldData ở định dạng ExtendedFieldAttribute
     */
    protected static InternalResponse getFieldData2(
            long documentFieldId,
            String transactionId
    ) throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().getFieldData(
                documentFieldId,
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
                response.getObject()
        );
    }
    
    /**
     * Get về toàn bộ giá trị của field + field detail không dựa trên gì hết
     * @param documentId
     * @param transactionId
     * @return fieldData ở định dạng ExtendedFieldAttribute
     */
    protected static InternalResponse getFieldsData(
            long documentId,            
            String transactionId
    ) throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_field().getFieldsData(documentId, transactionId);
        if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FAIL,                                
                                response.getStatus()
            );
        }        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    
    public static void main(String[] args) throws Exception{
        Resources.reloadFieldTypes();
        InternalResponse res = GetField.getFieldData(
                23, 
                "signature2",
                "transactionId");
        if(res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
            System.out.println("Message:"+res.getMessage());
        }else{
            ExtendedFieldAttribute field = (ExtendedFieldAttribute)res.getData();
            System.out.println("Data:"+field.getFieldName());
//            System.out.println(field.getDocumentFieldType());
            System.out.println(field.getType().getTypeName());
        }
        
        res = GetField.getFieldData(
                29, 
                "signature1",
                "transactionId");
        if(res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
            System.out.println("Message:"+res.getMessage());
        }else{
            ExtendedFieldAttribute field = (ExtendedFieldAttribute)res.getData();
            System.out.println("Data:"+field.getFieldName());
//            System.out.println(field.getDocumentFieldType());
            System.out.println(field.getType().getTypeName());
        }
        
    }
}
