/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.authorize;

import java.util.Date;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.general.database.DatabaseImpl_authorize;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
class ManageRefreshToken {

    /**
     * Write a refresh token into DB
     *
     * @param email - Email of user
     * @param session - String Session
     * @param clientCredentials
     * @param issueAt
     * @param expiresAt
     * @param clientID
     * @param createdBy
     * @param transactionId
     * @param hmac
     * @return No Object => Check status
     * @throws Exception
     */
    public static InternalResponse write(
            String jwtId,
            String session,
            boolean clientCredentials,
            String clientID,
            Date issueAt,
            Date expiresAt,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception {
        DatabaseImpl_authorize db = DatabaseFactory.getDatabaseImpl_authorize();
        DatabaseResponse res = db.writeRefreshToken(
                session,
                jwtId,
                clientCredentials,
                clientID,
                issueAt,
                expiresAt,
                hmac,
                createdBy,
                transactionId);
        if (res.getStatus() != A_FPSConstant.CODE_SUCCESS) {
//            String message = ResponseMessageController.getErrorMessageAdvanced(
//                    A_FPSConstant.CODE_FAIL,
//                    res.getStatus(),
//                    "en",
//                    null);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    res.getStatus()
            );
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse checkToken(
            String jwtId,
            String sessionId,
            String transactionId
    ) throws Exception{
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_authorize().checkSessionId(
                jwtId,
                sessionId,
                transactionId);
        
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
            
    public static void main(String[] args) throws Exception {
//        InternalResponse response = ManageRefreshToken.write(
//                "helo",
//                "sessionId",
//                true,
//                "MI_MobileApp",
//                new Date(System.currentTimeMillis()),
//                new Date(System.currentTimeMillis()),
//                "hmac",
//                "GiaTK",
//                "transaction1");
//        System.out.println("status:" + response.getStatus());
        InternalResponse response = ManageRefreshToken.checkToken("jwtids", "sessionid", "transaction");
        System.out.println("Status:"+response.getStatus());
    }
}
