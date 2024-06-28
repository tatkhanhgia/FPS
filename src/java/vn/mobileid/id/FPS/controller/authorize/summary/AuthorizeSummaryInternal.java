/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.authorize.summary;

import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class AuthorizeSummaryInternal {
    //<editor-fold defaultstate="collapsed" desc="Verify Token">
    /**
     * Verify Token
     * @param request
     * @param transactionId
     * @return InternalResponse with User as an Object
     * @throws Exception 
     */
    public static InternalResponse verifyAuthorizationToken(
            HttpServletRequest request,
            String transactionId) throws Exception {
        String token = Utils.getRequestHeader(request, "Authorization");
        if (Utils.isNullOrEmpty(token)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_UNAUTHORIZED,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_MISSING_AUTHORIZATION_HEADER
            );
        }
        InternalResponse response = AuthorizeSummary.processVerify(token, transactionId);
        return response;
    }
    //</editor-fold>
}
