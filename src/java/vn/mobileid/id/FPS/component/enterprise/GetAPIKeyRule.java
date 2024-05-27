/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.enterprise;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.APIKeyRule;
import vn.mobileid.id.FPS.object.Enterprise;
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
     * @param id
     * @param transactionId
     * @return Enterprise
     * @throws Exception 
     */
    protected static InternalResponse getAPIKeyRule(
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
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = GetAPIKeyRule.getAPIKeyRule(1, "");
        if(response.isValid()){
            APIKeyRule rule = (APIKeyRule) response.getData(); 
            System.out.println(rule.getAttributes().get(0).isEnabled());
        }
    }
}
