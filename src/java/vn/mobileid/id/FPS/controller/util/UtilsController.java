/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.controller.CatchException;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummary;
import vn.mobileid.id.FPS.controller.util.summary.UtilsSummary;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class UtilsController extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new UtilsController().doGet(req, res);
                break;
            }
            case "POST": {
                new UtilsController().doPost(req, res);
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
        //<editor-fold defaultstate="collapsed" desc="Get Field Type">
        if (req.getRequestURI().matches("^/fps/v1/documents/fields$")) {
            String transactionId = Utils.getTransactionId(req, null);
            String payload = Utils.getPayload(req);
            LogHandler.getInstance().request(
                    UtilsController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get Field Type",  payload));
            try {
                InternalResponse response = FieldSummary.getFieldType(req, transactionId);
                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage(),
                        transactionId);
            } catch (Exception ex) {
                CatchException.catchException(
                        ex,
                        req,
                        res,
                        payload,
                        0,
                        transactionId);
            }
            return;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get System Parameter">
        if (req.getRequestURI().matches("^/admin/system/configuration$")) {
            String transactionId = Utils.getTransactionId(req, null);
            String payload = Utils.getPayload(req);
            LogHandler.getInstance().request(
                    UtilsController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Get System Configuration", payload));
            try {
                InternalResponse response = new UtilsSummary().getSystemConfiguration(req, transactionId);
                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage(),
                        transactionId);
            } catch (Exception ex) {
                CatchException.catchException(
                        ex,
                        req,
                        res,
                        payload,
                        0,
                        transactionId);
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
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //<editor-fold defaultstate="collapsed" desc="Reload Resources">
        if (req.getRequestURI().matches("^/fps/resources/reload*$")) {
            String transactionId = Utils.getTransactionId(req, null);
            String payload = Utils.getPayload(req);
            LogHandler.getInstance().request(
                    UtilsController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Reload Resources", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = new UtilsSummary().reloadResources(req, transactionId);

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
                    CatchException.catchException(
                            ex,
                            req,
                            res,
                            payload,
                            (int) Utils.getIdFromURL(req.getRequestURI()),
                            transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "application/json",
                        null,
                        "none");
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Reload Thread Management">
        if (req.getRequestURI().matches("^/fps/threads/reload*$")) {
            String transactionId = Utils.getTransactionId(req, null);
            String payload = Utils.getPayload(req);
            LogHandler.getInstance().request(
                    UtilsController.class,
                    Utils.getDataRequestToLog(req, transactionId, "Reload Thread Management", payload));
            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("application/json")) {
                try {
                    InternalResponse response = new UtilsSummary().reloadThreadManagement(req, transactionId);

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
                    CatchException.catchException(
                            ex,
                            req,
                            res,
                            payload,
                            (int) Utils.getIdFromURL(req.getRequestURI()),
                            transactionId);
                }
            } else {
                Utils.sendMessage(
                        res,
                        HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "application/json",
                        null,
                        "none");
            }
            //</editor-fold>

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
