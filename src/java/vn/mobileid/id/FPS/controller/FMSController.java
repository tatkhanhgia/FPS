/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class FMSController extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new FMSController().doGet(req, res);
                break;
            }
            case "POST": {
                new FMSController().doPost(req, res);
                break;
            }
            case "DELETE": {
                new FMSController().doDelete(req, res);
                break;
            }
            default: {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        "",
                        "");
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utils.getRequestHeader(req, "x-language-name");

        try {
            if (req.getRequestURI().contains("download")) {
                String transactionId = Utils.getTransactionId(req, null);
                LogHandler.request(
                        AuthorizeController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Download from FMS", ""));

                InternalResponse response = FMS.download(req, transactionId);

                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/octet-stream",
                            response.getData(),
                            transactionId);
                } else {
                    String message = ResponseMessageController.getErrorMessageAdvanced(
                            response.getCode(),
                            response.getCodeDescription(),
                            response.getMessage(),
                            language,
                            transactionId);
                    response.setMessage(message);
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null,
                        "none");
            }
        } catch (Exception ex) {
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    null,
                    "none");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utils.getRequestHeader(req, "x-language-name");
        
        try {
            if (req.getRequestURI().contains("upload")) {
                String transactionId = Utils.getTransactionId(req, null);
                LogHandler.request(
                        AuthorizeController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Upload FMS", ""));

                InternalResponse response = FMS.upload(req, transactionId);

                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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
            } else {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null,
                        "none");
            }
        } catch (Exception ex) {
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    null,
                    "none");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utils.getRequestHeader(req, "x-language-name");
        
        try {
            if (req.getRequestURI().contains("delete")) {
                String transactionId = Utils.getTransactionId(req, null);
                LogHandler.request(
                        AuthorizeController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Delete in FMS", ""));

                InternalResponse response = FMS.delete(req, transactionId);

                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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
            } else {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null,
                        "");
            }
        } catch (Exception ex) {
            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    null,
                    "");
        }
    }
}
