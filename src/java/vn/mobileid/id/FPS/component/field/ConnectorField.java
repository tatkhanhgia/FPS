/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.field;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.DocumentStatus;
import fps_core.enumration.FieldTypeName;
import fps_core.enumration.RotateDegree;
import fps_core.objects.BasicFieldAttribute;
import fps_core.objects.CheckBoxFieldAttribute;
import fps_core.objects.Dimension;
import fps_core.objects.ExtendedFieldAttribute;
import fps_core.objects.FieldType;
import fps_core.objects.InitialsFieldAttribute;
import fps_core.objects.QRFieldAttribute;
import fps_core.objects.SignatureFieldAttribute;
import fps_core.objects.TextFieldAttribute;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.bouncycastle.util.encoders.Base64;
import vn.mobileid.id.FMS;
import vn.mobileid.id.FPS.component.document.CheckPayloadRequest;
import vn.mobileid.id.FPS.component.document.ConnectorDocument;
import vn.mobileid.id.FPS.component.document.ConnectorDocument_Internal;
import vn.mobileid.id.FPS.component.document.GetDocument;
import vn.mobileid.id.FPS.component.document.UpdateDocument;
import vn.mobileid.id.FPS.component.document.module.QRGenerator;
import vn.mobileid.id.FPS.component.document.process.ProcessingFactory;
import vn.mobileid.id.FPS.component.enterprise.ProcessModuleForEnterprise;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.controller.ResponseMessageController;
import vn.mobileid.id.FPS.fieldAttribute.QryptoFieldAttribute;
import vn.mobileid.id.FPS.object.Document;
import vn.mobileid.id.FPS.object.InternalResponse;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.FieldsSerializer;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.utils.ManagementTemporal;
import vn.mobileid.id.utils.TaskV2;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ConnectorField {

    // <editor-fold defaultstate="collapsed" desc="Add Field">
    public static InternalResponse addField(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Get Documents in Package && Verify Token">
        InternalResponse response = ConnectorDocument_Internal.getDocuments(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
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
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            ).setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse payload in input into Field Object in FPS">
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

        //<editor-fold defaultstate="collapsed" desc="Check if Field is Text Type => check childType">
        if (field instanceof TextFieldAttribute) {
            if (Utils.isNullOrEmpty(field.getTypeName())) {
                InternalResponse result = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_TEXT,
                        A_FPSConstant.SUBCODE_MISSING_TEXT_FIELD_TYPE
                );
                result.setUser(user);
                return result;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse from percentage unit to point unit">
        String temp_ = request.getHeader("x-dimension-unit");
        System.out.println("Dimension:" + temp_);
        if (temp_ != null && temp_.equals("percentage")) {
            if (document.getDocumentWidth() != 0 && document.getDocumentHeight() != 0) {
                field.setDimension(ProcessModuleForEnterprise.getInstance(user).parse(document, field.getDimension()));
            }
        }
        System.out.println("X:" + field.getDimension().getX());
        System.out.println("Y:" + field.getDimension().getY());
        System.out.println("W:" + field.getDimension().getWidth());
        System.out.println("H:" + field.getDimension().getHeight());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create QR Image if that type is QR Code">
        if (field instanceof QRFieldAttribute) {
            try {
                QRFieldAttribute qr = (QRFieldAttribute) field;
                if (Utils.isNullOrEmpty(qr.getValue())) {
                    qr.setValue("Waiting for process Qrypto");
                }
                byte[] imageQR = QRGenerator.generateQR(
                        qr.getValue(),
                        Math.round(qr.getDimension().getWidth()),
                        Math.round(qr.getDimension().getWidth()),
                        qr.IsTransparent());
                if (Utils.isNullOrEmpty(qr.getImageQR())) {
                    qr.setImageQR(Base64.toBase64String(imageQR));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_QR,
                        A_FPSConstant.SUBCODE_CANNOT_GENERATE_QR
                ).setException(ex).setUser(user);
                return response;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add Field">
        response = AddField.addField(
                document.getId(),
                field,
                "hmac",
                user.getAzp(),
                transactionId);

        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        int documentFieldId = (int) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Add Field Details">
        response = AddField.addDetailField(
                documentFieldId,
                field.getType().getTypeId(),
                field,
                "hmac",
                user.getAzp(),
                transactionId);
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                new ResponseMessageController()
                        .writeStringField("field_name", field.getFieldName())
                        .build()
        ).setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Add FieldV2">
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

        //Check exist of field name
        InternalResponse temp = GetField.getFieldData(documentId, field.getFieldName(), transactionId);
        if (temp.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_INVALID_FIELD_NAME
            ).setUser(user);
        }

        //Processing append field form into file 
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> appended = executor.submit(
                new TaskV2(
                        new Object[]{user, document_, documents.size(), field, transactionId},
                        transactionId) {
            @Override
            public InternalResponse call() {
                InternalResponse response = null;
                try {
                    if (this.get()[3] instanceof TextFieldAttribute) {
                        response = ProcessingFactory.createType_Module(ProcessingFactory.TypeProcess.TEXTFIELD).createFormField(
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

        //Processing add field data into DB
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
    public static InternalResponse updateField(
            HttpServletRequest request,
            String payload,
            String transactionId
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Check payload">
        if (payload == null) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_FAIL,
                    A_FPSConstant.SUBCODE_NO_PAYLOAD_FOUND
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Call throught connectorDocument to verify and get Documents of packageId">
        InternalResponse response = ConnectorDocument_Internal.getDocuments(request, transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user = response.getUser();
        List<Document> documents = (List<Document>) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Parse to field + check type of field">
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
            return response.setUser(user);
        }
        ExtendedFieldAttribute fieldOld = (ExtendedFieldAttribute) response.getData();
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
//                                InitialsFieldAttribute init = new ObjectMapper().readValue(value.getFieldValue(), InitialsFieldAttribute.class);
//                                init = (InitialsFieldAttribute) value.clone(init, value.getDimension());
//                                fieldsInit.add(init);
//                            } catch (JsonProcessingException ex) {
//                                Logger.getLogger(ConnectorField.class.getName()).log(Level.SEVERE, null, ex);
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
            return checking.setUser(user);
        }
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
                    System.out.println("FinalX:" + field.getDimension().getX());
                    System.out.println("FinalX:" + field.getDimension().getY());
                    System.out.println("FinalX:" + field.getDimension().getWidth());
                    System.out.println("FinalX:" + field.getDimension().getHeight());
                    field.setDimension(ProcessModuleForEnterprise.getInstance(user).parse(document_, field.getDimension()));
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
            //</editor-fold>
        }

        System.out.println("==================parse into point");
        System.out.println("FinalX:" + field.getDimension().getX());
        System.out.println("FinalX:" + field.getDimension().getY());
        System.out.println("FinalX:" + field.getDimension().getWidth());
        System.out.println("FinalX:" + field.getDimension().getHeight());

        //<editor-fold defaultstate="collapsed" desc="Merge 2 JSON - 1 in DB as ExternalFieldAtrtibute.getDetailValue - 1 in Payload">
        JsonNode merge = Utils.merge(
                fieldOld.getDetailValue(),
                new ObjectMapper()
                        .setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                        .writeValueAsString(field));
        String temp = merge.toPrettyString();
        System.out.println("Json Original:" + fieldOld.getDetailValue());
        System.out.println("Json Update:" + new ObjectMapper()
                .setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
                .writeValueAsString(field));
        System.out.println("FinalJSON:" + temp);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Merge Payload vs Field Value Old">
        JsonNode merge2 = Utils.merge(fieldOld.getFieldValue(), payload);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create new QR Image if that type is QR Code">
        if (field instanceof QRFieldAttribute) {
            try {
                QRFieldAttribute qr = (QRFieldAttribute) field;
                if (!Utils.isNullOrEmpty(qr.getValue())) {
                    byte[] imageQR = QRGenerator.generateQR(
                            (String) Utils.getFromJson_("value", merge2.toPrettyString()),
                            Math.round(qr.getDimension().getWidth()),
                            Math.round(qr.getDimension().getWidth()),
                            qr.IsTransparent());
                    qr.setImageQR(Base64.toBase64String(imageQR));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response = new InternalResponse(
                        A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                        A_FPSConstant.CODE_FIELD_QR,
                        A_FPSConstant.SUBCODE_CANNOT_GENERATE_QR
                ).setException(ex).setUser(user);
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
                field.getDimension().getWidth(),
                field.getDimension().getHeight(),
                field.getDimension().getY() + field.getDimension().getHeight(),
                field.getDimension().getX(),
                Utils.getFromJson("rotate", payload) == null
                ? null
                : RotateDegree.getRotateDegree(field.getRotate()),
                field.getVisibleEnabled() == null ? fieldOld.getVisibleEnabled() : field.getVisibleEnabled(),
                "hmac",
                user.getEmail(),
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update Field Details">
        UpdateField.updateFieldDetails(
                fieldOld.getDocumentFieldId(),
                user,
                temp,
                "hmac",
                transactionId);
        if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
            return response.setUser(user);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Replicate Field if that field is Initial">
        if (field instanceof InitialsFieldAttribute) {
//            InternalResponse resultThread = (InternalResponse) thread.get();
//            if(resultThread.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS){
//                return resultThread.setUser(user);
//            }
            if (field.getPage() <= 0) {
                field.setPage(fieldOld.getPage());
            }
            return ReplicateInitialField.replicateField(
                    (InitialsFieldAttribute) field,
                    document_,
                    user,
                    transactionId).setUser(user);
        }
        //</editor-fold>

        return new InternalResponse(
                A_FPSConstant.HTTP_CODE_SUCCESS,
                ""
        ).setUser(user);
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
                new ObjectMapper().writeValueAsString(temp)
        ).setUser(user);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Fields">
    public static InternalResponse getFields(
            HttpServletRequest request,
            String transactionId
    ) throws Exception {
        //Get Documents in Package (Verify + get Documents)
        InternalResponse response = ConnectorDocument_Internal.getDocuments(request, transactionId);
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
                new ObjectMapper().writeValueAsString(serializer)).setUser(user);
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
        InternalResponse response = ConnectorDocument.getDocuments(request, packageId, transactionId);
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
                response = ConnectorField_Internal.getField(
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
        List<TextFieldAttribute> textboxs = new ArrayList<>();
        List<InitialsFieldAttribute> initials = new ArrayList<>();
        List<QRFieldAttribute> qrs = new ArrayList<>();
        List<QryptoFieldAttribute> qryptos = new ArrayList<>();

        for (ExtendedFieldAttribute field : fields) {
            try {
                switch (field.getType().getTypeId()) {
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 20:
                    case 21:
                    case 24:
                    case 26:
                    case 27:
                    case 1: {
                        TextFieldAttribute text = new TextFieldAttribute();
                        text = new ObjectMapper().readValue(field.getDetailValue(), TextFieldAttribute.class);
                        text = (TextFieldAttribute) field.clone(text, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        textboxs.add(text);
                        break;
                    }
                    case 2: {
                        break;
                    }
                    case 3: {
                        break;
                    }
                    case 37: {
                        QryptoFieldAttribute qr = new ObjectMapper().readValue(field.getDetailValue(), QryptoFieldAttribute.class);
                        qr.setDimension(ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        qr = (QryptoFieldAttribute) field.clone(qr, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        qryptos.add(qr);
                        break;
                    }
                    case 4: {
                        QRFieldAttribute qr = new ObjectMapper().readValue(field.getDetailValue(), QRFieldAttribute.class);
                        qr.setDimension(ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        qr = (QRFieldAttribute) field.clone(qr, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
                        qrs.add(qr);
                        break;
                    }
                    case 5: {
                        InitialsFieldAttribute initialField = new ObjectMapper().readValue(field.getDetailValue(), InitialsFieldAttribute.class);
                        initialField = (InitialsFieldAttribute) field.clone(initialField, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

                        //<editor-fold defaultstate="collapsed" desc="Download Image from FMS if image is UUID">
                        if(initialField.getImage() != null && initialField.getImage().length() <= 32){
                            try{
                                InternalResponse response = FMS.downloadDocumentFromFMS(initialField.getImage(), "tran");
                                
                                if(response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS){
                                    initialField.setImage(Base64.toBase64String((byte[])response.getData()));
                                }
                            } catch(Exception ex){
                                System.err.println("Cannot Download Image in Initial from FMS");
                            }
                        }
                        //</editor-fold>
                        
                        initials.add(initialField);
                        break;
                    }
                    case 6: {
                        break;
                    }
                    case 7: {
                        //Mapping into SignatureFieldAttribute
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
                        signatureField = new ObjectMapper().readValue(json2, SignatureFieldAttribute.class);
                        signatureField = (SignatureFieldAttribute) field.clone(signatureField, ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));

//                        SignatureFieldAttribute signatureField = new SignatureFieldAttribute();
//                        if (!Utils.isNullOrEmpty(field.getFieldValue())) {
//                            signatureField = new ObjectMapper()
//                                    .setAnnotationIntrospector(new IgnoreIngeritedIntrospector())
//                                    .readValue(field.getFieldValue(), SignatureFieldAttribute.class);
//                        }
//                        signatureField.setDimension(ProcessModuleForEnterprise.getInstance(user).reverseParse(document, field.getDimension()));
//                        signatureField.setFieldName(field.getFieldName());
//                        signatureField.setPage(field.getPage());
//                        signatureField.setVisibleEnabled(field.getVisibleEnabled());
//                        signatureField.setType(field.getType());
                        signatureField.setLevelOfAssurance(field.getLevelOfAssurance());
//                        signatureField.setSuffix(field.getSuffix());
                        signatures.add(signatureField);
                        break;
                    }
                }
            } catch (Exception ex) {
                LogHandler.error(ConnectorField.class, "transactionId", ex);
                return null;
            }
        }
        Object[] array = new Object[10];
        array[0] = textboxs;
        array[1] = null;
        array[2] = null;
        array[3] = qrs;
        array[4] = initials;
        array[5] = null;
        array[6] = signatures;
        array[7] = null;
        array[8] = null;
        array[9] = qryptos;
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
        String temp = url.substring(url.lastIndexOf("/") + 1);
        switch (temp) {
            case "signature": {
                //<editor-fold defaultstate="collapsed" desc="Generate SignatureFieldAttribute from Payload">
                SignatureFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, SignatureFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    );
                }
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }

                field.setType(Resources.getFieldTypes().get(FieldTypeName.SIGNATURE.getParentName()));

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
            case "text": {
                //<editor-fold defaultstate="collapsed" desc="Generate TextFieldAttribute from Payload">
                TextFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, TextFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    );
                }
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }

                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.TEXTBOX);

                    if (!check) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_TEXT,
                                A_FPSConstant.SUBCODE_INVALID_TEXT_FIELD_TYPE
                        );
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                }
                if (!isUpdate) {
                    if (field.getAlign() == null) {
                        field.setAlign(TextFieldAttribute.Align.LEFT);
                    }
                    if (field.getColor() == null) {
                        field.setColor("BLACK");
                    }
                    try {
                        System.out.println("Font:" + field.getFont().getSize());
                        System.out.println("String:" + new String(field.getValue().getBytes("UTF-8"), "UTF-8"));
                    } catch (Exception ex) {
                    }
                }
                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
            case "checkbox": {
                //<editor-fold defaultstate="collapsed" desc="Generate CheckBoxFieldAttribute from Payload">
                CheckBoxFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, CheckBoxFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    );
                }
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }

                boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.CHECKBOX);

                if (!check) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_CHECKBOX,
                            A_FPSConstant.SUBCODE_INVALID_CHECKBOX_FIELD_TYPE
                    );
                }
                field.setType(Resources.getFieldTypes().get(field.getTypeName()));

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
            case "initial": {
                //<editor-fold defaultstate="collapsed" desc="Generate InitialsFieldAttribute from Payload">
                InitialsFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, InitialsFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    );
                }
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkAddInitialField(field, transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }

                field.setType(Resources.getFieldTypes().get(FieldTypeName.INITIAL.getParentName()));

                if (field.getImage() != null) {
                    try {
                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                Base64.decode(field.getImage()),
                                "png",
                                transactionId);
                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                            String uuid = (String) response.getData();
                            field.setImage(uuid);
                        }
                    } catch (Exception ex) {
                        System.err.println("Cannot upload image from QR to FMS!. Using default");
                    }
                }

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
            case "qrcode": {
                //<editor-fold defaultstate="collapsed" desc="Generate QRFieldAttribute from Payload">
                QRFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, QRFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    );
                }
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }

                if (Utils.isNullOrEmpty(field.getValue()) && !isUpdate) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FIELD_QR,
                            A_FPSConstant.SUBCODE_MISSING_ENCODE_STRING_OF_QR
                    );
                }

                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.QR);

                    if (!check) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_QR,
                                A_FPSConstant.SUBCODE_INVALID_QR_TYTPE
                        );
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.QR.getParentName()));
                }

                if (field.getImageQR() != null && field.getImageQR().length() > 100000) {
                    try {
                        InternalResponse response = vn.mobileid.id.FMS.uploadToFMS(
                                Base64.decode(field.getImageQR()),
                                "png",
                                transactionId);
                        if (response.getStatus() == A_FPSConstant.HTTP_CODE_SUCCESS) {
                            String uuid = (String) response.getData();
                            field.setImageQR(uuid);
                        }
                    } catch (Exception ex) {
                        System.err.println("Cannot upload image from QR to FMS!. Using default");
                    }
                }

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
            case "qrcode-qrypto": {
                //<editor-fold defaultstate="collapsed" desc="Generate QryptoFieldAttribute from Payload">
                QryptoFieldAttribute field = null;
                try {
                    field = new ObjectMapper().readValue(payload, QryptoFieldAttribute.class);
                } catch (JsonProcessingException ex) {
                    return new InternalResponse(
                            A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                            A_FPSConstant.CODE_FAIL,
                            A_FPSConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE
                    );
                }
                if (isCheckBasicField) {
                    InternalResponse response = CheckPayloadRequest.checkBasicField(field, transactionId);
                    if (response.getStatus() != A_FPSConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                }

                if (!Utils.isNullOrEmpty(field.getTypeName())) {
                    boolean check = CheckPayloadRequest.checkField(field, FieldTypeName.QRYPTO);

                    if (!check) {
                        return new InternalResponse(
                                A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                                A_FPSConstant.CODE_FIELD_QR,
                                A_FPSConstant.SUBCODE_INVALID_QR_TYTPE
                        );
                    }
                    field.setType(Resources.getFieldTypes().get(field.getTypeName()));
                } else {
                    field.setType(Resources.getFieldTypes().get(FieldTypeName.QRYPTO.getParentName()));
                }

                return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, field);
                //</editor-fold>
            }
        }
        return new InternalResponse(A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                new ResponseMessageController().writeStringField("error", "This type of Field not provide yet"));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DistributeFlowDelete">
    /**
     * Dùng để phân phối kênh "Delete" phụ thuộc vào ParentType của Field Use
     * for "Delete" flow and it based on ParentType of a Field
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

    //<editor-fold defaultstate="collapsed" desc="Check the status of Document">
    /**
     * Kiểm tra status của Document và trả về lỗi tương ứng Check the status of
     * the Document and return the error relative to that status
     *
     * @param document
     * @param transactionId
     * @return
     */
    private static InternalResponse checkStatusOfDocument(Document document, String transactionId) {
        if (document.getStatus().equals(DocumentStatus.PROCESSED)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_PROCESSING
            );
        }
        if (document.getStatus().equals(DocumentStatus.DELETED)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_DELETED
            );
        }
        if (document.getStatus().equals(DocumentStatus.PENDING)) {
            return new InternalResponse(
                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
                    A_FPSConstant.CODE_DOCUMENT,
                    A_FPSConstant.SUBCODE_THE_DOCUMENT_STATUS_IS_PENDING
            );
        }
        return new InternalResponse(A_FPSConstant.HTTP_CODE_SUCCESS, "");
    }
    //</editor-fold>   

    public static void main(String[] args) throws JsonProcessingException {
        String temp = "a_b_c_d";
        System.out.println(temp.substring(0,
                temp.lastIndexOf("_")));

    }
}
