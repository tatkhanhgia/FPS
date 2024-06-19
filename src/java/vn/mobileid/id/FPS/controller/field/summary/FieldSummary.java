/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller.field.summary;

import vn.mobileid.id.FPS.controller.field.summary.micro.AddField;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.RotateDegree;
import fps_core.objects.child.AttachmentFieldAttribute;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.child.CameraFieldAttribute;
import fps_core.objects.core.CheckBoxFieldAttribute;
import fps_core.objects.Dimension;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FieldType;
import fps_core.objects.child.ComboBoxFieldAttribute;
import fps_core.objects.child.DateTimeFieldAttribute;
import fps_core.objects.child.HyperLinkFieldAttribute;
import fps_core.objects.child.NumericStepperAttribute;
import fps_core.objects.child.RadioBoxFieldAttributeV2;
import fps_core.objects.core.FileFieldAttribute;
import fps_core.objects.core.InitialsFieldAttribute;
import fps_core.objects.core.QRFieldAttribute;
import fps_core.objects.child.RadioFieldAttribute;
import fps_core.objects.child.ToggleFieldAttribute;
import fps_core.objects.core.CheckBoxFieldAttributeV2;
import fps_core.objects.core.SignatureFieldAttribute;
import fps_core.objects.core.TextFieldAttribute;
import fps_core.objects.interfaces.AbstractReplicateField;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.bouncycastle.util.encoders.Base64;
import vn.mobileid.id.FPS.controller.fms.FMS;
import vn.mobileid.id.FPS.services.others.qryptoService.object.ItemDetails;
import vn.mobileid.id.FPS.services.others.qryptoService.object.Item_IDPicture4Label.IDPicture4Label;
import vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType;
import static vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType.Binary;
import static vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType.File;
import static vn.mobileid.id.FPS.services.others.qryptoService.object.ItemsType.ID_Picture_with_4_labels;
import static vn.mobileid.id.FPS.controller.document.summary.module.CheckStatusOfDocument.checkStatusOfDocument;
import vn.mobileid.id.FPS.controller.document.summary.DocumentSummary;
import vn.mobileid.id.FPS.controller.document.summary.DocumentSummaryInternal;
import vn.mobileid.id.FPS.controller.document.summary.micro.GetDocument;
import vn.mobileid.id.FPS.controller.document.summary.micro.UpdateDocument;
import vn.mobileid.id.FPS.controller.document.summary.module.QRGenerator;
import vn.mobileid.id.FPS.controller.document.summary.processingImpl.ProcessingFactory;
import vn.mobileid.id.FPS.controller.enterprise.summary.micro.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.services.others.responseMessage.ResponseMessageController;
import vn.mobileid.id.FPS.controller.field.summary.module.CheckFieldProcessedYet;
import vn.mobileid.id.FPS.controller.field.summary.micro.DeleteField;
import vn.mobileid.id.FPS.controller.field.summary.micro.GetField;
import vn.mobileid.id.FPS.controller.field.summary.module.ParseToField;
import vn.mobileid.id.FPS.controller.field.summary.module.ReplicateInitialField;
import vn.mobileid.id.FPS.controller.field.summary.micro.UpdateField;
import vn.mobileid.id.FPS.object.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.FieldsSerializer;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.PolicyConfiguration;
import vn.mobileid.id.FPS.systemManagement.Resources;
import vn.mobileid.id.FPS.utils.ManagementTemporal;
import vn.mobileid.id.FPS.services.others.threadManagement.TaskV2;
import vn.mobileid.id.FPS.services.others.threadManagement.ThreadManagement;
import vn.mobileid.id.FPS.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class FieldSummary {

    // <editor-fold defaultstate="collapsed" desc="Add Field">
    /**
     * Add field
     *
     * @param request
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse addField(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog = new fps_core.utils.LogHandler.HierarchicalLog("Add field");

        //<editor-fold defaultstate="collapsed" desc="Get Documents in Package && Verify Token">
//        hierarchicalLog.addStartHeading1("Start get Document + verify");
        InternalResponse response = DocumentSummaryInternal.getDocuments(request, transactionId);
        if (!response.isValid()) {
            hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
            hierarchicalLog.addEndHeading1("Get Document + verify => False");
            return response.setHierarchicalLog(hierarchicalLog);
        }

        User user = response.getUser();
        List<Document> listDoc = (List<Document>) response.getData();
        Document document = new Document();
        for (Document element : listDoc) {
            if (element.getRevision() == 1) {
                document = element;
                break;
            }
        }
        hierarchicalLog.addEndHeading1("Get Document and Verify token successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
//        hierarchicalLog.addStartHeading1("Checking payload");
        if (Utils.isNullOrEmpty(payload)) {
            hierarchicalLog.addEndHeading1("Checking payload fail");
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        hierarchicalLog.addEndHeading1("Checking payload successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse payload in input into Field Object in FPS">
        hierarchicalLog.addStartHeading1("Start parse to field");
        response = parseToField(
                request.getRequestURI(),
                payload,
                true,
                false,
                transactionId);

        hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            hierarchicalLog.addEndHeading1("End parse to field => False");
            return response.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        hierarchicalLog.addEndHeading1("Parse to field successfully");

        BasicFieldAttribute field = (BasicFieldAttribute) response.getData();
        //</editor-fold>

//        //<editor-fold defaultstate="collapsed" desc="Check if Field is Text Type => check childType">
//        if (field instanceof TextFieldAttribute) {
//            if (Utils.isNullOrEmpty(field.getTypeName())) {
//                InternalResponse result = new InternalResponse(
//                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                        A_FPSConstant.CODE_FIELD_TEXT,
//                        A_FPSConstant.SUBCODE_MISSING_TEXT_FIELD_TYPE
//                );
//                result.setUser(user);
//                return result;
//            }
//        }
//        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Parse from percentage unit to point unit">
        String temp_ = request.getHeader("x-dimension-unit");

        hierarchicalLog.addStartHeading1("Dimenstion type: " + temp_);
        if (temp_ != null && temp_.equals("percentage")) {
            if (document.getDocumentWidth() != 0 && document.getDocumentHeight() != 0) {
                hierarchicalLog.addStartHeading1("Start calculate dimension");
                field.setDimension(ProcessModuleForEnterprise.getInstance(user).parse(document, field.getDimension()));
                hierarchicalLog.addEndHeading1("Calculate dimension successfully");
            }
        }
        hierarchicalLog.addStartHeading1("Dimension:");
        hierarchicalLog.addStartHeading2("DimensionX: " + field.getDimension().getX());
        hierarchicalLog.addStartHeading2("DimensionX: " + field.getDimension().getY());
        hierarchicalLog.addStartHeading2("DimensionW: " + field.getDimension().getWidth());
        hierarchicalLog.addStartHeading2("DimensionH: " + field.getDimension().getHeight());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create QR Image if that type is QR Code">
        if (field instanceof QRFieldAttribute) {
            hierarchicalLog.addStartHeading1("Start create QR");
            try {
                QRFieldAttribute qr = (QRFieldAttribute) field;
                if (Utils.isNullOrEmpty(qr.getValue())) {
                    qr.setValue("Waiting for process Qrypto");
                }
                hierarchicalLog.addEndHeading2("Value QR: " + qr.getValue());
                byte[] imageQR = QRGenerator.generateQR(
                        qr.getValue(),
                        Math.round((float) qr.getDimension().getWidth()),
                        Math.round((float) qr.getDimension().getWidth()),
                        qr.IsTransparent());
                if (Utils.isNullOrEmpty(qr.getImageQR())) {
                    qr.setImageQR(Base64.toBase64String(imageQR));
                }
                hierarchicalLog.addEndHeading1("Create QR successfully");
            } catch (Exception ex) {
                LogHandler.error(FieldSummary.class, transactionId, ex);
                hierarchicalLog.addEndHeading1("Create QR fail");
                response = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_QR,
                        A_FPSConstant.SUBCODE_CANNOT_GENERATE_QR
                ).setException(ex).setUser(user).setHierarchicalLog(hierarchicalLog);
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add Field">
//        hierarchicalLog.addStartHeading1("Start add field");
        response = AddField.addField(
                document.getId(),
                field,
                "hmac",
                user.getAzp(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            hierarchicalLog.addEndHeading1("Add field fail");
            return response.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        int documentFieldId = (int) response.getData();
        hierarchicalLog.addEndHeading1("Add field successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add Field Details">
        response = AddField.addDetailField(
                documentFieldId,
                field.getType().getTypeId(),
                field,
                "hmac",
                user.getAzp(),
                transactionId);
        hierarchicalLog.addEndHeading1("Add field detail successfully");
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController()
                        .writeStringField("field_name", field.getFieldName())
                        .build()
        ).setUser(user).setHierarchicalLog(hierarchicalLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Add FieldV2">
    /**
     * Add field Version 2: Using to append field immediately when add field
     *
     * @param request
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse addFieldV2(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Check payload ">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Verify Token">
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse payload into Field Object in FPS based on the URL 'signature', 'text',...">
        response = parseToField(
                request.getRequestURI(),
                payload,
                true,
                false,
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        BasicFieldAttribute field = (BasicFieldAttribute) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add more data to Field Object">
        field.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        field.setProcessOn(dateFormat.format(Date.from(Instant.now())));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get all documents that belong to packageId in URL">
        long packageId = Utils.getIdFromURL(request.getRequestURI());
        response = GetDocument.getDocuments(
                packageId,
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        List<Document> documents = (List<Document>) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get the last revision of Document and the ID of the first Document">
        Document document_ = null;
        long documentId = 0;
        for (int i = documents.size() - 1; i >= 0; i--) {
            if (documents.get(i).getRevision() == documents.size()) {
                document_ = documents.get(i);
            }
            if (documents.get(i).getRevision() == 1) {
                documentId = documents.get(i).getId();
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse from percentage unit to point unit">
        String temp_ = request.getHeader("x-dimension-unit");
        if (temp_ != null && temp_.equals("percentage")) {
            if (document_.getDocumentWidth() != 0 && document_.getDocumentHeight() != 0) {
                field.setDimension(ProcessModuleForEnterprise.getInstance(user).parse(document_, field.getDimension()));
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check existed of field name">
        InternalResponse temp = GetField.getFieldData(documentId, field.getFieldName(), transactionId);
        if (temp.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_FIELD_NAME
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Processing append field form into file ">
//        ExecutorService executor = Executors.newFixedThreadPool(2);
        ThreadManagement executor = MyServices.getThreadManagement(2);

        Future<?> appended = executor.submit(
                new TaskV2(
                        new Object[]{user, document_, documents.size(), field, transactionId},
                        transactionId) {
            @Override
            public InternalResponse call() {
                InternalResponse response = null;
                try {
                    if (this.get()[3] instanceof TextFieldAttribute) {
                        response = new ProcessingFactory().createType_Module(ProcessingFactory.TypeProcess.TEXTFIELD).createFormField(
                                this.get());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                    response.setCode(A_FPSConstant.CODE_FIELD_TEXT);
                    response.setCodeDescription(A_FPSConstant.SUBCODE_CANNOT_CREATE_FORM_FIELD);
                }
                return response;
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Processing add field data into DB">
        Future<?> addField = executor.submit(
                new TaskV2(
                        new Object[]{documentId, field, user},
                        transactionId) {
            @Override
            public InternalResponse call() {
                InternalResponse response = null;
                try {
                    //Add Field 
                    response = AddField.addField(
                            (long) this.get()[0],
                            (BasicFieldAttribute) this.get()[1],
                            "hmac",
                            ((User) this.get()[2]).getAzp(),
                            this.getTransactionId());
                } catch (Exception ex) {
                    response.setStatus(A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR);
                    response.setCode(A_FPSConstant.CODE_FIELD_TEXT);
                    response.setCodeDescription(A_FPSConstant.SUBCODE_CANNOT_CREATE_FORM_FIELD);
                }
                return response;
            }
        }
        );
        //</editor-fold>

        executor.shutdown();

        InternalResponse response_addField = (InternalResponse) addField.get();
        if (response_addField.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response_addField.setUser(user);
        }

        InternalResponse response_appended = (InternalResponse) appended.get();

        if (response_appended != null) {
            if (response_appended.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response_appended.setUser(user);
            }
        }

        int documentFieldId = (int) response_addField.getData();

        response = AddField.addDetailField(
                documentFieldId,
                field.getType().getTypeId(),
                field,
                "hmac",
                user.getAzp(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController()
                        .writeStringField("field_name", field.getFieldName())
                        .build()
        ).setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Update Field">
    /**
     * Update field in Document
     *
     * @param request
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse updateField(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog = new fps_core.utils.LogHandler.HierarchicalLog("Update field");

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (payload == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            );
        }
        hierarchicalLog.addEndHeading1("Checking payload successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Call throught connectorDocument to verify and get Documents of packageId">
        InternalResponse response = DocumentSummaryInternal.getDocuments(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = response.getUser();
        List<Document> documents = (List<Document>) response.getData();
        hierarchicalLog.addEndHeading1("Get Document and Verify token successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse to field + check type of field">
        hierarchicalLog.addStartHeading1("Start parse to field");
        response = parseToField(
                request.getRequestURI(),
                payload,
                false,
                true,
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        BasicFieldAttribute field = (BasicFieldAttribute) response.getData();
        hierarchicalLog.addEndHeading1("Parse to field successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add more data to Field">
        field.setProcessBy(user.getAzp());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        field.setProcessOn(dateFormat.format(Date.from(Instant.now())));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Field old from DB">
        Document document_ = null;
        for (Document document : documents) {
            if (document.getRevision() == 1) {
                document_ = document;
                //Check status of the document
                response = checkStatusOfDocument(document_, transactionId);
                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    return response.setUser(user);
                }

                response = GetField.getFieldData(
                        document.getId(),
                        field.getFieldName(),
                        transactionId);
            }
        }
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            hierarchicalLog.addEndHeading1("Get field old fail");
            return response.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        ExtendedFieldAttribute fieldOld = (ExtendedFieldAttribute) response.getData();
        hierarchicalLog.addEndHeading1("Get field old successfully");
        //</editor-fold>

//        //<editor-fold defaultstate="collapsed" desc="If the field is Initials => Create Thread to get all Field in DB and store in "fieldsInit"">
//        ExecutorService executors = Executors.newFixedThreadPool(1);
//        Future<Object> thread = executors.submit(new TaskV2(
//                new Object[] {document_},
//                transactionId) {
//            @Override
//            public Object call() {
//                try {
//                    InternalResponse response = GetField.getFieldsData(
//                            (long)this.get()[0],
//                            transactionId);
//                    if(response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
//                        return response;
//                    }
//                    List<ExtendedFieldAttribute> fields = (List<ExtendedFieldAttribute>)response.getData();
//                    for (ExtendedFieldAttribute value : fields) {
//                        if(value.getType().getParentType().equals(FieldTypeName.INITIAL.getParentName())){
//                            try {
//                                InitialsFieldAttribute init = MyServices.getJsonService().readValue(value.getFieldValue(), InitialsFieldAttribute.class);
//                                init = (InitialsFieldAttribute) value.clone(init, value.getDimension());
//                                fieldsInit.add(init);
//                            } catch (JsonProcessingException ex) {
//                                Logger.getLogger(FieldSummary.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//                    }                    
//                    return response;
//                } catch (Exception ex) {
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_INTERNAL_SERVER_ERROR,
//                            A_FPSConstant.CODE_ERROR_WHILE_CALLING_THREAD,
//                            A_FPSConstant.SUBCODE_THREAD_GET_FIELDS_IN_UPDATE_INITIALFIELD
//                    ).setException(ex);
//                }
//            }
//        });
//        executors.shutdown();
//        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Check Process Status of Field">
        InternalResponse checking = CheckFieldProcessedYet.checkProcessed(fieldOld);
        if (checking.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            hierarchicalLog.addEndHeading1("Check process status fail");
            return checking.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        hierarchicalLog.addEndHeading1("Check process status successfully");
        //</editor-fold>

        if (field.getDimension() == null) {
            field.setDimension(new Dimension(
                    fieldOld.getDimension().getX(),
                    fieldOld.getDimension().getY(),
                    fieldOld.getDimension().getWidth(),
                    fieldOld.getDimension().getHeight()
            ));
        } else {
            //<editor-fold defaultstate="collapsed" desc="Parse from percentage unit to point unit">
            hierarchicalLog.addStartHeading1("Start check dimension and parse it");
            String temp_ = request.getHeader("x-dimension-unit");
            Object x = Utils.getFromJson_("x", payload);
            Object y = Utils.getFromJson_("y", payload);
            Object w = Utils.getFromJson_("width", payload);
            Object h = Utils.getFromJson_("height", payload);
            if (temp_ != null && temp_.equals("percentage")) {
                if (document_.getDocumentWidth() != 0 && document_.getDocumentHeight() != 0) {
                    if (x == null
                            || (x instanceof Integer
                                    ? (Integer) x == -1
                                    : false)) {
                        field.getDimension().setX(fieldOld.getDimension().getX() / document_.getDocumentWidth() * 100);
                    }
                    if (y == null
                            || (y instanceof Integer
                                    ? (Integer) y == -1
                                    : false)) {
                        float Y = (fieldOld.getDimension().getY() + fieldOld.getDimension().getHeight()) / document_.getDocumentHeight();
//                        double Y = (fieldOld.getDimension().getY() + fieldOld.getDimension().getHeight()) / document_.getDocumentHeight();
                        field.getDimension().setY((1 - Y) * 100);
                    }
                    if (w == null
                            || (w instanceof Integer
                                    ? (Integer) w == -1
                                    : false)) {
                        field.getDimension().setWidth(fieldOld.getDimension().getWidth() / document_.getDocumentWidth() * 100);
                    }
                    if (h == null
                            || (h instanceof Integer
                                    ? (Integer) h == -1
                                    : false)) {
                        field.getDimension().setHeight(fieldOld.getDimension().getHeight() / document_.getDocumentHeight() * 100);
                    }
                    field.setDimension(ProcessModuleForEnterprise.getInstance(user).parse(document_, field.getDimension()));
                    hierarchicalLog.addEndHeading2("Parse dimension with percentage successfully");
                }
            } else {
                if (x == null
                        || (x instanceof Integer
                                ? (Integer) x == -1
                                : false)) {
                    field.getDimension().setX(fieldOld.getDimension().getX());
                }
                if (y == null
                        || (y instanceof Integer
                                ? (Integer) y == -1
                                : false)) {
                    field.getDimension().setY(fieldOld.getDimension().getY());
                }
                if (w == null
                        || (w instanceof Integer
                                ? (Integer) w == -1
                                : false)) {
                    field.getDimension().setWidth(fieldOld.getDimension().getWidth());
                }
                if (h == null
                        || (h instanceof Integer
                                ? (Integer) h == -1
                                : false)) {
                    field.getDimension().setHeight(fieldOld.getDimension().getHeight());
                }
            }
            hierarchicalLog.addEndHeading1("Check dimension and parse it successfully");
            //</editor-fold>
        }

        hierarchicalLog.addStartHeading1("Dimension");
        hierarchicalLog.addStartHeading2("X:" + field.getDimension().getX());
        hierarchicalLog.addStartHeading2("Y:" + field.getDimension().getY());
        hierarchicalLog.addStartHeading2("W:" + field.getDimension().getWidth());
        hierarchicalLog.addStartHeading2("H:" + field.getDimension().getHeight());

        //<editor-fold defaultstate="collapsed" desc="Merge 2 JSON - 1 in DB as ExternalFieldAtrtibute.getDetailValue - 1 in Payload">
        String jsonUpdate = MyServices.getJsonService(
                new ObjectMapper().setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
        )
                .writeValueAsString(field);
        JsonNode merge = Utils.merge(
                fieldOld.getDetailValue(),
                jsonUpdate);
        String finalJson = merge.toPrettyString();
        hierarchicalLog.addStartHeading1("Json Original:" + fieldOld.getDetailValue().replaceAll(" ", ""));
        hierarchicalLog.addStartHeading1("Json Update:" + jsonUpdate.replaceAll(" ", ""));
        hierarchicalLog.addStartHeading1("FinalJSON:" + finalJson.replaceAll(" ", ""));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Merge Payload vs Field Value Old">
        JsonNode merge2 = Utils.merge(fieldOld.getFieldValue(), MyServices.getJsonService().writeValueAsString(field));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create new QR Image if that type is QR Code">
        if (field instanceof QRFieldAttribute) {
            try {
                QRFieldAttribute qr = (QRFieldAttribute) field;
                if (!Utils.isNullOrEmpty(qr.getValue())) {
                    byte[] imageQR = QRGenerator.generateQR(
                            (String) Utils.getFromJson_("value", merge2.toPrettyString()),
                            Math.round((float)qr.getDimension().getWidth()),
                            Math.round((float)qr.getDimension().getWidth()),
                            qr.IsTransparent() == null ? false : qr.IsTransparent());
                    qr.setImageQR(Base64.toBase64String(imageQR));
                    hierarchicalLog.addEndHeading1("Generate QR successfully");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                hierarchicalLog.addEndHeading1("Generate QR successfully");
                response = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_QR,
                        A_FPSConstant.SUBCODE_CANNOT_GENERATE_QR
                ).setException(ex).setUser(user).setHierarchicalLog(hierarchicalLog);
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update Field">
        response = UpdateField.updateField(
                fieldOld.getDocumentFieldId(),
                0,
                field.getRenamedAs(),
                merge2.toPrettyString(),
                null,
                field.getPage() <= 0 ? fieldOld.getPage() : field.getPage(),
                (float)field.getDimension().getWidth(),
                (float)field.getDimension().getHeight(),
                (float)(field.getDimension().getY() + field.getDimension().getHeight()),
                (float)field.getDimension().getX(),
                Utils.getFromJson("rotate", payload) == null
                ? null
                : RotateDegree.getRotateDegree(field.getRotate()),
                field.getVisibleEnabled() == null ? fieldOld.getVisibleEnabled() : field.getVisibleEnabled(),
                "hmac",
                user.getEmail(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            hierarchicalLog.addEndHeading1("Update field fail");
            return response.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        hierarchicalLog.addEndHeading1("Update field successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update Field Details">
        UpdateField.updateFieldDetails(
                fieldOld.getDocumentFieldId(),
                user,
                finalJson,
                "hmac",
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            hierarchicalLog.addEndHeading1("Update field detail fail");
            return response.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        hierarchicalLog.addEndHeading1("Update field detail successfully");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Replicate Field if that field is Initial">
        if (field instanceof AbstractReplicateField) {
            hierarchicalLog.addStartHeading1("Start creare replicate field");
            ((AbstractReplicateField) field).setType(fieldOld.getType());
            if (field.getPage() <= 0) {
                field.setPage(fieldOld.getPage());
            }
            field.setRemark(fieldOld.getRemark());
            response = ReplicateInitialField.replicateField(
                    (AbstractReplicateField) field,
                    document_,
                    user,
                    transactionId);

            if (response.isValid()) {
                hierarchicalLog.addEndHeading1("Creare replicate field successfully");
                return response.setUser(user).setHierarchicalLog(hierarchicalLog);
            }
            hierarchicalLog.addEndHeading1("Creare replicate field fail");
            return response.setUser(user).setHierarchicalLog(hierarchicalLog);
        }
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        ).setUser(user).setHierarchicalLog(hierarchicalLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Field Type">
    public static InternalResponse getFieldType(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Verify
        InternalResponse response = Utils.verifyAuthorizationToken(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = (User) response.getData();

        HashMap<String, FieldType> hashMap = Resources.getFieldTypes();
        List<FieldType> temp = new ArrayList<>();
        for (FieldType t : hashMap.values()) {
            temp.add(t);
        }
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                MyServices.getJsonService().writeValueAsString(temp)
        ).setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Fields">
    public static InternalResponse getFields(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Get Documents in Package (Verify + get Documents)
        InternalResponse response = DocumentSummaryInternal.getDocuments(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = response.getUser();
        List<Document> listDoc = (List<Document>) response.getData();

        //Get fields of each document
        Document document = new Document();
        List<ExtendedFieldAttribute> fields = new ArrayList<>();
        for (Document element : listDoc) {
            response = GetField.getFieldsData(
                    element.getId(),
                    transactionId);
            if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                return response.setUser(user);
            }
            fields.addAll((List<ExtendedFieldAttribute>) response.getData());
            document = element;
        }

        //sort fields
        Object[] array = sortFields(user, document, fields);
        if (array == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THIS_DOCUMENT_DOES_NOT_HAVE_ANY_FIELD
            ).setUser(user);
        }
        FieldsSerializer serializer = new FieldsSerializer(array);
        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                MyServices.getJsonService().writeValueAsString(serializer)).setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Delete form field">
    /**
     * Hàm sử dụng để xóa form field Method use for delete the form field
     *
     * @param request
     * @param packageId
     * @param payload
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse deleteFormField(
            HttpServletRequest request,
            long packageId,
            String payload,
            String transactionId
    ) throws Exception {
        //Get Document + verify Token
        InternalResponse response = DocumentSummary.getDocuments(request, packageId, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        User user = response.getUser();
        List<Document> documents = (List<Document>) response.getData();

        //Check payload and parse field name
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            );
        }
        String fieldName = Utils.getFromJson("field_name", payload);
        if (fieldName == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FIELD,
                    A_FPSConstant.SUBCODE_MISSING_FIELD_NAME
            );
        }

        //Get SignatureField with the name is in payload of request
        Document document_ = null;
        for (Document document : documents) {
            if (document.getRevision() == 1) {
                document_ = document;
                response = FieldSummaryInternal.getField(
                        document.getId(),
                        fieldName,
                        transactionId);
            }
        }
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        ExtendedFieldAttribute fieldData = (ExtendedFieldAttribute) response.getData();

        return distributeFlowDelete(document_, user, fieldData, transactionId);
    }
    // </editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Sort Field">
    /**
     * Từ list Field, sort theo ParentType và đưa vào Array tương ứng để trả về
     * client
     *
     * @param fields
     * @return
     */
    private static Object[] sortFields(
            User user,
            Document document,
            List<ExtendedFieldAttribute> fields) {
        List<SignatureFieldAttribute> signatures = new ArrayList<>();
        List<SignatureFieldAttribute> inpersons = new ArrayList<>();
        List<TextFieldAttribute> textboxs = new ArrayList<>();
        List<TextFieldAttribute> datetimes = new ArrayList<>();
        List<InitialsFieldAttribute> initials = new ArrayList<>();
        List<QRFieldAttribute> qrs = new ArrayList<>();
        List<QryptoFieldAttribute> qryptos = new ArrayList<>();
        List<FileFieldAttribute> images = new ArrayList<>();
        List<CheckBoxFieldAttribute> checkboxs = new ArrayList<>();
        List<CameraFieldAttribute> cameras = new ArrayList<>();
        List<RadioFieldAttribute> radios = new ArrayList<>();
        List<AttachmentFieldAttribute> attachments = new ArrayList<>();
        List<HyperLinkFieldAttribute> hypers = new ArrayList<>();
        List<ComboBoxFieldAttribute> combos = new ArrayList<>();
        List<ToggleFieldAttribute> toogles = new ArrayList<>();
        List<NumericStepperAttribute> steppers = new ArrayList<>();
        List<CheckBoxFieldAttributeV2> checkboxV2s = new ArrayList<>();
        List<RadioBoxFieldAttributeV2> radioboxV2s = new ArrayList<>();

        for (ExtendedFieldAttribute field : fields) {
            try {
                switch (field.getType().getTypeId()) {
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 15:
                    case 16:
                    case 20:
                    case 21:
                    case 26:
                    case 43:
                    case 45:
                    case 1: {
                        TextFieldAttribute text = new TextFieldAttribute();
                        text = MyServices.getJsonService().readValue(field.getDetailValue(), TextFieldAttribute.class);
                        text = (TextFieldAttribute) field.clone(text, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        textboxs.add(text);
                        break;
                    }
                    case 25: {
                        NumericStepperAttribute stepper = new NumericStepperAttribute();
                        stepper = MyServices.getJsonService().readValue(field.getDetailValue(), NumericStepperAttribute.class);
                        stepper = (NumericStepperAttribute) field.clone(stepper, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        steppers.add(stepper);
                        break;
                    }
                    case 22: {
                        ComboBoxFieldAttribute combo = new ComboBoxFieldAttribute();
                        combo = MyServices.getJsonService().readValue(field.getDetailValue(), ComboBoxFieldAttribute.class);
                        combo = (ComboBoxFieldAttribute) field.clone(combo, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        combos.add(combo);
                        break;
                    }
                    case 23: {
                        ToggleFieldAttribute toogle = new ToggleFieldAttribute();
                        toogle = MyServices.getJsonService().readValue(field.getDetailValue(), ToggleFieldAttribute.class);
                        toogle = (ToggleFieldAttribute) field.clone(toogle, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        toogles.add(toogle);
                        break;
                    }
                    case 27: {
                        HyperLinkFieldAttribute hyper = new HyperLinkFieldAttribute();
                        hyper = MyServices.getJsonService().readValue(field.getDetailValue(), HyperLinkFieldAttribute.class);
                        hyper = (HyperLinkFieldAttribute) field.clone(hyper, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        hypers.add(hyper);
                        break;
                    }
                    case 14:
                    case 24: {
                        DateTimeFieldAttribute dateTime = new DateTimeFieldAttribute();
                        dateTime = MyServices.getJsonService().readValue(field.getDetailValue(), DateTimeFieldAttribute.class);
                        dateTime = (DateTimeFieldAttribute) field.clone(dateTime, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        textboxs.add(dateTime);
                        break;
                    }
                    case 2: {
                        CheckBoxFieldAttribute checkBox = MyServices.getJsonService().readValue(field.getDetailValue(), CheckBoxFieldAttribute.class);
                        checkBox = (CheckBoxFieldAttribute) field.clone(checkBox, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        checkboxs.add(checkBox);
                        break;
                    }
                    case 47: {
                        CheckBoxFieldAttributeV2 checkBox = MyServices.getJsonService().readValue(field.getDetailValue(), CheckBoxFieldAttributeV2.class);
                        checkBox = (CheckBoxFieldAttributeV2) field.clone(checkBox, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        checkboxV2s.add(checkBox);
                        break;
                    }
                    case 3: {
                        RadioFieldAttribute radio = MyServices.getJsonService().readValue(field.getDetailValue(), RadioFieldAttribute.class);
                        radio.setDimension(ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        radio = (RadioFieldAttribute) field.clone(radio, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        radios.add(radio);
                        break;
                    }
                    case 51: {
                        RadioBoxFieldAttributeV2 radio = MyServices.getJsonService().readValue(field.getDetailValue(), RadioBoxFieldAttributeV2.class);
                        radio = (RadioBoxFieldAttributeV2) field.clone(radio, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        radioboxV2s.add(radio);
                        break;
                    }
                    case 37: {
                        QryptoFieldAttribute qr = MyServices.getJsonService().readValue(field.getDetailValue(), QryptoFieldAttribute.class);
                        qr.setDimension(ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        qr = (QryptoFieldAttribute) field.clone(qr, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        //<editor-fold defaultstate="collapsed" desc="Download File from UUID in items">
                        if (!Utils.isNullOrEmpty(qr.getItems())) {
                            try {
                                for (ItemDetails detail : qr.getItems()) {
                                    String file = null;
                                    IDPicture4Label tempp = null;
                                    switch (ItemsType.getItemsType(detail.getType())) {
                                        case Binary:
                                        case File: {
                                            file = (String) detail.getValue();
                                            break;
                                        }
                                        case ID_Picture_with_4_labels: {
                                            String temp_ = MyServices.getJsonService().writeValueAsString(detail.getValue());
                                            tempp = MyServices.getJsonService().readValue(temp_, IDPicture4Label.class);
                                            file = tempp.getBase64();
                                            break;
                                        }
                                        default: {
                                        }
                                    }
                                    if (file != null) {
                                        //<editor-fold defaultstate="collapsed" desc="Download image from FMS If need">
                                        if (file.length() <= 32) {
                                            try {
                                                InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.downloadDocumentFromFMS(
                                                        file,
                                                        "");
                                                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                                    byte[] data = (byte[]) response.getData();
                                                    if (tempp != null) {
                                                        tempp.setBase64(Base64.toBase64String(data));
                                                        detail.setValue(tempp);
                                                    } else {
                                                        detail.setValue(Base64.toBase64String(data));
                                                    }
                                                }
                                            } catch (Exception ex) {
                                                System.err.println("Cannot download image from QR in FMS!. Using default");
                                            }
                                        }
                                        //</editor-fold>
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (!Utils.isNullOrEmpty(qr.getImageQR())) {
                            //<editor-fold defaultstate="collapsed" desc="Download image from FMS If need">
                            if (qr.getImageQR().length() <= 32) {
                                try {
                                    InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.downloadDocumentFromFMS(
                                            qr.getImageQR(),
                                            "");
                                    if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                        byte[] data = (byte[]) response.getData();
                                        qr.setImageQR(Base64.toBase64String(data));
                                    }
                                } catch (Exception ex) {
                                    System.err.println("Cannot download image from QR in FMS!. Using default");
                                }
                            }
                            //</editor-fold>
                        }
                        //</editor-fold>

                        qryptos.add(qr);
                        break;
                    }
                    case 4: {
                        QRFieldAttribute qr = MyServices.getJsonService().readValue(field.getDetailValue(), QRFieldAttribute.class);
                        qr.setDimension(ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        qr = (QRFieldAttribute) field.clone(qr, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS if need">
                        if (!Utils.isNullOrEmpty(qr.getImageQR()) && qr.getImageQR().length() <= 32) {
                            try {
                                InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.downloadDocumentFromFMS(
                                        qr.getImageQR(),
                                        "");
                                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                    byte[] file = (byte[]) response.getData();
                                    qr.setImageQR(Base64.toBase64String(file));
                                }
                            } catch (Exception ex) {
                                System.err.println("Cannot download image from QR in FMS!");
                            }
                        }
                        //</editor-fold>

                        qrs.add(qr);
                        break;
                    }
                    case 5: {
                        InitialsFieldAttribute initialField = MyServices.getJsonService().readValue(field.getDetailValue(), InitialsFieldAttribute.class);
                        initialField = (InitialsFieldAttribute) field.clone(initialField, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS if image is UUID">
                        if (initialField.getImage() != null && initialField.getImage().length() <= 32) {
                            try {
                                InternalResponse response = FMS.downloadDocumentFromFMS(initialField.getImage(), "tran");

                                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                    initialField.setImage(Base64.toBase64String((byte[]) response.getData()));
                                }
                            } catch (Exception ex) {
                                System.err.println("Cannot Download Image from Initial in FMS");
                            }
                        }
                        //</editor-fold>

                        initials.add(initialField);
                        break;
                    }
                    case 6: {
                        //<editor-fold defaultstate="collapsed" desc="Mapping all date into SignatureField">
                        String json1 = field.getDetailValue();
                        String json2 = field.getFieldValue();
                        SignatureFieldAttribute signatureField = null;
                        if (Utils.getFromJson_("verification", json1) != null) {
                            if (Utils.isNullOrEmpty(json2)) {
                                json2 = json1;
                            } else {
                                JsonNode node = Utils.merge(json2, json1);
                                json2 = node.toPrettyString();
                            }
                        } else {
                            json2 = json1;
                        }
                        signatureField = MyServices.getJsonService().readValue(json2, SignatureFieldAttribute.class);
                        signatureField = (SignatureFieldAttribute) field.clone(signatureField, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        signatureField.setLevelOfAssurance(field.getLevelOfAssurance());
                        inpersons.add(signatureField);
                        break;
                        //</editor-fold>
                    }
                    case 7: {
                        //<editor-fold defaultstate="collapsed" desc="Mapping all date into SignatureField">
                        String json1 = field.getDetailValue();
                        String json2 = field.getFieldValue();
                        SignatureFieldAttribute signatureField = null;
                        if (Utils.getFromJson_("verification", json1) != null) {
                            if (Utils.isNullOrEmpty(json2)) {
                                json2 = json1;
                            } else {
                                JsonNode node = Utils.merge(json2, json1);
                                json2 = node.toPrettyString();
                            }
                        } else {
                            json2 = json1;
                        }
                        signatureField = MyServices.getJsonService().readValue(json2, SignatureFieldAttribute.class);
                        signatureField = (SignatureFieldAttribute) field.clone(signatureField, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        signatureField.setLevelOfAssurance(field.getLevelOfAssurance());
                        signatures.add(signatureField);
                        break;
                        //</editor-fold>
                    }
                    case 38: {
                        FileFieldAttribute image = MyServices.getJsonService().readValue(field.getDetailValue(), FileFieldAttribute.class);
                        image = (FileFieldAttribute) field.clone(image, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS if need">
                        if (!Utils.isNullOrEmpty(image.getFile()) && image.getFile().length() <= 32) {
                            try {
                                InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.downloadDocumentFromFMS(
                                        image.getFile(),
                                        "");
                                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                    byte[] file = (byte[]) response.getData();
                                    image.setFile(Base64.toBase64String(file));
                                }
                            } catch (Exception ex) {
                                System.err.println("Cannot download image from ImageField in FMS!");
                            }
                        }
                        //</editor-fold>

                        images.add(image);
                        break;
                    }
                    case 39: {
                        CameraFieldAttribute camera = MyServices.getJsonService().readValue(field.getDetailValue(), CameraFieldAttribute.class);
                        camera = (CameraFieldAttribute) field.clone(camera, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS if need">
                        if (!Utils.isNullOrEmpty(camera.getFile()) && camera.getFile().length() <= 32) {
                            try {
                                InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.downloadDocumentFromFMS(
                                        camera.getFile(),
                                        "");
                                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                    byte[] file = (byte[]) response.getData();
                                    camera.setFile(Base64.toBase64String(file));
                                }
                            } catch (Exception ex) {
                                System.err.println("Cannot download image from Camera in FMS!");
                            }
                        }
                        //</editor-fold>

                        cameras.add(camera);
                        break;
                    }
                    case 41: {
                        AttachmentFieldAttribute attach = MyServices.getJsonService().readValue(field.getDetailValue(), AttachmentFieldAttribute.class);
                        attach = (AttachmentFieldAttribute) field.clone(attach, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        //<editor-fold defaultstate="collapsed" desc="Download File from FMS if need">
                        if (attach.getFileData() != null
                                && !Utils.isNullOrEmpty(attach.getFile())
                                && attach.getFile().length() <= 32) {
                            try {
                                InternalResponse response = vn.mobileid.id.FPS.controller.fms.FMS.downloadDocumentFromFMS(
                                        attach.getFile(),
                                        "");
                                if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                                    byte[] file = (byte[]) response.getData();
                                    attach.setFile(Base64.toBase64String(file));
                                }
                            } catch (Exception ex) {
                                System.err.println("Cannot download image from Attachment Field in FMS!");
                            }
                        }
                        //</editor-fold>

                        attachments.add(attach);
                        break;
                    }
                }
            } catch (Exception ex) {
                LogHandler.error(FieldSummary.class, "transactionId", ex);
                return null;
            }
        }
        Object[] array = new Object[18];
        array[0] = textboxs;
        array[1] = checkboxs;
        array[2] = radios;
        array[3] = qrs;
        array[4] = initials;
        array[5] = inpersons;
        array[6] = signatures;
        array[7] = combos;
        array[8] = datetimes;
        array[9] = qryptos;
        array[10] = images;
        array[11] = cameras;
        array[12] = attachments;
        array[13] = hypers;
        array[14] = toogles;
        array[15] = steppers;
        array[16] = checkboxV2s;
        array[17] = radioboxV2s;
        return array;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ParseToField">
    /**
     * Từ url xác định loại field và parse vào loại đó Determine the type of
     * field and parse into that type. Based on URL
     *
     * @param url
     * @param payload
     * @param transactionId
     * @return
     */
    private static InternalResponse parseToField(
            String url,
            String payload,
            Boolean isCheckBasicField,
            Boolean isUpdate,
            String transactionId) {
        if (url.contains("/v2/") || url.contains("/V2/")) {
            return ParseToField.parseToFieldV2(url, payload, isCheckBasicField, isUpdate, transactionId);
        } else {
            return ParseToField.parseToField(url, payload, isCheckBasicField, isUpdate, transactionId);
        }
    }
    //</editor-fold>

//    //<editor-fold defaultstate="collapsed" desc="ParseToField Backup">
//    /**
//     * Từ url xác định loại field và parse vào loại đó Determine the type of
//     * field and parse into that type. Based on URL
//     *
//     * @param url
//     * @param payload
//     * @param transactionId
//     * @return
//     */
//    private static InternalResponse parseToField(
//            String url,
//            String payload,
//            Boolean isCheckBasicField,
//            Boolean isUpdate,
//            String transactionId) {
//        fps_core.utils.LogHandler.HierarchicalLog hierarchicalLog = new fps_core.utils.LogHandler.HierarchicalLog("Parse to Field");
//
//        String typeField = url.substring(url.lastIndexOf("/") + 1);
//        String temp = null;
//
//        hierarchicalLog.addStartHeading1("Field from URL: " + typeField);
//
//        switch (typeField) {
//            case "in_person_signature":
//                temp = FieldTypeName.INPERSON.getParentName();
//            case "signature": {
//                //<editor-fold defaultstate="collapsed" desc="Generate SignatureFieldAttribute from Payload">
////                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                SignatureFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, SignatureFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                field.setType(Resources.getFieldTypes().get(
//                        temp == null
//                                ? FieldTypeName.SIGNATURE.getParentName()
//                                : temp
//                ));
//
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field)
//                        .setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "date":
//            case "datetime": {
//                //<editor-fold defaultstate="collapsed" desc="Generate DateTime from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                DateTimeFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, DateTimeFieldAttribute.class);
//                } catch (Exception ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    hierarchicalLog.addStartHeading1("Start check field type");
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.DATETIME);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_TEXT,
//                                A_FPSConstant.SUBCODE_INVALID_TEXT_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    hierarchicalLog.addEndHeading1("Check field type successfully");
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.DATETIME.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.getAlignment() == null) {
//                        field.setDefaultAlignment();
//                    }
//                    if (field.getColor() == null) {
//                        field.setColor("BLACK");
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//                    if (field.isMultiline() == null) {
//                        field.setMultiline(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
//                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "text": {
//                //<editor-fold defaultstate="collapsed" desc="Generate TextFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Check type of TextField">
//                String type = Utils.getFromJson("type", payload);
//                Object checkDate = Utils.getFromJson_("date", payload);
//                Object checkAddress = Utils.getFromJson_("address", payload);
//
//                if (Utils.isNullOrEmpty(type) && !isUpdate) {
//                    hierarchicalLog.addEndHeading1("Check type of textfield fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD_TEXT,
//                            A_FPSConstant.SUBCODE_MISSING_TEXT_FIELD_TYPE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                TextFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, TextFieldAttribute.class);
//                    if (type != null) {
//                        if (checkDate != null || type.equalsIgnoreCase("datetime") || type.equalsIgnoreCase("date")) {
//                            field = MyServices.getJsonService().readValue(payload, DateTimeFieldAttribute.class);
//                        } else if (checkAddress != null || type.equalsIgnoreCase("hyperlink")) {
//                            field = MyServices.getJsonService().readValue(payload, HyperLinkFieldAttribute.class);
//                        }
//                    }
//                } catch (Exception ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.TEXTBOX);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_TEXT,
//                                A_FPSConstant.SUBCODE_INVALID_TEXT_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.TEXTBOX.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.getMaxLength() == null) {
//                        field.setMaxLength(100);
//                    }
//                    if (field.getAlignment() == null) {
//                        field.setDefaultAlignment();
//                    }
//                    if (field.getColor() == null) {
//                        field.setColor("BLACK");
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//                    if (field.isMultiline() == null) {
//                        field.setMultiline(false);
//                    }
//                    if (field.getFont() == null) {
//                        field.setFont(Font.init());
//                    }
//
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
//                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
//                    hierarchicalLog.addStartHeading1("Visible: " + field.getVisibleEnabled());
//                    hierarchicalLog.addStartHeading1("Font name: " + field.getFont().getName());
//                    hierarchicalLog.addStartHeading1("Font size: " + field.getFont().getSize());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "checkbox": {
//                //<editor-fold defaultstate="collapsed" desc="Generate CheckBoxFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                CheckBoxFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, CheckBoxFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CHECKBOX);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_CHECKBOX,
//                                A_FPSConstant.SUBCODE_INVALID_CHECKBOX_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.CHECKBOX.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.isChecked() == null) {
//                        field.setChecked(false);
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Checked: " + field.isChecked());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "checkboxV2": {
//                //<editor-fold defaultstate="collapsed" desc="Generate CheckBoxFieldAttributeV2 from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                CheckBoxFieldAttributeV2 field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, CheckBoxFieldAttributeV2.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CHECKBOXV2);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_CHECKBOX,
//                                A_FPSConstant.SUBCODE_INVALID_CHECKBOX_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.CHECKBOX.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "radio": {
//                //<editor-fold defaultstate="collapsed" desc="Generate RadioFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                RadioFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, RadioFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.RADIOBOX);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_RADIO_BOX,
//                                A_FPSConstant.SUBCODE_INVALID_TYPE_OF_RADIO
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.RADIOBOX.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.isChecked() == null) {
//                        field.setChecked(false);
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Checked: " + field.isChecked());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "initial": {
//                //<editor-fold defaultstate="collapsed" desc="Generate InitialsFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                InitialsFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, InitialsFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field for initial">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkAddInitialField(field, transactionId);
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                field.setType(Resources.getFieldTypes().get(FieldTypeName.INITIAL.getParentName()));
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.isApplyToAll() == null) {
//                        field.setApplyToAll(false);
//                    }
//                    if (field.isReplicateAllPages() == null) {
//                        field.setReplicateAllPages(false);
//                    }
//
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
//                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
//                if (field.getImage() != null && field.getImage().length()
//                        > PolicyConfiguration.getInstant()
//                                .getSystemConfig()
//                                .getAttributes()
//                                .get(0)
//                                .getMaximumFile()) {
//                    try {
//                        hierarchicalLog.addStartHeading1("Start upload to FMS");
//                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
//                                Base64.decode(field.getImage()),
//                                "png",
//                                transactionId);
//                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                            hierarchicalLog.addEndHeading1("upload to FMS successfully");
//                            String uuid = (String) response.getData();
//                            field.setImage(uuid);
//                        }
//
//                    } catch (Exception ex) {
//                        System.err.println("Cannot upload image from QR to FMS!. Using default");
//                    }
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "qrcode": {
//                //<editor-fold defaultstate="collapsed" desc="Generate QRFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                QRFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, QRFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                if (Utils.isNullOrEmpty(field.getValue()) && !isUpdate) {
//                    hierarchicalLog.addEndHeading1("Missing encode string of QR");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FIELD_QR,
//                            A_FPSConstant.SUBCODE_MISSING_ENCODE_STRING_OF_QR
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.QR);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_QR,
//                                A_FPSConstant.SUBCODE_INVALID_QR_TYTPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.QR.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.IsTransparent() == null) {
//                        field.setTransparent(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Transparent: " + field.IsTransparent());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Upload image QR into FMS if need">
//                if (field.getImageQR() != null
//                        && field.getImageQR().length()
//                        > PolicyConfiguration.getInstant()
//                                .getSystemConfig()
//                                .getAttributes()
//                                .get(0)
//                                .getMaximumFile()) {
//                    try {
//                        hierarchicalLog.addStartHeading1("Start upload to FMS");
//                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
//                                Base64.decode(field.getImageQR()),
//                                "png",
//                                transactionId);
//                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                            hierarchicalLog.addEndHeading1("Upload to FMS successfully");
//                            String uuid = (String) response.getData();
//                            field.setImageQR(uuid);
//                        }
//                    } catch (Exception ex) {
//                        System.err.println("Cannot upload image from QR to FMS!. Using default");
//                    }
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "qrcode-qrypto": {
//                //<editor-fold defaultstate="collapsed" desc="Generate QryptoFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                QryptoFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, QryptoFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.QRYPTO);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_QR,
//                                A_FPSConstant.SUBCODE_INVALID_QR_TYTPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.QRYPTO.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="If item is not null => check type is file, upload it into FMS">
//                if (!Utils.isNullOrEmpty(field.getItems())) {
//                    hierarchicalLog.addStartHeading1("Start checking items in Qrypto");
//                    try {
//                        for (ItemDetails detail : field.getItems()) {
//                            String file = null;
//                            IDPicture4Label tempp = null;
//                            switch (ItemsType.getItemsType(detail.getType())) {
//                                case Binary:
//                                case File: {
//                                    file = (String) detail.getValue();
//                                    break;
//                                }
//                                case ID_Picture_with_4_labels: {
//                                    String temp_ = MyServices.getJsonService().writeValueAsString(detail.getValue());
//                                    tempp = MyServices.getJsonService().readValue(temp_, IDPicture4Label.class);
//                                    file = tempp.getBase64();
//                                    break;
//                                }
//                                default: {
//                                }
//                            }
//                            if (file != null) {
//                                //<editor-fold defaultstate="collapsed" desc="Upload image into FMS If need">
//                                if (file.length()
//                                        > PolicyConfiguration.getInstant()
//                                                .getSystemConfig()
//                                                .getAttributes()
//                                                .get(0)
//                                                .getMaximumFile()) {
//                                    try {
//                                        hierarchicalLog.addStartHeading2("Upload image/file into FMS");
//                                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
//                                                Base64.decode(file),
//                                                "png",
//                                                transactionId);
//                                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                                            hierarchicalLog.addEndHeading2("Upload image/file into FMS successfully");
//                                            String uuid = (String) response.getData();
//                                            if (tempp != null) {
//                                                tempp.setBase64(uuid);
//                                                detail.setValue(tempp);
//                                            } else {
//                                                detail.setValue(uuid);
//                                            }
//                                        } else {
//                                            hierarchicalLog.addEndHeading2("Upload image/file into FMS fail");
//                                        }
//                                    } catch (Exception ex) {
//                                        System.err.println("Cannot upload image from QR to FMS!. Using default");
//                                    }
//                                }
//                                //</editor-fold>
//                            }
//                        }
//                    } catch (Exception ex) {
//                        hierarchicalLog.addEndHeading1("Upload the image/file from QR into FMS fail");
//                        LogHandler.error(FieldSummary.class, transactionId, ex);
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_QR_Qrypto,
//                                A_FPSConstant.SUBCODE_INVALID_TYPE_OF_ITEM
//                        ).setException(ex);
//                    }
//                    hierarchicalLog.addEndHeading1("Checking items in Qrypto successfully");
//                }
//                //</editor-fold> 
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "stamp": {
//                //<editor-fold defaultstate="collapsed" desc="Generate FileFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                FileFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, FileFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.STAMP);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_STAMP,
//                                A_FPSConstant.SUBCODE_INVALID_STAMP_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.STAMP.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.isApplyToAll() == null) {
//                        field.setApplyToAll(false);
//                    }
//                    if (field.isReplicateAllPages() == null) {
//                        field.setReplicateAllPages(false);
//                    }
//
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
//                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                if (!Utils.isNullOrEmpty(field.getFile())) {
//                    //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
//                    if (field.getFile() != null && field.getFile().length()
//                            > PolicyConfiguration.getInstant()
//                                    .getSystemConfig()
//                                    .getAttributes()
//                                    .get(0)
//                                    .getMaximumFile()) {
//                        try {
//                            hierarchicalLog.addStartHeading2("Start upload image/file into FMS");
//                            InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
//                                    Base64.decode(field.getFile()),
//                                    "png",
//                                    transactionId);
//                            if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                                hierarchicalLog.addEndHeading2("Upload successfully");
//                                String uuid = (String) response.getData();
//                                field.setFile(uuid);
//                            } else {
//                                hierarchicalLog.addEndHeading2("Upload fail");
//                            }
//                        } catch (Exception ex) {
//                            hierarchicalLog.addEndHeading2("Cannot upload image from ImageField to FMS!. Using default");
//                        }
//                    }
//                    //</editor-fold>
//                }
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "camera": {
//                //<editor-fold defaultstate="collapsed" desc="Generate CameraFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                CameraFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, CameraFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CAMERA);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_CAMERA,
//                                A_FPSConstant.SUBCODE_INVALID_CAMERA_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.CAMERA.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.isShowIcon() == null) {
//                        field.setShowIcon(false);
//                    }
//                    if (field.isApplyToAll() == null) {
//                        field.setApplyToAll(false);
//                    }
//                    if (field.isReplicateAllPages() == null) {
//                        field.setReplicateAllPages(false);
//                    }
//
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Is show icon: " + field.isShowIcon());
//                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
//                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                if (!Utils.isNullOrEmpty(field.getFile())) {
//                    //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
//                    if (field.getFile() != null && field.getFile().length()
//                            > PolicyConfiguration.getInstant()
//                                    .getSystemConfig()
//                                    .getAttributes()
//                                    .get(0)
//                                    .getMaximumFile() / 2) {
//                        try {
//                            hierarchicalLog.addStartHeading2("Upload image into FMS");
//                            InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
//                                    Base64.decode(field.getFile()),
//                                    "png",
//                                    transactionId);
//                            if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                                hierarchicalLog.addEndHeading2("Upload successfully");
//                                String uuid = (String) response.getData();
//                                field.setFile(uuid);
//                            } else {
//                                hierarchicalLog.addEndHeading2("Upload fail");
//                            }
//                        } catch (Exception ex) {
//                            hierarchicalLog.addEndHeading2("Cannot upload image from ImageField to FMS!. Using default");
//                        }
//                    }
//                    //</editor-fold>
//                }
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "attachment": {
//                //<editor-fold defaultstate="collapsed" desc="Generate Attachment from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                AttachmentFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, AttachmentFieldAttribute.class);
//                } catch (JsonProcessingException ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.ATTACHMENT);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
//                                A_FPSConstant.SUBCODE_INVALID_ATTACHMENT_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.ATTACHMENT.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.isShowIcon() == null) {
//                        field.setIsShowIcon(false);
//                    }
//                    if (field.isApplyToAll() == null) {
//                        field.setApplyToAll(false);
//                    }
//                    if (field.isReplicateAllPages() == null) {
//                        field.setReplicateAllPages(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Is show icon: " + field.isShowIcon());
//                    hierarchicalLog.addStartHeading1("Apply to all: " + field.isApplyToAll());
//                    hierarchicalLog.addStartHeading1("Replicate all pages: " + field.isReplicateAllPages());
//                    //</editor-fold>
//                }
//                //</editor-fold> 
//
//                if (field.getFileData() != null) {
//                    hierarchicalLog.addStartHeading1("Start checking file data");
//
//                    //<editor-fold defaultstate="collapsed" desc="Check data of File">
//                    hierarchicalLog.addStartHeading2("Checking file extension + file name");
//                    if (Utils.isNullOrEmpty(field.getFileExtension()) && Utils.isNullOrEmpty(field.getFileName())) {
//                        hierarchicalLog.addEndHeading2("Checking fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
//                                A_FPSConstant.SUBCODE_MISSING_EXTENSION
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    hierarchicalLog.addStartHeading2("Checking file extension + file name successfully");
//
//                    hierarchicalLog.addStartHeading2("Checking file data");
//                    if (Utils.isNullOrEmpty(field.getFile())) {
//                        hierarchicalLog.addEndHeading2("Checking file data fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_ATTACHMENT,
//                                A_FPSConstant.SUBCODE_MISSING_FILE_DATA_OF_ATTACHMENT
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    hierarchicalLog.addStartHeading2("Checking file data successfully");
//
//                    hierarchicalLog.addStartHeading2("Checking file extension");
//                    if (Utils.isNullOrEmpty(field.getFileExtension())) {
//                        try {
//                            String fileName = field.getFileName();
//                            String[] splits = fileName.split("\\.");
//                            field.setFileExtension(splits[splits.length - 1]);
//                        } catch (Exception e) {
//                            hierarchicalLog.addEndHeading2("Checking file extension fail");
//                            return new InternalResponse(
//                                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                    A_FPSConstant.CODE_FIELD_ATTACHMENT,
//                                    A_FPSConstant.SUBCODE_MISSING_EXTENSION
//                            ).setHierarchicalLog(hierarchicalLog);
//                        }
//                    }
//                    hierarchicalLog.addStartHeading2("Checking file extension successfully");
//                    //</editor-fold>
//
//                    //<editor-fold defaultstate="collapsed" desc="Upload into FMS if need">
//                    if (field.getFile() != null && field.getFile().length()
//                            > PolicyConfiguration.getInstant()
//                                    .getSystemConfig()
//                                    .getAttributes()
//                                    .get(0)
//                                    .getMaximumFile()) {
//                        try {
//                            hierarchicalLog.addStartHeading2("Upload file into FMS");
//                            InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
//                                    Base64.decode(field.getFile()),
//                                    field.getFileExtension(),
//                                    transactionId);
//                            if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
//                                hierarchicalLog.addStartHeading2("Upload successfully");
//                                String uuid = (String) response.getData();
//                                field.setFile(uuid);
//                            } else {
//                                hierarchicalLog.addStartHeading2("Upload fail");
//                            }
//                        } catch (Exception ex) {
//                            hierarchicalLog.addStartHeading2("Cannot upload image from ImageField to FMS!. Using default");
//                        }
//                    }
//                    //</editor-fold>
//
//                    hierarchicalLog.addEndHeading1("Checking file data successfully");
//                }
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "hyperlink": {
//                //<editor-fold defaultstate="collapsed" desc="Generate HyperLinkFieldAttribute from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                HyperLinkFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, HyperLinkFieldAttribute.class);
//                } catch (Exception ex) {
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.HYPERLINK);
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_HYPERLINK,
//                                A_FPSConstant.SUBCODE_INVALID_HYPERLINK_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.HYPERLINK.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.getAlignment() == null) {
//                        field.setDefaultAlignment();
//                    }
//                    if (field.getColor() == null) {
//                        field.setColor("BLACK");
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//                    if (field.isMultiline() == null) {
//                        field.setMultiline(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
//                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "combo": {
//                //<editor-fold defaultstate="collapsed" desc="Generate ComboBox Field from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                ComboBoxFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, ComboBoxFieldAttribute.class);
//                } catch (Exception ex) {
//                    LogHandler.error(FieldSummary.class, transactionId, ex);
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.COMBOBOX);
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_COMBOBOX,
//                                A_FPSConstant.SUBCODE_INVALID_COMBOBOX_FIELD_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.COMBOBOX.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.getAlignment() == null) {
//                        field.setDefaultAlignment();
//                    }
//                    if (field.getColor() == null) {
//                        field.setColor("BLACK");
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//                    if (field.isMultiline() == null) {
//                        field.setMultiline(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
//                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//            case "toggle": {
//                //<editor-fold defaultstate="collapsed" desc="Generate Toogle Field from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                ToggleFieldAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, ToggleFieldAttribute.class);
//                } catch (Exception ex) {
//                    LogHandler.error(FieldSummary.class, transactionId, ex);
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.TOGGLE);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_TOGGLE,
//                                A_FPSConstant.SUBCODE_INVALID_TOGGLE_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.TOGGLE.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.getAlignment() == null) {
//                        field.setDefaultAlignment();
//                    }
//                    if (field.getColor() == null) {
//                        field.setColor("BLACK");
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//                    if (field.isMultiline() == null) {
//                        field.setMultiline(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
//                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
//                //</editor-fold>
//            }
//            case "numeric_stepper": {
//                //<editor-fold defaultstate="collapsed" desc="Generate Stepper Field from Payload">
//                hierarchicalLog.addStartHeading1("Start parse into " + typeField);
//
//                //<editor-fold defaultstate="collapsed" desc="Parse String into Field">
//                NumericStepperAttribute field = null;
//                try {
//                    field = MyServices.getJsonService().readValue(payload, NumericStepperAttribute.class);
//                } catch (Exception ex) {
//                    LogHandler.error(FieldSummary.class, transactionId, ex);
//                    hierarchicalLog.addEndHeading1("Parse into field fail");
//                    return new InternalResponse(
//                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                            A_FPSConstant.CODE_FAIL,
//                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
//                    ).setHierarchicalLog(hierarchicalLog);
//                }
//                hierarchicalLog.addEndHeading1("Parse into field successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check basic field">
//                hierarchicalLog.addStartHeading1("Start check basic");
//                if (isCheckBasicField) {
//                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
//                    hierarchicalLog.addChildHierarchicalLog(response.getHierarchicalLog());
//                    if (!response.isValid()) {
//                        hierarchicalLog.addEndHeading1("Checked fail");
//                        return response.setHierarchicalLog(hierarchicalLog);
//                    }
//                }
//                hierarchicalLog.addEndHeading1("Checked successfully");
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Check field type">
//                if (!Utils.isNullOrEmpty(field.getTypeName())) {
//                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.NUMERIC_STEP);
//
//                    if (!check) {
//                        hierarchicalLog.addEndHeading1("Check field type fail");
//                        return new InternalResponse(
//                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                                A_FPSConstant.CODE_FIELD_NUMERIC_STEPPER,
//                                A_FPSConstant.SUBCODE_INVALID_NUMERIC_TYPE
//                        ).setHierarchicalLog(hierarchicalLog);
//                    }
//                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
//                } else {
//                    field.setType(Resources.getFieldTypes().get(FieldTypeName.NUMERIC_STEP.getParentName()));
//                }
//                hierarchicalLog.addStartHeading1("Final field type: " + field.getType().getTypeName());
//                //</editor-fold>
//
//                //<editor-fold defaultstate="collapsed" desc="Initial data of field">
//                if (!isUpdate) {
//                    if (field.getAlignment() == null) {
//                        field.setDefaultAlignment();
//                    }
//                    if (field.getColor() == null) {
//                        field.setColor("BLACK");
//                    }
//                    if (field.isReadOnly() == null) {
//                        field.setReadOnly(false);
//                    }
//                    if (field.isMultiline() == null) {
//                        field.setMultiline(false);
//                    }
//                    //<editor-fold defaultstate="collapsed" desc="Logger">
//                    hierarchicalLog.addStartHeading1("Alignment: " + field.getAlignment());
//                    hierarchicalLog.addStartHeading1("Text Color: " + field.getColor());
//                    hierarchicalLog.addStartHeading1("Read Only: " + field.isReadOnly());
//                    hierarchicalLog.addStartHeading1("Multiline: " + field.isMultiline());
//                    //</editor-fold>
//                }
//                //</editor-fold>
//
//                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field).setHierarchicalLog(hierarchicalLog);
//                //</editor-fold>
//            }
//        }
//        return new InternalResponse(A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                new ResponseMessageController().writeStringField("error", "This type of Field not provide yet"))
//                .setHierarchicalLog(hierarchicalLog);
//    }
//    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="DistributeFlowDelete">
    /**
     * Dùng để phân phối kênh "Delete" phụ thuộc vào ParentType của Field
     * Use for "Delete" flow and it based on ParentType of a Field
     *
     * @param document
     * @param field
     * @param transactionId
     * @return
     * @throws Exception
     */
    private static InternalResponse distributeFlowDelete(
            Document document,
            User user,
            ExtendedFieldAttribute field,
            String transactionId) throws Exception {
        InternalResponse response = new InternalResponse();

        //<editor-fold defaultstate="collapsed" desc="Check process status">
        InternalResponse gate = CheckFieldProcessedYet.checkProcessed(field);
        if (gate.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return gate.setUser(user);
        }
        //</editor-fold>

        switch (field.getType().getParentType()) {
            case "SIGNATURE": {
                //<editor-fold defaultstate="collapsed" desc="Check if this signature have hash => already "Get Hash" => Delete temporal data in DB">
                if (!Utils.isNullOrEmpty(field.getHash())) {
                    response = ManagementTemporal.getTemporal(
                            String.valueOf(document.getId()),
                            field.getHash(),
                            transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }

                    response = ManagementTemporal.removeTemporal(
                            String.valueOf(document.getId()),
                            transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Delete Field + Field Details in DB">
                response = DeleteField.deleteField(
                        document.getId(),
                        field.getFieldName(),
                        transactionId);
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Check is the last Signature Field => if true, change the status of Document to 'READY'">
                response = ManagementTemporal.listTemporal(String.valueOf(document.getId()), transactionId);
                if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                    response = UpdateDocument.updateStatusOfDocument(
                            document.getId(),
                            user,
                            DocumentStatus.READY,
                            transactionId);
                }
                //</editor-fold>
                break;
            }
            default: {
                //<editor-fold defaultstate="collapsed" desc="Delete Field + Field Details in DB">
                response = DeleteField.deleteField(
                        document.getId(),
                        field.getFieldName(),
                        transactionId);
                //</editor-fold>
            }
        }
        return response;
    }
    //</editor-fold>
}
