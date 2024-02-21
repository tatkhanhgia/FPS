/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.FieldTypeName;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.SignatureFieldAttribute;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import vn.mobileid.id.FPS.component.field.AddField;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.Resources;

/**
 *
 * @author GiaTK
 */
public class PreserveFormField {

    //<editor-fold defaultstate="collapsed" desc="Preserve Form Field">
    public static InternalResponse preserve(
            long documentId,
            User user,
            byte[] data,
            String transactionId) throws Exception {
        List<SignatureFieldAttribute> lists = DocumentUtils_itext7.getAllSignatures(data);
        if (lists == null || lists.isEmpty()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS, ""
            );
        }
        
        //Get All Field
        
        
        for (SignatureFieldAttribute signature : lists) {
            signature.setType(Resources.getFieldTypes().get(FieldTypeName.SIGNATURE.getParentName()));
            InternalResponse response = AddField.addField(
                    documentId,
                    signature,
                    "hmac",
                    user.getEmail(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                continue;
            }
            int fieldId = (int) response.getData();
            response = AddField.addDetailField(
                    fieldId,
                    Resources.getFieldTypes().get(FieldTypeName.SIGNATURE.getParentName()).getTypeId(),
                    signature,
                    "hmac",
                    user.getEmail(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            
            ObjectMapper ob = new ObjectMapper();            
            response = ConnectorField_Internal.updateValueOfField(
                    fieldId,
                    user,
                    ob.writeValueAsString(signature),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_DOCUMENT,
                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS, ""
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws IOException {
        List<SignatureFieldAttribute> signatures = DocumentUtils_itext7.getAllSignatures(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.pdf")));
        for (SignatureFieldAttribute signature : signatures) {
            System.out.println("Name:" + signature.getFieldName());
            System.out.println("Dimension:" + signature.getDimension().getX());
            System.out.println("Dimension:" + signature.getDimension().getY());
            System.out.println("Dimension:" + signature.getDimension().getWidth());
            System.out.println("Dimension:" + signature.getDimension().getHeight());
        }
    }
}
