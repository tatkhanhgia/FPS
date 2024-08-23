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
import fps_core.objects.child.HyperLinkFieldAttribute;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.document.summary.micro.UploadDocument;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.utils.Crypto;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IModuleProcessing;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/**
 *
 * @author GIATK
 */
class HyperLinkProcessing implements IDocumentProcessing, IModuleProcessing {

    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        HyperLinkFieldAttribute field = (HyperLinkFieldAttribute) objects[4];
        ExtendedFieldAttribute extendField = (ExtendedFieldAttribute) objects[5];
        String transactionId = (String) objects[6];
        byte[] file;

        //<editor-fold defaultstate="collapsed" desc="Check status of document">
        if (document.isEnabled()) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_DOCUMENT_STATSUS_IS_DISABLE
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download document from FMS">
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>

        
        try {
            //Analys file
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);
            
            //Append HyperLink into file
            byte[] appendedFile = DocumentUtils_itext7.createHyperLinkV2(file, field, transactionId);

            //<editor-fold defaultstate="collapsed" desc="Thread 1: Analysis file">
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
            
            //<editor-fold defaultstate="collapsed" desc="Thread 2: Upload to FMS">
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        byte[] appendedFile = (byte[]) this.get()[0];
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

            //<editor-fold defaultstate="collapsed" desc="Update new Document in DB   ">
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "none",
                    uuid,
                    "Appended HyperLink - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update field after processing">
            HyperLinkFieldAttribute hyperLink = MyServices.getJsonService().readValue(extendField.getDetailValue(), HyperLinkFieldAttribute.class);
            hyperLink = (HyperLinkFieldAttribute) extendField.clone(hyperLink, extendField.getDimension());
            
            hyperLink.setProcessStatus(ProcessStatus.PROCESSED.getName());
            hyperLink.setProcessBy(field.getProcessBy());
            hyperLink.setProcessOn(field.getProcessOn());

            response = FieldSummaryInternal.updateValueOfField(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(hyperLink),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                );
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update new data of TextField">
            response = FieldSummaryInternal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    )
                            .writeValueAsString(hyperLink),
                    uuid,
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_DOCUMENT,
                        A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                );
            }
            //</editor-fold>

            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        } catch (Exception ex) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append text value into file\"}"
            ).setException(ex);
        }
    }

    @Override
    public InternalResponse createFormField(Object... objects) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

}
