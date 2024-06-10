/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary.micro;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.CreateInternalResponse;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateAPILog {
    //<editor-fold defaultstate="collapsed" desc="Update API Log">
    /**
     * Updates the file cache information in an existing API log entry.
     * <p>
     * Cập nhật thông tin về bộ nhớ cache tệp trong một mục nhật ký API hiện có.
     *
     * @param apiLogId     The unique identifier of the API log entry to update.
     *                     Mã định danh duy nhất của mục nhật ký API cần cập nhật.
     * @param fileCatch    The new file cache information to be stored.
     *                     Thông tin bộ nhớ cache tệp mới cần được lưu trữ.
     * @param modifiedBy   The user or system responsible for the modification.
     *                     Người dùng hoặc hệ thống chịu trách nhiệm cho việc sửa đổi.
     * @param transactionId The transaction ID for tracking and logging purposes.
     *                     ID giao dịch để theo dõi và ghi nhật ký.
     * @return An InternalResponse indicating the result of the update:
     *         - If successful, returns a response with HTTP code 200 and the updated API log object as data.
     *         - If an error occurs, returns an error response with an appropriate HTTP code and error details.
     *         <p>
     *         Một InternalResponse cho biết kết quả của việc cập nhật:
     *         - Nếu thành công, trả về một phản hồi với mã HTTP 200 và đối tượng nhật ký API đã cập nhật dưới dạng dữ liệu.
     *         - Nếu xảy ra lỗi, trả về phản hồi lỗi với mã HTTP thích hợp và chi tiết lỗi.
     */
    public static InternalResponse updateAPILog(
            String apiLogId,
            String fileCatch,
            String modifiedBy,
            String transactionId
    ) {
        try {
            DatabaseResponse response = DatabaseFactory.getDatabaseImpl().updateFileCatchAPILog(
                    apiLogId,
                    fileCatch,
                    modifiedBy,
                    transactionId);

            if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
                return CreateInternalResponse.createErrorInternalResponse(response.getStatus());
            }
            return new InternalResponse().setData(response.getObject());
        } catch (Exception ex) {
            LogHandler.error(UpdateAPILog.class, transactionId, ex);
            return CreateInternalResponse.createErrorInternalResponse(
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    A_FPSConstant.CODE_API_LOG,
                    A_FPSConstant.SUBCODE_CANNOT_GET_APILOG)
                    .setException(ex);
        }
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception{
        InternalResponse response  = UpdateAPILog.updateAPILog(
                "112",
                "helklo new", 
                "modified",
                "tran");
    }
}
