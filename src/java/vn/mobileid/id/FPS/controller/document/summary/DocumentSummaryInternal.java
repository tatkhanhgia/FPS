/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary;

import vn.mobileid.id.FPS.controller.document.summary.micro.GetDocument;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class DocumentSummaryInternal {
    public static InternalResponse getDocuments(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        long packageId = Utils.getIdFromURL(request.getRequestURI());
        if (packageId == 0) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_NOT_FOUND,
                        A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_MISSING_UUID
            );
        }
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        response = GetDocument.getDocuments(packageId, transactionId);
        response.setUser(user);
        return response;
    }
}
