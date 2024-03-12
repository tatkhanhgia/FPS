/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import java.util.HashMap;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.helper.ORM_JPA.database.CreateConnection;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface DatabaseImpl_authorize{
    public DatabaseResponse getAPIKey(
            String clientId,
            String transactionID) throws Exception;
    
    public DatabaseResponse writeRefreshToken(
            String sessionId,
            String jwtId,
            boolean clientCredentials,
            String clientId,
            Date issuedAt,
            Date expiredAt,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;
    
    public DatabaseResponse checkSessionId(
            String jwtId,
            String sessionId,
            String transactionId
    ) throws Exception;
}

class DatabaseImpl_authorize_ implements DatabaseImpl_authorize{

    /* Template Call DB
    DatabaseResponse response = new DatabaseResponse();
        String nameStore = "";
                        
        response = CreateConnection.executeStoreProcedure(
                nameStore,
                null,
                "Get list of ResponseCode");
                
        LogHandler.debug(this.getClass(), response.getDebugString());
        
        if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return response;
        }
        
        List<ResponseCode> objects = new ArrayList<>();
        ResultSet rs = response.getRs();
        
        if(rs == null ){
            while(rs.next()){
            ResponseCode temp = new ResponseCode();
            temp.setCode(rs.getInt(""));
            temp.setCode_description(rs.getString(""));
            temp.setId(rs.getInt(""));
            temp.setName(rs.getString(""));
            objects.add(temp);
            }
            response.setObject(objects);
        } 
        return response;
     */
    private static int retryTimes = 1; // default no retry

    static {
        retryTimes = Configuration.getInstance().getRetry();
        if (retryTimes == 0) {
            retryTimes = 1;
        }
    }

    @Override
    public DatabaseResponse getAPIKey(
            String clientId,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_ENTERPRISE_API_KEY_GET_FROM_CLIENT_ID(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pCLIENT_ID", clientId);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get API Key");

        LogHandler.debug(this.getClass(), transactionID + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        Enterprise enterprise = new Enterprise();
        for (HashMap<String, Object> hashmap : response.getRows()) {
            enterprise.setIdOfClientID((long)hashmap.get("ID"));
            enterprise.setClientID((String) hashmap.get("CLIENT_ID"));
            enterprise.setClientSecret((String) hashmap.get("CLIENT_SECRET"));
            Long temp = (long) hashmap.get("ENTERPRISE_ID");
            enterprise.setId(temp.intValue());
        }

        response.setObject(enterprise);
        return response;
    }

    @Override
    public DatabaseResponse writeRefreshToken(
            String sessionId,
            String jwtId,
            boolean clientCredentials,
            String clientId,
            Date issuedAt,
            Date expiredAt,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception {
        String nameStore = "{ CALL USP_REFRESH_TOKEN_ADD(?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pUSER_EMAIL", null);
        datas.put("pSESSION_TOKEN", sessionId);
        datas.put("pJWT_ID", jwtId);
        datas.put("pGRANT_TYPE", clientCredentials == true ? 2 : 0);
        datas.put("pCLIENT_ID", clientId);
        datas.put("pISSUED_AT", issuedAt);
        datas.put("pEXPIRED_AT", expiredAt);
        datas.put("pHMAC", hmac);
        datas.put("pCREATED_BY", createdBy);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Write refreshToken");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        return response;
    }

    public DatabaseResponse checkSessionId(
            String jwtId,
            String sessionId,
            String transactionId
    ) throws Exception {
        String nameStore = "{ call USP_REFRESH_TOKEN_CHECK_EXISTS(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pJWT_ID", jwtId);
        datas.put("pSESSION_TOKEN", sessionId);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "check existed of Token");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }
}
