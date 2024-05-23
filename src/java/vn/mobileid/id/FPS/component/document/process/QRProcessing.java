/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.core.QRFieldAttribute;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.module.QRGenerator;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.FPS.component.document.process.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
class QRProcessing implements IDocumentProcessing{

    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        //Variable
        User user = (User)objects[0];
        Document document = (Document)objects[1];
        long documentFieldId = (long)objects[2];
        QRFieldAttribute field = (QRFieldAttribute)objects[3];
        String transactionId = (String)objects[4];
        byte[] file;
        
        //Check status document
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_DOCUMENT,
                            A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }

        //Download document from FMS
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(), 
                transactionId);
                
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();

        //Append data into field 
        try {
            //Create QR Image
            byte[] qr = QRGenerator.generateQR(
                    field.getValue(), 
                    Math.round(field.getDimension().getWidth()), 
                    Math.round(field.getDimension().getHeight()), 
                    field.IsTransparent());
            
            byte[] resultODF = DocumentUtils_itext7.addImage(
                    file, 
                    qr, 
                    field.getPage(), 
                    field.getDimension().getX(), 
                    field.getDimension().getY(), 
                    field.getDimension().getWidth(), 
                    field.getDimension().getHeight());
            
           
            //Update field after processing
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());
            response = ConnectorField_Internal.updateValueOfField(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(field),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_DOCUMENT,
                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }

            //Update new data of TextField
            response = ConnectorField_Internal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(field),
                    "HMAC",
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_DOCUMENT,
                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                );
            }

            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    resultODF
            );
        } catch (Exception ex) {
            LogHandler.error(
                    TextFieldProcessing.class,
                    transactionId,
                    ex);
            response = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot add QR into file\"}"
            );
            response.setException(ex);
            return response;
        }
    }

}
