/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.enterprise.summary;

import vn.mobileid.id.FPS.controller.enterprise.summary.micro.GetAPIKeyRule;
import vn.mobileid.id.FPS.controller.enterprise.summary.module.GetAPIKeyRuleOfEnterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;

/**
 *
 * @author GiaTK
 */
public class EnterpriseSummaryInternal {
    public EnterpriseSummaryInternal(){}
    
    //<editor-fold defaultstate="collapsed" desc="Get APIKeyRule of the User">
    /**
     * Get APIKeyRule of the User
     * @param user
     * @param transactionId
     * @return InternalResponse with APIKeyRule as an Object (have Enterprise too)
     */
    public InternalResponse getAPIKeyRuleOfUser(User user, String transactionId){
        return new GetAPIKeyRuleOfEnterprise().getAPIKeyRule(user, transactionId);
    }
    //</editor-fold>
}
