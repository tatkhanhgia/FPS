/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import java.util.Scanner;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.object.DocumentStatus;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.database.DatabaseFactory;
import vn.mobileid.id.helper.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Broadcast;

/**
 *
 * @author GiaTK
 */
public class UpdateDocument extends Broadcast{

    //<editor-fold defaultstate="collapsed" desc="Update status of the Document">
    /**
     * Cập nhật status của Document Update Status of Document
     *
     * @param documentId
     * @param status
     * @param transactionId
     * @return
     * @throws java.lang.Exception
     */
    protected static InternalResponse updateStatusOfDocument(
            long documentId,
            User user,
            DocumentStatus status,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = DatabaseFactory.getDatabaseImpl_document().updateStatusOfDocument(
                documentId,
                user,
                status,
                transactionId);

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            response.getStatus()
            );
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, ""
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
        System.out.println("1.Update status");
        int input = new Scanner(System.in).nextInt();
        switch (input) {
            case 1: {
                //Update status of the Document
                User user = new User();
                user.setEmail("GIATK");
                InternalResponse response = UpdateDocument.updateStatusOfDocument(
                        23,
                        user,
                        DocumentStatus.DELETED,
                        "transactionId");
                System.out.println("Status:" + response.getStatus());
                break;
            }
        }

    }
}
