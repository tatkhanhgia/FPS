/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.FileManagement;
import fps_core.objects.ImageFieldAttribute;
import fps_core.objects.QRFieldAttribute;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.UploadDocument;
import vn.mobileid.id.FPS.component.document.module.QRGenerator;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.utils.Crypto;
import vn.mobileid.id.utils.TaskV2;

/**
 *
 * @author GiaTK
 */
public class ImageProcessing implements DocumentProcessing{

    @Override
    public InternalResponse process(Object... objects) throws Exception {
        //Variable
        User user = (User)objects[0];
        Document document = (Document)objects[1];
        long documentFieldId = (long)objects[2];
        ImageFieldAttribute field = (ImageFieldAttribute)objects[3];
        String transactionId = (String)objects[4];
        int revision = (int) objects[5];
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
            byte[] image = Base64.getDecoder().decode(field.getImage());
            
            byte[] resultODF = DocumentUtils_itext7.addImage(
                    file, 
                    image, 
                    field.getPage(), 
                    field.getDimension().getX(), 
                    field.getDimension().getY(), 
                    field.getDimension().getWidth(), 
                    field.getDimension().getHeight());
            
           
            //Analys file
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<?> analysis = executor.submit(new TaskV2(new Object[]{resultODF}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });
            
            //Upload to FMS
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{resultODF}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        byte[] appendedFile = (byte[])this.get()[0];
                        response = FMS.uploadToFMS(appendedFile, "pdf", transactionId);
                        return response;
                    } catch (Exception ex) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                        response.setCode(A_FPSConstant.CODE_FMS);
                        response.setCodeDescription(A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
                        response.setException(ex);
                    }
                    return response;
                }
            });

            executor.shutdown();
            
            FileManagement fileManagement = (FileManagement) analysis.get();
            if (fileManagement == null) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_CANNOT_ANNALYSIS_THE_DOCUMENT
                );
            }
            fileManagement.setSize(resultODF.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(resultODF, fileManagement.getAlgorithm().getName())));
            response = (InternalResponse) uploadFMS.get();

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //Update new Document in DB    
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended Image - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            
            //Update field after processing
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());
            ObjectMapper ob = new ObjectMapper();
            response = ConnectorField_Internal.updateValueOfField(
                    documentFieldId,
                    user,
                    ob.writeValueAsString(field),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_DOCUMENT,
                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }

            //Update new data of ImageField
            response = ConnectorField_Internal.updateFieldDetail(
                    documentFieldId,
                    user,
                    ob.writeValueAsString(field),
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
                    "{\"message\":\"Cannot add image into file\"}"
            );
            response.setException(ex);
            return response;
        }
    }
    
}
