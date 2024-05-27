/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.FieldTypeName;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FileManagement;
import fps_core.objects.interfaces.AbstractReplicateField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.UploadDocument;
import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.component.field.ConnectorField_Internal;
import vn.mobileid.id.FPS.component.field.GetField;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.utils.Crypto;
import vn.mobileid.id.utils.TaskV2;
import vn.mobileid.id.FPS.component.document.process.interfaces.IDocumentProcessing;
import vn.mobileid.id.FPS.component.document.process.interfaces.IVersion;
import static vn.mobileid.id.FPS.component.document.process.interfaces.IVersion.Version.V1;
import static vn.mobileid.id.FPS.component.document.process.interfaces.IVersion.Version.V2;
import vn.mobileid.id.FPS.services.MyServices;

/**
 *
 * @author GiaTK
 */
public class AbstractReplicateProcessing<T extends AbstractReplicateField> extends IVersion implements IDocumentProcessing {
    private FieldTypeName fieldTypeName;
    
    private T field;
    
    private Function<T, byte[]> logicProcessMultipleField;
    
    private Function<T, byte[]> logicProcessField;
    
    private BiFunction<T, T, Boolean> logicAddField;
    
    private Function<T, Boolean> logicUploadFMS;
    
    public AbstractReplicateProcessing(
            T field,
            Version version,
            FieldTypeName fieldTypeName,
            BiFunction<T, T, Boolean> logicAddField,
            Function<T, byte[]> logicProcessField,
            Function<T, byte[]> logicProcessMultipleField,
            Function<T, Boolean> logicUploadFMS,
            String transactionId
    ){
        super(version);
        this.field = field;
        this.fieldTypeName = fieldTypeName;
        this.logicAddField = logicAddField;
        this.logicProcessField = logicProcessField;
        this.logicProcessMultipleField = logicProcessMultipleField;
        this.logicUploadFMS = logicUploadFMS;
    }
    
    @Override
    public InternalResponse processField(Object... objects) throws Exception {
        switch(getVersion()){
            case V1:
            case V2:{
                return flow(objects);
            }
        }
        return flow(objects);
    }
    

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Flow test">
    /**
     * If the field is have param "apply_to_all"
     * => Get all Initial from DB and store into a List
     * => Then call DocumentUtils_i7 to process all Initial field in the List
     * <p>
     * Otherwise call DocumentUtil_i7 to process one initial field
     * <p>
     * @param objects
     * @return
     * @throws Exception
     */
     public final InternalResponse flow(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
//        T field = (T) objects[4];
        T field = this.field;
        String transactionId = (String) objects[5];
        long documentIDOriginal = (long) objects[6];
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

        //<editor-fold defaultstate="collapsed" desc="Download Document from FMS">
        InternalResponse response = FMS.downloadDocumentFromFMS(document.getUuid(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        file = (byte[]) response.getData();
        //</editor-fold>

        //Append data into field 
        try {
            //<editor-fold defaultstate="collapsed" desc="Analysis File">
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
            //</editor-fold>

            byte[] appendedFile = null;

            //<editor-fold defaultstate="collapsed" desc="Check if Field is apply to all => create Multiple Field">
            if (field.isApplyToAll()) {
                if (!ProcessModuleForEnterprise.getInstance(user).getEnterprise().equals(
                        ProcessModuleForEnterprise.Enterprise.DOKOBIT_GATEWAY)) {
                    InternalResponse temp = GetField.getFieldsData(documentIDOriginal, transactionId);
                    if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return temp;
                    }
                    List<ExtendedFieldAttribute> fields = (List<ExtendedFieldAttribute>) temp.getData();
                    List<T> initFields = new ArrayList<>();
                    HashMap<String, Long> map = new HashMap<>();
                    for (ExtendedFieldAttribute initChild : fields) {
                        if (initChild.getType() != null
                                && initChild.getType().getParentType().equalsIgnoreCase(
                                        this.fieldTypeName.getParentName())) {
                            AbstractReplicateField fieldChild = MyServices.getJsonService().readValue(
                                    initChild.getFieldValue(),
                                    field.getClass());
                            fieldChild = (AbstractReplicateField) initChild.clone(fieldChild, initChild.getDimension());

                            if (logicAddField.apply((T) fieldChild, (T) field)) {
                                initFields.add((T) fieldChild);
                                map.put(fieldChild.getFieldName(), initChild.getDocumentFieldId());
                            }
                        }
                    }

                    appendedFile = logicProcessMultipleField.apply(field);

                    //<editor-fold defaultstate="collapsed" desc="Update all Initial Field">
                    for (T initChild : initFields) {
                        //Update field after processing
                        initChild.setProcessStatus(ProcessStatus.PROCESSED.getName());
                        response = ConnectorField_Internal.updateValueOfField(
                                map.get(initChild.getFieldName()),
                                user,
                                MyServices.getJsonService().writeValueAsString(initChild),
                                transactionId);
                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            return new InternalResponse(
                                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                    A_FPSConstant.CODE_DOCUMENT,
                                    A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
                            );
                        }

                        //Update new data of CheckboxField
                        response = ConnectorField_Internal.updateFieldDetail(
                                map.get(initChild.getFieldName()),
                                user,
                                MyServices.getJsonService().writeValueAsString(initChild),
                                "hmac",
                                transactionId);
                        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                            return new InternalResponse(
                                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                    A_FPSConstant.CODE_DOCUMENT,
                                    A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
                            );
                        }
                    }
                    //</editor-fold>
                }
                appendedFile = logicProcessField.apply(field);
            } else {
                appendedFile = logicProcessField.apply(field);
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Upload new Document into FMS">
            Future<?> uploadFMS = executor.submit(new TaskV2(new Object[]{appendedFile}, transactionId) {
                @Override
                public Object call() {
                    InternalResponse response = new InternalResponse();
                    try {
                        //Upload new Document into FMS
                        response = FMS.uploadToFMS((byte[]) this.get()[0], "pdf", transactionId);
                        return response;
                    } catch (Exception ex) {
                        response.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                        response.setException(ex);
                        response.setCode(A_FPSConstant.CODE_FMS);
                        response.setCodeDescription(A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
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
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Create new Revision of Document">
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended " + this.fieldTypeName.getParentName() + " Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update Field after processing">
            //<editor-fold defaultstate="collapsed" desc="Upload Image into FMS if need">
            logicUploadFMS.apply(field); 
            //</editor-fold>
            
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
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update new detail of Initial Field">
            response = ConnectorField_Internal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService(
                            new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                    )
                            .writeValueAsString(field),
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
            LogHandler.error(AbstractReplicateProcessing.class,
                    transactionId,
                    ex);
            InternalResponse res = new InternalResponse(
                    A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
                    A_FPSConstant.CODE_FIELD_INITIAL,
                    A_FPSConstant.SUBCODE_CANNOT_FILL_INITIALS
            );
            res.setException(ex);
            return res;
        }
    }
    //</editor-fold>
}
