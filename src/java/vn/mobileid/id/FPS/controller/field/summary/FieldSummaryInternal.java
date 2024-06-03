/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.field.summary;

import vn.mobileid.id.FPS.controller.field.summary.micro.GetField;
import vn.mobileid.id.FPS.controller.field.summary.micro.UpdateField;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;

/**
 *
 * @author GiaTK
 */
public class FieldSummaryInternal {
    public static InternalResponse getField(
            long documentId,
            String fieldName,
            String transactionId
    )throws Exception{
        return GetField.getFieldData(documentId, fieldName, transactionId);
    }
    
    public static InternalResponse updateValueOfField(
            long documentFieldId,
            User user,
            String dataAfterProcessing,
            String transactionId
    )throws Exception{
        return UpdateField.updateValueOfField(documentFieldId, user, dataAfterProcessing, transactionId);
    }
    
    public static InternalResponse updateFieldDetail(
            long documentFieldId,
            User user,
            Object value,
            String hmac,
            String transactionId
    )throws Exception{
        return UpdateField.updateFieldDetails(
                documentFieldId, 
                user,
                value,
                hmac,
                transactionId);
    }

    public static InternalResponse updateFieldDetail(
            long documentFieldId,
            User user,
            String value,
            String hmac,
            String transactionId
    )throws Exception{
        return UpdateField.updateFieldDetails(
                documentFieldId, 
                user,
                value,
                hmac,
                transactionId);
    }
}
