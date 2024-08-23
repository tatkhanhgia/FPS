/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.CatchException;
import vn.mobileid.id.FPS.controller.field.FieldController;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.controller.document.summary.DocumentSummary;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class DocumentController extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new DocumentController().doGet(req, res);
                break;
            }
            case "POST": {
                new DocumentController().doPost(req, res);
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

        //<editor-fold defaultstate="collapsed" desc="Download Document">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Download Document", ""));
            long packageId = 0;
            try {
                packageId = Utils.getIdFromURL(req.getRequestURI());
                InternalResponse response = DocumentSummary.downloadDocument(req, packageId, transactionId);

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

                Utils.createAPILog(
                        req,
                        "",
                        (int) packageId,
                        response,
                        response.getException(),
                        transactionId);

            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download Document Remove Appearance">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/remove_appearance$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Download Document Remove Appearance", ""));
            long packageId = 0;
            try {
                packageId = Utils.getIdFromURL(req.getRequestURI());
                InternalResponse response = DocumentSummary.downloadRemoveAppearanceDocument(req, packageId, transactionId);

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

                Utils.createAPILog(
                        req,
                        "",
                        (int) packageId,
                        response,
                        response.getException(),
                        transactionId);

            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Download Document Base64">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/base64$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Download Document", ""));
            long packageId = 0;
            try {
                packageId = Utils.getIdFromURL(req.getRequestURI());
                InternalResponse response = DocumentSummary.downloadDocumentBase64(req, packageId, transactionId);

                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
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

                Utils.createAPILog(
                        req,
                        "",
                        (int) packageId,
                        response,
                        response.getException(),
                        transactionId);
            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download Document Remove Appearance Base64">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/base64/remove_appearance$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Download Document Remove Appearance", ""));
            long packageId = 0;
            try {
                packageId = Utils.getIdFromURL(req.getRequestURI());
                InternalResponse response = DocumentSummary.downloadDocumentRemoveApperanceBase64(req, packageId, transactionId);

                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
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

                Utils.createAPILog(
                        req,
                        "",
                        (int) packageId,
                        response,
                        response.getException(),
                        transactionId);
            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Get Document Detail">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/details$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Document Details", ""));
            long packageId = 0;
            try {
                packageId = Utils.getIdFromURL(req.getRequestURI());
                InternalResponse response = DocumentSummary.getDocuments(req, packageId, transactionId);

                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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
                        "",
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
            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Document Verification">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/verification$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Document Verification", ""));
            long packageId = 0;
            try {
                packageId = Utils.getIdFromURL(req.getRequestURI());
                InternalResponse response = DocumentSummary.getDocumentVerification(req, packageId, transactionId);

                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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
                        "",
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
            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Document Image">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/images/[0-9]+$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Document Image", ""));
            long packageId = 0;
            try {
                String[] tokens = req.getRequestURI().split("/");
                packageId = Long.parseLong(tokens[4]);
                int page = Integer.parseInt(tokens[6]);

                InternalResponse response = DocumentSummary.getDocumentImage(req, packageId, page, transactionId);

                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            "application/octet-stream",
                            response,
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

                Utils.createAPILog(
                        req,
                        "",
                        (int) packageId,
                        response,
                        response.getException(),
                        transactionId);

            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Document Image Base64">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/images/[0-9]+/base64$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Document Image", ""));
            long packageId = 0;
            try {
                String[] tokens = req.getRequestURI().split("/");
                packageId = Long.parseLong(tokens[4]);
                int page = Integer.parseInt(tokens[6]);

                InternalResponse response = DocumentSummary.getDocumentImageBase64(req, packageId, page, transactionId);

                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    String message = ResponseMessageController.getErrorMessageAdvanced(
                            response.getCode(),
                            response.getCodeDescription(),
                            response.getMessage(),
                            language,
                            transactionId);
                    response.setMessage(message);
                    Utils.createAPILog(req, "", (int) packageId, response, response.getException(), transactionId);
                } else {
                    Utils.createAPILog(req, "", (int) packageId, response, response.getException(), transactionId);
                }

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage(),
                        transactionId);

            } catch (Exception ex) {
                CatchException.catchException(ex, req, res, "", (int) packageId, transactionId);
            }
            return;
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

        //<editor-fold defaultstate="collapsed" desc="Pre Check Document">
        if (req.getRequestURI().matches("^/fps/v1/documents/pre$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Pre check Document", ""));
            try {
                InternalResponse response = DocumentSummary.pre(req, req.getContentType(), transactionId);

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
                        "",
                        0,
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
                CatchException.catchException(ex, req, res, "", 0, transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Upload Document Base64">
        if (req.getRequestURI().matches("^/fps/v1/documents/base64$")) {
            String transactionId = Utils.getTransactionId(req, null);
            String payload = Utils.getPayload(req);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "upload Document", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = DocumentSummary.uploadDocumentBase64(req, payload, transactionId);

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
                            0,
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
                    CatchException.catchException(ex, req, res, payload, 0, transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "application/json",
                        null,
                        transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Upload Document">
        if (req.getRequestURI().matches("^/fps/v1/documents$")) {
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "upload Document", ""));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/octet-stream")) {
                try {
                    InternalResponse response = DocumentSummary.uploadDocument(req, transactionId);

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
                            "",
                            0,
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
                    CatchException.catchException(ex, req, res, "", 0, transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "application/json",
                        null,
                        transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Synchorized">
        if (req.getRequestURI().matches("^/fps/v1/synchronize")) {
            String payload = Utils.getPayload(req);
            String transactionId = Utils.getTransactionId(req, payload);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "upload Document", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = DocumentSummary.synchronizedUUID(req, payload, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS && response.getStatus() != A_FPSConstant.HTTP_CODE_CREATED) {
                        String message = ResponseMessageController.getErrorMessageAdvanced(
                                response.getCode(),
                                response.getCodeDescription(),
                                response.getMessage(),
                                language,
                                transactionId);
                        response.setMessage(message);
                    }

                    Utils.createAPILog(req, payload, 0, response, response.getException(), transactionId);

                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage(),
                            transactionId);
                } catch (Exception ex) {
                    CatchException.catchException(ex, req, res, payload, 0, transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "application/json",
                        null,
                        transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Hash of Signature Field">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/hash$")) {
            String payload = Utils.getPayload(req);
            long packageId = Utils.getIdFromURL(req.getRequestURI());
            String transactionId = Utils.getTransactionId(req, payload);
            LogHandler.getInstance().request(
                    FieldController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Signature Hash", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = DocumentSummary.getHashOfSignatureField(req, payload, packageId, transactionId);

                    Long temp = packageId;

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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
                            temp.intValue(),
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
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Sign Document">
        if (req.getRequestURI().matches("^/fps/v1/documents/[0-9]+/sign$")) {
            String payload = Utils.getPayload(req);
            long packageId = Utils.getIdFromURL(req.getRequestURI());
            String transactionId = Utils.getTransactionId(req, null);
            LogHandler.getInstance().request(
                    DocumentController.class,
                    Utils.getDataRequestToLog(req, transactionId, "sign Document", ""));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = DocumentSummary.signDocument(req, payload, packageId, transactionId);

                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
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
    }

    //==========================================================================
}
