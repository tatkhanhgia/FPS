/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.document.summary.processingImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.FieldTypeName;
import fps_core.enumration.ProcessStatus;
import fps_core.module.DocumentUtils_itext7;
import fps_core.objects.core.CheckBoxFieldAttribute;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FileManagement;
import fps_core.objects.core.InitialsFieldAttribute;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.codec.binary.Hex;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.controller.document.summary.micro.UploadDocument;
import vn.mobileid.id.FPS.controller.enterprise.summary.micro.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.controller.field.summary.FieldSummaryInternal;
import vn.mobileid.id.FPS.controller.field.summary.micro.GetField;
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
import static vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion.Version.V1;
import static vn.mobileid.id.FPS.controller.document.summary.processingImpl.interfaces.IVersion.Version.V2;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;

/**
 *
 * @author GiaTK
 */
class InitialsProcessing extends IVersion implements IModuleProcessing, IDocumentProcessing {

    public InitialsProcessing(Version version) {
        super(version);
    }

    //<editor-fold defaultstate="collapsed" desc="Create Form Field">
    @Override
    public InternalResponse createFormField(Object... objects) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Variable">
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        CheckBoxFieldAttribute field = (CheckBoxFieldAttribute) objects[3];
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
            byte[] appendedFile = DocumentUtils_itext7.createCheckBoxFormField_i7(file, field, transactionId);

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
                        response.setException(ex);
                        response.setCode(A_FPSConstant.CODE_FMS);
                        response.setCodeDescription(A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS);
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
            ).setException(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fill Form Field">
    @Override
    public InternalResponse fillFormField(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        CheckBoxFieldAttribute field = (CheckBoxFieldAttribute) objects[3];
        String transactionId = (String) objects[4];
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

            //Update new Document in DB    
            response = UploadDocument.uploadDocument(
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
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"message\":\"Cannot append text value into file\"}"
            ).setException(ex);
        }
    }
    //</editor-fold>

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
        switch (getVersion()) {
            case V1: {
                return flow2(objects);
            }
            case V2: {
                return flow3(objects);
            }
            case V3:{
                return flow4(objects);
            }
            default: {
                return flow2(objects);
            }
        }
    }

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Flow create Intial 1">
    /**
     * I don't know this :))) I forgot it
     *
     * @param objects
     * @return
     * @throws Exception
     */
    private static InternalResponse flow1(
            Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        InitialsFieldAttribute field = (InitialsFieldAttribute) objects[4];
        String transactionId = (String) objects[5];
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

            //Append InitialField into file
            byte[] appendedFile = DocumentUtils_itext7.createInitialsForm(file, field, transactionId);

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

            //Update new Document in DB    
            response = UploadDocument.uploadDocument(
                    document.getPackageId(),
                    revision + 1,
                    fileManagement,
                    DocumentStatus.READY,
                    "url",
                    "contents",
                    uuid,
                    "Appended Initials Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            //Update field after processing
            field.setProcessStatus(ProcessStatus.PROCESSED.getName());
            response = FieldSummaryInternal.updateValueOfField(
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

            //Update new data of CheckboxField
            response = FieldSummaryInternal.updateFieldDetail(
                    documentFieldId,
                    user,
                    MyServices.getJsonService().writeValueAsString(field),
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

    //<editor-fold defaultstate="collapsed" desc="Flow create Intial 2">
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
    private static InternalResponse flow2(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        InitialsFieldAttribute field = (InitialsFieldAttribute) objects[4];
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
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);
            
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

            //<editor-fold defaultstate="collapsed" desc="Check if Field is apply to all => create Multiple Initial">
            if (field.isApplyToAll()) {
                if (!ProcessModuleForEnterprise.getInstance(user).getEnterprise().equals(
                        ProcessModuleForEnterprise.Enterprise.DOKOBIT_GATEWAY)) {
                    InternalResponse temp = GetField.getFieldsData(documentIDOriginal, transactionId);
                    if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return temp;
                    }
                    List<ExtendedFieldAttribute> fields = (List<ExtendedFieldAttribute>) temp.getData();
                    List<InitialsFieldAttribute> initFields = new ArrayList<>();
                    HashMap<String, Long> map = new HashMap<>();
                    for (ExtendedFieldAttribute initChild : fields) {
                        if (initChild.getType() != null
                                && initChild.getType().getParentType().equalsIgnoreCase(FieldTypeName.INITIAL.getParentName())) {
                            InitialsFieldAttribute fieldChild = MyServices.getJsonService().readValue(
                                    initChild.getFieldValue(),
                                    InitialsFieldAttribute.class);
                            fieldChild = (InitialsFieldAttribute) initChild.clone(fieldChild, initChild.getDimension());

                            if (fieldChild.getImage() == null
                                    || fieldChild.getImage().isEmpty()
                                    || fieldChild.getImage().length() <= 32) {
                                fieldChild.setImage(field.getImage());
                            }

                            initFields.add(fieldChild);
                            map.put(fieldChild.getFieldName(), initChild.getDocumentFieldId());
                        }
                    }

                    appendedFile = DocumentUtils_itext7.createMultipleInitialsForm(
                            file,
                            field.getImage(),
                            initFields,
                            transactionId);

                    //<editor-fold defaultstate="collapsed" desc="Update all Initial Field">
                    for (InitialsFieldAttribute initChild : initFields) {
                        //Update field after processing
                        initChild.setProcessStatus(ProcessStatus.PROCESSED.getName());
                        response = FieldSummaryInternal.updateValueOfField(
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
                        response = FieldSummaryInternal.updateFieldDetail(
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
                appendedFile = DocumentUtils_itext7.createInitialsForm(
                        file,
                        field,
                        transactionId);
            } else {
                appendedFile = DocumentUtils_itext7.createInitialsForm(
                        file,
                        field,
                        transactionId);
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
                    "Appended Initials Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update Field after processing">
            //<editor-fold defaultstate="collapsed" desc="Upload Image of Fill (field) into FMS and update new UUID into initField">
            try {
                InternalResponse callFMS = FMS.uploadToFMS(
                        Base64.getDecoder().decode(field.getImage()),
                        "png",
                        transactionId);
                if (callFMS.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    System.err.println("Cannot upload new Image of Initial into FMS! Using default");
                } else {
                    String uuid_ = (String) callFMS.getData();
                    field.setImage(uuid_);
                }
            } catch (Exception ex) {
                System.err.println("Cannot upload new Image of Initial into FMS! Using default");
            }
            //</editor-fold>

            field.setProcessStatus(ProcessStatus.PROCESSED.getName());

            response = FieldSummaryInternal.updateValueOfField(
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
            response = FieldSummaryInternal.updateFieldDetail(
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

    //<editor-fold defaultstate="collapsed" desc="Flow create Initial 2 Version 2">
    /**
     * Like flow 2 but have the update that call new create InitialField Version2
     *
     * @param objects
     * @return
     * @throws Exception
     */
    private static InternalResponse flow3(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        InitialsFieldAttribute field = (InitialsFieldAttribute) objects[4];
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
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);
            
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

            //<editor-fold defaultstate="collapsed" desc="Check if Field is apply to all => create Multiple Initial">
            if (field.isApplyToAll()) {
//                if(!ProcessModuleForEnterprise.getInstance(user).getEnterprise().equals(
//                        ProcessModuleForEnterprise.Enterprise.DOKOBIT_GATEWAY)){
//                InternalResponse temp = GetField.getFieldsData(documentIDOriginal, transactionId);
//                if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                    return temp;
//                }
//                List<ExtendedFieldAttribute> fields = (List<ExtendedFieldAttribute>) temp.getData();
//                List<InitialsFieldAttribute> initFields = new ArrayList<>();
//                HashMap<String, Long> map = new HashMap<>();
//                for (ExtendedFieldAttribute initChild : fields) {
//                    if (initChild.getType() != null 
//                            && initChild.getType().getParentType().equalsIgnoreCase(FieldTypeName.INITIAL.getParentName())) {
//                        InitialsFieldAttribute fieldChild = MyServices.getJsonService().readValue(
//                                initChild.getFieldValue(),
//                                InitialsFieldAttribute.class);
//                        fieldChild = (InitialsFieldAttribute) initChild.clone(fieldChild, initChild.getDimension());
//
//                        if (fieldChild.getImage() == null
//                                || fieldChild.getImage().isEmpty()
//                                || fieldChild.getImage().length() <= 32) {
//                            fieldChild.setImage(field.getImage());
//                        }
//
//                        initFields.add(fieldChild);
//                        map.put(fieldChild.getFieldName(), initChild.getDocumentFieldId());
//                    }
//                }
//
//                appendedFile = DocumentUtils_itext7.createMultipleInitialsFormV2(
//                        file,
//                        field.getImage(),
//                        initFields,
//                        transactionId);
//
//                //<editor-fold defaultstate="collapsed" desc="Update all Initial Field">
//                for (InitialsFieldAttribute initChild : initFields) {
//                    //Update field after processing
//                    initChild.setProcessStatus(ProcessStatus.PROCESSED.getName());
//                    ObjectMapper ob = MyServices.getJsonService();
//                    response = FieldSummaryInternal.updateValueOfField(
//                            map.get(initChild.getFieldName()),
//                            user,
//                            ob.writeValueAsString(initChild),
//                            transactionId);
//                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_DOCUMENT,
//                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
//                        );
//                    }
//
//                    //Update new data of CheckboxField
//                    response = FieldSummaryInternal.updateFieldDetail(
//                            map.get(initChild.getFieldName()),
//                            user,
//                            ob.writeValueAsString(initChild),
//                            "hmac",
//                            transactionId);
//                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_DOCUMENT,
//                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
//                        );
//                    }
//                }
//                //</editor-fold>
//            }
            } else {
                appendedFile = DocumentUtils_itext7.createInitialsFormV2(
                        file,
                        field,
                        transactionId);
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
                    "Appended Initials Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update Field after processing">
            //<editor-fold defaultstate="collapsed" desc="Upload Image of Fill (field) into FMS and update new UUID into initField">
            try {
                InternalResponse callFMS = FMS.uploadToFMS(
                        Base64.getDecoder().decode(field.getImage()),
                        "png",
                        transactionId);
                if (callFMS.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    System.err.println("Cannot upload new Image of Initial into FMS! Using default");
                } else {
                    String uuid_ = (String) callFMS.getData();
                    field.setImage(uuid_);
                }
            } catch (Exception ex) {
                System.err.println("Cannot upload new Image of Initial into FMS! Using default");
            }
            //</editor-fold>

            field.setProcessStatus(ProcessStatus.PROCESSED.getName());

            response = FieldSummaryInternal.updateValueOfField(
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
            response = FieldSummaryInternal.updateFieldDetail(
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
    
    //<editor-fold defaultstate="collapsed" desc="Flow create Initial 2 Version 3">
    /**
     * Like flow 2 but have the update that call new create InitialField Version2
     *
     * @param objects
     * @return
     * @throws Exception
     */
    private static InternalResponse flow4(Object... objects) throws Exception {
        //Variable
        User user = (User) objects[0];
        Document document = (Document) objects[1];
        int revision = (int) objects[2];
        long documentFieldId = (long) objects[3];
        InitialsFieldAttribute field = (InitialsFieldAttribute) objects[4];
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
//            ExecutorService executor = Executors.newFixedThreadPool(2);
            ThreadManagement executor = MyServices.getThreadManagement(2);

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

            //<editor-fold defaultstate="collapsed" desc="Check if Field is apply to all => create Multiple Initial">
            if (field.isApplyToAll()) {
//                if(!ProcessModuleForEnterprise.getInstance(user).getEnterprise().equals(
//                        ProcessModuleForEnterprise.Enterprise.DOKOBIT_GATEWAY)){
//                InternalResponse temp = GetField.getFieldsData(documentIDOriginal, transactionId);
//                if (temp.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                    return temp;
//                }
//                List<ExtendedFieldAttribute> fields = (List<ExtendedFieldAttribute>) temp.getData();
//                List<InitialsFieldAttribute> initFields = new ArrayList<>();
//                HashMap<String, Long> map = new HashMap<>();
//                for (ExtendedFieldAttribute initChild : fields) {
//                    if (initChild.getType() != null 
//                            && initChild.getType().getParentType().equalsIgnoreCase(FieldTypeName.INITIAL.getParentName())) {
//                        InitialsFieldAttribute fieldChild = MyServices.getJsonService().readValue(
//                                initChild.getFieldValue(),
//                                InitialsFieldAttribute.class);
//                        fieldChild = (InitialsFieldAttribute) initChild.clone(fieldChild, initChild.getDimension());
//
//                        if (fieldChild.getImage() == null
//                                || fieldChild.getImage().isEmpty()
//                                || fieldChild.getImage().length() <= 32) {
//                            fieldChild.setImage(field.getImage());
//                        }
//
//                        initFields.add(fieldChild);
//                        map.put(fieldChild.getFieldName(), initChild.getDocumentFieldId());
//                    }
//                }
//
//                appendedFile = DocumentUtils_itext7.createMultipleInitialsFormV2(
//                        file,
//                        field.getImage(),
//                        initFields,
//                        transactionId);
//
//                //<editor-fold defaultstate="collapsed" desc="Update all Initial Field">
//                for (InitialsFieldAttribute initChild : initFields) {
//                    //Update field after processing
//                    initChild.setProcessStatus(ProcessStatus.PROCESSED.getName());
//                    ObjectMapper ob = MyServices.getJsonService();
//                    response = FieldSummaryInternal.updateValueOfField(
//                            map.get(initChild.getFieldName()),
//                            user,
//                            ob.writeValueAsString(initChild),
//                            transactionId);
//                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_DOCUMENT,
//                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD
//                        );
//                    }
//
//                    //Update new data of CheckboxField
//                    response = FieldSummaryInternal.updateFieldDetail(
//                            map.get(initChild.getFieldName()),
//                            user,
//                            ob.writeValueAsString(initChild),
//                            "hmac",
//                            transactionId);
//                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_DOCUMENT,
//                                A_FPSConstant.SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS
//                        );
//                    }
//                }
//                //</editor-fold>
//            }
            } else {
                appendedFile = DocumentUtils_itext7.createInitialsFormV3(
                        file,
                        field,
                        transactionId);
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
                    "Appended Initials Field - " + field.getFieldName(),
                    "hmac",
                    user.getAzp(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Update Field after processing">
            //<editor-fold defaultstate="collapsed" desc="Upload Image of Fill (field) into FMS and update new UUID into initField">
            try {
                InternalResponse callFMS = FMS.uploadToFMS(
                        Base64.getDecoder().decode(field.getImage()),
                        "png",
                        transactionId);
                if (callFMS.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    System.err.println("Cannot upload new Image of Initial into FMS! Using default");
                } else {
                    String uuid_ = (String) callFMS.getData();
                    field.setImage(uuid_);
                }
            } catch (Exception ex) {
                System.err.println("Cannot upload new Image of Initial into FMS! Using default");
            }
            //</editor-fold>

            field.setProcessStatus(ProcessStatus.PROCESSED.getName());

            response = FieldSummaryInternal.updateValueOfField(
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
            response = FieldSummaryInternal.updateFieldDetail(
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
