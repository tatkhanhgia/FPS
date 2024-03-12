/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import fps_core.objects.ExtendedFieldAttribute;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */ 
public class GetField{
    /**
     * Get về toàn bộ giá trị của field + field detail dựa trên documentId và fieldName.
     * Get all information both field and field detail that belongs to documentId and FieldName in DB
     * @param documentId
     * @param fieldName
     * @param transactionId
     * @return fieldData ở định dạng ExtendedFieldAttribute
     */
    public  static InternalResponse getFieldData(
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
     * Get về toàn bộ giá trị của field + field detail dựa trên documentFieldId.Get all information both field and field detail that belongs to DocumentFieldId in DB
     * @param documentFieldId
     * @param transactionId
     * @return fieldData ở định dạng ExtendedFieldAttribute
     */
    public  static InternalResponse getFieldData(
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
     * Get về toàn bộ giá trị của field + field detail thuộc về DocumentId không dựa trên gì hết
     * Get all data of field (ExtendedField) of the Document Id
     * @param documentId
     * @param transactionId
     * @return fieldData ở định dạng ExtendedFieldAttribute
     */
    public  static InternalResponse getFieldsData(
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
