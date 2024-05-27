/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database;

import vn.mobileid.id.FPS.services.impls.databaseConnection.DatabaseConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import fps_core.enumration.RotateDegree;
import fps_core.objects.core.BasicFieldAttribute;
import fps_core.objects.Dimension;
import fps_core.objects.core.ExtendedFieldAttribute;
import fps_core.objects.FieldType;
import fps_core.objects.core.SignatureFieldAttribute;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.object.User;
import vn.mobileid.id.FPS.serializer.IgnoreChildIntrospector;
import vn.mobileid.id.FPS.serializer.IgnoreIngeritedIntrospector;
import vn.mobileid.id.FPS.services.MyServices;
import vn.mobileid.id.FPS.systemManagement.Configuration;
import vn.mobileid.id.FPS.systemManagement.LogHandler;
import vn.mobileid.id.FPS.systemManagement.Resources;
import vn.mobileid.id.helper.ORM_JPA.database.CreateConnection;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public interface DatabaseImpl_field {

    /**
     * Get về giá trị field + field_detail của documentId + field_name truyền
     * vào
     *
     * @param documentId
     * @param fieldName
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse getFieldData(
            long documentId,
            String fieldName,
            String transactionId
    ) throws Exception;

    /**
     * Get về giá trị field + field_detail documentFieldId truyền vào
     *
     * @param documentFieldId
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse getFieldData(
            long documentFieldId,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getFieldType(
            String transactionId
    ) throws Exception;

    public DatabaseResponse addField(
            long documentId,
            Object field,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;

    /**
     * Thêm chi tiết field (field_detail)
     *
     * @param documentFieldId
     * @param fieldTypeId
     * @param data
     * @param hmac
     * @param createdBy
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse addFieldDetails(
            long documentFieldId,
            long fieldTypeId,
            Object data,
            String hmac,
            String createdBy,
            String transactionId
    ) throws Exception;

    /**
     * Cập nhật lại thông tin Value trong DocumentField khi đã xử lý xong
     *
     * @param documentFieldId
     * @param dataAfterProcess
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse updateValuesOfField(
            long documentFieldId,
            User user,
            String dataAfterProcess,
            String transactionId
    ) throws Exception;

    /**
     * Cập nhật lại thông tin Hash trong DocumentField khi đã xử lý xong
     *
     * @param documentFieldId
     * @param dataAfterProcess
     * @param transactionId
     * @return
     * @throws Exception
     */
    public DatabaseResponse updateHashOfField(
            long documentFieldId,
            User user,
            String hash,
            String transactionId
    ) throws Exception;

    /**
     * Cập nhật lại field details
     *
     * @param documentFieldId
     * @param documentFieldDetailType
     * @param value
     * @param hmac
     * @param modifiedBy
     * @param transactionId
     * @return
     * @throws java.lang.Exception
     */
    public DatabaseResponse updateFieldDetails(
            long documentFieldId,
            long documentFieldDetailType,
            String value,
            String hmac,
            String modifiedBy,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getFieldsData(
            long documentId,
            String trasactionId
    ) throws Exception;

    public DatabaseResponse deleteField(
            long documentId,
            String fieldName,
            String transactionId
    ) throws Exception;

    public DatabaseResponse deleteField(
            long documentFieldId,
            String transactionId
    ) throws Exception;

    public DatabaseResponse updateField(
            long documentFieldId,
            int type,
            String name,
            String value,
            String hash,
            int pageNumber,
            float width,
            float height,
            float top,
            float left,
            RotateDegree rotate,
            boolean visible,
            String hmac,
            String modifiedBy,
            String transactionId
    ) throws Exception;
}

class DatabaseImpl_field_ implements DatabaseImpl_field {

    private static int retryTimes = 1; // default no retry

    static {
        retryTimes = Configuration.getInstance().getRetry();
        if (retryTimes == 0) {
            retryTimes = 1;
        }
    }

    @Override
    public DatabaseResponse getFieldType(String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_ATTR_TYPE_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                null,
                null,
                "Get Fields Type");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        List<FieldType> listField = new ArrayList<>();
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            FieldType type = new FieldType();
            Long temp = (long) row.get("ID");
            type.setTypeId(temp.intValue());
            type.setTypeName((String) row.get("NAME"));
            type.setRequired((boolean) row.get("REQUIRED"));
            type.setParentType((String) row.get("REMARK"));
            listField.add(type);
        }
        response.setObject(listField);
        return response;
    }

    @Override
    public DatabaseResponse addField(
            long documentId,
            Object inputClass,
            String hmac,
            String createdBy,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        BasicFieldAttribute field = (BasicFieldAttribute) inputClass;
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_ID", documentId);
        datas.put("pTYPE", field.getType().getTypeId());
        datas.put("pNAME", field.getFieldName());
        datas.put("pVALUE", MyServices.getJsonService(
                new ObjectMapper().setAnnotationIntrospector(new IgnoreChildIntrospector())
        )
                .writeValueAsString(inputClass));
        datas.put("pPAGE_NUMBER", field.getPage());
        datas.put("pFIELD_WIDTH", field.getDimension().getWidth());
        datas.put("pFIELD_HEIGHT", field.getDimension().getHeight());
        datas.put("pFIELD_TOP", field.getDimension().getY() + field.getDimension().getHeight());
        datas.put("pFIELD_LEFT", field.getDimension().getX());
        datas.put("pROTATE", field.getRotate());
        try {
            datas.put("pVISIBLE_ENABLED", field.getVisibleEnabled() == null ? false : field.getVisibleEnabled());
        } catch (Exception ex) {
            datas.put("pVISIBLE_ENABLED", false);
        }
        try {
            if (inputClass instanceof SignatureFieldAttribute) {
                SignatureFieldAttribute temp = new SignatureFieldAttribute();
                temp.setLevelOfAssurance(((SignatureFieldAttribute) inputClass).getLevelOfAssurance());
                temp.setSuffix(((SignatureFieldAttribute) inputClass).getSuffix());
                if (temp.getLevelOfAssurance() == null) {
                    datas.put("pLEVEL_OF_ASSURANCE", null);
                } else {
                    datas.put("pLEVEL_OF_ASSURANCE", MyServices.getJsonService()
                            .writeValueAsString(
                                    temp
                            ));
                }
            } else {
                datas.put("pLEVEL_OF_ASSURANCE", null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        datas.put("pHMAC", hmac);
        datas.put("pCREATED_BY", createdBy);

        HashMap<String, Integer> registedOut = new HashMap<>();
        registedOut.put("pDOCUMENT_FIELD_ID", Types.BIGINT);
        registedOut.put("pRESPONSE_CODE", Types.VARCHAR);
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                registedOut,
                "Add Field");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        int documentFieldId = 0;
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            Long temp = (long) row.get("pDOCUMENT_FIELD_ID");
            documentFieldId = temp.intValue();
        }
        response.setObject(documentFieldId);
        return response;
    }

    @Override
    public DatabaseResponse addFieldDetails(
            long documentFieldId,
            long fieldTypeId,
            Object data,
            String hmac,
            String createdBy,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_DETAIL_ADD(?,?,?,?,?,?)}";

        ObjectMapper ob = new ObjectMapper();
        ob.setAnnotationIntrospector(new IgnoreIngeritedIntrospector());
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);
        datas.put("pDOCUMENT_FIELD_ATTR_TYPE", fieldTypeId);
        datas.put("pVALUE", MyServices.getJsonService(ob).writeValueAsString(data));
        datas.put("pHMAC", hmac);
        datas.put("pCREATED_BY", createdBy);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Add Detail Field");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    /**
     * Get về dữ liệu của Field và Field detail của documentId và fieldname bất
     * kì
     *
     * @param documentId
     * @param transactionId
     * @return
     * @throws Exception
     */
    @Override
    public DatabaseResponse getFieldData(
            long documentId,
            String fieldName,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_GET_FIELD_DETAIL_FROM_FIELD_NAME(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_ID", documentId);
        datas.put("pFIELD_NAME", fieldName);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "get Field + FieldDetail in DB");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

//        List<ExtendedFieldAttribute> listExField = new ArrayList<>();
        ExtendedFieldAttribute attribute = new ExtendedFieldAttribute();
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            attribute.setFieldValue((String) row.get("DOCUMENT_FIELD_VALUE"));
            attribute.setDocumentFieldId((long) row.get("ID"));
            attribute.setFieldName((String) row.get("DOCUMENT_FIELD_NAME"));
            attribute.setDetailValue((String) row.get("DOCUMENT_FIELD_DETAIL_VALUE"));
            attribute.setPage((int) row.get("PAGE_NUMBER"));
            Dimension dimension = new Dimension();
            dimension.setX((float) row.get("FIELD_LEFT"));
            float bottom = (float) row.get("FIELD_TOP") - (float) row.get("FIELD_HEIGHT");
            dimension.setY(bottom);
            dimension.setWidth((float) row.get("FIELD_WIDTH"));
            dimension.setHeight((float) row.get("FIELD_HEIGHT"));
            attribute.setDimension(dimension);
            attribute.setRotate((int) row.get("ROTATE"));
            try {
                attribute.setVisibleEnabled((Boolean) row.get("VISIBLE_ENABLED"));
            } catch (Exception ex) {
                attribute.setVisibleEnabled(Boolean.FALSE);
            }
            attribute.setHash((String) row.get("HASH"));
            for (FieldType type : Resources.getFieldTypes().values()) {
                if (type.getTypeId() == (int) row.get("DOCUMENT_FIELD_TYPE")) {
                    attribute.setType(type);
                    break;
                }
            }
            if (row.get("LEVEL_OF_ASSURANCE") != null) {
                SignatureFieldAttribute temp = MyServices.getJsonService().readValue(
                        (String) row.get("LEVEL_OF_ASSURANCE"),
                        SignatureFieldAttribute.class);
                attribute.setLevelOfAssurance(temp.getLevelOfAssurance());
            }

            try {
                BasicFieldAttribute basic = MyServices.getJsonService().readValue((String) row.get("DOCUMENT_FIELD_VALUE"), BasicFieldAttribute.class);
                attribute.setProcessStatus(basic.getProcessStatus());
                attribute.setProcessBy(basic.getProcessBy());
                attribute.setProcessOn(basic.getProcessOn());
            } catch (Exception ex) {
            }
        }
        response.setObject(attribute);
        return response;
    }

    @Override
    public DatabaseResponse updateValuesOfField(
            long documentFieldId,
            User user,
            String dataAfterProcess,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);
        datas.put("pTYPE", null);
        datas.put("pNAME", null);
        datas.put("pVALUE", dataAfterProcess);
        datas.put("pHASH", null);
        datas.put("pPAGE_NUMBER", null);
        datas.put("pFIELD_WIDTH", null);
        datas.put("pFIELD_HEIGHT", null);
        datas.put("pFIELD_TOP", null);
        datas.put("pFIELD_LEFT", null);
        datas.put("pROTATE", null);
        datas.put("pVISIBLE_ENABLED", null);
        datas.put("pHMAC", null);
        datas.put("pLAST_MODIFIED_BY", user.getAzp());

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Update values in Field");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse updateFieldDetails(
            long documentFieldId,
            long documentFieldDetailType,
            String value,
            String hmac,
            String modifiedBy,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_DETAIL_UPDATE(?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);
        datas.put("pDOCUMENT_FIELD_ATTR_TYPE", documentFieldDetailType == 0 ? null : documentFieldDetailType);
        datas.put("pVALUE", value);
        datas.put("pHMAC", hmac);
        datas.put("pLAST_MODIFIED_BY", modifiedBy);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Update field details");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getFieldsData(
            long documentId,
            String trasactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_GET_FIELD_DETAIL_LIST(?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_ID", documentId);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "get Field + FieldDetail in DB");

        LogHandler.debug(this.getClass(), trasactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

        List<ExtendedFieldAttribute> listExField = new ArrayList<>();
        for (HashMap<String, Object> row : response.getRows()) {
            ExtendedFieldAttribute attribute = new ExtendedFieldAttribute();

            try {
                BasicFieldAttribute basic = MyServices.getJsonService().readValue((String) row.get("DOCUMENT_FIELD_VALUE"), BasicFieldAttribute.class);
                attribute.setProcessStatus(basic.getProcessStatus());
                attribute.setProcessBy(basic.getProcessBy());
                attribute.setProcessOn(basic.getProcessOn());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            attribute.setFieldValue((String) row.get("DOCUMENT_FIELD_VALUE"));
            attribute.setDocumentFieldId((long) row.get("ID"));
            attribute.setFieldName((String) row.get("DOCUMENT_FIELD_NAME"));
            attribute.setDetailValue((String) row.get("DOCUMENT_FIELD_DETAIL_VALUE"));
            attribute.setDetailType((long) row.get("DOCUMENT_FIELD_ATTR_TYPE"));
            attribute.setPage((int) row.get("PAGE_NUMBER"));
            Dimension dimension = new Dimension();
            dimension.setX((float) row.get("FIELD_LEFT"));
            float bottom = (float) row.get("FIELD_TOP") - (float) row.get("FIELD_HEIGHT");
            dimension.setY(bottom);
            dimension.setWidth((float) row.get("FIELD_WIDTH"));
            dimension.setHeight((float) row.get("FIELD_HEIGHT"));
            attribute.setDimension(dimension);
            attribute.setVisibleEnabled(row.get("VISIBLE_ENABLED") == null ? Boolean.FALSE : (Boolean) row.get("VISIBLE_ENABLED"));
            attribute.setHash((String) row.get("HASH"));
            if (row.get("ROTATE") != null) {
                attribute.setRotate((int) row.get("ROTATE"));
            } else {
                attribute.setRotate(0);
            }
            for (FieldType type : Resources.getFieldTypes().values()) {
                if (type.getTypeId() == (int) row.get("DOCUMENT_FIELD_TYPE")) {
                    attribute.setType(type);
                    break;
                }
            }
            if (!Utils.isNullOrEmpty((String) row.get("LEVEL_OF_ASSURANCE"))) {
                SignatureFieldAttribute temp = MyServices.getJsonService().readValue((String) row.get("LEVEL_OF_ASSURANCE"), SignatureFieldAttribute.class);
                attribute.setLevelOfAssurance(temp.getLevelOfAssurance());
                attribute.setSuffix(temp.getSuffix());
            }
            listExField.add(attribute);
        }
        response.setObject(listExField);
        return response;
    }

    @Override
    public DatabaseResponse updateHashOfField(
            long documentFieldId,
            User user,
            String hash,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);
        datas.put("pTYPE", null);
        datas.put("pNAME", null);
        datas.put("pVALUE", null);
        datas.put("pHASH", hash);
        datas.put("pPAGE_NUMBER", null);
        datas.put("pFIELD_WIDTH", null);
        datas.put("pFIELD_HEIGHT", null);
        datas.put("pFIELD_TOP", null);
        datas.put("pFIELD_LEFT", null);
        datas.put("pROTATE", null);
        datas.put("pVISIBLE_ENABLED", null);
        datas.put("pHMAC", null);
        datas.put("pLAST_MODIFIED_BY", user.getAzp());

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "Update values in Field");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getFieldData(
            long documentFieldId,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_DETAIL_GET(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);
        datas.put("pDOCUMENT_FIELD_ATTR_TYPE", 0);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "get Field + FieldDetail in DB");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
            return response;
        }

//        List<ExtendedFieldAttribute> listExField = new ArrayList<>();
        ExtendedFieldAttribute attribute = new ExtendedFieldAttribute();
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            attribute.setFieldValue((String) row.get("DOCUMENT_FIELD_VALUE"));
            attribute.setDocumentFieldId((long) row.get("ID"));
            attribute.setFieldName((String) row.get("DOCUMENT_FIELD_NAME"));
            attribute.setDetailValue((String) row.get("DOCUMENT_FIELD_DETAIL_VALUE"));
            attribute.setPage((int) row.get("PAGE_NUMBER"));
            Dimension dimension = new Dimension();
            dimension.setX((float) row.get("FIELD_LEFT"));
            float bottom = (float) row.get("FIELD_TOP") - (float) row.get("FIELD_HEIGHT");
            dimension.setY(bottom);
            dimension.setWidth((float) row.get("FIELD_WIDTH"));
            dimension.setHeight((float) row.get("FIELD_HEIGHT"));
            attribute.setDimension(dimension);
            attribute.setRotate((int) row.get("ROTATE"));
            attribute.setVisibleEnabled((Boolean) row.get("VISIBLE_ENABLED"));
            for (FieldType type : Resources.getFieldTypes().values()) {
                if (type.getTypeId() == (int) row.get("DOCUMENT_FIELD_TYPE")) {
                    attribute.setType(type);
                    break;
                }
            }

            try {
                BasicFieldAttribute basic = MyServices.getJsonService().readValue((String) row.get("DOCUMENT_FIELD_VALUE"), BasicFieldAttribute.class);
                attribute.setProcessStatus(basic.getProcessStatus());
                attribute.setProcessBy(basic.getProcessBy());
                attribute.setProcessOn(basic.getProcessOn());
            } catch (Exception ex) {
            }
        }
        response.setObject(attribute);
        return response;
    }

    @Override
    public DatabaseResponse deleteField(
            long documentId,
            String fieldName,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_DELETE_FROM_FIELD_NAME(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_ID", documentId);
        datas.put("pFIELD_NAME", fieldName);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "delete Field in DB");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse deleteField(
            long documentFieldId,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_DELETE(?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "delete Field in DB");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse updateField(
            long documentFieldId,
            int type,
            String name,
            String value,
            String hash,
            int pageNumber,
            float width,
            float height,
            float top,
            float left,
            RotateDegree rotate,
            boolean visible,
            String hmac,
            String modifiedBy,
            String transactionId) throws Exception {
        String nameStore = "{ call USP_DOCUMENT_FIELD_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> datas = new HashMap<>();
        datas.put("pDOCUMENT_FIELD_ID", documentFieldId);
        datas.put("pTYPE", type == 0 ? null : type);
        datas.put("pNAME", name);
        datas.put("pVALUE", value);
        datas.put("pHASH", hash);
        datas.put("pPAGE_NUMBER", pageNumber <= 0 ? null : pageNumber);
        datas.put("pFIELD_WIDTH", width <= 0 ? null : width);
        datas.put("pFIELD_HEIGHT", height <= 0 ? null : height);
        datas.put("pFIELD_TOP", top);
        datas.put("pFIELD_LEFT", left);
        datas.put("pROTATE", rotate != null ? rotate.getDegree() : null);
        datas.put("pVISIBLE_ENABLED", visible);
        datas.put("pHMAC", hmac);
        datas.put("pLAST_MODIFIED_BY", modifiedBy);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                DatabaseConnectionManager.getInstance().openReadOnlyConnection(),
                nameStore,
                datas,
                null,
                "update Field in DB");

        LogHandler.debug(this.getClass(), transactionId + " _ " + response.getDebugString());

        return response;
    }
}
