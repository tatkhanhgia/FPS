/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.authorize.summary.micro;

import java.util.Date;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.database.interfaces.IAuthorizeDB;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class ManageRefreshToken {

    //<editor-fold defaultstate="collapsed" desc="Write Token">
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
        IAuthorizeDB db = DatabaseFactory.getDatabaseImpl_authorize();
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
            return CreateInternalResponse.createBadRequestInternalResponse(res.getStatus());
        }
        return new InternalResponse();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check valid of Session Token">
    /**
     * Checks the validity of a token (JWT ID) and its associated session ID for a given transaction.
     * <p>
     * Kiểm tra tính hợp lệ của một mã token (JWT ID) và ID phiên liên kết của nó cho một giao dịch cụ thể.
     *
     * @param jwtId The JWT ID to check.
     *              Mã JWT cần kiểm tra.
     * @param sessionId The session ID associated with the JWT.
     *                  ID phiên liên kết với JWT.
     * @param transactionId The ID of the transaction.
     *                      ID của giao dịch.
     * @return An InternalResponse indicating the result of the token check:
     *          - If the token and session ID are valid, returns a successful response (HTTP code 200, status code SUCCESS).
     *          - If the token or session ID is invalid, returns an error response (HTTP code 400, status code indicating the specific error).
     * <p>
     *         Một InternalResponse cho biết kết quả kiểm tra mã token:
     *         - Nếu mã token và ID phiên hợp lệ, trả về phản hồi thành công (mã HTTP 200, mã trạng thái SUCCESS).
     *         - Nếu mã token hoặc ID phiên không hợp lệ, trả về phản hồi lỗi (mã HTTP 400, mã trạng thái cho biết lỗi cụ thể).
     * @throws Exception If an unexpected error occurs during the token check.
     *         Nếu có lỗi bất ngờ xảy ra trong quá trình kiểm tra mã token.
     */
    public static InternalResponse checkToken(
            String jwtId,
            String sessionId,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_authorize().checkSessionId(
                jwtId,
                sessionId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return CreateInternalResponse.createBadRequestInternalResponse(response.getStatus());
        }
        return new InternalResponse();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Delete Token">
    /**
    * Deletes a token associated with the given session ID for a specific transaction.
    * <p>
    * Xóa một mã token liên kết với ID phiên được cung cấp cho một giao dịch cụ thể.
    *
    * @param sessionId     The ID of the session whose token should be deleted.
    *                      ID của phiên mà mã token cần được xóa.
    * @param transactionId The ID of the transaction.
    *                      ID của giao dịch.
    * @return An InternalResponse indicating the result of the deletion:
    *         - If the deletion is successful, returns a successful response (HTTP code 200, status code SUCCESS).
    *         - If the deletion fails, returns an error response (HTTP code 400, status code indicating the specific error).
    *         <p>
    *         Một InternalResponse cho biết kết quả của việc xóa:
    *         - Nếu xóa thành công, trả về phản hồi thành công (mã HTTP 200, mã trạng thái SUCCESS).
    *         - Nếu xóa không thành công, trả về phản hồi lỗi (mã HTTP 400, mã trạng thái cho biết lỗi cụ thể).
    * @throws Exception If an unexpected error occurs during the deletion process.
    *                   Nếu có lỗi bất ngờ xảy ra trong quá trình xóa.
    */
    public static InternalResponse deleteToken(
            String sessionId,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_authorize().deleteToken(
                sessionId,
                transactionId);
 
        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return CreateInternalResponse.createBadRequestInternalResponse(response.getStatus());
        }  
        return new InternalResponse();
    }
    //</editor-fold>

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
//        InternalResponse response = ManageRefreshToken.checkToken("jwtids", "sessionid", "transaction");
//        System.out.println("Status:" + response.getStatus());

          InternalResponse response  = ManageRefreshToken.deleteToken("1686-16354-27159", "");
          System.out.println(response.getStatus());
          
    }
}
