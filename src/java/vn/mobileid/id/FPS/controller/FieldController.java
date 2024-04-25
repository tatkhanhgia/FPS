/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import vn.mobileid.id.FPS.component.document.ConnectorDocument;
import vn.mobileid.id.FPS.component.field.ConnectorField;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class FieldController extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new FieldController().doGet(req, res);
                break;
            }
            case "POST": {
                new FieldController().doPost(req, res);
                break;
            }
            case "PUT": {
                new FieldController().doPut(req, res);
                break;
            }
            case "DELETE": {
                new FieldController().doDelete(req, res);
                break;
            }
            default: {
                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                res.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utils.getRequestHeader(req, "x-language-name");

        //<editor-fold defaultstate="collapsed" desc="Get Fields">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields$")) {
            String transactionId = Utils.getTransactionId(req, null);
            String payload = Utils.getPayload(req);
            LogHandler.request(
                    FieldController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Fields", payload));
            try {
                InternalResponse response = ConnectorField.getFields(req, transactionId);

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
            } catch (Exception ex) {
                catchException(ex, req, res, payload, (int) Utils.getIdFromURL(req.getRequestURI()), transactionId);
            }
        } //</editor-fold>
        else {
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
        String language = Utils.getRequestHeader(req, "x-language-name");
        String payload = Utils.getPayload(req);
        try {
            //<editor-fold defaultstate="collapsed" desc="Add field">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields.*$") && !req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields/hash$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Add Field", payload));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                    try {
                        InternalResponse response = ConnectorField.addField(req, payload, transactionId);

                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS && response.getStatus() != A_FPSConstant.HTTP_CODE_CREATED) {
                            String message = ResponseMessageController.getErrorMessageAdvanced(
                                    response.getCode(),
                                    response.getCodeDescription(),
                                    response.getMessage(),
                                    language,
                                    transactionId);
                            response.setMessage(message);
                        }

                        Utils.createAPILog(
                                req,
                                payload,
                                (int) packageId,
                                response,
                                response.getException(),
                                transactionId);

                        Utils.sendMessage(
                                res,
                                response.getStatus(),
                                "application/json",
                                response.getMessage(),
                                transactionId);
                        return;
                    } catch (Exception ex) {
                        catchException(ex, req, res, payload, (int) packageId, transactionId);
                        return;
                    }
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Fill Initials Field Version1">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/initial$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = ConnectorDocument.fillInitialField_V1(req, packageId, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        String message = ResponseMessageController.getErrorMessageAdvanced(
                                response.getCode(),
                                response.getCodeDescription(),
                                response.getMessage(),
                                language,
                                transactionId);
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req,
                            payload,
                            (int) packageId,
                            response,
                            response.getException(),
                            transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                } else {
                    Utils.sendMessage(
                            res,
                            HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                            "application/json",
                            null,
                            transactionId);
                }
                return;
            } //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Fill Initials Field Version2">
            if (req.getRequestURI().matches("^/fps/v2/documents/[0-9]+/initial$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = ConnectorDocument.fillInitialField_V2(req, packageId, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        String message = "";
                        if (response.getInternalData() != null) {
                            message = ResponseMessageController.createErrorMessage((List<InternalData>) response.getInternalData().getValue());
                        } else {
                            message = ResponseMessageController.getErrorMessageAdvanced(
                                    response.getCode(),
                                    response.getCodeDescription(),
                                    response.getMessage(),
                                    language,
                                    transactionId);
                        }
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req,
                            payload,
                            (int) packageId,
                            response,
                            response.getException(),
                            transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                } else {
                    Utils.sendMessage(
                            res,
                            HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                            "application/json",
                            null,
                            transactionId);
                }
                return;
            } //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Fill Stamp Field Version2">
            if (req.getRequestURI().matches("^/fps/v2/documents/[0-9]+/stamp$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Stamp Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = ConnectorDocument
                            .fillFileField_V2(req, packageId, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        String message = ResponseMessageController.getErrorMessageAdvanced(
                                response.getCode(),
                                response.getCodeDescription(),
                                response.getMessage(),
                                language,
                                transactionId);
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req,
                            payload,
                            (int) packageId,
                            response,
                            response.getException(),
                            transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                } else {
                    Utils.sendMessage(
                            res,
                            HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                            "application/json",
                            null,
                            transactionId);
                }
                return;
            } //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="Fill QR Qrypto Field">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/qrcode-qrypto$")) {
                System.out.println("Hello");
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = ConnectorDocument.createQRQrypto(req, packageId, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        String message = ResponseMessageController.getErrorMessageAdvanced(
                                response.getCode(),
                                response.getCodeDescription(),
                                response.getMessage(),
                                language,
                                transactionId);
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req,
                            payload,
                            (int) packageId,
                            response,
                            response.getException(),
                            transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                } else {
                    Utils.sendMessage(
                            res,
                            HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                            "application/json",
                            null,
                            transactionId);
                }
                return;
            } //</editor-fold>
            else {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null,
                        "");
            }
        } catch (Exception ex) {
            catchException(
                    ex,
                    req,
                    res,
                    payload,
                    (int) Utils.getIdFromURL(req.getRequestURI()),
                    language);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utils.getRequestHeader(req, "x-language-name");
        String payload = Utils.getPayload(req);

        try {
            //<editor-fold defaultstate="collapsed" desc="Update Field">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields/.*$") && !req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields/hash$")) {
                String transactionId = Utils.getTransactionId(req, payload);

                try {
                    LogHandler.request(
                            FieldController.class,
                            Utils.getDataRequestToLog(req, transactionId, "update Field", ""));
                    if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                        InternalResponse response = ConnectorField.updateField(req, payload, transactionId);

                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            String message = ResponseMessageController.getErrorMessageAdvanced(
                                    response.getCode(),
                                    response.getCodeDescription(),
                                    response.getMessage(),
                                    language,
                                    transactionId);
                            response.setMessage(message);
                        }

                        Utils.createAPILog(req,
                                payload,
                                (int) Utils.getIdFromURL(req.getRequestURI()),
                                response,
                                response.getException(),
                                transactionId);

                        Utils.sendMessage(
                                res,
                                response.getStatus(),
                                "application/json",
                                response.getMessage(),
                                transactionId);
                    } else {
                        Utils.sendMessage(
                                res,
                                HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                                "application/json",
                                null,
                                "none");
                    }
                } catch (Exception ex) {
                    catchException(ex, req, res, payload, 0, transactionId);
                }
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Fill Form Field">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                LogHandler.request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                    InternalResponse response = ConnectorDocument.fillFormField_V1(req, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        String message = "";
                        if (response.getInternalData() != null) {
                            message = ResponseMessageController.createErrorMessage((List<InternalData>) response.getInternalData().getValue());
                        } else {
                            message = ResponseMessageController.getErrorMessageAdvanced(
                                    response.getCode(),
                                    response.getCodeDescription(),
                                    response.getMessage(),
                                    language,
                                    transactionId);
                        }
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req,
                            payload,
                            (int) Utils.getIdFromURL(req.getRequestURI()),
                            response,
                            response.getException(),
                            transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);

                    return;
                } else {
                    Utils.sendMessage(
                            res,
                            HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                            "application/json",
                            null,
                            "");
                }
                //</editor-fold>

            } else {
                Utils.sendMessage(
                        res,
                        A_FPSConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null,
                        "");

            }

        } catch (Exception ex) {
            catchException(
                    ex,
                    req,
                    res,
                    payload,
                    (int) Utils.getIdFromURL(req.getRequestURI()),
                    language);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utils.getRequestHeader(req, "x-language-name");
        String payload = Utils.getPayload(req);

        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields$")) {
            String transactionId = Utils.getTransactionId(req, payload);
            long packageId = Utils.getIdFromURL(req.getRequestURI());
            LogHandler.request(
                    FieldController.class,
                    Utils.getDataRequestToLog(req, transactionId, "delete Field", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = ConnectorField.deleteFormField(req, packageId, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        String message = ResponseMessageController.getErrorMessageAdvanced(
                                response.getCode(),
                                response.getCodeDescription(),
                                response.getMessage(),
                                language,
                                transactionId);
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req,
                            payload,
                            (int) Utils.getIdFromURL(req.getRequestURI()),
                            response,
                            response.getException(),
                            transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                } catch (Exception ex) {
                    catchException(ex, req, res, payload, (int) packageId, transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "application/json",
                        null,
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
    }
    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Catch Exception">

    private static void catchException(
            Exception ex,
            HttpServletRequest req,
            HttpServletResponse res,
            String payload,
            int documentId,
            String transactionId) {
        try {
            User user = Utils.getUserFromBearerToken(req.getHeader("Authorization"));

            InternalResponse response = new InternalResponse();
            response.setUser(user);
            response.setMessage("INTERNAL EXCEPTION");
            response.setException(ex);

            LogHandler
                    .error(
                            FieldController.class,
                            transactionId,
                            ex);

            Utils.sendMessage(
                    res,
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    "application/json",
                    A_FPSConstant.INTERNAL_EXP_MESS,
                    transactionId);

            Utils.createAPILog(req, payload, documentId, response, response.getException(), transactionId);

        } catch (IOException ex1) {
            Logger.getLogger(DocumentController.class
                    .getName()).log(Level.SEVERE, null, ex1);
        }
    }
    //</editor-fold>
}
