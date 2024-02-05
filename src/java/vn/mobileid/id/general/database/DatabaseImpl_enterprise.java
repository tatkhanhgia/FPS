/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.helper.database.CreateConnection;
import vn.mobileid.id.helper.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public interface DatabaseImpl_enterprise{
    public DatabaseResponse getEntepriseInfo(
            int enterpriseId,
            String enterpriseName,
            String transactionID) throws Exception;
    
    
}
class DatabaseImpl_enterprise_ implements DatabaseImpl_enterprise{

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

        LogHandler.debug(this.getClass(), transactionID+" _ "+response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        Enterprise enterprise = new Enterprise();
        for (HashMap<String, Object> hashmap : response.getRows()) {    
            Long temp = (long)hashmap.get("ID");
            enterprise.setId(temp.intValue());
            enterprise.setName((String)hashmap.get("NAME"));
            enterprise.setOwnerId((long)hashmap.get("OWNER"));
            enterprise.setMobileNumber((String)hashmap.get("MOBILE_NUMBER"));
            enterprise.setStatus((int)hashmap.get("STATUS"));
            enterprise.setDomain((String)hashmap.get("DOMAIN"));
            enterprise.setSubdomain((String)hashmap.get("SUBDOMAIN"));                              
            enterprise.setCreatedAt(Utils.sqlDateToJavaDate((LocalDateTime) hashmap.get("CREATED_AT")));
            enterprise.setCreatedBy((String)hashmap.get("CREATED_BY"));                           
            enterprise.setCreatedAt(Utils.sqlDateToJavaDate((LocalDateTime) hashmap.get("LAST_MODIFIED_AT")));
            enterprise.setModifiedBy((String)hashmap.get("LAST_MODIFIED_BY"));
        }

        response.setObject(enterprise);
        return response;
    }

}
