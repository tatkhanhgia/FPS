/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.util.summary.micro;

import java.util.HashSet;
import java.util.Set;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.database.implement.DatabaseImpl;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class CreateAPILog {
    //<editor-fold defaultstate="collapsed" desc="Create API Log">
    /**
    * Creates a log entry for an API request in the database.
    * <p>
    * Tạo một bản ghi log cho một yêu cầu API trong cơ sở dữ liệu.
    *
    * @param pENTERPRISE_ID   The ID of the enterprise associated with the API request.
    *                         ID của doanh nghiệp liên quan đến yêu cầu API.
    * @param pDOCUMENT_ID     The ID of the document related to the API request (if applicable).
    *                         ID của tài liệu liên quan đến yêu cầu API (nếu có).
    * @param pTRANSACTION_ID  The unique identifier for the API transaction.
    *                         Trình định danh duy nhất cho giao dịch API.
    * @param pAPP_NAME        The name of the application making the API request.
    *                         Tên của ứng dụng thực hiện yêu cầu API.
    * @param pAPI_KEY         The API key used for authentication.
    *                         Khóa API được sử dụng để xác thực.
    * @param pVERSION         The version of the API being called.
    *                         Phiên bản của API được gọi.
    * @param pSERVICE_NAME    The name of the API service being called.
    *                         Tên của dịch vụ API được gọi.
    * @param pURL            The URL of the API endpoint.
    *                         URL của điểm cuối API.
    * @param pHTTP_VERB      The HTTP method used (e.g., GET, POST, PUT, DELETE).
    *                         Phương thức HTTP được sử dụng (ví dụ: GET, POST, PUT, DELETE).
    * @param pSTATUS_CODE    The HTTP status code of the API response.
    *                         Mã trạng thái HTTP của phản hồi API.
    * @param pHEADERS        The request headers sent with the API request.
    *                         Các tiêu đề yêu cầu được gửi kèm theo yêu cầu API.
    * @param pFILECACHED     The file cached (if applicable).
    *                         Tệp được lưu trong bộ nhớ cache (nếu có).
    * @param pREQUEST        The request body of the API call.
    *                         Nội dung yêu cầu của gọi API.
    * @param pRESPONSE       The response body from the API.
    *                         Nội dung phản hồi từ API.
    * @param pEXCEPTION      Any exception message (if an error occurred).
    *                         Bất kỳ thông báo ngoại lệ nào (nếu có lỗi xảy ra).
    * @param pHMAC           The HMAC signature for authentication (if applicable).
    *                         Chữ ký HMAC để xác thực (nếu có).
    * @param pCREATED_BY     The user or system that initiated the API request.
    *                         Người dùng hoặc hệ thống đã khởi tạo yêu cầu API.
    * @param transactionId   A transaction ID for logging purposes.
    *                         Một ID giao dịch cho mục đích ghi log.
    */
    public static void createAPILog(
            long pENTERPRISE_ID, 
            long pDOCUMENT_ID,
            String pTRANSACTION_ID,
            String pAPP_NAME, 
            String pAPI_KEY, 
            String pVERSION,
            String pSERVICE_NAME, 
            String pURL, 
            String pHTTP_VERB, 
            int pSTATUS_CODE, 
            String pHEADERS,
            String pFILECACHED,
            String pREQUEST, 
            String pRESPONSE, 
            String pEXCEPTION,
            String pHMAC, 
            String pCREATED_BY,
            String transactionId
    ){
        try{
            DatabaseResponse response = DatabaseFactory.getDatabaseImpl().createAPILog(
                    pENTERPRISE_ID, 
                    pDOCUMENT_ID,
                    pTRANSACTION_ID,
                    pAPP_NAME,
                    pAPI_KEY, 
                    pVERSION, 
                    pSERVICE_NAME, 
                    pURL, 
                    pHTTP_VERB,
                    pSTATUS_CODE,
                    pHEADERS,
                    pFILECACHED,
                    pREQUEST,
                    pRESPONSE, 
                    pEXCEPTION,
                    pHMAC, 
                    pCREATED_BY, 
                    transactionId);
        } catch(Exception ex){
            System.err.println("Cannot create API Log for transaction:"+transactionId);
        }
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        createAPILog(
                0, 
                0,
                "app",
                "app", 
                "api", 
                "ver", 
                "ser",
                "url",
                "post", 
                100,
                "header",
                "file cached",
                "reques", 
                "resp", 
                "ex",
                "hmac",
                "gia", 
                "transactionId");
    }
}
