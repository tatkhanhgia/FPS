/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.database.implement;

import vn.mobileid.id.FPS.services.impls.databaseConnection.DatabaseConnectionManager;
import java.time.LocalDateTime;
import java.util.HashMap;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.database.interfaces.IEnterpriseDB;
import vn.mobileid.id.FPS.object.APIKeyRule;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.helper.ORM_JPA.database.CreateConnection;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class EnterpriseDBImpl implements IEnterpriseDB {

    /* Template Call DB
    DatabaseResponse response = new DatabaseResponse();
        String nameStore = "";
                        
        response = CreateConnection.executeStoreProcedure(
                nameStore,
                null,
                "Get list of ResponseCode");
                
        LogHandler.getInstance().debug(this.getClass(), response.getDebugString());
        
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
    public DatabaseResponse getEntepriseInfo(
            int enterpriseId,
            String enterpriseName,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_ENTERPRISE_INFO_GET(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pENTERPRISE_ID", enterpriseId == 0 ? null : enterpriseId);
        datas.put("pENTERPRISE_NAME", enterpriseName);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get Enterprise Info");

        LogHandler.getInstance().debug(this.getClass(), transactionID + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        Enterprise enterprise = new Enterprise();
        for (HashMap<String, Object> hashmap : response.getRows()) {
            Long temp = (long) hashmap.get("ID");
            enterprise.setId(temp.intValue());
            enterprise.setName((String) hashmap.get("NAME"));
            enterprise.setOwnerId((long) hashmap.get("OWNER"));
            enterprise.setMobileNumber((String) hashmap.get("MOBILE_NUMBER"));
            enterprise.setStatus((int) hashmap.get("STATUS"));
            enterprise.setDomain((String) hashmap.get("DOMAIN"));
            enterprise.setSubdomain((String) hashmap.get("SUBDOMAIN"));
            enterprise.setCreatedAt(Utils.sqlDateToJavaDate((LocalDateTime) hashmap.get("CREATED_AT")));
            enterprise.setCreatedBy((String) hashmap.get("CREATED_BY"));
            enterprise.setCreatedAt(Utils.sqlDateToJavaDate((LocalDateTime) hashmap.get("LAST_MODIFIED_AT")));
            enterprise.setModifiedBy((String) hashmap.get("LAST_MODIFIED_BY"));
        }

        response.setObject(enterprise);
        return response;
    }

    @Override
    public DatabaseResponse getRule(
            long apiKeyRule,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_API_KEY_TYPE_GET(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pAPI_KEY_TYPE", apiKeyRule);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get API Key Rule");

        LogHandler.getInstance().debug(this.getClass(), transactionID + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        for (HashMap<String, Object> hashmap : response.getRows()) {
            try {
                String value = (String) hashmap.get("VALUE");
                APIKeyRule rule = MyServices.getJsonService().readValue(value, APIKeyRule.class);
                response.setObject(rule);
            } catch (Exception ex) {
                LogHandler.getInstance().error(EnterpriseDBImpl.class, transactionID, ex);
            }
        }

        return response;
    }

}
