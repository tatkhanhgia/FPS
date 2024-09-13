/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.systemManagement;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author GiaTK
 * Manage all Log in System.
 * Note: can manage the log in FPS_Core too
 */
public class LogHandler {

    private static boolean showLog4jDebugLog;
    private static boolean showLog4jInfoLog;
    private static boolean showLog4jWarnLog;
    private static boolean showLog4jErrorLog;
    private static boolean showLog4jFatalLog;
    private static boolean showLog4jRequestLog;
    private static boolean showLog4jDatabaseLog;

    private static boolean isPrintStackTrace;

//    private static boolean configCaching;
    private static LogHandler instance;

    public static LogHandler getInstance() {
        if (instance == null) {
            instance = new LogHandler();
        }
        return instance;
    }

    private LogHandler() {
        readConfig();
    }

    private static void readConfig() {
        showLog4jDebugLog = Configuration.getInstance().isShowDebugLog();
        showLog4jInfoLog = Configuration.getInstance().isShowInfoLog();
        showLog4jWarnLog = Configuration.getInstance().isShowWarnLog();
        showLog4jErrorLog = Configuration.getInstance().isShowErrorLog();
        showLog4jRequestLog = Configuration.getInstance().isShowRequestLog();
        isPrintStackTrace = Configuration.getInstance().isShowPrintStacktrace();
        showLog4jDatabaseLog = Configuration.getInstance().isShowDatabaseLog();
    }

    public boolean isShowRequestLog() {
        return showLog4jRequestLog;
    }

    public boolean isShowDebugLog() {
        return showLog4jDebugLog;
    }

    public boolean isShowInfoLog() {
        return showLog4jInfoLog;
    }

    public boolean isShowWarnLog() {
        return showLog4jWarnLog;
    }

    public boolean isShowErrorLog() {
        return showLog4jErrorLog;
    }

    public boolean isShowFatalLog() {
        return showLog4jFatalLog;
    }

    public static boolean isPrintStackTrace() {
        return isPrintStackTrace;
    }

    public static boolean isShowDatabaseLog() {
        return showLog4jDatabaseLog;
    }

    //<editor-fold defaultstate="collapsed" desc="Using for log the request/response into file">
    /**
     * Using for log the request/response into file
     *
     * @param object defines the class
     * @param message
     */
    public void request(Class object, String message) {
        if (isShowRequestLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.log(Level.forName("REQUEST", 350), message);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the debug into file">
    /**
     * Using for log the debug into file
     *
     * @param object
     * @param message
     */
    public void debug(Class object, String message) {
        if (isShowDebugLog()) {
            Logger LOG = LogManager.getLogger(object);
            StringBuilder builder = new StringBuilder();
            builder.append("\n\tClass:").append(object.getCanonicalName());
            builder.append("\n\t");
            builder.append("Message:").append(message);
            LOG.debug(builder.toString());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the debug into file with Transaction(Class,Transaction,Message)">
    /**
     * Using for log the debug into file
     *
     * @param object
     * @param transactionID
     * @param message
     */
    public void debug(
            Class object,
            String transactionID,
            String message) {
        if (isShowDebugLog()) {
            Logger LOG = LogManager.getLogger(object);
            StringBuilder builder = new StringBuilder();
            builder.append("Class:").append(object.getCanonicalName());
            builder.append("\n");
            builder.append("Transaction:").append(transactionID);
            builder.append("\n");
            builder.append("Message:").append(message);
            LOG.debug(builder.toString());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the error into file with transaction (Class, Transaction, Message)">
    /**
     * Using for log the error into file
     *
     * @param object
     * @param message
     */
    public void error(
            Class object,
            String transaction,
            String message) {
        error(object, transaction, message, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the error into file with Transaction (Class, Transaction, Exception)">
    /**
     * Using for log the error into file
     *
     * @param object
     * @param transactionID
     * @param message
     * @param ex
     */
    public void error(
            Class object,
            String transactionID,
            Exception ex) {
        error(object, transactionID, null, ex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the error into file with Transaction (Class, Transaction, Message, Exception)">
    /**
     * Using for log the error into file
     *
     * @param object
     * @param transactionID
     * @param message
     * @param ex
     */
    public void error(
            Class object,
            String transactionID,
            String message,
            Exception ex) {
        if (isPrintStackTrace()) {
            ex.printStackTrace();
        }

        //<editor-fold defaultstate="collapsed" desc="Calculate Exception">
        Logger LOG = LogManager.getLogger(object);
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction:").append(transactionID);
        sb.append("\n");
        sb.append("Message:").append(message);
        sb.append("\n");
        if (ex != null) {
            StackTraceElement[] trace = null;
//            Throwable[] throwAble = ex.getSuppressed();
            sb.append(ex.getClass().getName());
            sb.append(": ");
            sb.append(ex.getLocalizedMessage());
            if (ex.getCause() != null) {
                sb.append("\n");
                sb.append("Cause by:").append(ex.getCause().getLocalizedMessage());
                trace = ex.getCause().getStackTrace();
            } else {
                trace = ex.getStackTrace();
            }
            sb.append("\n\tStack trace:");
            for (StackTraceElement element : trace) {
                sb.append("\n\t\t");
                sb.append(element.getClassName())
                        .append(" at (")
                        .append(element.getMethodName())
                        .append(":")
                        .append(element.getLineNumber())
                        .append(")");
            }
//            List<String> temp = new ArrayList<>();
//            for (int i = trace.length - 1; i >= 0; i--) {
//                if (trace[i].getClassName().equals(ManagementController.class.getCanonicalName())) {
//                    for (int j = i; j >= 0; j--) {
//                        temp.add(trace[j].getClassName() + " at(" + trace[j].getMethodName() + ":" + trace[j].getLineNumber() + ")");
//                    }
//                    break;
//                }
//            }
//            for (int i = (temp.size() - 1); i >= 0; i--) {
//                sb.append(String.format("%5s", temp.get(i)));
//                sb.append("\n\t");
//            }
        }
        //</editor-fold>

        if (isShowErrorLog()) {
            LOG.error(sb.toString());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the info into file">
    /**
     * Using for log the info into file
     *
     * @param object
     * @param message
     */
    public void info(Class object, String message) {
        if (isShowInfoLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.info(message);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the info into file with Transaction">
    /**
     * Using for log the info into file
     *
     * @param object
     * @param transactionID
     * @param message
     */
    public void info(
            Class object,
            String transactionID,
            String message) {
        if (isShowInfoLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.info("TransactionID:" + transactionID + "\n" + message);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the fatal into file with Transaction">
    public void fatal(
            Class object,
            String transactionID,
            String message
    ) {
        if (isShowFatalLog()) {
            String messageTemp = "\n\t";
            messageTemp += "Fatal in class " + object.getName();
            messageTemp += "\n\tTransactionID:" + transactionID;
            messageTemp += "\n\tError:" + message;
            Logger LOG = LogManager.getLogger(object);
            LOG.fatal(messageTemp);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the debug of Database into file">
    /**
     * Using for log the debug of Database into file
     *
     * @param object
     * @param message
     */
    public void logDB(Class object, String message) {
        if (isShowDatabaseLog()) {
            Logger LOG = LogManager.getLogger(object);
            StringBuilder builder = new StringBuilder();
            builder.append("\n=====DATABASE LOGGER=====\n");
            builder.append("Class:").append(object.getCanonicalName());
            builder.append("\n");
            builder.append("Debug DB:").append(message);
            LOG.log(Level.getLevel("LOG_DATABASE"), builder.toString());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the error of request (When user cannot call to server) into file">
    /**
     * Using for log the error of request into file
     *
     * @param object
     * @param statusResponse
     * @param headers
     * @param url
     * @param ip
     * @param body
     */
    public void logErrorRequest(
            Class object,
            int statusResponse,
            String headers,
            String url,
            String ip,
            String body) {
        Logger LOG = LogManager.getLogger(object);
        StringBuilder builder = new StringBuilder();
        builder.append("\n=====ERROR REQUEST LOGGER=====\n");
        builder.append("Class:").append(object.getCanonicalName());
        builder.append("\n");
        builder.append("Status response:").append(statusResponse);
        builder.append("\n");
        builder.append("URL:").append(url);
        builder.append("\n");
        builder.append("IP:").append(ip);
        builder.append("\n");
        builder.append("Headers:").append(headers);
        builder.append("\n");
        builder.append("Body:").append(body);
        LOG.log(Level.getLevel("ERROR_REQUEST"), builder.toString());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the developer warning into file">
    /**
     * Using for log the developer warning into file
     *
     * @param object
     * @param message
     */
    public void logDevWarning(
            Class object,
            String message) {
        Logger LOG = LogManager.getLogger(object);
        StringBuilder builder = new StringBuilder();
        builder.append("\n=====DEVELOPER WARNING LOGGER=====\n");
        builder.append("Class:").append(object.getCanonicalName());
        builder.append("\n");
        builder.append("Message:").append(message);
        LOG.log(Level.getLevel("DEVELOPER_WARNING"), builder.toString());
    }

    //</editor-fold>
    //==========================For LogHandler Core=============================
    public static void showHierarchicalLog(fps_core.utils.LogHandler.HierarchicalLog log) {
        fps_core.utils.LogHandler.showHierarchicalLog(log);
    }

    public static String hierachicalLogToString(fps_core.utils.LogHandler.HierarchicalLog log) {
        return fps_core.utils.LogHandler.hierachicalLogToString(log);
    }
}
