/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.FPS.systemManagement;

import fps_core.objects.FieldType;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.FPS.object.ResponseCode;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.database.interfaces.IDatabase;
import vn.mobileid.id.FPS.database.interfaces.IFieldDB;
import vn.mobileid.id.FPS.services.others.threadManagement.ScheduledThreadManagement;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK Read Attribute in DB
 */
public class Resources extends HttpServlet {

    @Override
    public void init() {
        Configuration.getInstance();
        Resources.init_();
        ScheduledThreadManagement.getInstance();
    }

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);

    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();
    private static volatile HashMap<String, FieldType> fieldTypes = new HashMap<>();

    public static synchronized void init_() {
        LogHandler.getInstance().debug(Resources.class, "=======Initial Resources======");
        try {
            if (responseCodes.isEmpty()) {
                reloadResponseCodes();
            }
            if (fieldTypes.isEmpty()){
                reloadFieldTypes();
            }

        } catch (RuntimeException runTimeException){
            LogHandler.getInstance().error(Resources.class, "", runTimeException);
        } catch (Exception ex) {
            LogHandler.getInstance().error(Resources.class, "", ex);
        }
        LogHandler.getInstance().debug(Resources.class, "======Initial Resources sucessfully======");
    }

    public static void reloadResponseCodes() throws Exception {
        IDatabase db = DatabaseFactory.getDatabaseImpl();
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
        IFieldDB db = DatabaseFactory.getDatabaseImpl_field();
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
