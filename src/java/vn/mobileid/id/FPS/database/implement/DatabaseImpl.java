/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.database.implement;

import vn.mobileid.id.FPS.services.impls.databaseConnection.DatabaseConnectionManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.database.interfaces.IDatabase;
import vn.mobileid.id.FPS.object.APILog;
import vn.mobileid.id.FPS.object.ResponseCode;
import vn.mobileid.id.FPS.object.TemporalObject;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.object.policy.PolicyResponse;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.helper.ORM_JPA.database.CreateConnection;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl implements IDatabase {

    /* Template Call DB
    DatabaseResponse response = new DatabaseResponse();
        String nameStore = "{ CALL USP_RESPONSE_GET_LIST()}";

        response = CreateConnection.createConnection(
                nameStore,
                null,
                "Get list of ResponseCode");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        
        List<ResponseCode> objects = new ArrayList<>();                
        for(HashMap<String, Object> hashmap : response.getRows()){            
            ResponseCode temp = new ResponseCode();            
            temp.setCode(Integer.parseInt((String) hashmap.get("NAME")));
            temp.setCode_description((String)hashmap.get("ERROR_DESCRIPTION"));            
            temp.setId((long)hashmap.get("ID"));
            temp.setName((String)hashmap.get("NAME"));
            objects.add(temp);
        }
        
        response.setObject(objects);
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
    public DatabaseResponse getListResponseCode() throws Exception {
        String nameStore = "{ CALL USP_RESPONSE_GET_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                null,
                null,
                "Get list of ResponseCode");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }

        List<ResponseCode> objects = new ArrayList<>();
        for (HashMap<String, Object> hashmap : response.getRows()) {
            ResponseCode temp = new ResponseCode();
            temp.setCode((String) hashmap.get("ERROR"));
            temp.setCode_description((String) hashmap.get("ERROR_DESCRIPTION"));
            temp.setId((long) hashmap.get("ID"));
            temp.setName((String) hashmap.get("NAME"));
            objects.add(temp);
        }

        response.setObject(objects);
        return response;
    }

    @Override
    public DatabaseResponse getPolicies(
            int type,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_GENERAL_POLICY_ATTR_GET(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pATTR_ID", type);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get Policy");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        PolicyResponse policy = new PolicyResponse();
        for (HashMap<String, Object> hashmap : response.getRows()) {
            policy.setId(((Long) hashmap.get("ID")).intValue());
            policy.setEnabled((Boolean) hashmap.get("ENABLED"));
            policy.setGeneral_policy_attr_type_id(((Long) hashmap.get("GENERAL_POLICY_ATTR_TYPE_ID")).intValue());
            policy.setValue((String) hashmap.get("VALUE"));
            policy.setCreated_by((String) hashmap.get("BLOB"));
            policy.setModified_by((String) hashmap.get("HMAC"));
            policy.setCreated_at(Utils.sqlDateToJavaDate((LocalDateTime) hashmap.get("CREATED_AT")));
            policy.setModified_at(Utils.sqlDateToJavaDate((LocalDateTime) hashmap.get("LAST_MODIFIED_AT")));
        }

        response.setObject(policy);
        return response;
    }

    @Override
    public DatabaseResponse temporalAdd(
            String documentHash,
            String fieldHash,
            int type,
            byte[] data,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_TEMPORAL_ADD(?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pPARENT", documentHash);
        datas.put("pCHILD", fieldHash);
        datas.put("pTYPE", type);
        datas.put("pDATA", data);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Upload Temporal Data");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse temporalGet(
            String identifyName,
            String identifyName2,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_TEMPORAL_GET(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pPARENT_NAME", identifyName);
        datas.put("pCHILD_NAME", identifyName2);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get Temporal Data");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        TemporalObject data = new TemporalObject();
        for (HashMap<String, Object> hashmap : response.getRows()) {
            data.setParentName((String) hashmap.get("PARENT_NAME"));
            data.setChildName((String) hashmap.get("CHILD_NAME"));
            data.setType((int) hashmap.get("TYPE"));
            data.setData((byte[]) hashmap.get("DATA"));
        }
        response.setObject(data);
        return response;
    }

    @Override
    public DatabaseResponse temporalDelete(
            String identifyName,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_TEMPORAL_DELETE(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pNAME", identifyName);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Delete Temporal Data");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse temporalList(
            String identifyName,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_TEMPORAL_LIST(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pPARENT_NAME", identifyName);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Get List Temporal Data");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

//        TemporalObject data = new TemporalObject();
//        for (HashMap<String, Object> hashmap : response.getRows()) {            
//           data.setParentName((String)hashmap.get("PARENT_NAME"));
//           data.setChildName((String)hashmap.get("CHILD_NAME"));
//           data.setType((int)hashmap.get("TYPE"));
//           data.setData((byte[])hashmap.get("DATA"));
//        }
//        response.setObject(data);
        return response;
    }

    @Override
    public DatabaseResponse createAPILog(
            long pENTERPRISE_ID,
            long pDOCUMENT_ID,
            String pTRANSACTION_ID,
            String pAPP_NAME,
            String pAPI_KEY,
            String pVERSION,
            String pSERVICE_NAME,
            String pURL,
            String pHTTP_VERB,
            int pSTATUS_CODE,
            String pHEADERS,
            String pFILECACHED,
            String pREQUEST,
            String pRESPONSE,
            String pEXCEPTION,
            String pHMAC,
            String pCREATED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_API_LOG_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pENTERPRISE_ID", pENTERPRISE_ID);
        datas.put("pDOCUMENT_ID", pDOCUMENT_ID);
        datas.put("pTRANSACTION", pTRANSACTION_ID);
        datas.put("pAPP_NAME", pAPP_NAME);
        datas.put("pAPI_KEY", pAPI_KEY);
        datas.put("pVERSION", pVERSION);
        datas.put("pSERVICE_NAME", pSERVICE_NAME);
        datas.put("pURL", pURL);
        datas.put("pHTTP_VERB", pHTTP_VERB);
        datas.put("pSTATUS_CODE", pSTATUS_CODE);
        datas.put("pHEADERS", pHEADERS);
        datas.put("pFILE_CACHED", pFILECACHED);
        datas.put("pREQUEST", pREQUEST);
        datas.put("pRESPONSE", pRESPONSE);
        datas.put("pEXCEPTION", pEXCEPTION);
        datas.put("pHMAC", pHMAC);
        datas.put("pCREATED_BY", pCREATED_BY);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Upload API Log");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getRemarkLanguage(
            String table,
            String name,
            String languageName,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_REMARK_LANGUAGE_GET(?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pTABLE_NAME", table);
        datas.put("pNAME", name);
        datas.put("pLANGUAGE_NAME", languageName);

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pVALUE", java.sql.Types.VARCHAR);
        output.put("pRESPONSE_CODE", java.sql.Types.VARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                output,
                "Get Remark Language");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() == A_FPSConstant.CODE_SUCCESS) {
            List<HashMap<String, Object>> rows = response.getRows();
            for (HashMap<String, Object> row : rows) {
                if (row.containsKey("pVALUE")) {
                    String value = (String) row.get("pVALUE");
                    response.setObject(value);
                }
            }
        }

        return response;
    }

    @Override
    public DatabaseResponse getAPILogs(String transactionId) throws Exception {
        String nameStore = "{ CALL USP_API_LOG_GET_FILE_CACHED_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                MyServices.getDatabaseConnection().getReadOnlyConnection(),
                nameStore,
                null,
                null,
                "Get API Logs");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() == A_FPSConstant.CODE_SUCCESS) {
            List<HashMap<String, Object>> rows = response.getRows();
            List<APILog> apiLogs = new ArrayList<>();
            for (HashMap<String, Object> row : rows) {
                if (row.containsKey("ID") && row.containsKey("FILE_CACHED")) {
                    APILog apiLog = new APILog();
                    String fileCached = (String) row.get("FILE_CACHED");
                    long id = (Long) row.get("ID");
                    apiLog.setId(id);
                    apiLog.setFileCacheString(fileCached);
                    apiLogs.add(apiLog);
                }
            }
            response.setObject(apiLogs);
        }

        return response;
    }

    @Override
    public DatabaseResponse updateFileCatchAPILog(
            long apiLogId,
            String fileCatch,
            String modifiedBy,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_API_LOG_UPDATE_FILE_CACHED(?,?,?,?)}";

        HashMap<String, Object> inputs = new HashMap<>();
        inputs.put("pAPI_LOG_ID", apiLogId);
        inputs.put("pFILE_CACHED", fileCatch);
        inputs.put("pLAST_MODIFIED_BY", modifiedBy);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                MyServices.getDatabaseConnection().getWriteOnlyConnection(),
                nameStore,
                inputs,
                null,
                "Update API Logs");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }
}
