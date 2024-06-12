/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.enterprise.summary.module;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.enterprise.summary.EnterpriseSummary;
import vn.mobileid.id.FPS.controller.enterprise.summary.micro.GetAPIKeyRule;
import vn.mobileid.id.FPS.controller.enterprise.summary.micro.GetKEYAPI;
import vn.mobileid.id.FPS.enumeration.Rule;
import vn.mobileid.id.FPS.object.APIKeyRule;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;

/**
 *
 * @author GiaTK
 */
public class GetAPIKeyRuleOfEnterprise {

    public GetAPIKeyRuleOfEnterprise() { 
    }

    //<editor-fold defaultstate="collapsed" desc="Get API Key Rule of ClientID">
    /**
     * Get APIKeyRule of the ClientId 
     * @param clientId
     * @param transactionId
     * @return InternalResponse with APIKeyRule as an Object (have Enterprise too)
     */
    public InternalResponse getAPIKeyRule(String clientId, String transactionId) {
        try {
            InternalResponse response = GetKEYAPI.getKEYAPI(clientId, "transaction");
            Enterprise enterprise = null;

            if (response.isValid()) {
                enterprise = response.getEnt();
                response = GetAPIKeyRule.getAPIKeyRule(
                        enterprise.getApiKeyRule(),
                        "transactionID");

                return response.setEnt(enterprise);
            }
            return response;
        } catch (Exception ex) {
            return new InternalResponse(A_FPSConstant.HTTP_CODE_BAD_REQUEST,"");
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get API Key Rule of ClientID">
    /**
     * Get APIKeyRule of the ClientId 
     * @param user
     * @param transactionId
     * @return InternalResponse with APIKeyRule as an Object (have Enterprise too)
     */
    public InternalResponse getAPIKeyRule(User user, String transactionId) {
        return getAPIKeyRule(user.getScope(), transactionId);
    }
    //</editor-fold>

    public static void main(String[] args) { 
        InternalResponse response = new GetAPIKeyRuleOfEnterprise().getAPIKeyRule("MID_MobileApp", "");
        System.out.println("Status:"+response.getStatus());
        APIKeyRule rule = (APIKeyRule)response.getData();
        System.out.println(rule.isRuleEnabled(Rule.QRYPTO_DATE_FORMAT));
        System.out.println(rule.getRule(Rule.QRYPTO_DATE_FORMAT).getData());
    }
}
