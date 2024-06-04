/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.utils;

import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class CreateInternalResponse {
    //<editor-fold defaultstate="collapsed" desc="Create  Error Internal Response">
    /**
    * Creates an `InternalResponse` object representing a Bad Request (HTTP 400) error with the specified sub-code.
    * <p>
    * Tạo một đối tượng `InternalResponse` biểu thị lỗi Bad Request (HTTP 400) với mã phụ được chỉ định.
    *
    * @param subCode  A more specific sub-code for the error, providing additional details.
    *                 Một mã phụ cụ thể hơn cho lỗi, cung cấp thêm chi tiết.
    * @return An InternalResponse object representing a Bad Request error with the given sub-code.
    *         Một đối tượng InternalResponse đại diện cho lỗi Bad Request với mã phụ đã cho.
    */
    public static InternalResponse createErrorInternalResponse(
            int subCode
    ){
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                A_FPSConstant.CODE_FAIL,
                subCode
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Create Error Internal Response">
    /**
    * Creates an `InternalResponse` object representing an error condition.
    * <p>
    * Tạo một đối tượng `InternalResponse` biểu thị một điều kiện lỗi.
    *
    * @param code     A custom error code to further categorize the error.
    *                 Một mã lỗi tùy chỉnh để phân loại thêm lỗi.
    * @param subCode  A more specific sub-code for the error, providing additional details.
    *                 Một mã phụ cụ thể hơn cho lỗi, cung cấp thêm chi tiết.
    * @return An InternalResponse object representing the error with the specified codes.
    *         Một đối tượng InternalResponse đại diện cho lỗi với các mã được chỉ định.
    */
    public static InternalResponse createErrorInternalResponse(
            int code,
            int subCode
    ){
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                code,
                subCode);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Create Error Internal Response">
    /**
    * Creates an `InternalResponse` object representing an error condition.
    * <p>
    * Tạo một đối tượng `InternalResponse` biểu thị một điều kiện lỗi.
    *
    * @param httpCode The HTTP status code associated with the error (e.g., 400 for Bad Request, 500 for Internal Server Error).
    *                 Mã trạng thái HTTP liên quan đến lỗi (ví dụ: 400 cho Yêu cầu không hợp lệ, 500 cho Lỗi Máy chủ Nội bộ).
    * @param code     A custom error code to further categorize the error.
    *                 Một mã lỗi tùy chỉnh để phân loại thêm lỗi.
    * @param subCode  A more specific sub-code for the error, providing additional details.
    *                 Một mã phụ cụ thể hơn cho lỗi, cung cấp thêm chi tiết.
    * @return An InternalResponse object representing the error with the specified codes.
    *         Một đối tượng InternalResponse đại diện cho lỗi với các mã được chỉ định.
    */
    public static InternalResponse createErrorInternalResponse(
            int httpCode,
            int code,
            int subCode
    ){
        return new InternalResponse(httpCode,code,subCode);
    }
    //</editor-fold>
}
