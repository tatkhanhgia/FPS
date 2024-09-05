/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.systemManagement;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.FPS.object.email.SMTPProperties;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GIATK
 * Read Attribute in file Config (local)
 *
 */
public class Configuration {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);

    private static Configuration instance;

    private Properties prop = new Properties();
    private Properties appInfo = new Properties();
//    private Properties propSMTP = new Properties();

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    private String dbReadOnlyUrl;
    private String dbReadOnlyUsername;
    private String dbReadOnlyPassword;
    private String dbReadOnlyDriver;

    private String dbWriteOnlyUrl;
    private String dbWriteOnlyUsername;
    private String dbWriteOnlyPassword;
    private String dbWriteOnlyDriver;

    private int initPoolSize;
    private int minPoolIdle;
    private int maxPoolIdle;
    private int maxConnection = 1;
    private boolean showProcedures;
    private int retry = 1;
    private int appUserDBID = 1;

    private boolean showLog4jDebugLog;
    private boolean showLog4jInfoLog;
    private boolean showLog4jWarnLog;
    private boolean showLog4jErrorLog;
    private boolean show4jFatalLog;
    private boolean show4jRequestLog;
    private boolean showLog4JDatabaseLog;
    
    //For System
    private boolean showPrintStacktrace = true;

    private String serverTimeType;

    //SMTP
//    private boolean ssl_enable;
//    private boolean tsl_enable;
//    private boolean auth;
//    private String host;
//    private int port;
//    private String username;
//    private String password;
//    private String sendFromAddr;
//    private String sendFromName;
    //FMS
    private String urlFMS;
    private boolean isGTK_Dev = false;

    //Qrypto
    private String hostQrypto;
    private String qryptoAuthentication;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private Configuration() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            //Load Paperless Services Properties-----------------------------------
            //<editor-fold defaultstate="collapsed" desc="Load Application Properties">
//            if (Utils.isNullOrEmpty(System.getenv("DTIS_DB_URL"))) {            
            InputStream appProperties = loader.getResourceAsStream(
                    "resources/config/app.properties");
            if (appProperties != null) {
                prop.load(appProperties);
                if (prop.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/app.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        prop.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                appProperties.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/app.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    prop.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
//            } else {
//                LOG.debug("Using getenv to obtain configuration values");
//            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Load Infor Props">
            InputStream appInfoProperties = loader.getResourceAsStream("resources/config/info.properties");
            if (appInfoProperties != null) {
                appInfo.load(appInfoProperties);
                if (appInfo.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/info.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        appInfo.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                appInfoProperties.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/info.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    appInfo.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Load SMTP Props">
            InputStream SMTP = loader.getResourceAsStream("resources/config/smtp.properties");
            Properties propSMTP = new Properties();
            if (SMTP != null) {
                propSMTP.load(SMTP);
                if (propSMTP.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/smtp.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        propSMTP.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                SMTP.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/smtp.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    propSMTP.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Load QRYPTO Properties">
            InputStream qrypto = loader.getResourceAsStream("resources/config/qrypto.properties");
            Properties propQrypto = new Properties();
            if (qrypto != null) {
                propQrypto.load(qrypto);
                if (propQrypto.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/qrypto.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        propQrypto.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                qrypto.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/qrypto.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    propQrypto.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            //</editor-fold>

            dbUrl = prop.getProperty("FPS.db.url") == null ? System.getenv("DTIS_DB_URL") : prop.getProperty("FPS.db.url");
            dbUsername = prop.getProperty("FPS.db.username") == null ? System.getenv("DTIS_DB_USERNAME") : prop.getProperty("FPS.db.username");
            dbPassword = prop.getProperty("FPS.db.password") == null ? System.getenv("DTIS_DB_PASSWORD") : prop.getProperty("FPS.db.password");
            dbDriver = prop.getProperty("FPS.db.driver") == null ? System.getenv("DTIS_DB_DRIVER") : prop.getProperty("FPS.db.driver");

            dbReadOnlyUrl = prop.getProperty("FPS.db.readonly.url") == null ? System.getenv("DTIS_DB_READONLY_URL") : prop.getProperty("FPS.db.readonly.url");
            dbReadOnlyUsername = prop.getProperty("FPS.db.readonly.username") == null ? System.getenv("DTIS_DB_READONLY_USERNAME") : prop.getProperty("FPS.db.readonly.username");
            dbReadOnlyPassword = prop.getProperty("FPS.db.readonly.password") == null ? System.getenv("DTIS_DB_READONLY_PASSWORD") : prop.getProperty("FPS.db.readonly.password");
            dbReadOnlyDriver = prop.getProperty("FPS.db.readonly.driver") == null ? System.getenv("DTIS_DB_READONLY_DRIVER") : prop.getProperty("FPS.db.readonly.driver");

            dbWriteOnlyUrl = prop.getProperty("FPS.db.writeonly.url") == null ? System.getenv("DTIS_DB_WRITEONLY_URL") : prop.getProperty("FPS.db.writeonly.url");
            dbWriteOnlyUsername = prop.getProperty("FPS.db.writeonly.username") == null ? System.getenv("DTIS_DB_WRITEONLY_USERNAME") : prop.getProperty("FPS.db.writeonly.username");
            dbWriteOnlyPassword = prop.getProperty("FPS.db.writeonly.password") == null ? System.getenv("DTIS_DB_WRITEONLY_PASSWORD") : prop.getProperty("FPS.db.writeonly.password");
            dbWriteOnlyDriver = prop.getProperty("FPS.db.writeonly.driver") == null ? System.getenv("DTIS_DB_WRITEONLY_DRIVER") : prop.getProperty("FPS.db.writeonly.driver");

            initPoolSize = Integer.parseInt(prop.getProperty("FPS.db.init.connection") == null ? System.getenv("DTIS_DB_INIT_CONNECTION") : prop.getProperty("FPS.db.init.connection"));
            minPoolIdle = Integer.parseInt(prop.getProperty("FPS.db.min.idle.connection") == null ? System.getenv("DTIS_DB_MIN_IDLE_CONNECTION") : prop.getProperty("FPS.db.min.idle.connection"));
            maxPoolIdle = Integer.parseInt(prop.getProperty("FPS.db.max.idle.connection") == null ? System.getenv("DTIS_DB_MAX_IDLE_CONNECTION") : prop.getProperty("FPS.db.max.idle.connection"));
            maxConnection = Integer.parseInt(prop.getProperty("FPS.db.max.connection") == null ? System.getenv("DTIS_DB_MAX_CONNECTION") : prop.getProperty("FPS.db.max.connection"));
            showProcedures = Boolean.parseBoolean(prop.getProperty("FPS.db.logging.procedures.enabled") == null ? System.getenv("DTIS_DB_LOGGING_PROCEDURES_ENABLED") : prop.getProperty("FPS.db.logging.procedures.enabled"));
            retry = Integer.parseInt(prop.getProperty("FPS.db.retry") == null ? System.getenv("DTIS_DB_RETRY") : prop.getProperty("FPS.db.retry"));

            appUserDBID = Integer.parseInt(prop.getProperty("FPS.db.app.userid") == null ? System.getenv("DTIS_DB_APP_USERID") : prop.getProperty("FPS.db.app.userid"));

            showLog4jDebugLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_DEBUG") == null ? prop.getProperty("FPS.log4j.debug", "true") : System.getenv("DTIS_LOG4J_DEBUG"));
            showLog4jInfoLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_INFO") == null ? prop.getProperty("FPS.log4j.info", "true") : System.getenv("DTIS_LOG4J_INFO"));
            showLog4jWarnLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_WARN") == null ? prop.getProperty("FPS.log4j.warn", "true") : System.getenv("DTIS_LOG4J_WARN"));
            showLog4jErrorLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_ERROR") == null ? prop.getProperty("FPS.log4j.error", "true") : System.getenv("DTIS_LOG4J_ERROR"));
            show4jFatalLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_FATAL") == null ? prop.getProperty("FPS.log4j.fatal", "true") : System.getenv("DTIS_LOG4J_FATAL"));
            show4jRequestLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_REQUEST") == null ? prop.getProperty("FPS.log4j.request", "true") : System.getenv("DTIS_LOG4J_REQUEST"));
            showLog4JDatabaseLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_DATABASE_LOG") == null ? prop.getProperty("FPS.log4j.database.log", "true") : System.getenv("DTIS_LOG4J_DATABASE_LOG"));
            
            serverTimeType = prop.getProperty("server.time.type") == null ? System.getenv("SERVER_TIME_TYPE") : prop.getProperty("server.time.type");
            urlFMS = prop.getProperty("FMS.url");
            try {
                isGTK_Dev = Boolean.parseBoolean(prop.getProperty("FMS.isGTK_Dev"));
            } catch (Exception ex) {
            }
            
            try {                
                showPrintStacktrace = Boolean.parseBoolean(
                        Optional.ofNullable(prop.getProperty("FPS.log.print.stacktrace"))
                        .orElseGet(()->{
                            return "false";
                        })
                );
            } catch (Exception e) {
            }

            hostQrypto = propQrypto.getProperty("qrypto.host") == null
                    ? System.getenv("DTIS_DB_LOGGING_PROCEDURES_ENABLED")
                    : propQrypto.getProperty("qrypto.host");
            qryptoAuthentication = propQrypto.getProperty("qrypto.authorization") == null
                    ? System.getenv("DTIS_DB_LOGGING_PROCEDURES_ENABLED")
                    : propQrypto.getProperty("qrypto.authorization");

//            ssl_enable = Boolean.parseBoolean(propSMTP.getProperty("mail.smtp.ssl.enable") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.ssl.enable"));
//            tsl_enable = Boolean.parseBoolean(propSMTP.getProperty("mail.smtp.starttls.enable") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.starttls.enable"));
//            auth = Boolean.parseBoolean(propSMTP.getProperty("mail.smtp.auth") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.auth"));
//            host = propSMTP.getProperty("mail.smtp.host") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.host");
//            port = Integer.parseInt(propSMTP.getProperty("mail.smtp.port") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.port"));
//            username = propSMTP.getProperty("mail.smtp.username") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.username");
//            password = propSMTP.getProperty("mail.smtp.password") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.password");
//            sendFromAddr = propSMTP.getProperty("mail.smtp.sendfromaddr") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.sendfromaddr");
//            sendFromName = propSMTP.getProperty("mail.smtp.sendfromname") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.sendfromname");
            SMTPProperties.setProp(propSMTP);

            if (serverTimeType == null) {
                serverTimeType = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error while loading app.properties.");
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbReadOnlyUrl() {
        return dbReadOnlyUrl;
    }

    public String getDbReadOnlyUsername() {
        return dbReadOnlyUsername;
    }

    public String getDbReadOnlyPassword() {
        return dbReadOnlyPassword;
    }

    public String getDbReadOnlyDriver() {
        return dbReadOnlyDriver;
    }

    public String getDbWriteOnlyUrl() {
        return dbWriteOnlyUrl;
    }

    public String getDbWriteOnlyUsername() {
        return dbWriteOnlyUsername;
    }

    public String getDbWriteOnlyPassword() {
        return dbWriteOnlyPassword;
    }

    public String getDbWriteOnlyDriver() {
        return dbWriteOnlyDriver;
    }

    public int getInitPoolSize() {
        return initPoolSize;
    }

    public int getMinPoolIdle() {
        return minPoolIdle;
    }

    public int getMaxPoolIdle() {
        return maxPoolIdle;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public boolean isShowProcedures() {
        return showProcedures;
    }

    public int getRetry() {
        return retry;
    }

    public int getAppUserDBID() {
        return appUserDBID;
    }

    public Properties getAppInfo() {
        return appInfo;
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
        return show4jFatalLog;
    }

    private void getEnvConfig() {
        LOG.info("Load System env:");
        System.getenv().forEach((k, v) -> {
            LOG.info("\t" + k + ":" + v);
        });
    }

    public String getServerTimeType() {
        return serverTimeType;
    }

    public boolean isShowRequestLog() {
        return this.show4jRequestLog;
    }

    public String getUrlFMS() {
        return urlFMS;
    }

    public boolean IsGTK_Dev() {
        return isGTK_Dev;
    }

    public void setIsGTK_Dev() {
        this.isGTK_Dev = true;
    }

    public String getHostQrypto() {
        return hostQrypto;
    }

    public String getQryptoAuthentication() {
        return qryptoAuthentication;
    }

    public boolean isShowPrintStacktrace() {
        return showPrintStacktrace;
    }

    public void setShowPrintStacktrace(boolean showPrintStacktrace) {
        this.showPrintStacktrace = showPrintStacktrace;
    }

    public boolean isShowDatabaseLog() {
        return showLog4JDatabaseLog;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(Optional.ofNullable(null).orElseGet(()->{return "false";}));
    }
}
