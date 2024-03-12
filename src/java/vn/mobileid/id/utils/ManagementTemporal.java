/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class ManagementTemporal {
    
    //<editor-fold defaultstate="collapsed" desc="Add Temporal">
    public static InternalResponse addTemporal(
            String identifyName,
            String hash,
            int type,
            byte[] data,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl().temporalAdd(
                identifyName,
                hash,
                type,
                data,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, ""
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Temporal">
    public static InternalResponse getTemporal(
            String identifyName,
            String identifyName2,
            String transactionId) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl().temporalGet(
                identifyName,
                identifyName2,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, response.getObject()
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Remove Temporal">
    public static InternalResponse removeTemporal(
            String identifyName,
            String transactionId) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl().temporalDelete(
                identifyName,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, ""
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="List Temporal">
    public static InternalResponse listTemporal(
            String identifyName,
            String transactionId
    )throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl().temporalList(identifyName, transactionId);
        if(response.getStatus() != A_FPSConstant.CODE_SUCCESS){
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL, 
                            response.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
    
    
    public static void main(String[] args) throws Exception {
//        InternalResponse response;
//        response = ManagementTemporal.addTemporal(
//                "id",
//                TemporalObject.Type.TEMPORAL_DATA.getId(),
//                null, 
//                "");
//        
//        System.out.println(response.getStatus());



//        InternalResponse response = ManagementTemporal.getTemporal(
//                "id",
//                "");
//        TemporalObject temp = (TemporalObject)response.getData();
//        
//        System.out.println(response.getStatus());
//        System.out.println(temp.getIdentifyName());
//        System.out.println(temp.getType());
//        System.out.println(temp.getData().length);
        

        InternalResponse response;
        response = ManagementTemporal.removeTemporal(
                "id",                
                "");
        
        System.out.println(response.getStatus());
    }
}
