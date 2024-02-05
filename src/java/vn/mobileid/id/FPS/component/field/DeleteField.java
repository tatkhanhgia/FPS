/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Broadcast;

/**
 *
 * @author GiaTK
 */
public class DeleteField extends Broadcast{
    
    //<editor-fold defaultstate="collapsed" desc="Delete Field with DocumentId and FieldName">
    /**
     * Delete the field of document in DB. Defined documentId and fieldName
     * @param documentId
     * @param fieldName
     * @param transactionId
     * @throws Exception 
     */
    public static InternalResponse deleteField(
            long documentId,
            String fieldName,
            String transactionId
    )throws Exception{
        DatabaseResponse res = DatabaseFactory.getDatabaseImpl_field().deleteField(
                documentId,
                fieldName, 
                transactionId);
        
        if(res.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            res.getStatus()
            );
        }
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Delete field with documentFieldId">
    public static InternalResponse deleteField(
            long documentFieldId,
            String transactionId
    )throws Exception{
        DatabaseResponse res = DatabaseFactory.getDatabaseImpl_field().deleteField(documentFieldId, transactionId);
        
        if(res.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            res.getStatus()
            );
        }
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
//</editor-fold>
}
