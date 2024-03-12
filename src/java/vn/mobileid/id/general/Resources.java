/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general;

import fps_core.objects.FieldType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.FPS.object.ResponseCode;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.general.database.DatabaseImpl_field;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK Read Attribute in DB
 */
public class Resources extends HttpServlet {

    @Override
    public void init() {
        System.out.println("=====Load Resource=====");
        Configuration.getInstance();
        Resources.init_();
    }

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);

    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();
    private static volatile HashMap<String, FieldType> fieldTypes = new HashMap<>();

    public static synchronized void init_() {
        try {
            if (responseCodes.isEmpty()) {
                reloadResponseCodes();
            }
            if (fieldTypes.isEmpty()){
                reloadFieldTypes();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append("Error while init! \n");
            sb.append(ex.getLocalizedMessage());
            sb.append("\n");
            sb.append("Cause by:"+ ex.getCause().getMessage());
            sb.append("\n");
            StackTraceElement[] trace = ex.getCause().getStackTrace();
            List<String> temp = new ArrayList<>();
            for (int i = trace.length - 1; i >= 0; i--) {
                if (trace[i].getClassName().equals(Resources.class.getCanonicalName())) {
                    for (int j = i; j >= 0; j--) {
                        if (trace[j].getClassName().equals("CreateConnection")) {
                            temp.add(trace[j].getClassName()+ " at("+ trace[j].getMethodName() + ":" + trace[j].getLineNumber() + ")");                            
                            break;
                        }
                        temp.add(trace[j].getClassName()+ " at("+ trace[j].getMethodName() + ":" + trace[j].getLineNumber() + ")");                                                    
                    }
                    break;
                }
            }
            for(int i=(temp.size()-1);i>=0;i--){
                sb.append(String.format("%5s", temp.get(i))); 
                sb.append("\n");
            }
            System.out.println(sb.toString());            
        }
    }

    public static void reloadResponseCodes() throws Exception {
        DatabaseImpl db = DatabaseFactory.getDatabaseImpl();
        responseCodes = new HashMap<>();
        DatabaseResponse response = db.getListResponseCode();
        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS && response.getObject()==null) {
            throw new Exception("Error while reloading responseCode");
        }
        List<ResponseCode> listOfResponseCode = (List<ResponseCode>) response.getObject();
        for (ResponseCode responseCode : listOfResponseCode) {
            responseCodes.put(responseCode.getName(), responseCode);
        }
    }

    public static void reloadFieldTypes() throws Exception{
        DatabaseImpl_field db = DatabaseFactory.getDatabaseImpl_field();
        fieldTypes = new HashMap<>();
        DatabaseResponse response = db.getFieldType("TransactionId");
        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS && response.getObject()==null) {
            throw new Exception("Error while reloading field Type");
        }
        List<FieldType> listOfFieldType= (List<FieldType>) response.getObject();
        for (FieldType fieldType : listOfFieldType) {
            fieldTypes.put(fieldType.getTypeName(), fieldType);
        } 
    }
    
    public static HashMap<String, ResponseCode> getResponseCodes() {
        return responseCodes;
    }
    
    public static HashMap<String, FieldType> getFieldTypes(){        
        return fieldTypes;
    }
}
