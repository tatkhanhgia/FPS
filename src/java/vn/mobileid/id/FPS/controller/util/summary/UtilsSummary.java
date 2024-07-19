/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary;

import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.authorize.summary.AuthorizeSummaryInternal;
import vn.mobileid.id.FPS.controller.util.summary.module.ReloadRAM;
import vn.mobileid.id.FPS.controller.util.summary.module.ReloadThreadManagement;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.object.policy.SystemConfiguration;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;

/**
 *
 * @author GiaTK
 */
public class UtilsSummary {

    // <editor-fold defaultstate="collapsed" desc="Reload Resources">
    /**
     * Reload Resources in RAM
     *
     * @param request
     * @param transactionId
     * @return InternalResponse
     * @throws Exception
     */
    public InternalResponse reloadResources(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = AuthorizeSummaryInternal.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        ReloadRAM.reloadResources(transactionId);

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        ).setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Reload Thread">
    /**
     * Reload Resources in RAM
     *
     * @param request
     * @param transactionId
     * @return InternalResponse
     * @throws Exception
     */
    public InternalResponse reloadThreadManagement(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = AuthorizeSummaryInternal.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        ReloadThreadManagement.reloadThreadManagement(transactionId);

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        ).setUser(user);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Show System Configuration">
    /**
     * Get System configuration
     * @param request
     * @param transactionId
     * @return InternalRespone with message return to the client
     * @throws Exception 
     */
    public InternalResponse getSystemConfiguration(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Verify">
        InternalResponse response = AuthorizeSummaryInternal.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>    
        
        ResponseMessageController responseMessageBuilder = new ResponseMessageController();
        
        //<editor-fold defaultstate="collapsed" desc="Get Data From Configuration">
        boolean isGTK_Dev = Configuration.getInstance().IsGTK_Dev();
        responseMessageBuilder.writeBooleanField("is_gtk_dev", isGTK_Dev);
        
        String urlFMS = Configuration.getInstance().getUrlFMS();
        responseMessageBuilder.writeStringField("url_fms", urlFMS);
        
        String qryptoAuthentication = Configuration.getInstance().getQryptoAuthentication();
        responseMessageBuilder.writeStringField("qrypto_authentication", qryptoAuthentication);
        
        String hostQrypto = Configuration.getInstance().getHostQrypto();
        responseMessageBuilder.writeStringField("host_qrypto", hostQrypto);
        
        String serverTimeType = Configuration.getInstance().getServerTimeType();
        responseMessageBuilder.writeStringField("server_time_type", serverTimeType);
        
        String dbDriver = Configuration.getInstance().getDbDriver();
        responseMessageBuilder.writeStringField("db_driver", dbDriver);
        
        String dbUrl = Configuration.getInstance().getDbUrl();
        responseMessageBuilder.writeStringField("db_url", dbUrl);
        
        String dbReadDriver = Configuration.getInstance().getDbReadOnlyDriver();
        responseMessageBuilder.writeStringField("db_read_driver", dbReadDriver);
        
        String dbReadURL = Configuration.getInstance().getDbReadOnlyUrl();
        responseMessageBuilder.writeStringField("db_read_url", dbReadURL);
        
        String dbWriteDriver = Configuration.getInstance().getDbWriteOnlyDriver();
        responseMessageBuilder.writeStringField("db_write_driver", dbWriteDriver);
        
        String dbWriteURL = Configuration.getInstance().getDbWriteOnlyUrl();
        responseMessageBuilder.writeStringField("db_write_url", dbWriteURL);
        
        int initPoolSize = Configuration.getInstance().getInitPoolSize();
        responseMessageBuilder.writeNumberField("db_pool_size", initPoolSize);
        
        int maxConnection  = Configuration.getInstance().getMaxConnection();
        responseMessageBuilder.writeNumberField("db_max_connection", maxConnection);
        
        int maxPoolIdle = Configuration.getInstance().getMaxPoolIdle();
        responseMessageBuilder.writeNumberField("db_max_pool_idle", maxPoolIdle);
        
        int minPoolIdle = Configuration.getInstance().getMinPoolIdle();
        responseMessageBuilder.writeNumberField("db_min_pool_idle", minPoolIdle);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Get Data From Policy">
        SystemConfiguration systemConfig = PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0);
        responseMessageBuilder.writeStringField("system_date_format", systemConfig.getDateFormat());
        responseMessageBuilder.writeNumberField("system_accept_maximum_file_size", systemConfig.getMaximumFile());
        responseMessageBuilder.writeStringField("system_qr_expired_time", systemConfig.getQrExpiredTime());
        responseMessageBuilder.writeStringField("system_qr_host", systemConfig.getQrHost());
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Get Data from LogHandle">
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isConfigCaching());
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isShowDebugLog());
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isShowErrorLog());
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isShowFatalLog());
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isShowInfoLog());
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isShowRequestLog());
        responseMessageBuilder.writeBooleanField(dbUrl, LogHandler.isShowWarnLog());
        //</editor-fold>
    
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                responseMessageBuilder.build()
        ).setUser(user);
    }
    //</editor-fold>
}
