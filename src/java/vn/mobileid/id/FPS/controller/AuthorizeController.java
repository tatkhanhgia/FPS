/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller;

import fps_core.enumration.Language;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import vn.mobileid.id.FPS.component.authorize.ConnectorAuthorize;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.Token;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.PropertiesSerializer;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class AuthorizeController extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new AuthorizeController().doGet(req, res);
                break;
            }
            case "POST": {
                new AuthorizeController().doPost(req, res);
                break;
            }
            default: {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        "",
                        "none");
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getRequestURI().contains("info")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.request(
                    AuthorizeController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Info", ""));
            Properties prop = Configuration.getInstance().getAppInfo();
            PropertiesSerializer serializer = new PropertiesSerializer(prop);
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    "application/json",
                    MyServices.getJsonService().writeValueAsString(serializer),
                    "none");
        } else {
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                    "application/json",
                    null,
                    "none");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String transactionId = "none";
        if (req.getRequestURI().contains("authenticate")) {
            String language = Utils.getRequestHeader(req, "x-language-name");
            String payload = null;
            try {
                payload = Utils.getPayload(req);
                payload = payload.replaceAll("\n", "");
                payload = payload.replaceAll("\t", "");
            } catch (IOException e) {
                String message = ResponseMessageController.getErrorMessageAdvanced(
                        A_FPSConstant.CODE_FAIL,
                        A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND,
                        "",
                        language == null ? 
                                Language.ENGLISH.getId():
                                language,
                        "none");
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        "application/json",
                        message,
                        "");
                return;
            }
            transactionId = Utils.getTransactionId(req, payload);
            LogHandler.request(
                    AuthorizeController.class,
                    Utils.getDataRequestToLog(
                            req, 
                            transactionId, 
                            "Client Credentials Authorization",
                            payload));
            try {
                InternalResponse response = ConnectorAuthorize.processLogin(req, payload, transactionId);
                
                try{
                    Token object = MyServices.getJsonService().readValue(payload, Token.class);
                    Enterprise ent = new Enterprise();
                    ent.setClientID(object.getClientId());
                    response.setEnt(ent);
                    Utils.createAPILog(
                        req, 
                        payload, 
                        0, 
                        response, 
                        response.getException(),
                        transactionId);
                    
                } catch(Exception ex){
                    System.err.println("Cannot create API Log in Login");
                }
                
                if(response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
                    String message = ResponseMessageController.getErrorMessageAdvanced(
                            response.getCode(),
                            response.getCodeDescription(),
                            response.getMessage(),
                            language,
                            transactionId);
                    response.setMessage(message);
                }
                
                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage(),
                        transactionId);
            } catch (Exception ex) {
                catchException(ex, req, res, payload, 0, transactionId);
            }
        } else {
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                    "application/json",
                    null,
                    transactionId);
        }
    }

   //===========================================================================
    //<editor-fold defaultstate="collapsed" desc="Catch Exception">
    private static void catchException(
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
            
            LogHandler.error(
                    AuthorizeController.class,
                    transactionId,
                    ex);
            
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    A_FPSConstant.INTERNAL_EXP_MESS,
                    transactionId);
            
            Utils.createAPILog(req, payload, documentId, response, response.getException(),transactionId);
        } catch (IOException ex1) {
            Logger.getLogger(DocumentController.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    //</editor-fold>
}
