/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import vn.mobileid.id.FPS.controller.authorize.AuthorizeController;
import vn.mobileid.id.FPS.controller.document.DocumentController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CatchException {
    //<editor-fold defaultstate="collapsed" desc="Catch Exception">
    /**
     * Catch Exception in Controller
     * @param ex
     * @param req
     * @param res
     * @param payload
     * @param documentId
     * @param transactionId 
     */
    public static void catchException(
            Exception ex,
            HttpServletRequest req,
            HttpServletResponse res, 
            String payload,
            int documentId,
            String transactionId){
        try {
            User user = Utils.getUserFromBearerToken(req.getHeader("Authorization"));
            
            InternalResponse response = new InternalResponse();
            response.setUser(user);
            response.setMessage("INTERNAL EXCEPTION");
            response.setException(ex);
            
            LogHandler.getInstance().error(
                    AuthorizeController.class,
                    transactionId,
                    ex);
            
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    null,
                    transactionId);
            
            Utils.createAPILog(req, payload, documentId, response, response.getException(),transactionId);
        } catch (Exception ex1) {
            Logger.getLogger(DocumentController.class.getName()).log(Level.SEVERE, null, ex1);
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    null,
                    transactionId);
        }
    }
    //</editor-fold>
}
