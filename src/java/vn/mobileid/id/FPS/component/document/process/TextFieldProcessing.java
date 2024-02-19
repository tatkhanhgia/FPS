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
import fps_core.objects.TextFieldAttribute;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.UploadDocument;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.enumration.ChildTextFieldTypeName;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.utils.Broadcast;
import vn.mobileid.id.utils.Crypto;
import vn.mobileid.id.utils.TaskV2;

/**
 *
 * @author GIATK
 */
class TextFieldProcessing implements DocumentProcessing, ModuleProcessing {

    /*
    Thực hiện luồng V1 => Tạo form field + append cùng 1 lúc tại thời điểm process
     */
    @Override
    public InternalResponse process(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        TextFieldAttribute field = (TextFieldAttribute) objects[4];
        String transactionId = (String) objects[5];
        byte[] file;
        Broadcast broadcast;

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
            //Analys file
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<?> analysis = executor.submit(new TaskV2(new Object[]{file}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });

            //Append textField into file
            byte[] appendedFile = null;
            if (field.getType().getTypeId() == ChildTextFieldTypeName.TEXT_FIELD.getId()) {
                appendedFile = DocumentUtils_itext7.createTextField_i7(file, field, transactionId);
            } else {
                appendedFile = DocumentUtils_itext7.createTextFormField_i7(file, field, transactionId);
            }

            //Upload to FMS
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
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
            fileManagement.setSize(appendedFile.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(appendedFile, fileManagement.getAlgorithm().getName())));
            response = (InternalResponse) uploadFMS.get();

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //Update new Document in DB    
            broadcast = new UploadDocument();
            response = broadcast.call(
                    broadcast.getMethod("uploadDocument", UploadDocument.class),
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended Text Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            //Update field after processing
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());
            ObjectMapper ob = new ObjectMapper();
//            ob.setAnnotationIntrospector(new IgnoreIngeritedIntrospector());
//            ob.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
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

            //Update new data of TextField
            response = ConnectorField_Internal.updateFieldDetail(
                    documentFieldId,
                    user,
                    ob.writeValueAsString(field),
                    uuid,
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
                    ""
            );
        } catch (Exception ex) {
            LogHandler.error(
                    TextFieldProcessing.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append text value into file\"}"
            ).setException(ex);
        }
    }

    @Override
    public InternalResponse createFormField(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        TextFieldAttribute field = (TextFieldAttribute) objects[3];
        String transactionId = (String) objects[4];
        byte[] file;
        Broadcast broadcast;

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
            //Analys file
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<?> analysis = executor.submit(new TaskV2(new Object[]{file}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });

            //Append textField into file
            byte[] appendedFile = DocumentUtils_itext7.createTextFormField_i7(file, field, transactionId);

            //Upload to FMS
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    byte[] appendedFile = (byte[]) this.get()[0];
                    try {
                        //Update new Document in FMS
                        response = FMS.uploadToFMS(appendedFile,
                                "pdf",
                                transactionId
                        );

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
            fileManagement.setSize(appendedFile.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(appendedFile, fileManagement.getAlgorithm().getName())));
            response = (InternalResponse) uploadFMS.get();

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //Update new Document in DB    
            broadcast = new UploadDocument();
            response = broadcast.call(
                    broadcast.getMethod("uploadDocument", UploadDocument.class),
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Created Text Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            return response;
        } catch (Exception ex) {
            LogHandler.error(
                    TextFieldProcessing.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append text value into file\"}"
            ).setException(ex);
        }
    }

    @Override
    public InternalResponse fillFormField(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        TextFieldAttribute field = (TextFieldAttribute) objects[3];
        String transactionId = (String) objects[4];
        byte[] file;
        Broadcast broadcast;

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
            //Analys file
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<?> analysis = executor.submit(new TaskV2(new Object[]{file}, transactionId) {
                @Override
                public Object call() {
                    try {
                        return DocumentUtils_itext7.analysisPDF_i7((byte[]) this.get()[0]);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            });

            //Append textField into file
            byte[] appendedFile = DocumentUtils_itext7.appendValue_i7(
                    file,
                    field,
                    field.getValue(),
                    transactionId);

            //Upload to FMS
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        //Update new Document in FMS
                        return FMS.uploadToFMS(appendedFile,
                                "pdf",
                                transactionId
                        );

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
            fileManagement.setSize(appendedFile.length);
            fileManagement.setDigest(Hex.encodeHexString(Crypto.hashData(appendedFile, fileManagement.getAlgorithm().getName())));
            response = (InternalResponse) uploadFMS.get();

            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            String uuid = (String) response.getData();

            //Update new Document in DB    
            broadcast = new UploadDocument();
            response = broadcast.call(
                    broadcast.getMethod("uploadDocument", UploadDocument.class),
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended Text Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            return response;
        } catch (Exception ex) {
            LogHandler.error(
                    TextFieldProcessing.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append text value into file\"}"
            ).setException(ex);
        }
    }

    @Override
    public InternalResponse deleteFormField(Object... objects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InternalResponse replaceFormField(Object... objects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
