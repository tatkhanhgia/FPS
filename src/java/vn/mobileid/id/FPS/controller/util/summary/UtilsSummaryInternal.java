/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary;

import java.util.List;
import vn.mobileid.id.FPS.controller.util.summary.micro.GetAPILog;
import vn.mobileid.id.FPS.object.APILog;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;

/**
 *
 * @author GiaTK
 */
public class UtilsSummaryInternal {
    public UtilsSummaryInternal(){}
    
    //<editor-fold defaultstate="collapsed" desc="Get API Logs from DB">
    /**
     * Get APILog from DB
     * @return InternalResponse with List of APILog as an Object
     */
    public InternalResponse getAPILogs(){
        try{
            return GetAPILog.getAPILog("");
        }catch(Exception ex){
            LogHandler.getInstance().error(
                    UtilsSummaryInternal.class,
                    "",
                    "Cannot get API Logs",
                    ex);
            return CreateInternalResponse.createBadRequestInternalResponse(
                    0,
                    0);
        }
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        InternalResponse response = new UtilsSummaryInternal().getAPILogs();
        if(response.isValid()){
            List<APILog> list = (List<APILog>)response.getData();
            for(APILog log : list){
                System.out.println(log.getId() +"-"+log.getFileCaches().get(0).getUuid());
            }
        } else {
            System.out.println("Status:"+response.getStatus());
        }
    }
}
