/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.api.ManagementController;

/**
 *
 * @author GiaTK
 * Manage all Log in System. 
 * Note: can manage the log in FPS_Core too
 */
public class LogHandler {

    private static boolean showDebugLog;
    private static boolean showInfoLog;
    private static boolean showWarnLog;
    private static boolean showErrorLog;
    private static boolean showFatalLog;
    private static boolean showRequestLog;

    private static boolean configCaching;

    private static LogHandler instance;
    
    private static void readConfig() {
        showDebugLog = Configuration.getInstance().isShowDebugLog();
        showInfoLog = Configuration.getInstance().isShowInfoLog();
        showWarnLog = Configuration.getInstance().isShowWarnLog();
        showErrorLog = Configuration.getInstance().isShowErrorLog();
        showRequestLog = Configuration.getInstance().isShowRequestLog();
    }

    public static boolean isShowRequestLog() {
        readConfig();
        return showRequestLog;
    }

    public static boolean isShowDebugLog() {
        readConfig();
        return showDebugLog;
    }

    public static boolean isShowInfoLog() {
        readConfig();
        return showInfoLog;
    }

    public static boolean isShowWarnLog() {
        readConfig();
        return showWarnLog;
    }

    public static boolean isShowErrorLog() {
        readConfig();
        return showErrorLog;
    }

    public static boolean isShowFatalLog() {
        readConfig();
        return showFatalLog;
    }

    public static boolean isConfigCaching() {
        readConfig();
        return configCaching;
    }

    //<editor-fold defaultstate="collapsed" desc="Using for log the request/response into file">
    /**
     * Using for log the request/response into file
     *
     * @param object defines the class
     * @param message
     */
    public static void request(Class object, String message) {
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
    public static void debug(Class object, String message) {
        if (isShowDebugLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.debug(message);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the debug into file with Transaction">
    /**
     * Using for log the debug into file
     *
     * @param object
     * @param transactionID
     * @param message
     */
    public static void debug(
            Class object,
            String transactionID,
            String message) {
        if (isShowDebugLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.debug("TransactionID:" + transactionID + "\n" + message);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the error into file with transaction">
    /**
     * Using for log the error into file
     *
     * @param object
     * @param message
     */
    public static void error(
            Class object,
            String transaction,
            String message) {
        if (isShowErrorLog()) {
            Logger LOG = LogManager.getLogger(object);
            String temp = transaction
                    + "\n\t" + message;
            LOG.error(message);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Using for log the error into file with Transaction">
    /**
     * Using for log the error into file
     *
     * @param object
     * @param transactionID
     * @param message
     * @param ex
     */
    public static void error(
            Class object,
            String transactionID,
            Exception ex) {
        if (isShowErrorLog()) {
            Logger LOG = LogManager.getLogger(object);
            StringBuilder sb = new StringBuilder();
            sb.append("Transaction:").append(transactionID);
            sb.append("\n");
            StackTraceElement[] trace = null;
//            Throwable[] throwAble = ex.getSuppressed();
            sb.append(ex.getClass().getName());
            sb.append(": ");
            sb.append(ex.getLocalizedMessage());
            if (ex.getCause() != null) {
                sb.append("\n");
                sb.append("Cause by:").append(ex.getCause().getMessage());
                trace = ex.getCause().getStackTrace();
            } else {
                trace = ex.getStackTrace();
            }
            sb.append("\n\t");
            List<String> temp = new ArrayList<>();
            for (int i = trace.length - 1; i >= 0; i--) {
                if (trace[i].getClassName().equals(ManagementController.class.getCanonicalName())) {
                    for (int j = i; j >= 0; j--) {
                        temp.add(trace[j].getClassName() + " at(" + trace[j].getMethodName() + ":" + trace[j].getLineNumber() + ")");
                    }
                    break;
                }
            }
            for (int i = (temp.size() - 1); i >= 0; i--) {
                sb.append(String.format("%5s", temp.get(i)));
                sb.append("\n\t");
            }
            LOG.error(sb.toString());
            System.err.println(sb.toString());
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
    public static void info(Class object, String message) {
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
    public static void info(
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
    public static void fatal(
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
            LOG.fatal(message);
        }
    }
    //</editor-fold>

    //==========================For LogHandler Core=============================
    
    public static void showHierarchicalLog(fps_core.utils.LogHandler.HierarchicalLog log){
        fps_core.utils.LogHandler.showHierarchicalLog(log);
    }
}
