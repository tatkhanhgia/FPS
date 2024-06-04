/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary.micro;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class GetAPILog {
    //<editor-fold defaultstate="collapsed" desc="Get API Log">
    /**
    * Get a log entry for an API request in the database.
    * <p>
    * Lấy một bản ghi log cho một yêu cầu API trong cơ sở dữ liệu.
    *
    * @param transactionId   A transaction ID for logging purposes.
    *                         Một ID giao dịch cho mục đích ghi log.
     * @return InternalResponse with List APILog
    */
    public static InternalResponse getAPILog(
            String transactionId
    ){
        try{
            DatabaseResponse response = DatabaseFactory.getDatabaseImpl().getAPILogs(transactionId);
            
            if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
                return CreateInternalResponse.createErrorInternalResponse(response.getStatus());
            }
            return new InternalResponse().setData(response.getObject());
        } catch(Exception ex){
            ex.printStackTrace();
            System.err.println("Cannot get API Log for transaction:"+transactionId);
            return CreateInternalResponse.createErrorInternalResponse(
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR, 
                    A_FPSConstant.CODE_API_LOG,
                    A_FPSConstant.SUBCODE_CANNOT_GET_APILOG)
                    .setException(ex);
        }
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        InternalResponse internalResponse = GetAPILog.getAPILog("");
        System.out.println("Code:"+internalResponse.getStatus());
    }
}
