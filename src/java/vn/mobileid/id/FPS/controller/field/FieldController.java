/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.field;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.controller.CatchException;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.controller.document.summary.DocumentSummary;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummary;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.InternalResponse.InternalData;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Utils;

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
            LogHandler.getInstance().request(
                    FieldController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Fields", payload));
            try {
                InternalResponse response = FieldSummary.getFields(req, transactionId);

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
                CatchException.catchException(ex, req, res, payload, (int) Utils.getIdFromURL(req.getRequestURI()), transactionId);
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
            if (req.getRequestURI().matches("^/fps/v[12]/documents/[0-9]+/fields.*$") && !req.getRequestURI().matches("^/fps/v[12]/documents/[0-9]+/fields/hash$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.getInstance().request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Add Field", payload));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                    try {
                        InternalResponse response = FieldSummary.addField(req, payload, transactionId);

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
                        CatchException.catchException(ex, req, res, payload, (int) packageId, transactionId);
                        return;
                    }
                } else {
                    Utils.sendMessage(
                            res,
                            A_FPSConstant.HTTP_CODE_UNSUPPORTED_MEDIA_TYPE,
                            "application/json",
                            null,
                            transactionId);
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Fill Initials Field Version1">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/initial$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.getInstance().request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = DocumentSummary.fillInitialField_V1(req, packageId, payload, transactionId);

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
                LogHandler.getInstance().request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = DocumentSummary.fillInitialField_V2(req, packageId, payload, transactionId);

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
                LogHandler.getInstance().request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Stamp Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = DocumentSummary
                            .fillFileField_V2(req, packageId, payload, transactionId);

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

            //<editor-fold defaultstate="collapsed" desc="Fill QR Qrypto Field">
            if (req.getRequestURI().matches("^/fps/(v1|v2)/documents/[0-9]+/qrcode-qrypto$")) {
                String transactionId = Utils.getTransactionId(req, payload);
                long packageId = Utils.getIdFromURL(req.getRequestURI());
                LogHandler.getInstance().request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {

                    InternalResponse response = DocumentSummary.createQRQrypto(req, packageId, payload, transactionId);

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
            CatchException.catchException(
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
            if (req.getRequestURI().matches("^/fps/v(1|2)/documents/[0-9]+/fields/.*$") && !req.getRequestURI().matches("^/fps/v(1|2)/documents/[0-9]+/fields/hash$")) {
                String transactionId = Utils.getTransactionId(req, payload);

                try {
                    LogHandler.getInstance().request(
                            FieldController.class,
                            Utils.getDataRequestToLog(req, transactionId, "update Field", ""));
                    if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                        InternalResponse response = FieldSummary.updateField(req, payload, transactionId);

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
                    CatchException.catchException(ex, req, res, payload, 0, transactionId);
                }
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Fill Form Field">
            if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/fields$")) {
                System.out.println("Payload:"+payload);
                String transactionId = Utils.getTransactionId(req, payload);
                LogHandler.getInstance().request(
                        FieldController.class,
                        Utils.getDataRequestToLog(req, transactionId, "Fill Form Field", ""));
                if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                    InternalResponse response = DocumentSummary.fillFormField_V1(req, payload, transactionId);

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
            CatchException.catchException(
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
            LogHandler.getInstance().request(
                    FieldController.class,
                    Utils.getDataRequestToLog(req, transactionId, "delete Field", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = FieldSummary.deleteFormField(req, packageId, payload, transactionId);

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
                    CatchException.catchException(ex, req, res, payload, (int) packageId, transactionId);
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
}
