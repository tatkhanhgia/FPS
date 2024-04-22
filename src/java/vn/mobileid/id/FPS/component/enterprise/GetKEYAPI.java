/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.enterprise;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.general.database.DatabaseImpl_authorize;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
class GetKEYAPI{

    public static InternalResponse getKEYAPI(            
            String clientID,
            String transactionID) throws Exception {
        DatabaseImpl_authorize db = DatabaseFactory.getDatabaseImpl_authorize();
        
        DatabaseResponse res = db.getAPIKey(
                clientID,
                transactionID);

        if (res.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            System.out.println("Get that bai:");
            System.out.println("Code:"+res.getStatus());
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    res.getStatus()
            );
        }

        Enterprise user = (Enterprise) res.getObject();
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                user).setEnt(user);
    }
    
    public static void main(String[] arhs) throws Exception{
        InternalResponse res = GetKEYAPI.getKEYAPI("MI_MobileApp", "h9fSyjob8OF2SjlLSJY0");
        System.out.println("res:"+res.getStatus());
        System.out.println(((Enterprise)res.getData()).getClientID());
        System.out.println(((Enterprise)res.getData()).getClientSecret());
    }
}
