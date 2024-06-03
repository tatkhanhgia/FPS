/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.authorize.summary;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
class Checker {
    //<editor-fold defaultstate="collapsed" desc="Check Login request">
    /**
     * Check login request 
     * @param payload
     * @return 
     */
    public static InternalResponse checkLoginRequest(String payload){
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND);
        }
        return new InternalResponse();
    }
    //</editor-fold>
}
