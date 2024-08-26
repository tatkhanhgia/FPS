/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.enterprise.summary.micro;

import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.APIKeyRule;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class GetAPIKeyRule {
    //<editor-fold defaultstate="collapsed" desc="Get API Key Rule">
    /**
     * Trả về thông tin của API Key Rule - Return the rule of API Key
     * @param apiKeyType
     * @param transactionId
     * @return InternalResponse with APIKeyRule as an Object
     * @throws Exception 
     */
    public static InternalResponse getAPIKeyRule(
            long apiKeyType,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_enterprise().getRule(apiKeyType, transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, response.getObject());
    }
    //</editor-fold>
    
}
