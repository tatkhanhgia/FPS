/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary;

import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.authorize.summary.AuthorizeSummaryInternal;
import vn.mobileid.id.FPS.controller.util.summary.module.ReloadRAM;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;

/**
 *
 * @author GiaTK
 */
public class UtilsSummary {

    // <editor-fold defaultstate="collapsed" desc="Reload Resources">
    /**
     * Reload Resources in RAM
     * @param request
     * @param transactionId
     * @return InternalResponse 
     * @throws Exception 
     */
    public static InternalResponse reloadResources(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = AuthorizeSummaryInternal.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        ReloadRAM.reloadResources(transactionId);
        
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        ).setUser(user);
    }
    // </editor-fold>
}
