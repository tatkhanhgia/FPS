/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FileManagement;
import fps_core.objects.child.RadioBoxFieldAttributeV2;
import fps_core.objects.child.RadioFieldAttribute;
import fps_core.objects.core.BasicFieldAttribute;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.document.summary.micro.UploadDocument;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.systemManagement.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Crypto;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IModuleProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/**
 *
 * @author GiaTK
 */
class RadioProcessing extends IVersion implements IModuleProcessing, IDocumentProcessing {

    public RadioProcessing(Version version) {
        super(version);
    }

    @Override
    public InternalResponse createFormField(Object... objects) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Variable">
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        RadioFieldAttribute field = (RadioFieldAttribute) objects[3];
        String transactionId = (String) objects[4];
        byte[] file;
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check status of Document">
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download from FMS">
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>

        //Append data into field 
        try {
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);

            //<editor-fold defaultstate="collapsed" desc="Analysis file">
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
            //</editor-fold>

            //Append textField into file
            byte[] appendedFile = DocumentUtils_itext7.createRadioBoxFormField_gtk(file, field, transactionId);

            //<editor-fold defaultstate="collapsed" desc="Upload to FMS">
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
            //</editor-fold>

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
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Created Checkbox Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            return response;
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append checkbox value into file\"}"
            );
        }
    }

    @Override
    public InternalResponse fillFormField(Object... objects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InternalResponse deleteFormField(Object... objects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InternalResponse replaceFormField(Object... objects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        BasicFieldAttribute field = (BasicFieldAttribute) objects[4];
        ExtendedFieldAttribute extendField = (ExtendedFieldAttribute) objects[5];
        String transactionId = (String) objects[6];
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
            //Analys file
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);

            //Append CheckBoxField into file
            byte[] appendedFile = createRadioBox(file, field, transactionId);

            Future<?> analysis = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
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
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        //Upload new Document into FMS
                        response = FMS.uploadToFMS(appendedFile, "pdf", transactionId);
                        return response;
                    } catch (Exception ex) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_BAD_REQUEST);
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
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended RadioBox Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            //Update field after processing
            RadioFieldAttribute checkboxField = MyServices.getJsonService().readValue(extendField.getDetailValue(), RadioFieldAttribute.class);
            checkboxField = (RadioFieldAttribute) extendField.clone(checkboxField, extendField.getDimension());
            checkboxField.setProcessStatus(ProcessStatus.PROCESSED.getName());
            checkboxField.setProcessBy(field.getProcessBy());
            checkboxField.setProcessOn(field.getProcessOn());
            
            response = FieldSummaryInternal.updateValueOfField(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(checkboxField),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }

            //Update new data of RadioField
            response = FieldSummaryInternal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    )
                            .writeValueAsString(checkboxField),
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
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append radiobox value into file\"}"
            ).setException(ex);
        }
    }
    
    //=======================INTERNAL METHOD====================================
    
    //<editor-fold defaultstate="collapsed" desc="Call DocumentUtils to create RadioBox with Version">
    /**
     * Call DocumentUtils_i7.createRadioBox with Version
     *
     * @param pdf
     * @param field
     * @param transactionId
     * @return null if error
     */
    private byte[] createRadioBox(
            byte[] pdf,
            Object field,
            String transactionId
    ) throws Exception {
        switch (getVersion()) {
            case V1: {
                return DocumentUtils_itext7.createRadioBoxFormField_i7(pdf, (RadioFieldAttribute) field, transactionId);
            }
            case V2: {
                return DocumentUtils_itext7.createRadioBoxFormField_i7V2(pdf, (RadioBoxFieldAttributeV2) field, transactionId);
            }
            case V3: {
                return DocumentUtils_itext7.createRadioBoxFormField_i7V3(pdf, (RadioBoxFieldAttributeV2) field, transactionId);
            }
            default: {
                return DocumentUtils_itext7.createRadioBoxFormField_i7(pdf, (RadioFieldAttribute) field, transactionId);
            }
        }
    }
    //</editor-fold>
}
