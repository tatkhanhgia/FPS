/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.enterprise;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.Enterprise;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
class GetEnterpriseInfo {

    //<editor-fold defaultstate="collapsed" desc="Get Enterprise Info">
    /**
     * Trả về thông tin của enterprise - Return the information data of the enterprise
     * @param id
     * @param transactionId
     * @return Enterprise
     * @throws Exception 
     */
    protected static InternalResponse getEnterpriseInfo(
            int id,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_enterprise().getEntepriseInfo(
                id,
                null,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                (Enterprise) response.getObject()).setEnt((Enterprise) response.getObject());
    }
    //</editor-fold>

    public static void main(String[] arhs) throws Exception {
        InternalResponse res = GetEnterpriseInfo.getEnterpriseInfo(3, "hello");
        System.out.println("Status:" + res.getStatus());
        if (res.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            System.out.println("Error:" + res.getMessage());
        } else {
            System.out.println(((Enterprise) res.getData()).getCreatedAt());
        }
    }
}
