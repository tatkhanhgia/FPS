/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.module;

import fps_core.enumration.DocumentStatus;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class CheckStatusOfDocument {
    //<editor-fold defaultstate="collapsed" desc="Check the status of Document">
    /**
     * Kiểm tra status của Document và trả về lỗi tương ứng Check the status of
     * the Document and return the error relative to that status
     *
     * @param document
     * @param transactionId
     * @return
     */
    public static InternalResponse checkStatusOfDocument(Document document, String transactionId) {
//        if (document.getStatus().equals(DocumentStatus.PROCESSING)) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                    A_FPSConstant.CODE_DOCUMENT,
//                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_PROCESSING
//            );
//        }
        if (document.getStatus().equals(DocumentStatus.PROCESSED)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_PROCESSING
            );
        }
        if (document.getStatus().equals(DocumentStatus.DELETED)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_DELETED
            );
        }
        if (document.getStatus().equals(DocumentStatus.PENDING)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_PENDING
            );
        }
        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
    }
    //</editor-fold>
}
